package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    String clientAddress;
    String clientPort;
    String currency;

    PracticalTest02MainActivity mainActivity;

    Socket socket = null;

    CurrencyInformation data = null;

    public ClientThread(String clientAddress, String clientPort, String currency, PracticalTest02MainActivity mainActivity) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.currency = currency;
        this.mainActivity = mainActivity;
    }

    public CurrencyInformation getData() {
        return data;
    }

    public void run() {
        try {
            Log.d(Constants.TAG, "[CLIENT THREAD] Started thread");
            socket = new Socket(clientAddress, Integer.parseInt(clientPort));

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter bufferedWriter = Utilities.getWriter(socket);

            bufferedWriter.println(currency);
            bufferedWriter.flush();

            CurrencyInformation currencyInformation = new CurrencyInformation();
            if (currency.equals(Constants.USD)) {
                Log.d(Constants.TAG, "[CLIENT THREAD] started to read");
                currencyInformation.usd = bufferedReader.readLine();
            } else if (currency.equals(Constants.EUR)){
                currencyInformation.eur = bufferedReader.readLine();
            }

            data = currencyInformation;
            Log.d(Constants.TAG, "[CLIENT THREAD] Received data: " + data);
            mainActivity.notify(currencyInformation);

            socket.close();
            this.interrupt();
        } catch (Exception e) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + e.toString());
        }
    }

    public void stopThread() {
        this.interrupt();

        if (socket != null) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }
}
