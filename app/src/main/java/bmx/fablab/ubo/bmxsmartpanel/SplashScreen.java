package bmx.fablab.ubo.bmxsmartpanel;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by root on 23/06/17.
 */

public class SplashScreen extends AppCompatActivity {

    private static int SPLACH_TIMEOUT = 3000;

    private TextView textView;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        textView = (TextView)findViewById(R.id.textView);
        setFontLCD(textView, "val.ttf");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent log = new Intent(SplashScreen.this, Login.class);
                startActivity(log);
                finish();
            }
        }, SPLACH_TIMEOUT);
    }

    public void setFontLCD(TextView textView, String fontName) {
        if(fontName != null){
            try {
                Typeface typeface = Typeface.createFromAsset(getAssets(), fontName);
                textView.setTypeface(typeface);
            } catch (Exception e) {
                //Log.e("FONT", fontName + " not found", e);
            }
        }
    }
}
