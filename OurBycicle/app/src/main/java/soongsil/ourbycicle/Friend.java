package soongsil.ourbycicle;

import java.io.Serializable;

/**
 * Created by jihuiyeon on 2016. 10. 28..
 */
public class Friend implements Serializable{
    private static String id;
    private static String name;

    public Friend(String id, String name){
        this.id = id;
        this.name = name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getId(){
        return id;
    }

    public static String getName() {
        return name;
    }
}
