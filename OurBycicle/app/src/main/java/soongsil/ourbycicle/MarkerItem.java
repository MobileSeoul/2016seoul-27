package soongsil.ourbycicle;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


/**
 * Created by samsung on 2016-10-21.
 */
public class MarkerItem implements ClusterItem {
    private LatLng mPosition;
    private Rental rItem;
    private Toilet tItem;
    private Comforts cItem;
    private String name;
    private float color;
    private int flag;
    public MarkerItem(Rental item){//대여소
        flag=1;
        rItem=item;
        name=item.getRentalName();
        mPosition=new LatLng(item.getY(),item.getX());
        color= BitmapDescriptorFactory.HUE_ORANGE;
    }
    public MarkerItem(Toilet item){//화장실
        flag=2;
        tItem=item;
        name=item.getfName();
        mPosition=new LatLng(item.getY(),item.getX());
        color= BitmapDescriptorFactory.HUE_BLUE;
    }
    //추가
    public MarkerItem(Comforts item){
        flag=item.getFlag();
        cItem=item;
        name=item.getName();
        mPosition=new LatLng(item.getY(),item.getX());
        color= BitmapDescriptorFactory.HUE_CYAN;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }
    public String getName(){
        return name;
    }
    public String getGuNmae(){
        return rItem.getGuName();
    }
    public String getExtraInfo(){ return rItem.getExtraInfo(); }
    public String getToiletName(){return tItem.getfName();}
    public float getColor(){return color;}
    public int getFlag(){return flag;}
}
