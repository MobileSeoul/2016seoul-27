package soongsil.ourbycicle;

import java.io.Serializable;

public class Comforts implements Serializable{
    String guAddress;
    String name;
    String id;
    String category;
    int flag;
    double x;
    double y;
    public Comforts(String guAddress, String name, String id, String category, double x, double y)
    {
        this.guAddress = guAddress;
        this.name = name;
        this.id = id;
        this.category = category;

        if(category.equals("공기주입기"))
            flag=3;
        else if(category.equals("자전거수리센터"))
            flag=4;
        else if(category.equals("자전거주차장"))
            flag=5;
        else
            flag = 0;
        this.x = x;
        this.y = y;

    }
    public String getName(){    return name; }
    public double getX(){   return x;   }
    public double getY(){   return y;   }
    public int getFlag(){return flag;}
}

