package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PracticalTest02MainActivity extends AppCompatActivity {


    EditText serverPortEditText = null;
    EditText clientAddressEditText = null;
    EditText clientPortEditText = null;
    EditText currencyEditText = null;

    Button startServerButton = null;
    Button getWeatherForecastButton = null;
    Button startClientButton = null;

    TextView responseTextView = null;

    ServerThread serverThread = null;

    public void notify(CurrencyInformation data) {
        runOnUiThread(() -> {
            if (currencyEditText.getText().toString().equals(Constants.USD)) {
                responseTextView.setText(data.usd);
            } else if (currencyEditText.getText().toString().equals(Constants.EUR)) {
                responseTextView.setText(data.eur);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);


        serverPortEditText = (EditText)findViewById(R.id.serverport);
        clientAddressEditText = (EditText)findViewById(R.id.clientip);
        clientPortEditText = (EditText)findViewById(R.id.clientport);
        currencyEditText = (EditText)findViewById(R.id.clientinput1);

        startServerButton = (Button)findViewById(R.id.serverbtn);
        startClientButton = (Button)findViewById(R.id.clientbtn);

        responseTextView = (TextView)findViewById(R.id.response);

        startServerButton.setOnClickListener(view -> {
            String port = serverPortEditText.getText().toString();
            if (port.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread != null) {
                serverThread.stopThread();
            }

            try {
                serverThread = new ServerThread(port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (serverThread.getServerSocket() == null) {
                Toast.makeText(getApplicationContext(), "Could not create server thread!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread.start();
            Toast.makeText(getApplicationContext(), "Server started!", Toast.LENGTH_SHORT).show();

        });

        startClientButton.setOnClickListener(view -> {
            String port = clientPortEditText.getText().toString();
            if (port.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            String address = clientAddressEditText.getText().toString();
            if (address.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client address should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "Server is not running!", Toast.LENGTH_SHORT).show();
                return;
            }

            ClientThread clientThread = new ClientThread(address, port, currencyEditText.getText().toString(), this);
            clientThread.start();
        });

    }
}