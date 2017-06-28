package bmx.fablab.ubo.bmxsmartpanel;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.PrintWriter;
import java.net.Socket;

import bmx.fablab.ubo.bmxsmartpanel.ServeurApplication.Emission;

public class MainActivity extends AppCompatActivity {

    /* Objets Réseaux */
    public static Socket socket;
    public Emission emissionTask;
    /* Constants */
    public static final String CONNECTED = "OK";
    public static final String NOT_CONNECTED = "NOTOK";
    public static final String CHUTE = "CHUTE";
    public static final String START = "START";
    public static final String SERVERCLOSE = "CLOSE";
    public static final String STOPCLIENT = "STOP";
    public static boolean shutdown = false;

    /* Autres */
    private Dialog dialog;
    static private Button chut_button;
    static private Button start_button;
    static private TextView alert_view;
    public static Handler messageHandler;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chut_button = (Button) findViewById(R.id.chut_button);
        start_button = (Button) findViewById(R.id.start_button);
        alert_view = (TextView) findViewById(R.id.alert_view);
        socket = Login.SocketHandler.getSocket();
        dialog = ProgressDialog.show(this, "Information", "Patienter s'il vous plaît...");
        dialog.dismiss();
        chut_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(CHUTE);
            }
        });
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(START);
            }
        });
        messageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                message = msg.getData().getString("msg");
                if(MainActivity.SERVERCLOSE.equals(message)) {
                    // quand on arrete le serveur
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                    finish();
                }else {
                    alert_view.setText(message);
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        shutdown = true;
        if(!SERVERCLOSE.equals(message)) sendMessage(STOPCLIENT);
        super.onDestroy();
    }

    public void sendMessage(String msg) {
        try {
            if(!SERVERCLOSE.equals(message)) dialog.show();
            emissionTask = new Emission(new PrintWriter(socket.getOutputStream()), this, msg);
            emissionTask.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void emissionTaskComplete(){
        if(!SERVERCLOSE.equals(message)) dialog.dismiss();
    }

}
