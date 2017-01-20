package com.example.jesse.bedroomlight;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.example.jesse.bedroomlight.fragments.HomeFragment;
import com.example.jesse.bedroomlight.fragments.Settings;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    static String HOST_IP;
    static String HOST_Port;
    static String ClientID;
    MqttClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupConnection(this);





        ToggleButton toggle = (ToggleButton) findViewById(R.id.bedsideLightToggle);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    pubOn("ledStatus");
                } else {
                    // The toggle is disabled
                    pubOff("ledStatus");
                }
            }
        });

        ToggleButton toggle2 = (ToggleButton) findViewById(R.id.mainLightToggle);
        toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    pubOn("bedroom/light/main");
                } else {
                    // The toggle is disabled
                    pubOff("bedroom/light/main");
                }
            }
        });

    }


    public void homeButtonClicked(MenuItem item){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        HomeFragment fragment = new HomeFragment();
        fragmentTransaction.add(R.id.mainFrame, fragment);
        fragmentTransaction.commit();

        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void settingsButtonClicked(MenuItem item){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Settings fragment = new Settings();
        fragmentTransaction.add(R.id.mainFrame, fragment);
        fragmentTransaction.commit();

        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    private boolean connect() {
        try {

            String topics[] = {"ledStatus"};
            int qos[] = {1};
            client.subscribe(topics, qos);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void pubOn(String topic){
        Log.d("publish","on");
        String payload = "1";
        try {
            client.publish(topic, payload.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void pubOff(String topic){
        Log.d("publish","off");
        String payload = "0";
        try {
           client.publish(topic, payload.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

        setupConnection(this);
    }

    public void setupConnection(Context context){


        SharedPreferences sharedPref = context.getSharedPreferences("AppInfo", MODE_PRIVATE);

        HOST_IP = sharedPref.getString("hostIPAddress","10.0.0.11");
        HOST_Port = sharedPref.getString("portNumber","1883");
        ClientID = sharedPref.getString("clientID","android_client1");


        try {

            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient("tcp://" + HOST_IP + ":" + HOST_Port, ClientID, persistence);
            client.connect();

            if(connect()) {
                Toast.makeText(MainActivity.this,"Connected",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MainActivity.this,"failed",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}