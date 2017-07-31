package bmx.fablab.ubo.bmxsmartpanel;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;

import bmx.fablab.ubo.bmxsmartpanel.ServeurApplication.Emission;
import bmx.fablab.ubo.bmxsmartpanel.Utilitaires.Property;

public class MainActivity extends AppCompatActivity {
    /* Objets Réseaux */
    public Emission emissionTask;
    /* Autres */
    private Dialog dialog;
    private Button chut_button;
    private Button start_button;
    private TextView alert_view;
    private TextView id_view;
    private ImageView wifi_view;
    private String message;
    private String identMessage;
    private boolean isReceiverRegistered = false;
    private boolean isConnected = false;
    /* Utilitaire */
    public static Handler messageHandler;
    /* Constantes */
    private Property property;
    public static String CHUTE;
    public static String START;
    public static String SERVERCLOSE;
    public static String STOPCLIENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        property = new Property(this, "prop.properties");
        loadProperty();
        chut_button = (Button) findViewById(R.id.chut_button);
        start_button = (Button) findViewById(R.id.start_button);
        alert_view = (TextView) findViewById(R.id.alert_view);
        id_view = (TextView) findViewById(R.id.id_view);
        wifi_view = (ImageView)findViewById(R.id.wifi_view);
        dialog = ProgressDialog.show(this, "Information", "Patienter s'il vous plaît...");
        dialog.dismiss();
        start_button.setVisibility(View.GONE);
        chut_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chut_button.setVisibility(View.GONE);
                start_button.setVisibility(View.VISIBLE);
                sendMessage(CHUTE);
            }
        });
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chut_button.setVisibility(View.VISIBLE);
                start_button.setVisibility(View.GONE);
                sendMessage(START);
            }
        });
        try {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "appleFont/AppleGaramond.ttf");
            chut_button.setTypeface(typeface);
            start_button.setTypeface(typeface);
            alert_view.setTypeface(typeface);
            id_view.setTypeface(typeface);
        } catch (Exception e) {
            //Log.e("FONT", fontName + " not found", e);
        }
        /* Handle message */
        messageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                identMessage = msg.getData().getString("id");
                message = msg.getData().getString("msg");
                if(SERVERCLOSE.equals(message)) {
                    // quand on arrete le serveur
                    Intent i = new Intent(MainActivity.this, Login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }else {
                    alert_view.setText(message);
                    Log.println(Log.ASSERT, "TAGEUR", identMessage);
                    id_view.setText("Message de " + identMessage);
                    Toast.makeText(MainActivity.this, "Vous avez reçu un nouveau message.", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void loadProperty(){
        CHUTE = property.getProperty("CHUTE");
        START = property.getProperty("START");
        SERVERCLOSE = property.getProperty("SERVERCLOSE");
        STOPCLIENT = property.getProperty("STOPCLIENT");
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            isReceiverRegistered = true;
            registerReceiver(broadcastReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isReceiverRegistered) {
            isReceiverRegistered = false;
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!SERVERCLOSE.equals(message)) sendMessage(STOPCLIENT);
    }

    public void sendMessage(String msg) {
        try {
            if(!SERVERCLOSE.equals(msg) && !STOPCLIENT.equals(msg)) dialog.show();
            emissionTask = new Emission(new PrintWriter(Login.getSocket().getOutputStream()), this, msg);
            emissionTask.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void emissionTaskComplete(){
        dialog.dismiss();
    }

    private NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            final NetworkInfo info = getNetworkInfo(context);
            if (info != null && info.isConnected()) {
                isConnected = true;
                if (WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4) > -50){
                    wifi_view.setImageResource(R.drawable.wifi_high);
                }else if(WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4) < -50 &&
                        WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4) > -60 ){
                    wifi_view.setImageResource(R.drawable.wifi_good);
                }else if(WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4) < -60
                        && WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4) > -70){
                    wifi_view.setImageResource(R.drawable.wifi_weak);
                }else{
                    wifi_view.setImageResource(R.drawable.wifi_no_signal);
                }
            }else {
                isConnected = false;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Paramètres WIFI");
                alertDialogBuilder.setMessage("Voulez-vous activez le WIFI ?")
                        .setCancelable(false)
                        .setPositiveButton("Oui",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                wifiManager.setWifiEnabled(true);
                            }
                        })
                        .setNegativeButton("Non",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                wifiManager.setWifiEnabled(false);
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    };
}
