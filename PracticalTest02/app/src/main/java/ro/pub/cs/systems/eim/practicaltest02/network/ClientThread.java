package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {

    // TODO: add params for the data
    private TextView responseDataTextView = null;

    private String clientIpAddress = null;
    private int clientPort = 0;
    private Socket socket = null;

    // add the constructor


    @Override
    public void run() {
        try {
            Socket socket = new Socket(clientIpAddress, clientPort);

            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT_THREAD] Could not create socket!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT_THREAD] BufferedReader / PrintWriter is null");
                return;
            }

            // Send the data
            // printWriter.println(...), printWriter.flush(), repeat.

            // Receive the response and set it on the text view
            String response;
            while ((response = bufferedReader.readLine()) != null) {
                final String responseData = response;
                responseDataTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        responseDataTextView.setText(responseData);
                    }
                });
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "[CLIENT THREAD] Could not open socket: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
