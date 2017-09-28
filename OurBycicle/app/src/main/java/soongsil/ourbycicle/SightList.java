package soongsil.ourbycicle;

// 김수운 - 관광지 카테고리별 관광지 리스트

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

///*
public class SightList extends Activity {
    ArrayList<Sight> sights = new ArrayList<Sight>();
    ListView listView;
    SightAdapter adapter;
    Context mContext = this;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sight_list);

        Intent itn = getIntent();
        String category = itn.getStringExtra("category");
        ArrayList<Sight> list = (ArrayList<Sight>)itn.getSerializableExtra("sights");


        // 관광지 사진 가져와서 리스트에 넣는 쓰레드 실행
        GetBitmap get = new GetBitmap(category, list);
        get.execute();

        listView = (ListView)findViewById(R.id.sightList);
        adapter = new SightAdapter(this, R.layout.my_sight_list, sights);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 클릭하면 Dialog로 관광지 상세정보 보여주기
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                // R.layout.dialog는 xml 파일명이고  R.id.popup은 보여줄 레이아웃 아이디
                View layout = inflater.inflate(R.layout.sight_info,(ViewGroup) findViewById(R.id.layout_root));
                AlertDialog.Builder aDialog = new AlertDialog.Builder(mContext);

                // 관광지 정보 Dialog에 넣기
                aDialog.setTitle(sights.get(i).getName());
                aDialog.setView(layout); //dialog.xml 파일을 뷰로 셋팅
                TextView sightAdd = (TextView)layout.findViewById(R.id.sightAdd);
                sightAdd.setText(sights.get(i).getAddress());
                TextView sightInfo = (TextView)layout.findViewById(R.id.sightInfo);
                sightInfo.setText(sights.get(i).getInfo());

                // 이미지 넣기
                ImageView sightImage = (ImageView)layout.findViewById(R.id.sightImage);
                DisplayMetrics metrics = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) getApplicationContext()
                        .getSystemService(Context.WINDOW_SERVICE);
                windowManager.getDefaultDisplay().getMetrics(metrics);

                sightImage.setImageBitmap(sights.get(i).getImage());
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) sightImage.getLayoutParams();
                params.width = metrics.widthPixels;
                params.height = metrics.widthPixels * 319/500;
                sightImage.setLayoutParams(params);

                //팝업창 생성
                AlertDialog ad = aDialog.create();
                ad.show();
            }
        });
    }

    // 관광지 사진 가져오기 - 쓰레드
    class GetBitmap extends AsyncTask<Void, Void, Void> {
        private String category;
        private ArrayList<Sight> list;

        public GetBitmap(String category, ArrayList<Sight> list){
            this.category = category;
            this.list = list;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // TODO Auto-generated method stub
            URL bitUrl;
            String img_url;
            HttpURLConnection bitConnection = null;

            for(int i=0; i<list.size(); i++){
                if(list.get(i).getCategory().equalsIgnoreCase(category)) {
                    try {
                        img_url = list.get(i).getImgAdd();
                        bitUrl = new URL("http://116.33.179.48:50/"+img_url);
                        bitConnection = (HttpURLConnection) bitUrl.openConnection();

                        bitConnection.setDoInput(true);
                        bitConnection.setRequestMethod("POST");
                        bitConnection.setConnectTimeout(5000);
                        bitConnection.setReadTimeout(5000);

                        Bitmap bit = null;
                        int res_img = bitConnection.getResponseCode();
                        if (res_img == HttpURLConnection.HTTP_OK) {
                            InputStream is = bitConnection.getInputStream();
                            bit = BitmapFactory.decodeStream(is);
                            is.close();
                            list.get(i).setImage(bit);
                        }
                        sights.add(list.get(i));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        protected void onPostExecute(Void a) {
            adapter.notifyDataSetChanged();
        }
    }
}
//*/