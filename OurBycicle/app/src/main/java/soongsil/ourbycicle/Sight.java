package soongsil.ourbycicle;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by mandu on 16-09-28.
 */
public class Sight implements Serializable {
    private Bitmap image;
    private String imgAdd;     // 서버로부터 받는 이미지 주소
    private String name;
    private String category;
    private String address;
    private String info;
    public Sight(Bitmap img, String add, String n, String c, String a, String i)
    {
        image = img;
        imgAdd = add;
        name = n;
        category = c;
        address = a;
        info = i;
    }

    public void setImage(Bitmap image){
        this.image = image;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setInfo(String info){
        this.info = info;
    }

    public Bitmap getImage(){
        return image;
    }

    public String getImgAdd(){
        return imgAdd;
    }

    public String getName(){
        return name;
    }

    public String getCategory(){
        return category;
    }

    public String getAddress() {
        return address;
    }

    public String getInfo() {
        return info;
    }
}
