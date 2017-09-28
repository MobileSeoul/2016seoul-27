package soongsil.ourbycicle;

import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

/**
 * Created by samsung on 2016-08-25.
 */
public class Rental implements Serializable {

    private String guName;// 구 이름
    private String rentalName;//대여소 이름 ex) 합정역 7번출구 앞
    private String id;//식별자..어따쓰지ㅎ
    private double x;// ㅎ
    private double y;// ㅋ
    private int holderNum;// 거치대 수  ( 정보가 없으면 -1 임 )
    private String extraInfo;// 상세정보

    public Rental(String guName,String rentalName,String id,String category,double x, double y, int holderNum,String time,String info, String fee){

        this.guName=guName;//구
        this.rentalName=rentalName;//대여소 이름(주소)
        this.id=id;
        this.x = x;
        this.y = y;
        this.holderNum=holderNum; //거치대 수

        extraInfo = rentalName+"\n\n"+category + "\n"+info+"\n이용가능시간: "+time+"\n\n이용요금:"+fee;

    }

    public String getGuName(){ return guName; }
    public String getRentalName(){ return rentalName; }
    public double getX(){ return x; }
    public double getY(){ return y; }
    public int getHolderNum(){return holderNum;}
    public String getExtraInfo(){return extraInfo;}

}
