package ro.pub.cs.systems.eim.practicaltest02.network;

import android.support.v4.os.IResultReceiver;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {
            // 1. Se creeaza bufferedReader, printWriter

            // 2. Se citesc datele cu acestea
            // 2.5. Iau structura de date deja existenta cu serverThread.getData()

            // 3. Se creeaza cerere HTTP (daca trebuie) -> HttpGet sau HttpPost:
            // HttpClient httpClient = new DefaultHttpClient()
            // new HttpGet(url)
            // httpResponse: httpClient.execute(httpGet)
            // httpEntity: httpResponse.getEntity()
            // String response: EntityUtils.toString(httpEntity)
            // 3.5: Daca e prin socketi, se creeaza socketClient cu IP si port etc.

            // 4. Se ia raspunsul si se parseaza cu Json probabil -> se trimite cu printWriter
            // JSONObject content = new JSONObject(pageSourceCode);

            // Create bufferedReader, printWriter
            bufferedReader = Utilities.getReader(socket);
            printWriter = Utilities.getWriter(socket);

            // Read the data from the client
            String word = bufferedReader.readLine();
            if (word == null) {
                Log.d(Constants.TAG, "No word received");
                return;
            }

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constants.WEB_URL + word);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();

            if (httpEntity == null) {
                Log.d(Constants.TAG, "[COMMUNICATION THREAD] httpEntity is null");
                return;
            }

            String response = EntityUtils.toString(httpEntity);

            // Parsing the answer
            JSONArray content = new JSONArray(response);
            JSONArray meanings = content.getJSONObject(0).getJSONArray(Constants.MEANINGS);
            JSONArray definitions = meanings.getJSONObject(0).getJSONArray("definitions");
            String definition = definitions.getJSONObject(0).getString("definition");

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Definition is: " + definition);

            printWriter.println(definition);
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
                    if (Constants.DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
