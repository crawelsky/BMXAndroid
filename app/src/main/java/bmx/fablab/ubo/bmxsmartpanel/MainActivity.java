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

import bmx.fablab.ubo.bmxsmartpanel.ServeurApplication.Emission;

public class MainActivity extends AppCompatActivity {
    /* Objets Réseaux */
    public Emission emissionTask;
    /* Constants */
    public static final String CONNECTED = "OK";
    public static final String CHUTE = "CHUTE";
    public static final String START = "START";
    public static final String SERVERCLOSE = "CLOSE";
    public static final String STOPCLIENT = "STOP";
    /* Autres */
    private Dialog dialog;
    private Button chut_button;
    private Button start_button;
    private TextView alert_view;
    private String message;
    private String ident;
    /* Utilitaire */
    public static Handler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ident = getIntent().getStringExtra("id");
        System.out.println("iccccccccccccccccccccccccccccccccccccc " + ident);
        chut_button = (Button) findViewById(R.id.chut_button);
        start_button = (Button) findViewById(R.id.start_button);
        alert_view = (TextView) findViewById(R.id.alert_view);
        dialog = ProgressDialog.show(this, "Information", "Patienter s'il vous plaît...");
        dialog.dismiss();
        chut_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chut_button.setEn
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
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

}
