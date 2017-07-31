package bmx.fablab.ubo.bmxsmartpanel;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import bmx.fablab.ubo.bmxsmartpanel.Utilitaires.Property;

/**
 * Created by root on 23/06/17.
 */

public class Login extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
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
    /* Constants */
    private Property property;
    public static String CONNECTED;
    public static String ALREADY_CONNECTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textView2 = (TextView) findViewById(R.id.textView2);
        connect_button = (Button) findViewById(R.id.connect_button);
        ident_edit = (EditText) findViewById(R.id.ident_edit);
        property = new Property(this, "prop.properties");
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
                int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    return;
                }
                loadProperty();
                if(!ident_edit.getText().toString().isEmpty() ) {
                    ident = ident_edit.getText().toString();
                    int permission = checkSelfPermission(Manifest.permission.CAMERA);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[] {Manifest.permission.CAMERA},
                                REQUEST_CODE_ASK_PERMISSIONS);
                        return;
                    }
                    new IntentIntegrator(Login.this).initiateScan();
                }else{
                    Toast.makeText(Login.this, "Login ou mot de passe non saisie !", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadProperty() {
            CONNECTED = property.getProperty("CONNECTED");
            ALREADY_CONNECTED = property.getProperty("ALREADY_CONNECTED");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Aucun code flashé !", Toast.LENGTH_LONG).show();
            } else {
                if(result.getContents().contains("-")
                        // Car on a
                        && result.getContents().length() > 22 && result.getContents().length() < 38) {
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
            Toast.makeText(Login.this, "Problème d'authentification, pensé à changer d'identifiant !", Toast.LENGTH_LONG).show();
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

