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
            if (data.usd != null && data.eur != null) {
                printDataToClient(data, currency);
                return;
            }
            Log.d(Constants.TAG, "[COMMUNICATION THREAD] am facut http get");

            CurrencyInformation currencyInformation = new CurrencyInformation();

            String pageSourceCode = "";


//            String webPageAddress = Constants.WEB_SERVICE_ADDRESS + "?q=" + city + "&appid=" + Constants.WEB_SERVICE_API_KEY;
//            HttpURLConnection connection = (HttpURLConnection) new URL(webPageAddress).openConnection();
//            InputStream in = new BufferedInputStream(connection.getInputStream());

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + currency + ".json");
            HttpResponse httpGetResponse = httpClient.execute(httpGet);

            Log.d(Constants.TAG, "[COMMUNICATION THREAD] am trecut de execute");
//            HttpEntity httpGetEntity = httpGetResponse.getEntity();
//            pageSourceCode = EntityUtils.toString(httpGetEntity);
//            if (pageSourceCode == null) {
//                return;
//            }
//
//            JSONObject content = new JSONObject(pageSourceCode);
////            JSONArray weatherArray = content.getJSONArray(Constants.WEATHER);
//
//            JSONObject main = content.getJSONObject(Constants.MAIN);
//            String usd = content.getString(Constants.USD);
//            String eur = content.getString(Constants.EUR);

            currencyInformation.usd = "20";
            currencyInformation.eur = "25";

            serverThread.setData(currencyInformation);
            Log.d(Constants.TAG, "[COMMUNICATION THREAD] " + currencyInformation);
            printDataToClient(currencyInformation, currency);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
