package soongsil.ourbycicle;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by samsung on 2016-10-14.
 */
public class FriendsInfo {

    private String name;

    public FriendsInfo(String name){
        this.name=name;
    }

    void setName(String name){this.name=name;}

    String getName(){return name;}

}
