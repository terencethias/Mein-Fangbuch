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
import android.widget.Button;
import android.widget.EditText;

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

public class Fang_Hinzufügen extends Fragment {

    EditText fischart;
    EditText länge;
    EditText anzahl;
    EditText gewaesser;
    EditText datum;
    Button fanghinzusenden;

    final String  scripturlstring = "https://terence-thias.000webhostapp.com/fangbuch/fanghinzu.php";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fanghinzufuegen,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        fischart = (EditText)getActivity().findViewById(R.id.fischart);
        länge = (EditText)getActivity().findViewById(R.id.länge);
        anzahl = (EditText)getActivity().findViewById(R.id.anzahl);
        gewaesser = (EditText)getActivity().findViewById(R.id.gewässer);
        datum = (EditText)getActivity().findViewById(R.id.datum);
        fanghinzusenden = (Button) getActivity().findViewById(R.id.fanghinzusenden);

        fanghinzusenden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCatchDataToDb();
            }
        });



    }

    public void sendCatchDataToDb(){


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Die Key-Value pairs die per POST Methode an php-skript gehen
                    String textparam = URLEncoder.encode("fischart", "UTF-8")
                            + "=" + URLEncoder.encode(fischart.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("laenge", "UTF-8")
                            + "=" + URLEncoder.encode(länge.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("anzahl", "UTF-8")
                            + "=" + URLEncoder.encode(anzahl.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("gewaesser", "UTF-8")
                            + "=" + URLEncoder.encode(gewaesser.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("datum", "UTF-8")
                            + "=" + URLEncoder.encode(datum.getText().toString(), "UTF-8");

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











                    answerInputStream.close();
                    connection.disconnect();

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
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
