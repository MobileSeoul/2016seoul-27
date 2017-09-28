package soongsil.ourbycicle;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jihuiyeon on 2016. 10. 26..
 */
public class TokenIntentService extends IntentService {
    public static final String COMMAND = "COMMAND";
    public static final int CMD_REG = 0;
    public static final int CMD_DEL = 1;

    public static final String TOKEN_REG = "TOKEN_REG";
    public static final String TOKEN_DEL = "TOKEN_DEL";
    private final String SERVER_URL = "http://116.33.179.48:50/token.php";

    private String mGcmToken = null;
    private String id = null;

    public TokenIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        createToken();

        //희연
        id = intent.getStringExtra("id");

        sendRegTokenToServer(mGcmToken);
        String msg= "token="+SERVER_URL+"&id="+id;

    }

//    private String getReqUrl(String mGcmToken) {
    //    return SERVER_URL+"token="+mGcmToken;
   // }

    private void sendRegTokenToServer(String aUrl) {
        final int HTTP_CONN_TIME_MSEC = 20000;

        InputStreamReader in = null;
        OutputStream out = null;

        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(HTTP_CONN_TIME_MSEC);

            out = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write("id="+id+"&token="+mGcmToken);
            writer.flush();
            writer.close();
            out.close();

            connection.connect();

            int responseCode = connection.getResponseCode();
            if(responseCode<200 || responseCode>=300){
                return;
            }
            else {
                in = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(in);
                mGcmToken = reader.readLine();
                setPreference(mGcmToken);
                Log.e("token", "----------"+mGcmToken+"----------");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if(null != in){
                    in.close();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void createToken(){
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            mGcmToken = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            setPreference(mGcmToken);
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("createToken() error.");
        }
    }

    private void setPreference(String mGcmToken) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(TOKEN_REG, mGcmToken);
        edit.commit();
    }
}
