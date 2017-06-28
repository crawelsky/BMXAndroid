package bmx.fablab.ubo.bmxsmartpanel.ServeurApplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import bmx.fablab.ubo.bmxsmartpanel.Login;

/**
 * Created by didi on 22/06/17.
 */

public class Client extends AsyncTask<String, String, Socket>{

    static public final int SERVERPORT = 5030;
    static public final String SERVERIP = "10.0.2.2";
    public Socket socket = null;
    private Login login;

    public Client(Login l){
        login = l;
    }

    @Override
    protected Socket doInBackground(String... strings) {
        try {
            InetSocketAddress serverAddr = new InetSocketAddress(SERVERIP, SERVERPORT);
            socket = new Socket();
            socket.connect(serverAddr, 3000);
            Log.d("Info", "Socket connecter !");
        } catch (UnknownHostException e) {
            System.err.println("Impossible de se connecter à l'adresse 172.19.240.188" );
        } catch (IOException e) {
            System.err.println("Aucun serveur à l'écoute du port 5030" );
        }
        return socket;
    }

    @Override
    protected void onPostExecute(Socket socket) {
        login.clientTaskComplete(socket);
    }
}
