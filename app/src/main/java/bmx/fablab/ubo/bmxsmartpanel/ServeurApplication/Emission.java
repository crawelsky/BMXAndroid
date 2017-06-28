package bmx.fablab.ubo.bmxsmartpanel.ServeurApplication;

/**
 * Created by didi on 22/06/17.
 */

import android.os.AsyncTask;

import java.io.PrintWriter;

import bmx.fablab.ubo.bmxsmartpanel.MainActivity;

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
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        main.emissionTaskComplete();
    }
}