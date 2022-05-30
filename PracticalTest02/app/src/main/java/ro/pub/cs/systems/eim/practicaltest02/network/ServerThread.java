package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.model.DictionaryInformation;

public class ServerThread extends Thread {

    private int port;
    private ServerSocket serverSocket;

    private HashMap<String, DictionaryInformation> data;

    public HashMap<String, DictionaryInformation> getData() {
        return data;
    }

    public void setData(HashMap<String, DictionaryInformation> data) {
        this.data = data;
    }

    public ServerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            Log.e(Constants.TAG, "[SERVER THREAD] Server socket couldn't be created: " + e.getMessage());
            e.printStackTrace();
        }

        this.data = new HashMap<>();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Log.i(Constants.TAG, "[SERVER_THREAD] Waiting for a client invocation");
                Socket socket = serverSocket.accept();

                Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();

            } catch (IOException e) {
                Log.e(Constants.TAG, "[SERVER THREAD] Could not accept connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + e.getMessage());
                if (Constants.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }
}
