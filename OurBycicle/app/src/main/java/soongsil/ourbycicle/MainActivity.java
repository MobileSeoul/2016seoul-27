package soongsil.ourbycicle;

import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener
        , ActionBar.TabListener {

    private MainPagerAdapter pagerAdapter;
    private static ViewPager viewPager;

    private Context mContext;
    private AppCompatActivity mainActivity;
    private static ArrayList<Rental> rentalList;
    private static ArrayList<Toilet> toiletList;
    private static ArrayList<Comforts> comfortsList;//쟁
    private static ArrayList<Sight> sights;
    private static ArrayList<SCategory> categories = new ArrayList<SCategory>();
    private static ArrayList<Request> requests = new ArrayList<Request>();
    private static ArrayList<Friend> friends = new ArrayList<Friend>();

    private static ImageSpan imageSpan;//쟁
    private long backPressedTime = 0; // 수운 (한 번 더 누르면 종료?)
    private static boolean isSaved=false;
    private URL url,sightURL, comfortsURL;//쟁
    private final int RENTAL_FLAG = 1;//쟁
    private final int COMFORTS_FLAG = 2;//쟁
    private final int SIGHT_FLAG = 3;//쟁

    private Button rentalButton, facilityButton, sightButton, ridingInfButton;
    public static GoogleApiClient mGoogleApiClient;
    public static LocationRequest mLastLocation;
    public static GoogleMap mMap;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 111;
    /* 화장실 API 인증키*/
    public final static String KEY = "5a7767664c726c613130334954437a53";
    public final static String APIURL = "http://openAPI.seoul.go.kr:8088";
    String data = "";
    // ArrayList 목록 (명칭, X 좌표, Y 좌표)
 /*   private ArrayList<String> fnameList = new ArrayList<String>();
    private ArrayList<Double> xList = new ArrayList<Double>();
    private ArrayList<Double> yList = new ArrayList<Double>();*/
    private String fName;
    private Double X,Y;
    static int check=0;
    private static String id;

    private static Bundle args;
    private static boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //쟁
    //    Drawable d = getResources().getDrawable(R.drawable.tab_icon1);
   //     d.setBounds(0, 0, 40, 40);
   //    imageSpan = new ImageSpan(getBaseContext(), R.drawable.tab_icon1);

        mContext = MainActivity.this;
        mainActivity = this;
        rentalList = new ArrayList<Rental>();
        comfortsList = new ArrayList<Comforts>();
        sights = new ArrayList<Sight>();
        toiletList = new ArrayList<Toilet>();

        if(!isNew) {
            id = getIntent().getStringExtra("id");
       //     friends.add(new Friend("admin", "관리자"));
          //  friends.add(new Friend("sue", "김수운"));
          //  friends.add(new Friend("yeonjooo", "이연주"));
            friends.add(new Friend("zaeng", "전재영"));
        //    friends.add(new Friend("huiyeon", "지희연"));
        }

        //지희연
        if (!isSaved) {
            // 카테고리 추가
            categories.add(new SCategory(R.drawable.c1, "한강 자전거길"));
            categories.add(new SCategory(R.drawable.c2, "양재천 자전거길"));
            categories.add(new SCategory(R.drawable.c3, "중랑천 자전거길"));

            //쟁 - 전재영
            try {
                url = new URL("http://116.33.179.48:50/rental.json");
                sightURL = new URL("http://116.33.179.48:50/sightseeing.json");
                comfortsURL = new URL("http://116.33.179.48:50/comforts.json");//쟁
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JsonLoadingTask rentalTask = new JsonLoadingTask(url, RENTAL_FLAG);
            rentalTask.execute();
            JsonLoadingTask comfortsTask = new JsonLoadingTask(comfortsURL, COMFORTS_FLAG);
            comfortsTask.execute();
            JsonLoadingTask sightTask = new JsonLoadingTask(sightURL, SIGHT_FLAG);
            sightTask.execute();


            //김수운 - 화장실 API
            Parser();
            isSaved = true;
        }

        //이연주
        /*Google Play Services에 연결(API에 연결)*/
        if (mGoogleApiClient == null) {//GoogleAPIClient 인스턴스 생성
            mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())//?ㅅ?
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        /*위치 요청! 초기 요청이므로 다시 요청하지 않음*/
        mLastLocation = new LocationRequest();
        mLastLocation.setInterval(10000);//위치 요청 주기
        mLastLocation.setFastestInterval(10000);
        mLastLocation.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLastLocation);//엱
        //     BroadcastReceiver.PendingResult<LocationSettingsRequest> result=LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,builder.build());
        mGoogleApiClient.connect();//엱
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }

        //서버로 token 전송
        Intent intent = new Intent(this, TokenIntentService.class);
        //희연
        intent.putExtra("id", id);

        startService(intent);
        // MainActivity Pager
//        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
 //       final ActionBar actionBar = getSupportActionBar();

 //       actionBar.setDisplayHomeAsUpEnabled(false);
 //       actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
   /*     viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);/////

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                //   actionBar.setCustomView(k);
            }
        });*/
       // getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#7e8287")));

        // Shows the up carat near app icon in ActionBar
    //    getSupportActionBar().setDisplayUseLogoEnabled(false);

 /*       int[] icon = new int[3];
        icon[0] = R.drawable.tab_icon1;
        icon[1] = R.drawable.tab_icon2;
        icon[2] = R.drawable.tab_icon3;

        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(pagerAdapter.getPageTitle(i))
                    .setIcon(getResources().getDrawable(icon[i])).setTabListener(this));
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }*/
        //fragment..
    }

    // 김수운 - 액션바에 LOGOUT 버튼 추가
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflater함수를 이용해서 menu 리소스를 menu로 변환.
        // 한 줄 코드
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        } else finish();
    }

    // LOGOUT 버튼 눌렀을 때
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_button:
                setResult(RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // 이연주
    @Override
    public void onConnected(Bundle bundle) {
        Location initLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {}
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLastLocation, this);


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

  /*      int[] icon = new int[3];
        icon[0] = R.drawable.tab_icon1;
        icon[1] = R.drawable.tab_icon2;
        icon[2] = R.drawable.tab_icon3;

        for (int i = 0; i < pagerAdapter.getCount(); i++) {
          actionBar.addTab(actionBar.newTab().setText(pagerAdapter.getPageTitle(i))
                   .setIcon(getResources().getDrawable(icon[i])).setTabListener(this));
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }*/
        for(int i=0; i<pagerAdapter.getCount(); i++){
            actionBar.addTab(actionBar.newTab()
                    .setText(pagerAdapter.getPageTitle(i)).setTabListener(this));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {  }

    @Override
    public void onLocationChanged(Location location) {  }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {  }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
/*        Location initLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {}
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLastLocation, this);
    */
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    public static class MainPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager fm;

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fm.findFragmentByTag("android:switcher:"+viewPager.getId()+":"+getItemId(position));

            if(fragment != null)
                return fragment;
            args = new Bundle();
            args.putString("id", id);
            args.putSerializable("request", requests);
            args.putSerializable("friends", friends);
            switch (position){
                case 2:
                    args.putSerializable("sights", sights);
                    args.putSerializable("categories", categories);
                    return SightCategory.newInstance(args);

                case 0:
                    //args.putSerializable("rentalList", rentalList);a
                    //args.putSerializable("comfortsList", comfortsList);
                    //args.putSerializable("toiletList",toiletList);
          //          args.putSerializable("rentalList", rentalList);
          //          args.putSerializable("comfortsList", comfortsList);
          //          args.putSerializable("toiletList", toiletList);
                    LocationParecelInfo info=new LocationParecelInfo(mGoogleApiClient,mLastLocation);//엱
                    args.putParcelable("locationInfo", info);
              //      return MyInfoFragment.newInstance(args);//엱
                    return MyInfoFragment.newInstance(args);
                case 1://엱
                    args.putSerializable("rentalList", rentalList);
                    args.putSerializable("comfortsList", comfortsList);
                    args.putSerializable("toiletList", toiletList);
                    LocationParecelInfo info2=new LocationParecelInfo(mGoogleApiClient,mLastLocation);//엱
                    args.putParcelable("locationInfo", info2);
       //             Bundle bundle=new Bundle();
      //              bundle.put
      //              args.putBundle("mLastLocation",mLastLocation);
      //              args.putSerializable("mLastLocation",mLastLocation);
                    return FacilitiesFragment.newInstance(args);
/*
                    args.putSerializable("sights", sights);
                    args.putSerializable("categories", categories);
                    return SightCategory.newInstance(args);*/
                default:
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 2:
                    return "관광지";
                case 0:
                    return "내 정보";
                case 1:
                    return "편의시설";
                default:
                    return super.getPageTitle(position);
            }
        }
    }


    /*
    class AsyncTask< Params, Progress, Result >
    UI부분과 Thread처리 부분을 동시에 사용 가능한 클래스

    excute() 로 파라미터를 클래스에 전달 가능
    dolnBackground() 에서 파라미터를 받음
    onPostExecute() : dolnBackground 호출 후 호출
     */
    class JsonLoadingTask  extends AsyncTask<String, Void, JSONObject> {
        private URL url;
        private ProgressDialog pDialog;
        private int flag;


        public JsonLoadingTask(URL url, int flag) {
            this.url = url;
            this.flag = flag;//쟁
        }

        @Override
        protected void onPostExecute(JSONObject jRootObject) {
            //  super.onPostExecute(jObject);
            //백그라운드 작업 후 실행되는 함수
            String result = "";
            try {
                //전체 json 객체에서 "DATA" : { .... } 받아오기
                switch (flag) {
                    case RENTAL_FLAG://대여소
                        JSONArray jArray = jRootObject.getJSONArray("dataroot");
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jObject = jArray.getJSONObject(i);
                            //"DATA" : { ... } 에서 필요한 항목들 받아오기
                            //JSON 항목들 파싱
                            String address = jObject.optString("OldAddress");
                            String name = jObject.optString("Name");
                            String id = jObject.optString("ID");
                            String category = jObject.optString("Category");
                            String str_x = jObject.optString("X");
                            String str_y = jObject.optString("Y");
                            String str_holderNum = jObject.optString("Count");
                            String time = jObject.optString("Time");
                            String info = jObject.optString("Info");
                            String fee = jObject.optString("Fee");

                            // 타입 다른 것들 변환
                            double x = Double.parseDouble(str_x);
                            double y = Double.parseDouble(str_y);
                            int holderNum;
                            if (str_holderNum.isEmpty())
                                holderNum = -1;
                            else
                                holderNum = Integer.parseInt(str_holderNum);
                            StringTokenizer tokenizer = new StringTokenizer(address, "  ");
                            String guName = "default";
                            if (tokenizer.hasMoreTokens())
                                guName = tokenizer.nextToken();

                            //리스트에 추가
                            Rental item = new Rental(guName, name, id, category, x, y, holderNum, time, info, fee);
                            rentalList.add(item);
                        }// 파싱 for문 끝
                        check++;
                        break;
                    case COMFORTS_FLAG://편의시설
                        JSONArray jComfortsArray = jRootObject.getJSONArray("DATA");
                        for (int i = 0; i < jComfortsArray.length(); i++) {
                            JSONObject jObject = jComfortsArray.getJSONObject(i);
                            //"DATA" : { ... } 에서 필요한 항목들 받아오기
                            //JSON 항목들 파싱
                            String address = jObject.optString("Address");
                            address = address.trim();
                            String name = jObject.optString("Name");
                            String id = jObject.optString("ID");
                            String category = jObject.optString("Category");
                            String str_x = jObject.optString("X");
                            String str_y = jObject.optString("Y");

                            // 타입 다른 것들 변환
                            double x = Double.parseDouble(str_x);
                            double y = Double.parseDouble(str_y);
                            StringTokenizer tokenizer = new StringTokenizer(address, "  ");
                            String guName = address;
                            if (tokenizer.hasMoreTokens())
                                guName = tokenizer.nextToken();

                            //리스트에 추가
                            Comforts item = new Comforts(guName, name, id, category, x, y);
                            comfortsList.add(item);
                        }
                        check++;
                        /*
                        for(int i=0; i<comfortsList.size();i++)
                            adapter.addItem(comfortsList.get(i).getName(),Double.toString(comfortsList.get(i).getX()),Double.toString(comfortsList.get(i).getY()));
                        adapter.notifyDataSetChanged();*/
                        break;
                   /* case SIGHT_FLAG://test용
                        for (int i = 0; i < sightList.size(); i++)
                            adapter.addItem(sightList.get(i).getName(), sightList.get(i).getAddress(), sightList.get(i).getAddress());
                        adapter.notifyDataSetChanged();
                        break;*/
                    default:
                        break;
                } //switch

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //백그라운드 실행 함수
        @Override
        protected JSONObject doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            JSONObject jRootObject = null;
            try {
                //url 과 connect
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                int res = urlConnection.getResponseCode();
                //해당 url에서 정보 읽어오기
                if(res==HttpURLConnection.HTTP_OK) {
                    InputStream test = urlConnection.getInputStream();
                    InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(in);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    //읽어온 String을 JSONObject로
                    jRootObject = new JSONObject(result.toString());
                    url = null;
                }

                // 김수운 - 관광지 정보 읽어오는 부분
                if(flag== SIGHT_FLAG)//관광지 정보 읽어오기
                {
                    JSONArray jSightArray = jRootObject.getJSONArray("DATA");
                    for (int i = 0; i < jSightArray.length(); i++) {
                        JSONObject jObject = jSightArray.getJSONObject(i);
                        //"DATA" : { ... } 에서 필요한 항목들 받아오기
                        //JSON 항목들 파싱
                        String address = jObject.optString("address");
                        address = address.trim();
                        String name = jObject.optString("name");
                        String category = jObject.optString("category");
                        String info = jObject.optString("info");
                        String img_url = jObject.optString("image");

                        //리스트에 추가
                        sights.add(new Sight(null, img_url, name, category, address, info));
                    }
                   // check++;
                    }
                } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (NetworkOnMainThreadException e) {
                //return new JSONObject();
            } catch (Exception e) {
                e.getMessage();
                e.printStackTrace();
            }
            return jRootObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    /*화장실API*/
    public void Parser() {
        //Android 4.0 이상 부터는 네트워크를 이용할 때 반드시 Thread 사용해야 함
        new Thread(new Runnable() {
            @Override
            public void run() {
                getXmlData();
            }
        }).start();
    }

    //XmlPullParser를 이용하여 Naver 에서 제공하는 OpenAPI XML 파일 파싱하기(parsing)
    void getXmlData() {
        StringBuffer buffer = new StringBuffer();
        String queryUrl = APIURL + "/" + KEY + "/xml/SearchPublicToiletPOIService/";   //요청 URL

        try {
            for(int num = 1000; num <= 4938; num += 1000){

                String tempURL = queryUrl + String.valueOf(num-999) + "/" + String.valueOf(num);
                URL url = new URL(tempURL);         //문자열로 된 요청 url을 URL 객체로 생성.
                InputStream is = url.openStream();  //url위치로 입력스트림 연결

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new InputStreamReader(is, "UTF-8"));  //inputstream 으로부터 xml 입력받기

                String tag, fname = "", aname = "";
                xpp.next();
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    switch (eventType) {

                        case XmlPullParser.START_TAG:
                            tag = xpp.getName();    // 태그 이름 얻어오기

                            if (tag.equals("FNAME")) {
                                xpp.next();
                                fname = xpp.getText();    // 잠시 명칭 저장
                            } else if (tag.equals("ANAME")) {
                                xpp.next();
                                aname = xpp.getText();
                                // ANAME이 지하철, 공공청사가 아닌 경우 아닌 것만 추가
                                if (aname.equals("공공") || aname.equals("공중")) {
                                    fName=fname;
                                }
                            } else if (tag.equals("X_WGS84")) {
                                xpp.next();
                                if (aname.equals("공공") || aname.equals("공중")) {
                                    X=Double.parseDouble(xpp.getText());
                                }
                            } else if (tag.equals("Y_WGS84")) {
                                xpp.next();
                                if (aname.equals("공공") || aname.equals("공중")) {
                                    Y=Double.parseDouble(xpp.getText());
                                    //리스트에 추가
                                    Toilet item = new Toilet(fName,X,Y);
                                    toiletList.add(item);
                                }
                            }
                            break;
                    }
                    eventType = xpp.next();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public GoogleApiClient getGoogleClient(){
        return mGoogleApiClient;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        isNew = true;

        switch (intent.getStringExtra("function")){
            case "req_friend":
                requests.add(new Request(intent.getStringExtra("friend_id"), intent.getStringExtra("name"), false));
                args.putSerializable("request", requests);
                break;
            case "res_friend":
                friends.add(new Friend(intent.getStringExtra("friend_id"), intent.getStringExtra("name")));
                args.putSerializable("friends", friends);
                break;
            case "req_group":
                requests.add(new Request(intent.getStringExtra("friend_id"), intent.getStringExtra("name"), true));
                args.putSerializable("request", requests);
                break;
            default:
                break;
        }
    }
}
