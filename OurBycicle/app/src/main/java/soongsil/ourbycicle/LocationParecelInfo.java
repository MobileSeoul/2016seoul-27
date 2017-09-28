package soongsil.ourbycicle;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by samsung on 2016-10-30.
 */
public class LocationParecelInfo implements Parcelable{
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLastLocation;
//    private GoogleMap mMap;
    public LocationParecelInfo(){

    }
    public LocationParecelInfo(Parcel in){

    }
    public LocationParecelInfo(GoogleApiClient mGoogleApiClient,LocationRequest mLastLocation){
        this.mGoogleApiClient=mGoogleApiClient;
        this.mLastLocation=mLastLocation;
     //   this.mMap=mMap;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(flags!=0){
        dest.writeValue(mGoogleApiClient);
        dest.writeValue(mLastLocation);}
    //    dest.writeValue(mMap);

    }
    public GoogleApiClient getGoogleApiClient(){return mGoogleApiClient;}
    public LocationRequest getLocationRequest(){return mLastLocation;}
 //   public GoogleMap getGoogleMap(){return mMap;}
    private void readFromParcel(Parcel in){

        mGoogleApiClient = (GoogleApiClient)in.readValue(GoogleApiClient.class.getClassLoader());
        mLastLocation = (LocationRequest)in.readValue(LocationRequest.class.getClassLoader());
  //      mMap=(GoogleMap)in.readValue(GoogleMap.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR=new Parcelable.Creator(){
        public LocationParecelInfo createFromParcel(Parcel in) {
            return new LocationParecelInfo(in);
        }

        public  LocationParecelInfo[] newArray(int size) {
            return new  LocationParecelInfo[size];
        }


    };
}
