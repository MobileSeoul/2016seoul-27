package soongsil.ourbycicle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jihuiyeon on 2016. 9. 18..
 */
public class SightAdapter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater inflater;
    ArrayList<Sight> sights = new ArrayList<Sight>();

    public  SightAdapter(Context context, int layout, ArrayList<Sight> sights){
        this.context = context;
        this.layout = layout;
        this.sights = sights;

        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return sights.size();
    }

    @Override
    public Object getItem(int i) {
        return sights.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = inflater.inflate(layout, viewGroup, false);

        ImageView image = (ImageView)view.findViewById(R.id.sImage);
        image.setImageBitmap(sights.get(i).getImage());
        TextView name = (TextView)view.findViewById(R.id.sName);
        name.setText(sights.get(i).getName());

        return view;
    }
}
