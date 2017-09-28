package soongsil.ourbycicle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jihuiyeon on 2016. 9. 7..
 */
public class SCategoryAdapter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater inflater;
    ArrayList<SCategory> categories = new ArrayList<SCategory>();

    public SCategoryAdapter(Context context, int layout, ArrayList<SCategory> categories){
        this.context = context;
        this.layout = layout;
        this.categories = categories;
        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public SCategory getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = inflater.inflate(layout, viewGroup, false);

        TextView text = (TextView)view.findViewById(R.id.sightCategory);
        text.setText(categories.get(i).getName());
        text.setBackgroundResource(categories.get(i).getImage());

        return view;
    }
}
