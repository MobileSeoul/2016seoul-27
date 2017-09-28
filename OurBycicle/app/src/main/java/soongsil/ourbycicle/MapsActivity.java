package soongsil.ourbycicle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.multidex.MultiDex;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener{

    public static GoogleApiClient mGoogleApiClient;
    public static LocationRequest mLastLocation;
    public static GoogleMap mMap;
    double pre_lat,pre_lon;
    double tempLat=37.506020,tempLon=127.004335;//임시 위치
    double tempLat2=37.493975,tempLon2=126.984282;
    private String clickedGuName;
    private Location initLocation;
    private Animation setAnimUp,setAnimDown;
    TextView showDistance, showTime,showSpeed;
    Button startButton, stopButton, friends_search, friends_mail,send,findButton,cancelButton;
    boolean isRunning, isStart;
    String timeString;
    long currentTime, startTime;
    Timer timerObject;
    private LinearLayout friendsListLayout,mapsLayout;
    float totalDistance=0;
    private InfoAdapter infoAdapter;
    private ListView listView;
    AlertDialog dialog;

    private static ArrayList<Friend> friends = new ArrayList<Friend>();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapsLayout=(LinearLayout)findViewById(R.id.mapsLayout);

        /*map*/
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient=MainActivity.mGoogleApiClient;
        mLastLocation=MainActivity.mLastLocation;
        mMap=MainActivity.mMap;

        /*나의 정보 관리*/
        startButton=(Button)findViewById(R.id.startButton);
        stopButton=(Button)findViewById(R.id.stopButton);
        showTime = (TextView)findViewById(R.id.showTime);           // 주행 시간
        showTime.setText("00 : 00");
        showSpeed = (TextView)findViewById(R.id.showSpeed);        // 현재 속도
        showSpeed.setText("0 km/h");
        showDistance = (TextView)findViewById(R.id.showDistance);  // 주행 거리
        showDistance.setText("0 km");

        /* 시작&일시정지 버튼 설정 */
        isStart = false;        // 주행을 시작한 상태인지
        isRunning = false;      // 현재 달리는 중인지
        startButton.setText("START");
        stopButton.setVisibility(View.INVISIBLE);

        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isStart){
                    isStart = true;    // 주행을 시작했음을 알림
                    stopButton.setVisibility(View.VISIBLE);     // 종료 버튼 활성화
                }

                if(isRunning)   // 달리는 중이면(PAUSE 버튼 눌렀을 때)
                    pauseRunning();
                else            // 달리는 중이 아니면(START 버튼 눌렀을 때)
                    startCountTime();

                isRunning = !isRunning;         // 주행->일시정지 or 일시정지->주행
            }
        });

        /* 종료 버튼 설정 */
        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = false;  // 주행 정지 알림
                isStart = false;    // 주행 종료 알림
                stopRunning();
            }
        });

        /*친구 관리 목록*/
        setAnimUp= AnimationUtils.loadAnimation(this, R.anim.setting_up);
        setAnimDown=AnimationUtils.loadAnimation(this,R.anim.setting_down);
        friendsListLayout=(LinearLayout)findViewById(R.id.friendsListLayout);
        friends_search=(Button)findViewById(R.id.friend_search);
        friends_mail=(Button)findViewById(R.id.friends_send);

        ButtonClickListener buttonClickListener=new ButtonClickListener();
        friends_search.setOnClickListener(buttonClickListener);
        friends_mail.setOnClickListener(buttonClickListener);
/*
        final ArrayList<FriendsInfo> friendsInfos=new ArrayList<>();
        friendsInfos.add(new FriendsInfo( "이연주"));
        friendsInfos.add(new FriendsInfo("쟁"));
        friendsInfos.add(new FriendsInfo("김수운"));
        friendsInfos.add(new FriendsInfo("지희연"));
        friendsInfos.add(new FriendsInfo("양승민"));
        friendsInfos.add(new FriendsInfo("김수동"));
        friendsInfos.add(new FriendsInfo("유재우"));
        friendsInfos.add(new FriendsInfo("시우민"));
        friendsInfos.add(new FriendsInfo("김종대"));
        friendsInfos.add(new FriendsInfo("박보검"));
        friendsInfos.add(new FriendsInfo("이상윤"));
        friendsInfos.add(new FriendsInfo("이태민"));
        friendsInfos.add(new FriendsInfo("김민석"));
*/
        listView=(ListView)findViewById(R.id.friends_listview);
        infoAdapter=new InfoAdapter(this,R.layout.friends_info, friends);
        listView.setAdapter(infoAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {///listView 안의 content 이벤트
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        send=(Button)findViewById(R.id.send);
        send.setOnClickListener(buttonClickListener);
        ImageView arrowUp=(ImageView)findViewById(R.id.arrowUp);
        arrowUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsListLayout.setVisibility(View.VISIBLE);
                friendsListLayout.startAnimation(setAnimUp);
                mapsLayout.setVisibility(View.GONE);
            }
        });
        ImageView arrowDown=(ImageView)findViewById(R.id.arrowDown);
        arrowDown.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {

                //           friendsListLayout.startAnimation(setAnimDown);
                friendsListLayout.setVisibility(View.INVISIBLE);
                mapsLayout.setVisibility(View.VISIBLE);
                mapsLayout.setClickable(true);
                infoAdapter.setVisibleCheckBox(false);
                infoAdapter.notifyDataSetChanged();
                send.setVisibility(View.INVISIBLE);
            }});

        /*친구 단체 메세지 전송 alert dialog*/
        LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View sendView=inflater.inflate(R.layout.send_groupmessage,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(MapsActivity.this);
        builder.setView(sendView);
        findButton=(Button)sendView.findViewById(R.id.findButton);//'찾기' 버튼
        cancelButton=(Button)sendView.findViewById(R.id.cancelButton);//'취소' 버튼
        findButton.setOnClickListener(buttonClickListener);
        cancelButton.setOnClickListener(buttonClickListener);
        dialog = builder.create();


    }
    /* startButton */
    // 메인 쓰레드 생성
    Runnable timeThread = new Runnable() {
        public void run()
        {
            showTime.setText(timeString);
        }
    };

    // 쓰레드 동작 설정
    class UpdateTimeTask extends TimerTask
    {
        public void run()
        {
            currentTime = System.currentTimeMillis();
            long millis = currentTime - startTime;
            int hours = (int) (millis / 3600000);
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            millis = (millis / 10) % 100;
            timeString = String.format("%02d : %02d" , minutes, seconds);
            showTime.post(timeThread);   // 메인 쓰레드에 전달, 해당 문자열 출력
        }
    };
    void startCountTime()//시간 재기
    {
        startButton.setText("PAUSE");

        if(timerObject == null) {
            currentTime = 0;
            startTime = 0;
        }
        startTime = System.currentTimeMillis() - (currentTime - startTime);
        timerObject = new Timer();
        timerObject.schedule(new UpdateTimeTask(), 100, 10);
    }

    void pauseRunning()
    {
        startButton.setText("START");
        timerObject.cancel();
        timerObject.purge();
    }

    void stopRunning()
    {
        if (timerObject != null) {
            timerObject.cancel();
            timerObject.purge();
            timerObject = null;
        }

        startButton.setText("START");
        stopButton.setVisibility(View.INVISIBLE);   // STOP 버튼 비활성화
        showTime.setText("00 : 00");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getBaseContext(), "onMap", Toast.LENGTH_SHORT).show();

        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {}
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLastLocation, this);
        mMap=googleMap;
        initLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);//디바이스의 마지막 위치 정보 가져옴
        if(initLocation==null)
            return;
        pre_lat=initLocation.getLatitude();
        pre_lon=initLocation.getLongitude();

        if (initLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pre_lat, pre_lon), 15));
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(pre_lat, pre_lon)).title("init"));
        }

    }

    public void onLocationChanged(Location location) {
        Toast.makeText(getBaseContext(), "<대여>locationChanged", Toast.LENGTH_SHORT).show();
        initLocation=location;
        double lat=location.getLatitude();
        double lon=location.getLongitude();

        /*위치 변화가 있으면 지도 업데이트*/
        if((pre_lat!=lat) || (pre_lon!=lon)) {
            Location preLocation=new Location("pre");
            Location newLocation=new Location("new");
            preLocation.setLatitude(pre_lat);
            preLocation.setLongitude(pre_lon);
            newLocation.setLatitude(lat);
            newLocation.setLongitude(lon);
            totalDistance+=newLocation.distanceTo(preLocation)/1000f;

            //Toast.makeText(getApplicationContext(),"<대여> "+Float.toString(totalDistance),Toast.LENGTH_SHORT).show();
            showDistance.setText(Float.toString(totalDistance)+" km");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16));
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("hi"));

            pre_lat=lat;pre_lon=lon;
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}
    public void onConnected(Bundle bundle) {

    }


    public void onConnectionFailed(ConnectionResult connectionResult) {}

    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "연결이 끊겼습니다", Toast.LENGTH_SHORT).show();
    }


    class ButtonClickListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.friend_search:
                    dialog.show();
                    break;
                case R.id.friends_send:
                    send.setVisibility(View.VISIBLE);
                    infoAdapter.setVisibleCheckBox(true);
                    infoAdapter.notifyDataSetChanged();
                    break;
                case R.id.send://보내기 event 처리
                    infoAdapter.setVisibleCheckBox(false);
                    infoAdapter.notifyDataSetChanged();
                    send.setVisibility(View.INVISIBLE);
                    break;
                case R.id.findButton:
                    Toast.makeText(getApplicationContext(),"찾기 버튼",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.cancelButton:
                    dialog.dismiss();
                    break;
                default: break;
            }
        }
    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

}
