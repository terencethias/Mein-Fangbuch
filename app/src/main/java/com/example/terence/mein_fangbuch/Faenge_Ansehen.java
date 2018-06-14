package com.example.terence.mein_fangbuch;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

    String Tablename;
    String[] fischart;
    String[] länge ;
    String[] anzahl ;
    String[] gewässer  ;
    String[] datum ;

    ListView list;
    String[] titles;
    String[] descriptions;


    final String  scripturlstring = "https://terence-thias.000webhostapp.com/fangbuch/show_table.php";
    TextView table;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.faenge_ansehen, container,false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {





        list = (ListView)getActivity().findViewById(R.id.listview);
        sendCatchDataToDb();

    }


    public void sendCatchDataToDb(){


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String textparam = URLEncoder.encode("tablename", "UTF-8")
                            + "=" + URLEncoder.encode(Tablename, "UTF-8");


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
                     fischart = new String[size];
                     länge = new String[size];
                     anzahl = new String[size];
                     gewässer = new String[size];
                     datum = new String[size];

                    while(i < size)
                    {
                        JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                        fischart[i]= mJsonObject.getString("Fischart");
                        länge[i]= mJsonObject.getString("Länge");
                        anzahl[i]= mJsonObject.getString("Anzahl");
                        gewässer[i]= mJsonObject.getString("Gewässer");
                        datum[i]= mJsonObject.getString("Datum");

/*
                        Log.e("Fischarten",fischart[i]);
                        Log.e("Fischarten",länge[i]);
                        Log.e("Fischarten",anzahl[i]);
                        Log.e("Fischarten",gewässer[i]);
                        Log.e("Fischarten",datum[i]);
                        */
                        i++;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {//Vergleich des in UI eingebenen Passworts mit in Db gespreichterm
                            TerenceAdapter adapter = new TerenceAdapter(getActivity(), fischart, länge, anzahl, gewässer, datum);
                            list.setAdapter(adapter);
                        }
                    });



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



    class TerenceAdapter extends ArrayAdapter<String> {

        int size =1;
        Context context;
        String[] fischartenArray;
        String[] längeArray;
        String[] anzahlArray;
        String[] gewässerArray;
        String[] datumArray;



        TerenceAdapter(Context c, String[] fischart, String[] länge, String[] anzahl, String[] gewässer, String[] datum){
            super(c, R.layout.single_row, R.id.textView1, fischart);
            this.context = c;
            this.fischartenArray = fischart;
            this.längeArray = länge;
            this.anzahlArray = anzahl;
            this.gewässerArray = gewässer;
            this.datumArray = datum;
        }



        class MyViewHolder
        {
            TextView fischarten;
            TextView längen;
            TextView anzahl;
            TextView gewässer;
            TextView datum;
            MyViewHolder(View v)
            {
                fischarten = (TextView) v.findViewById(R.id.textView1);
                längen = (TextView) v.findViewById(R.id.textView2);
                anzahl = (TextView) v.findViewById(R.id.textView3);
                gewässer = (TextView) v.findViewById(R.id.textView4);
                datum = (TextView) v.findViewById(R.id.textView5);



            }

        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            MyViewHolder holder = null;
            if(row==null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.single_row, parent,false);
                holder = new MyViewHolder(row);
                row.setTag(holder);
            }
            else{

                holder = (MyViewHolder) row.getTag();

            }



            holder.fischarten.setText("Fischart:    " +fischartenArray[position]);
            holder.längen.setText("Länge in cm:    " +längeArray[position]);
            holder.anzahl.setText("Anzahl:    " +anzahlArray[position]);
            holder.gewässer.setText("Gewässer:    " + gewässerArray[position]);
            holder.datum.setText("Datum:    " + datumArray[position]);

            return row;
        }
    }




    public void setTableNameVariable(String tablename){



        Tablename = tablename;

    }




}
