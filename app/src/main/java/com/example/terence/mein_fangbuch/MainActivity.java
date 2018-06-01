package com.example.terence.mein_fangbuch;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    final String scripturlstring = "https://terence-thias.000webhostapp.com/fangbuch/login.php";

    TextView tv;   //TextView soll anzeigen ob loggin erfolgreich war
    EditText un;   // un = Username -- hier wir un eingtragen
    EditText pw;   //pw=Passowrt
    Button logBtn;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Erzeugt mir ein Fragment in activity_main , hier Start-Fragtment Login/Registrierscreen
/*
        Login_Fragment logfrag = new Login_Fragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.drawer_layout,logfrag,"login");
        transaction.commit();
*/



        tv = (TextView) findViewById(R.id.login_message); //getActivity gibt context
        un = (EditText) findViewById(R.id.un);
        pw = (EditText) findViewById(R.id.pw);
        logBtn= (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register_Fragment register_fragment = new Register_Fragment();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                //transaction.replace(R.id.login_screen,register_fragment,"musiclist");
                transaction.addToBackStack(null);
                transaction.add(R.id.drawer_layout,register_fragment,"register");

                transaction.commit();
            }
        });


        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetAvailable()) {
                    sendLoginDataToDb();
                }
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.e("fanghinzu",Integer.toString(id));
        if (id == R.id.fanghinzu) {
            Fang_Hinzufügen addcatch = new Fang_Hinzufügen();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.drawer_layout,addcatch,"addcatch");
            //transaction.addToBackStack(null);
            //transaction.add(R.id.drawer_layout,addcatch,"addcatch");
            transaction.commit();

            Log.e("fanghinzu","gedrückt");

        } else if (id == R.id.nav_meinfangbuch) {

        } else if (id == R.id.nav_fängeverwalten) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





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








                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {//Vergleich des in UI eingebenen Passworts mit in Db gespreichterm
                                if(answer.equals(pw.getText().toString())) {
                                    tv.setText("Sie sind nun eingeloggt !");
                                    Log.e("bla",answer);
                                }
                                // Log.e("bla",answer + " " +pw.getText().toString() );
                            }
                        });



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
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =  connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }




}
