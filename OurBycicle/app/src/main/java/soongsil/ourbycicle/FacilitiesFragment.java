package soongsil.ourbycicle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.internal.FusedLocationProviderResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by samsung on 2016-10-30.
 */
public class FacilitiesFragment extends Fragment implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener, Serializable {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLastLocation;
    private GoogleMap mMap;
    private double pre_lat, pre_lon;
    private Animation setAnimLeft, setAnimRight;
    private RelativeLayout setLayout;
    private Switch rental,toilet, airput, repair,park;
    private ArrayList<Rental> rentalList;
    private ArrayList<Toilet> toiletList;
    private ArrayList<Comforts> comfortsList;
    private ClusterManager<MarkerItem> mClusterManager;
    private SupportMapFragment mapFragment;
    private boolean tFlag = true;
    private Collection mMarkers;
    private Location initLocation;
    private ImageButton settingButton,closeButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getActivity().getSupportFragmentManager();
  //      Fragment fragment = (fm.findFragmentById(R.id.map_facility));

        if(!fm.isDestroyed()){
            FragmentTransaction ft = fm.beginTransaction();
        //    ft.hide(mapFragment);
            ft.remove(mapFragment);
        //     ft.commitAllowingStateLoss();
            ft.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //    return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_facilities, container, false);



        /*switch*/
        rental=(Switch)view.findViewById(R.id.rental);
        toilet = (Switch) view.findViewById(R.id.toilet);
        airput = (Switch) view.findViewById(R.id.airput);
        repair=(Switch)view.findViewById(R.id.repair);
        park=(Switch)view.findViewById(R.id.park);
        SwitchClickListener switchClickListener = new SwitchClickListener();
        rental.setOnCheckedChangeListener(switchClickListener);
        toilet.setOnCheckedChangeListener(switchClickListener);
        airput.setOnCheckedChangeListener(switchClickListener);
        airput.setOnCheckedChangeListener(switchClickListener);
        park.setOnCheckedChangeListener(switchClickListener);//아니다 요까지
         /*setting animation*/
        setAnimLeft = AnimationUtils.loadAnimation(getContext(), R.anim.setting_left);
        setAnimRight = AnimationUtils.loadAnimation(getContext(), R.anim.setting_right);
        setLayout = (RelativeLayout) view.findViewById(R.id.setLayout);

        settingButton=(ImageButton)view.findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayout.setVisibility(View.VISIBLE);
                setLayout.startAnimation(setAnimLeft);
            }
        });

        closeButton = (ImageButton) view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayout.startAnimation(setAnimRight);
                setLayout.setVisibility(View.INVISIBLE);
            }
        });

        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map_facility);
        mapFragment.getMapAsync(this);

        //엱
        Bundle bundle = getArguments();
        rentalList = (ArrayList<Rental>) bundle.getSerializable("rentalList");//서버한테 받은 대여소 위치 정보
        toiletList = (ArrayList<Toilet>) bundle.getSerializable("toiletList");
        comfortsList=(ArrayList<Comforts>)bundle.getSerializable("comfortsList");//추가
        LocationParecelInfo f = bundle.getParcelable("locationInfo");
        mLastLocation = f.getLocationRequest();
        mGoogleApiClient = f.getGoogleApiClient();
        initLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);//엱
        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {}
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLastLocation, this);

        return view;
    }




    class SwitchClickListener implements Switch.OnCheckedChangeListener{//통으로 바꾸지


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            java.util.Collection<Marker> use=mClusterManager.getMarkerCollection().getMarkers();
            ArrayList<Marker> userList=new ArrayList<Marker>(use);
            switch (buttonView.getId()){
                case R.id.rental:
                    if(isChecked) {
                        for(int i=0;i<userList.size();i++) {////////////추가
                            if( Integer.parseInt(userList.get(i).getSnippet())==1)
                                userList.get(i).setVisible(true);
                        }
                        mClusterManager.cluster();
                    }
                    else {
                        for(int i=0;i<userList.size();i++)/////////추가
                            if( Integer.parseInt(userList.get(i).getSnippet())==1)
                                userList.get(i).setVisible(false);
                        mClusterManager.cluster();
                    }
                    break;
                case R.id.toilet://여기서부터 바꿈
                    if(isChecked) {
                        for(int i=0;i<userList.size();i++) {////////////추가
                            if( Integer.parseInt(userList.get(i).getSnippet())==2)
                                userList.get(i).setVisible(true);
                        }
                        mClusterManager.cluster();
                    }
                    else {
                        for(int i=0;i<userList.size();i++)/////////추가
                            if( Integer.parseInt(userList.get(i).getSnippet())==2)
                                userList.get(i).setVisible(false);
                        mClusterManager.cluster();
                    }
                    break;
                case R.id.airput:
                    if(isChecked) {
                        for(int i=0;i<userList.size();i++) {////////////추가
                            if( Integer.parseInt(userList.get(i).getSnippet())==3)
                                userList.get(i).setVisible(true);
                        }
                        mClusterManager.cluster();
                    }
                    else {
                        for(int i=0;i<userList.size();i++)/////////추가
                            if( Integer.parseInt(userList.get(i).getSnippet())==3)
                                userList.get(i).setVisible(false);
                        mClusterManager.cluster();
                    }
                    break;
                case R.id.repair:
                    if(isChecked) {
                        for(int i=0;i<userList.size();i++) {////////////추가
                            if( Integer.parseInt(userList.get(i).getSnippet())==4)
                                userList.get(i).setVisible(true);
                        }
                        mClusterManager.cluster();
                    }
                    else {
                        for(int i=0;i<userList.size();i++)/////////추가
                            if( Integer.parseInt(userList.get(i).getSnippet())==4)
                                userList.get(i).setVisible(false);
                        mClusterManager.cluster();
                    }
                    break;
                case R.id.park:
                    if(isChecked) {
                        for(int i=0;i<userList.size();i++) {////////////추가
                            if( Integer.parseInt(userList.get(i).getSnippet())==5)
                                userList.get(i).setVisible(true);
                        }
                        mClusterManager.cluster();
                    }
                    else {
                        for(int i=0;i<userList.size();i++)/////////추가
                            if( Integer.parseInt(userList.get(i).getSnippet())==5)
                                userList.get(i).setVisible(false);
                        mClusterManager.cluster();
                    }
                    break;
                default:break;
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {}
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng rentalLocation;


   //     Bundle bundle=getActivity().getIntent().getParcelableExtra("locationInfo");
  //      LocationParecelInfo info=bundle.getParcelable("locationInfo");

        mMap=googleMap;
//        rentalList = (ArrayList<Rental>)getActivity().getIntent().getSerializableExtra("rentalList");//서버한테 받은 대여소 위치 정보
//        toiletList=(ArrayList<Toilet>)getActivity().getIntent().getSerializableExtra("toiletList");

       pre_lat=initLocation.getLatitude();
        pre_lon=initLocation.getLongitude();

        if (initLocation != null) {
            /*현재 위치 지도에 표시*/
            mMap.addMarker(new MarkerOptions().position(new LatLng(pre_lat, pre_lon)).title("init"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pre_lat, pre_lon), 13));
            /*다중 마커 등록*/
            mClusterManager=new ClusterManager<MarkerItem>(getActivity(), mMap);
            mClusterManager.setRenderer(new myRenderer(getActivity(), mMap, mClusterManager));
            mClusterManager.setAlgorithm(new GridBasedAlgorithm<MarkerItem>());

            //      mClusterManager.setAlgorithm(new myAlgorithm());
            mMap.setOnCameraChangeListener(mClusterManager);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {//부가 정보 나타낼 dialog
                @Override
                public void onInfoWindowClick(Marker marker) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("상세 정보");
                    builder.setMessage(marker.getTitle());
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            });
            mMap.setOnMarkerClickListener(mClusterManager);
            /*대여소 정보 추가*/
            for(int j=0;j<rentalList.size();j++)
                mClusterManager.addItem(new MarkerItem(rentalList.get(j)));
            for(int j=0;j<toiletList.size();j++)
                mClusterManager.addItem(new MarkerItem(toiletList.get(j)));
            for(int j=0;j<comfortsList.size();j++)//추가
                mClusterManager.addItem(new MarkerItem(comfortsList.get(j)));
        }
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(pre_lat, pre_lon))
                .radius(200)
                .strokeColor(Color.parseColor("#884169e1"))
                .fillColor(Color.parseColor("#5587cefa"))
                .strokeWidth(2f));


//http://www.java2s.com/Open-Source/Android_Free_Code/Map/Utility/com_google_maps_android_clusteringClusterManager_java.htm 갱신
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
    @Override
    public void onConnected(Bundle bundle) {
    }
    public class myRenderer extends DefaultClusterRenderer<MarkerItem> {

        public myRenderer(Context context, GoogleMap map, ClusterManager<MarkerItem> clusterManager) {
            super(context, map, clusterManager);

        }
        //http://stackoverflow.com/questions/21267775/clustering-map-markers-on-zoom-out-and-unclustering-on-zoom-in
        //http://stackoverflow.com/questions/22287207/clustermanager-repaint-markers-of-google-maps-v2-utils
        //http://googlemaps.github.io/android-maps-utils/javadoc/com/google/maps/android/clustering/ClusterManager.html
        @Override
        protected void onBeforeClusterItemRendered(MarkerItem item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);

            markerOptions.snippet(Integer.toString(item.getFlag()));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(item.getColor()));
            markerOptions.title(item.getName());

        }


    }
    public static FacilitiesFragment newInstance(Bundle args){
        FacilitiesFragment frag = new FacilitiesFragment();
        frag.setArguments(args);
        return frag;
    }

}
