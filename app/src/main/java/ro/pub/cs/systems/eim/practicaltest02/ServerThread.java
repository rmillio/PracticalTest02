package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

public class ServerThread extends Thread {
    String port;
    ServerSocket serverSocket = null;

    CurrencyInformation data;

    public ServerThread(String port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(Integer.parseInt(port));
        this.data = new CurrencyInformation();
    }

    public void setData(CurrencyInformation data) {
        this.data = data;
    }

    public CurrencyInformation getData() {
        return data;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        try {

            while (!Thread.currentThread().isInterrupted()) {
                // server is listening for client requests
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");
                new CommunicationThread(this, serverSocket.accept()).start();
            }
        } catch (Exception ignored) {
        }
    }

    public void stopThread() {
        this.interrupt();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}