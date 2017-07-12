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
    public static String message = null;
    public Reception(BufferedReader in){
        this.in = in;
    }

    public void run() {
        while(true){
            try {
                message = in.readLine();
            } catch (IOException e) {
                message = null;
                e.printStackTrace();
            }
            if(message != null){
                Message msg = Message.obtain();
                Bundle bd = new Bundle();
                bd.putString("msg", message);
                msg.setData(bd);
                MainActivity.messageHandler.sendMessage(msg);
                message = null;
            }
        }
    }
}
