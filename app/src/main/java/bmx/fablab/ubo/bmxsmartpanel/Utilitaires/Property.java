package bmx.fablab.ubo.bmxsmartpanel.Utilitaires;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by JeanDelest on 25/07/2017.
 */

public class Property {

    private Properties prop;

    public Property(Context context, String propName){
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(propName);
            prop = new Properties();
            prop.load(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getProperty(String id){
        return prop.getProperty(id);
    }

}
