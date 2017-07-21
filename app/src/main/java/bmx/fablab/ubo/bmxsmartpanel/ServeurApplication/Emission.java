package bmx.fablab.ubo.bmxsmartpanel.ServeurApplication;

/**
 * Created by didi on 22/06/17.
 */

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintWriter;

import bmx.fablab.ubo.bmxsmartpanel.Login;
import bmx.fablab.ubo.bmxsmartpanel.MainActivity;

import static bmx.fablab.ubo.bmxsmartpanel.MainActivity.STOPCLIENT;

public class Emission extends AsyncTask<String, String, Boolean> {

    private PrintWriter out;
    private MainActivity main;
    private String message;

    public Emission(PrintWriter out, MainActivity m, String msg) {
        this.out = out;
        main = m;
        message = msg;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        out.println(message);
        out.flush();
        if(STOPCLIENT.equals(message)){
            try {
                Login.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        main.emissionTaskComplete();
    }
}