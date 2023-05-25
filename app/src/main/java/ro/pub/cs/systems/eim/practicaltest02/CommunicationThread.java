package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {

    ServerThread serverThread;
    Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public void printDataToClient(CurrencyInformation data, String currency) {
        try {
            PrintWriter printWriter = Utilities.getWriter(socket);
            switch (currency) {
                case Constants.USD:
                    printWriter.println(data.usd);
                    break;
                case Constants.EUR:
                    printWriter.println(data.eur);
                    break;
                default:
                    printWriter.println("Invalid information type");
            }
            printWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);

            String currency = bufferedReader.readLine();
            Log.d(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice..." + currency);
            CurrencyInformation data = serverThread.getData();
            if (data.usd != null && data.eur != null && !serverThread.getStale()) {
                Log.d(Constants.TAG, "[COMMUNICATION THREAD] DATA WAS CACHED");
                printDataToClient(data, currency);
                return;
            }
            Log.d(Constants.TAG, "[COMMUNICATION THREAD] am facut http get");

            CurrencyInformation currencyInformation = new CurrencyInformation();

            String pageSourceCode = "";

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + currency + ".json");
            HttpResponse httpGetResponse = httpClient.execute(httpGet);

            Log.d(Constants.TAG, "[COMMUNICATION THREAD] am trecut de execute");
            HttpEntity httpGetEntity = httpGetResponse.getEntity();
            pageSourceCode = EntityUtils.toString(httpGetEntity);
            if (pageSourceCode == null) {
                return;
            }

            JSONObject content = new JSONObject(pageSourceCode);

            JSONObject main = content.getJSONObject(Constants.BPI);
            JSONObject usd = main.getJSONObject(Constants.USD);
            JSONObject eur = main.getJSONObject(Constants.EUR);

            currencyInformation.usd = usd.getString(Constants.RATE);
            currencyInformation.eur = eur.getString(Constants.RATE);

            serverThread.setData(currencyInformation);
            Log.d(Constants.TAG, "[COMMUNICATION THREAD] " + currencyInformation);
            printDataToClient(currencyInformation, currency);

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

    }
}
