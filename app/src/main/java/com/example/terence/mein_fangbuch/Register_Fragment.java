package com.example.terence.mein_fangbuch;

import android.app.Fragment;
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

public class Register_Fragment extends Fragment {
    final String scripturlstring2 = "https://terence-thias.000webhostapp.com/fangbuch/register.php";
    EditText vn;
    EditText nn;
    EditText registerpw;
    EditText kontrollpw;
    Button registerbutton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_screen,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vn = (EditText) getActivity().findViewById(R.id.vorname);
        nn = (EditText) getActivity().findViewById(R.id.nachname);
        registerpw = (EditText) getActivity().findViewById(R.id.register_passwort);
        kontrollpw = (EditText) getActivity().findViewById(R.id.kontrollpasswort);
        registerbutton =(Button) getActivity().findViewById(R.id.registerbutton);

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(registerpw.getText().toString().equals(kontrollpw.getText().toString())) {
                    register();
                    getActivity().onBackPressed();
                }
            }
        });


    }

    public void register(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Die Key-Value pairs die per POST Methode an php-skript gehen
                    String textparam2 = URLEncoder.encode("vorname", "UTF-8")
                            + "=" + URLEncoder.encode(vn.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("nachname", "UTF-8")
                            + "=" + URLEncoder.encode(nn.getText().toString(), "UTF-8")+"&"+
                            URLEncoder.encode("passwort", "UTF-8")
                            + "=" + URLEncoder.encode(registerpw.getText().toString(), "UTF-8");

                    URL scripturl = new URL(scripturlstring2);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();
                    connection.setDoOutput(true);
                    //connection.setConnectTimeout(1000);
                    //connection.setRequestMethod("POST");

                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setFixedLengthStreamingMode(textparam2.getBytes().length);

                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam2);
                    contentWriter.flush();
                    contentWriter.close();




                    InputStream answerInputStream = connection.getInputStream();
                    final String  answer = getTextFromInputStream(answerInputStream);

                    Log.e("answer",answer);



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

}
