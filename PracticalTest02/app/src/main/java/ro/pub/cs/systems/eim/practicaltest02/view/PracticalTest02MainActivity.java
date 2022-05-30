package ro.pub.cs.systems.eim.practicaltest02.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02.R;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {
    private EditText serverPortEditText;
    private EditText clientAddressEditText;
    private EditText clientPortEditText;

    private EditText dictionaryWordEditText;

    private Button connectToServerButton;
    private Button showResultsButton;

    TextView responseDataTextView;

    private ServerThread serverThread;
    private ClientThread clientThread;

    private ConnectToServerButtonOnClickListener connectToServerButtonOnClickListener = new ConnectToServerButtonOnClickListener();
    private class ConnectToServerButtonOnClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            // Get the server port from editText
            String serverPort = serverPortEditText.getText().toString();

            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Start the server
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN_ACTIVITY] Could not create server thread");
                return;
            }

            serverThread.start();
        }
    }

    private ShowResultsButtonOnClickListener showResultsButtonOnClickListener = new ShowResultsButtonOnClickListener();
    private class ShowResultsButtonOnClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            // Get the client ip address and port
            String clientIpAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();

            // Get the data
            String word = dictionaryWordEditText.getText().toString();

            Log.v(Constants.TAG, "[MAIN_ACTIVITY] WORD IS: " + word);

            // Initialize the clientThread (same as serverThread)
            clientThread = new ClientThread(clientIpAddress, Integer.parseInt(clientPort), word, responseDataTextView);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText) findViewById(R.id.serverPortEditText);
        clientAddressEditText = (EditText) findViewById(R.id.clientIpAddressEditText);
        clientPortEditText = (EditText) findViewById(R.id.clientPortEditText);

        connectToServerButton = (Button) findViewById(R.id.connectButtonEditText);
        connectToServerButton.setOnClickListener(connectToServerButtonOnClickListener);

        showResultsButton = (Button) findViewById(R.id.submitButton);
        showResultsButton.setOnClickListener(showResultsButtonOnClickListener);

        responseDataTextView = (TextView) findViewById(R.id.responseDataTextView);

        dictionaryWordEditText = (EditText) findViewById(R.id.dictionaryWordEditText);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}