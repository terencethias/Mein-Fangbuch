package com.example.terence.mein_fangbuch;

import android.app.Fragment;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
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

public class Login_Fragment extends Fragment {

    //Url zum login.php skript..dieses liefert mir das Passwort zu einem Username
    final String scripturlstring = "https://terence-thias.000webhostapp.com/fangbuch/login.php";

    TextView tv;   //TextView soll anzeigen ob loggin erfolgreich war
    EditText un;   // un = Username -- hier wir un eingtragen
    EditText pw;   //pw=Passowrt
    Button logBtn;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_screen,container,false);
        //Zuweisen der fragment xml layoutdatei und inflaten(macht xml zu javaObjekt)
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv = (TextView) getActivity().findViewById(R.id.login_message); //getActivity gibt context
        un = (EditText) getActivity().findViewById(R.id.un);
        pw = (EditText) getActivity().findViewById(R.id.pw);
        logBtn= (Button) getActivity().findViewById(R.id.login);

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetAvailable()) {
                    sendLoginDataToDb();
                } else {
                    Toast.makeText(getActivity(), "Internet ist nicht Verfügbar.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //  Funktion in der der Daten an Database geschickt werden
    public void sendLoginDataToDb(){


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Die Key-Value pairs die per POST Methode an php-skript gehen
                    String textparam = URLEncoder.encode("vorname", "UTF-8")
                            + "=" + URLEncoder.encode(un.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("passwort", "UTF-8")
                            + "=" + URLEncoder.encode(pw.getText().toString(), "UTF-8");

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




                if(isAdded()){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {//Vergleich des in UI eingebenen Passworts mit in Db gespreichterm
                            if(answer.equals(pw.getText().toString())) {
                                tv.setText("Sie sind nun eingeloggt !");
                                Log.e("bla",answer);
                            }
                           // Log.e("bla",answer + " " +pw.getText().toString() );
                        }
                    });}

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
