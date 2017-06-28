package bmx.fablab.ubo.bmxsmartpanel;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    private PrintWriter out = null;
    private BufferedReader in = null;
    /* Autres */
    private Dialog dialog;
    private TextView info_view;
    private Button connect_button;
    private EditText login_edit;
    private EditText pass_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        info_view = (TextView) findViewById(R.id.info_view);
        connect_button = (Button) findViewById(R.id.connect_button);
        login_edit = (EditText) findViewById(R.id.login_edit);
        login_edit.setText("log");
        pass_edit = (EditText) findViewById(R.id.pass_edit);
        pass_edit.setText("pwd");
        dialog = ProgressDialog.show(this, "Information", "Patienter s'il vous plaît...");
        dialog.dismiss();
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!login_edit.getText().toString().isEmpty() || !pass_edit.getText().toString().isEmpty()) {
                    clientTask = new Client(Login.this);
                    clientTask.execute();
                    dialog.show();
                }else{
                    info_view.setText("Login ou mot de passe non saisie !");
                }
            }
        });
    }

    public void clientTaskComplete(Socket s){
        socket = s;
        if(socket.isConnected()){
            connexionTask = new Connexion(socket, Login.this, login_edit.getText().toString(), pass_edit.getText().toString());
            connexionTask.execute();
        }else {
            info_view.setText("Aucun serveur n'est à l'écoute du port : " + Client.SERVERPORT);
            dialog.dismiss();
        }
        clientTask.cancel(true);
    }

    public void connexionTaskComplete(boolean data) {
        dialog.dismiss();
        if (data) {
            SocketHandler.setSocket(socket);
            try {
                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Thread t = new Thread(new Reception(socket, in));
                t.setDaemon(true);
                t.start();
                Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);
                finish();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else{
            info_view.setText("Login ou mot de passe non valide !");
        }
        connexionTask.cancel(true);
    }

    static public class SocketHandler {
        private static Socket socket;

        public static synchronized Socket getSocket(){
            return socket;
        }

        public static synchronized void setSocket(Socket socket){
            SocketHandler.socket = socket;
        }
    }
}

