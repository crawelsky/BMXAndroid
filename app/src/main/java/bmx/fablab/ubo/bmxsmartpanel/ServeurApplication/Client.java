package bmx.fablab.ubo.bmxsmartpanel.ServeurApplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import bmx.fablab.ubo.bmxsmartpanel.Login;

/**
 * Created by didi on 22/06/17.
 */

public class Client extends AsyncTask<String, String, Socket>{

    static public final int SERVERPORT = 5030;
    static public String SERVERIP;
    public Socket socket = null;
    private Login login;

    public Client(Login l, String ipserver){
        login = l;
        SERVERIP = ipserver;
    }

    @Override
    protected Socket doInBackground(String... strings) {
        try {
            socket = new Socket(SERVERIP, SERVERPORT);
            Log.d("Info", "Socket connecter !");
        } catch (UnknownHostException e) {
            System.err.println("Impossible de se connecter à l'adresse " + SERVERIP );
        } catch (IOException e) {
            System.err.println("Aucun serveur à l'écoute du port " + SERVERPORT );
        }
        return socket;
    }

    @Override
    protected void onPostExecute(Socket socket) {
        login.clientTaskComplete(socket);
    }
}
