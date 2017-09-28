package soongsil.ourbycicle;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jihuiyeon on 2016. 10. 28..
 */
public class NoticeAdapter extends BaseAdapter{
    private ArrayList<Request> list = new ArrayList<>();
    private ArrayList<Friend> friends = new ArrayList<>();
    private Context context;
    private int layout;
    private LayoutInflater inflater;
    private boolean visible;
    private static String result, id;

    public NoticeAdapter(Context context, int layout, ArrayList<Request> list, ArrayList<Friend> friends, String id){
        this.context = context;
        this.layout = layout;
        this.list = list;
        this.friends = friends;
        this.id = id;
        visible = true;

        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Request getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = inflater.inflate(layout, viewGroup, false);

        TextView textView = (TextView) view.findViewById(R.id.noticeName);
        if(list.get(i).isGroup()){
            textView.setText(list.get(i).getName()+"님의 그룹신청");
        }
        else {
            textView.setText(list.get(i).getName()+"님의 친구신청");
        }

        Button OKbtn = (Button)view.findViewById(R.id.noticeOK);
        OKbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!list.get(i).isGroup()) {
                    Thread thread = new Thread(){
                        InputStreamReader in = null;
                        OutputStream out = null;
                        private final String SERVER_URL = "http://116.33.179.48:50/";

                        @Override
                        public void run() {
                            try {
                                URL url = new URL(SERVER_URL + "request_friend.php");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("POST");
                                connection.setDoInput(true);
                                connection.setDoOutput(true);
                                connection.setConnectTimeout(2000);

                                out = connection.getOutputStream();
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                                writer.write("key=RES_FRIEND&myID=" + id + "&friendID=" + list.get(i).getId());
                                writer.flush();
                                writer.close();
                                out.close();

                                connection.connect();

                                int responseCode = connection.getResponseCode();
                                if (responseCode < 200 || responseCode >= 300)

                                {
                                    return;
                                } else

                                {
                                    //    String result = null;
                                    in = new InputStreamReader(connection.getInputStream(), "UTF-8");
                                    BufferedReader reader = new BufferedReader(in);
                                    result = reader.readLine();
                                    Log.e("friend 수락", "----------" + result + "----------");

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (null != in) {
                                        in.close();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    friends.add(new Friend(list.get(i).getId(), list.get(i).getName()));
                }
                else {
                    Thread thread = new Thread(){
                        InputStreamReader in = null;
                        OutputStream out = null;
                        private final String SERVER_URL = "http://116.33.179.48:50/";

                        @Override
                        public void run() {
                            try {
                                URL url = new URL(SERVER_URL + "request_friend.php");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("POST");
                                connection.setDoInput(true);
                                connection.setDoOutput(true);
                                connection.setConnectTimeout(2000);

                                out = connection.getOutputStream();
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                                writer.write("key=RES_GROUP&myID=" + id + "&friendID=" + list.get(i).getId());
                                writer.flush();
                                writer.close();
                                out.close();

                                connection.connect();

                                int responseCode = connection.getResponseCode();
                                if (responseCode < 200 || responseCode >= 300)

                                {
                                    return;
                                } else

                                {
                                    //    String result = null;
                                    in = new InputStreamReader(connection.getInputStream(), "UTF-8");
                                    BufferedReader reader = new BufferedReader(in);
                                    result = reader.readLine();
                                    Log.e("group 수락", "----------" + result + "----------");

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (null != in) {
                                        in.close();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                list.remove(i);
                notifyDataSetChanged();
                Toast.makeText(context, "수락하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        Button NObtn = (Button)view.findViewById(R.id.noticeNo);
        NObtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "거절하셨습니다.", Toast.LENGTH_SHORT).show();
                list.remove(i);
                notifyDataSetChanged();
            }
        });

        if(visible){
            textView.setVisibility(View.VISIBLE);
            OKbtn.setVisibility(View.VISIBLE);
            NObtn.setVisibility(View.VISIBLE);
            OKbtn.setClickable(true);
            NObtn.setClickable(true);
        }
        else {
            textView.setVisibility(View.INVISIBLE);
            OKbtn.setVisibility(View.INVISIBLE);
            NObtn.setVisibility(View.INVISIBLE);
            OKbtn.setClickable(false);
            NObtn.setClickable(false);
        }

        return view;
    }

    public boolean setVisibilityBtn(boolean visible){
        this.visible = visible;
        notifyDataSetChanged();

        return visible;
    }

    public ArrayList<Friend> getFriends(){
        return friends;
    }
}
