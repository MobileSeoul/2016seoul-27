package soongsil.ourbycicle;


import java.io.Serializable;

/**
 * Created by jihuiyeon on 2016. 9. 7..
 */
public class SCategory implements Serializable {
    private int image;
    private String name;

    public SCategory(int image, String name){
        this.image = image;
        this.name = name;
    }

    public void setImage(int image){
        this.image = image;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getImage(){
        return image;
    }

    public String getName(){
        return name;
    }
}