package soongsil.ourbycicle;

import java.io.Serializable;

/**
 * Created by samsung on 2016-08-25.
 */
public class Toilet implements Serializable {
    private String fName;
    private double x;
    private double y;

    public Toilet(String fName, double x, double y){
        this.fName=fName;
        this.x = x;
        this.y = y;

    }

    public String getfName(){return fName;}
    public double getX(){ return x; }
    public double getY(){ return y; }


}
