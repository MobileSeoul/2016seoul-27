package soongsil.ourbycicle;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.StringTokenizer;

/**
 * Created by jihuiyeon on 2016. 10. 27..
 */
public class ExGcmListenerService extends GcmListenerService {
    Intent intent;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        intent = new Intent(this, MainActivity.class);
        String message = data.getString("message");
    //    String message = bundle.getBundle("data").getString("msg");
        Log.e("GCM", "--------------"+message+"--------------------");
        String key;

        if(message == null)
            return;

        StringTokenizer st = new StringTokenizer(message, "|");
        key = st.nextToken();

        switch (key){
            case "REQ_FRIEND": {
                String tmp = st.nextToken();
                String tmp1 = st.nextToken();
                req_friend(tmp, tmp1);
                break;
            }
            case "RES_FRIEND":{
                String tmp = st.nextToken();
                String tmp1 = st.nextToken();
                res_friend(tmp, tmp1);
                break;
            }
            case "REQ_GROUP":{
                String tmp = st.nextToken();
                String tmp1 = st.nextToken();
                req_group(tmp, tmp1);
                break;
            }
            case "RES_GROUP":{
                String tmp = st.nextToken();
                String tmp1 = st.nextToken();
                res_group(tmp, tmp1);
                break;
            }
            default:
                break;
        }

    //    sendNotification("test", message);

//        req_friend("id1", "name1");
    }

    private void res_group(String id, String name) {
        intent.putExtra("function", "res_group");
        intent.putExtra("friend_id", id);
        intent.putExtra("name", name);

        sendNotification("그룹신청 수락", name+"님이 그룹신청을 수락하셨습니다.");
    }

    private void req_group(String id, String name) {
        intent.putExtra("function", "req_group");
        intent.putExtra("friend_id", id);
        intent.putExtra("name", name);

        sendNotification("그룹신청", name+"님의 그룹신청.");
    }

    private void req_friend(String id, String name){
        intent.putExtra("function", "req_friend");
        intent.putExtra("friend_id", id);
        intent.putExtra("name", name);

        sendNotification("친구신청", name+"님의 친구신청.");
    }

    private void res_friend(String id, String name){
        intent.putExtra("function", "res_friend");
        intent.putExtra("friend_id", id);
        intent.putExtra("name", name);

        sendNotification("친구신청 수락", name+"님이 친구신청을 수락하셨습니다.");
    }

    private void sendNotification(String title, String message) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title).setContentText(message).setAutoCancel(true).setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
