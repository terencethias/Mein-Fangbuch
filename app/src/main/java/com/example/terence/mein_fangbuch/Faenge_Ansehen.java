package com.example.terence.mein_fangbuch;

import android.app.Fragment;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Faenge_Ansehen extends Fragment {


    final String  scripturlstring = "https://terence-thias.000webhostapp.com/fangbuch/show_table.php";
    TextView table;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.faenge_ansehen, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        table = (TextView) getActivity().findViewById(R.id.table);
        sendCatchDataToDb();
    }


    public void sendCatchDataToDb(){


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String textparam = URLEncoder.encode("fischart", "UTF-8")
                            + "=" + URLEncoder.encode("ds", "UTF-8");
                    //Die Key-Value pairs die per POST Methode an php-skript gehen
                   /* String textparam = URLEncoder.encode("fischart", "UTF-8")
                            + "=" + URLEncoder.encode(fischart.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("laenge", "UTF-8")
                            + "=" + URLEncoder.encode(länge.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("anzahl", "UTF-8")
                            + "=" + URLEncoder.encode(anzahl.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("gewaesser", "UTF-8")
                            + "=" + URLEncoder.encode(gewaesser.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("datum", "UTF-8")
                            + "=" + URLEncoder.encode(datum.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("tablename", "UTF-8")
                            + "=" + URLEncoder.encode(Tablename, "UTF-8");
*/
                    URL scripturl = new URL(scripturlstring);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();
                    connection.setDoOutput(true);
                    //connection.setConnectTimeout(1000);
                    //connection.setRequestMethod("POST");

                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();
                    contentWriter.close();




                    InputStream answerInputStream = connection.getInputStream();
                    final String  answer = getTextFromInputStream(answerInputStream);


                    JSONArray mJsonArray = new JSONArray(answer);

                    int size=  mJsonArray.length();

                    int i = 0;
                    String[] fischart = new String[size];
                    String[] länge = new String[size];
                    String[] anzahl = new String[size];
                    String[] gewässer = new String[size];
                    String[] datum = new String[size];

                    while(i < size)
                    {
                        JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                        fischart[i]= mJsonObject.getString("Fischart");
                        länge[i]= mJsonObject.getString("Länge");
                        anzahl[i]= mJsonObject.getString("Anzahl");
                        gewässer[i]= mJsonObject.getString("Gewässer");
                        datum[i]= mJsonObject.getString("Datum");


                        Log.e("Fischarten",fischart[i]);
                        Log.e("Fischarten",länge[i]);
                        Log.e("Fischarten",anzahl[i]);
                        Log.e("Fischarten",gewässer[i]);
                        Log.e("Fischarten",datum[i]);
                        i++;
                    }


                    answerInputStream.close();
                    connection.disconnect();

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }

    //Methode zum lesen des echos vom php-skript
    public String getTextFromInputStream(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();

        String aktuelleZeile;

        try {
            while((aktuelleZeile = reader.readLine()) != null ){
                stringBuilder.append(aktuelleZeile);
                stringBuilder.append("\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return stringBuilder.toString().trim();
    }
    //ist Internet da, true oder false
    public boolean internetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =  connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
