package bmx.fablab.ubo.bmxsmartpanel;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import bmx.fablab.ubo.bmxsmartpanel.ServeurApplication.Client;
import bmx.fablab.ubo.bmxsmartpanel.ServeurApplication.Connexion;
import bmx.fablab.ubo.bmxsmartpanel.ServeurApplication.Reception;

/**
 * Created by root on 23/06/17.
 */

public class Login extends AppCompatActivity {

    /* Objets Réseaux */
    public static Socket socket;
    private Connexion connexionTask;
    private Client clientTask;
    public static Thread threadReception;
    private BufferedReader in = null;
    /* Autres */
    private Dialog dialog;
    private TextView textView2;
    private Button connect_button;
    private EditText ident_edit;
    private String login;
    private String pass;
    private String ipserver;
    private String ident;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textView2 = (TextView) findViewById(R.id.textView2);
        connect_button = (Button) findViewById(R.id.connect_button);
        ident_edit = (EditText) findViewById(R.id.ident_edit);
        try {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "appleFont/AppleGaramond.ttf");
            textView2.setTypeface(typeface);
            ident_edit.setTypeface(typeface);
            connect_button.setTypeface(typeface);
        } catch (Exception e) {
            //Log.e("FONT", fontName + " not found", e);
        }
        dialog = ProgressDialog.show(this, "Information", "Patienter s'il vous plaît...");
        dialog.dismiss();
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ident_edit.getText().toString().isEmpty() ) {
                    ident = ident_edit.getText().toString();
                    new IntentIntegrator(Login.this).initiateScan();
                }else{
                    Toast.makeText(Login.this, "Login ou mot de passe non saisie !", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Aucun code flashé !", Toast.LENGTH_LONG).show();
            } else {
                if(result.getContents().contains("-")
                        && result.getContents().length() > 10) {
                    getServerInformation(result.getContents());
                    clientTask = new Client(this, ipserver);
                    clientTask.execute();
                    dialog.show();
                }else{
                    Toast.makeText(this, "Le QrCode scanné n'est pas valide !", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void clientTaskComplete(Socket s){
        socket = s;
        if(s != null){
            connexionTask = new Connexion(socket, Login.this, login, pass, ident);
            connexionTask.execute();
        }else {
            Toast.makeText(Login.this, "Aucun serveur n'est à l'écoute du port : " + Client.SERVERPORT, Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
        clientTask.cancel(true);
    }

    public void connexionTaskComplete(boolean data) {
        dialog.dismiss();
        if (data) {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                threadReception = new Thread(new Reception(in));
                threadReception.setDaemon(true);
                threadReception.start();
                Intent i = new Intent(Login.this, MainActivity.class);
                i.putExtra("id", ident_edit.getText());
                startActivity(i);
                finish();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else{
            Toast.makeText(Login.this, "Login ou mot de passe non valide !", Toast.LENGTH_LONG).show();
        }
        connexionTask.cancel(true);
    }


    public void getServerInformation(String qrCode){
        String result[] = qrCode.split("-", -1);
        login = result[0];
        pass = result[1];
        ipserver = result[2];
    }

    public static Socket getSocket(){
        return socket;
    }

}

