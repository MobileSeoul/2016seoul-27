package soongsil.ourbycicle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mandu on 16-10-28.
 */
public class MyInfoFragment extends Fragment implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener {

    //ArrayList<Comforts> comfortsList = new ArrayList<Comforts>();
    // ArrayList<Toilet> toiletList = new ArrayList<Toilet>();
    // ArrayList<Rental> rentalList = new ArrayList<Rental>();

    private Button rentalButton, facilityButton, sightButton, ridingInfButton;

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
    private Double X, Y;

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
    private LinearLayout friendsListLayout,mapsLayout, noticeLayout;
    float totalDistance=0;
    private InfoAdapter infoAdapter;
    private ListView listView, noticeList;
    AlertDialog dialog;
    private NoticeAdapter noticeAdapter;
    private SupportMapFragment mapFragment;

    private final String SERVER_URL = "http://116.33.179.48:50/";
    private String id;
    private EditText editText;
    String result;

    private static ArrayList<Request> requests = new ArrayList<>();
    private static ArrayList<Friend> friends = new ArrayList<>();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);

        //  comfortsList = (ArrayList<Comforts>) bundle.getSerializable("comfortsList");
        // toiletList = (ArrayList<Toilet>) bundle.getSerializable("toiletList");
        // rentalList = (ArrayList<Rental>) bundle.getSerializable("rentalList");

        Bundle bundle = getArguments();
        id = bundle.getString("id");
        requests = (ArrayList<Request>)bundle.getSerializable("request");
        friends = (ArrayList<Friend>)bundle.getSerializable("friends");

        //엱 이 위에 삭제
        mapsLayout = (LinearLayout) view.findViewById(R.id.mapsLayout);


        /*map*/
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*
        mGoogleApiClient = MainActivity.mGoogleApiClient;
        mLastLocation = MainActivity.mLastLocation;
        mMap = MainActivity.mMap;
*/
        /*나의 정보 관리*/
        startButton = (Button) view.findViewById(R.id.startButton);
        stopButton = (Button) view.findViewById(R.id.stopButton);
        showTime = (TextView) view.findViewById(R.id.showTime);           // 주행 시간
        showTime.setText("00 : 00");
        showSpeed = (TextView) view.findViewById(R.id.showSpeed);        // 현재 속도
        showSpeed.setText("0 km/h");
        showDistance = (TextView) view.findViewById(R.id.showDistance);  // 주행 거리
        showDistance.setText("0 km");

        /* 시작&일시정지 버튼 설정 */
        isStart = false;        // 주행을 시작한 상태인지
        isRunning = false;      // 현재 달리는 중인지
        startButton.setText("START");
        stopButton.setVisibility(View.INVISIBLE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStart) {
                    isStart = true;    // 주행을 시작했음을 알림
                    stopButton.setVisibility(View.VISIBLE);     // 종료 버튼 활성화
                }

                if (isRunning)   // 달리는 중이면(PAUSE 버튼 눌렀을 때)
                    pauseRunning();
                else            // 달리는 중이 아니면(START 버튼 눌렀을 때)
                    startCountTime();

                isRunning = !isRunning;         // 주행->일시정지 or 일시정지->주행
            }
        });

        /* 종료 버튼 설정 */
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = false;  // 주행 정지 알림
                isStart = false;    // 주행 종료 알림
                stopRunning();
            }
        });

        /*친구 관리 목록*/
        setAnimUp = AnimationUtils.loadAnimation(getContext(), R.anim.setting_up);
        setAnimDown = AnimationUtils.loadAnimation(getContext(), R.anim.setting_down);
        friendsListLayout = (LinearLayout) view.findViewById(R.id.friendsListLayout);
        friends_search = (Button) view.findViewById(R.id.friend_search);
        friends_mail = (Button) view.findViewById(R.id.friends_send);

        ButtonClickListener buttonClickListener = new ButtonClickListener();
        friends_search.setOnClickListener(buttonClickListener);
        friends_mail.setOnClickListener(buttonClickListener);
/*
        final ArrayList<FriendsInfo> friendsInfos = new ArrayList<>();
        friendsInfos.add(new FriendsInfo("이연주"));
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
        listView = (ListView) view.findViewById(R.id.friends_listview);
        infoAdapter = new InfoAdapter(getContext(), R.layout.friends_info, friends);
        listView.setAdapter(infoAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {///listView 안의 content 이벤트
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        send = (Button) view.findViewById(R.id.send);
        send.setOnClickListener(buttonClickListener);
        ImageView arrowUp = (ImageView) view.findViewById(R.id.arrowUp);
        arrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsListLayout.setVisibility(View.VISIBLE);
                friendsListLayout.startAnimation(setAnimUp);
                mapsLayout.setVisibility(View.GONE);
            }
        });
        ImageView arrowDown = (ImageView) view.findViewById(R.id.arrowDown);
        arrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsListLayout.startAnimation(setAnimDown);
                friendsListLayout.setVisibility(View.GONE);
                mapsLayout.setVisibility(View.VISIBLE);
                mapsLayout.setClickable(true);
                infoAdapter.setVisibleCheckBox(false);
                infoAdapter.notifyDataSetChanged();
                send.setVisibility(View.INVISIBLE);
            }
        });

        //친구 or 그룹 신청

        ImageView request = (ImageView)view.findViewById(R.id.request);

        noticeLayout = (LinearLayout) view.findViewById(R.id.notice_layout);
        noticeList = (ListView) view.findViewById(R.id.notice);
        noticeAdapter = new NoticeAdapter(getContext(), R.layout.my_notice, requests, friends, id);
        noticeList.setAdapter(noticeAdapter);

        ImageView noticeDown = (ImageView)view.findViewById(R.id.notice_down);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeAdapter.setVisibilityBtn(true);
                noticeLayout.setClickable(true);
                noticeLayout.setVisibility(View.VISIBLE);
                noticeLayout.startAnimation(setAnimUp);
                mapsLayout.setVisibility(View.GONE);
            }
        });
        noticeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeLayout.startAnimation(setAnimDown);
                noticeLayout.setVisibility(View.INVISIBLE);
                mapsLayout.setVisibility(View.VISIBLE);
                mapsLayout.setClickable(true);
                friends = noticeAdapter.getFriends();
            }
        });

        /*친구 단체 메세지 전송 alert dialog*/

        LayoutInflater friend_inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sendView = inflater.inflate(R.layout.send_groupmessage, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(sendView);
        findButton = (Button) sendView.findViewById(R.id.findButton);//'친구신청' 버튼
        cancelButton = (Button) sendView.findViewById(R.id.cancelButton);//'취소' 버튼
        findButton.setOnClickListener(buttonClickListener);
        cancelButton.setOnClickListener(buttonClickListener);
        editText = (EditText) sendView.findViewById(R.id.editText);
        dialog = builder.create();

        //엱
     //   Bundle bundle = getArguments();
        LocationParecelInfo f = bundle.getParcelable("locationInfo");
        mLastLocation = f.getLocationRequest();
        mGoogleApiClient = f.getGoogleApiClient();
        //    mMap=f.getGoogleMap();
        initLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);//엱
        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {}
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLastLocation, this);


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        //      Fragment fragment = (fm.findFragmentById(R.id.map_facility));

        if(!fm.isDestroyed()) {
            FragmentTransaction ft = fm.beginTransaction();
            //   ft.hide(mapFragment);
            ft.remove(mapFragment);
            //    ft.commitAllowingStateLoss();
            ft.commit();
        }
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
    class UpdateTimeTask extends TimerTask {
        public void run() {
            currentTime = System.currentTimeMillis();
            long millis = currentTime - startTime;
            int hours = (int) (millis / 3600000);
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            millis = (millis / 10) % 100;
            timeString = String.format("%02d : %02d", minutes, seconds);
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
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainActivity.getMenuInflater().inflate(R.menu.mapsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //삭제
        mMap=googleMap;
        pre_lat=initLocation.getLatitude();
        pre_lon=initLocation.getLongitude();

        if (initLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pre_lat, pre_lon), 15));
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(pre_lat, pre_lon)).title("init"));
        }

    }

    public void onLocationChanged(Location location) {
        initLocation=location;
        double lat=location.getLatitude();
        double lon=location.getLongitude();

        /*위치 변화가 있으면 지도 업데이트*/
        //  if(mMap!=null) {
        if ((pre_lat != lat) || (pre_lon != lon)) {
            Location preLocation = new Location("pre");
            Location newLocation = new Location("new");
            preLocation.setLatitude(pre_lat);
            preLocation.setLongitude(pre_lon);
            newLocation.setLatitude(lat);
            newLocation.setLongitude(lon);

            float currentDistance=newLocation.distanceTo(preLocation)/1000f;
            String s=String.format("%.2f",currentDistance);
            if(currentDistance>0.01) {
                totalDistance += Float.parseFloat(s);

                showDistance.setText(Float.toString(totalDistance) + " km");

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16));//mMap
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("hi"));

                pre_lat = lat;
                pre_lon = lon;}
        }

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}

    @Override
    public void onConnected(Bundle bundle) {

    }

    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(), "연결이 끊겼습니다", Toast.LENGTH_SHORT).show();
    }


    class ButtonClickListener implements View.OnClickListener {
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
                {
                    final ArrayList<Friend> checkedList = infoAdapter.getCheckedList();
                    //    String result;
                    Thread thread = new Thread() {
                        InputStreamReader in = null;
                        OutputStream out = null;
                        //   String result;

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

                                String msg = "key=REQ_GROUP&myID=" + id;
                                for(int j=0;j<checkedList.size();j++)
                                    msg +="&friendID[]="+checkedList.get(j).getId();

                                writer.write(msg);
                                writer.flush();
                                writer.close();
                                out.close();

                                connection.connect();

                                int responseCode = connection.getResponseCode();
                                if (responseCode < 200 || responseCode >= 300)
                                {
                                    Log.e("fail", "----------connection fail-----------");
                                    return;
                                } else

                                {
                                    //    String result = null;
                                    in = new InputStreamReader(connection.getInputStream(), "UTF-8");
                                    BufferedReader reader = new BufferedReader(in);
                                    result = reader.readLine();
                                    Log.e("request group", "----------" + result + "----------");

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
                    if (result.equalsIgnoreCase("ok")) {
                        Toast.makeText(getContext(), "그룹신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "잘못된 ID입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                    infoAdapter.setVisibleCheckBox(false);
                    infoAdapter.notifyDataSetChanged();
                    send.setVisibility(View.INVISIBLE);
                    break;
                case R.id.findButton:
               //     Toast.makeText(getContext(),"찾기 버튼",Toast.LENGTH_SHORT).show();

                {
                //    String result;
                    Thread thread = new Thread() {
                        InputStreamReader in = null;
                        OutputStream out = null;
                     //   String result;

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
                                writer.write("key=REQ_FRIEND&myID=" + id + "&friendID=" + editText.getText());
                                writer.flush();
                                writer.close();
                                out.close();

                                connection.connect();

                                int responseCode = connection.getResponseCode();
                                if (responseCode < 200 || responseCode >= 300)
                                {
                                    Log.e("fail", "----------connection fail-----------");
                                    return;
                                } else

                                {
                                //    String result = null;
                                    in = new InputStreamReader(connection.getInputStream(), "UTF-8");
                                    BufferedReader reader = new BufferedReader(in);
                                    result = reader.readLine();
                                    Log.e("request friend", "----------" + result + "----------");

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
                    if (result.equalsIgnoreCase("ok")) {
                        Toast.makeText(getContext(), "친구신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "잘못된 ID입니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                    break;
                case R.id.cancelButton:
                    dialog.dismiss();
                    break;
                default: break;
            }
        }
    }
    /*
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(mContext);
    }
*/
/*
        rentalButton = (Button) view.findViewById(R.id.rental);
        facilityButton = (Button) view.findViewById(R.id.facility);
        sightButton = (Button) view.findViewById(R.id.sight);
        ridingInfButton = (Button) view.findViewById(R.id.ridingInfo);

        ButtonClickListener buttonClickListener = new ButtonClickListener();
        rentalButton.setOnClickListener(buttonClickListener);
        facilityButton.setOnClickListener(buttonClickListener);
        sightButton.setOnClickListener(buttonClickListener);
        ridingInfButton.setOnClickListener(buttonClickListener);
*/
        /*permission 등록*/




    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "연결이 끊겼습니다.", Toast.LENGTH_SHORT).show();
    }

    public void onStart() {
        //       mGoogleApiClient.connect();//googleApiClient build 후 connect
        super.onStart();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getContext(), "연결 종료", Toast.LENGTH_SHORT).show();
        //업데이트 종료
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();

        super.onDestroy();
    }


/*
    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

    }
*/

    public static MyInfoFragment newInstance(Bundle args){
        MyInfoFragment frag = new MyInfoFragment();
        frag.setArguments(args);
        return frag;
    }
}
