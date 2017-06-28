package bmx.fablab.ubo.bmxsmartpanel.ServeurApplication;

import android.os.Bundle;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import bmx.fablab.ubo.bmxsmartpanel.MainActivity;


/**
 * Created by didi on 22/06/17.
 */


public class Reception implements Runnable {

    private Socket socket;
    private BufferedReader in;
    public static String message = null;
    public Reception(Socket socket, BufferedReader in){
        this.in = in;
        this.socket = socket;
    }

    public void run() {
        while(!MainActivity.shutdown){
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
