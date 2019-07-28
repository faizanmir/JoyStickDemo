package avishkaar.com.joystickdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WifiDetection extends AppCompatActivity implements  DialogAlert.dialogToMain {
    ArrayList<String>scanResults;
    RecyclerView wifiRecyclerView;
    Button scan,stop;
    List<ScanResult> networks;
    WifiRecyclerViewAdapter wifiRecyclerViewAdapter;
    WifiManager wifiManager;
    BroadcastReceiver broadcastReceiver;
    private static final String TAG = "WifiDetection";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_detection);
        init();
        wifiRecyclerViewAdapter = new WifiRecyclerViewAdapter(scanResults, new WifiRecyclerViewAdapter.SSIDPass() {
            @Override
            public void passSSID(String SSID) {
                Log.e(TAG, "SSID: " + SSID  );
                DialogAlert obj = new DialogAlert();
                obj.recieveSSID(SSID);
                obj.show(getSupportFragmentManager(),"DialogTag");
            }
        });

        wifiRecyclerView.setAdapter(wifiRecyclerViewAdapter);

        wifiRecyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                networks = wifiManager.getScanResults();
                for (ScanResult result:networks
                     ) {
                    scanResults.add(result.SSID);
                    wifiRecyclerViewAdapter.notifyDataSetChanged();
                }

            }
        };
        registerReceiver(broadcastReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " + "Scan Started..." );
                scanResults.clear();
                wifiManager.startScan();
            }
        });

    }

    void init()
    {
        scanResults = new ArrayList<>();
       wifiRecyclerView = findViewById(R.id.wifiRecyclerView);
       scan = findViewById(R.id.scanWifi);
       stop = findViewById(R.id.stopScan);
       networks = new ArrayList<>();
       wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }





    void connectToWifi(String SSID,String password)
    {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", SSID);
        wifiConfiguration.preSharedKey = String.format("\"%s\"", password);
        int netId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        Intent intent = new Intent(WifiDetection.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void nameAndPasswordToMain(String SSID, String password) {
        connectToWifi(SSID,password);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
