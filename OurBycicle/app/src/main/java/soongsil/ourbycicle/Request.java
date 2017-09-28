package soongsil.ourbycicle;

import java.io.Serializable;

/**
 * Created by jihuiyeon on 2016. 10. 29..
 */
public class Request implements Serializable{
    private String id;
    private String name;
    private boolean isGroup;

    public Request(String id, String name, boolean isGroup){
        this.id = id;
        this.name = name;
        this.isGroup = isGroup;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isGroup() {
        return isGroup;
    }
}
