package bmx.fablab.ubo.bmxsmartpanel.ServeurApplication;

import android.os.Bundle;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;

import bmx.fablab.ubo.bmxsmartpanel.MainActivity;


/**
 * Created by didi on 22/06/17.
 */


public class Reception implements Runnable {

    private BufferedReader in;
    public static String id = null;
    public static String message = null;
    public Reception(BufferedReader in){
        this.in = in;
    }

    public void run() {
        while(true){
            try {
                id = in.readLine();
                message = in.readLine();
                Message msg = Message.obtain();
                Bundle bd = new Bundle();
                bd.putString("id", id);
                bd.putString("msg", message);
                msg.setData(bd);
                MainActivity.messageHandler.sendMessage(msg);
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
