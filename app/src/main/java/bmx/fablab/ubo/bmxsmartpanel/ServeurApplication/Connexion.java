package bmx.fablab.ubo.bmxsmartpanel.ServeurApplication;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bmx.fablab.ubo.bmxsmartpanel.Login;
import bmx.fablab.ubo.bmxsmartpanel.MainActivity;

/**
 * Created by didi on 22/06/17.
 */

public class Connexion extends AsyncTask<String, String, Boolean>  {

    private String log;
    private String pwd;
    private Login logActivity;
    private Socket socket = null;
    public static Thread t2;
    private PrintWriter out = null;
    private BufferedReader in = null;

    public Connexion(Socket s, Login logActivity, String log, String pwd){
        socket = s;
        this.logActivity = logActivity;
        this.log = log;
        this.pwd = pwd;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        boolean result = false;
        try {
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(log);
            out.flush();
            // Envoie password;
            out.println(pwd);
            out.flush();
            if (MainActivity.CONNECTED.equals(in.readLine())) {
                result = true;
            } else {
                System.err.println("Vos informations sont incorrectes ");
            }
        }catch (Exception a){
            a.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean data) {
        logActivity.connexionTaskComplete(data);
    }
}