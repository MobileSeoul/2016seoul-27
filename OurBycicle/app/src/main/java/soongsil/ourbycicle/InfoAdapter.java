package soongsil.ourbycicle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class InfoAdapter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater inflater;
    ArrayList<Friend> friends = new ArrayList<Friend>();
    CheckBox box;
    boolean check=false;//Invisible
    private static ArrayList<Friend> checkedList = new ArrayList<Friend>();

    public InfoAdapter(Context context, int layout, ArrayList<Friend> friends){
        this.context = context;
        this.layout = layout;
        this.friends = friends;

        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int i) {
        return friends.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = inflater.inflate(layout, viewGroup, false);

        TextView name = (TextView)view.findViewById(R.id.fName);
        box=(CheckBox)view.findViewById(R.id.checkBox);
        box.setChecked(false);
        if(check)
            box.setVisibility(View.VISIBLE);
        else
            box.setVisibility(View.INVISIBLE);
        name.setText(friends.get(i).getName());

        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(box.isChecked())
                    checkedList.add(friends.get(i));
                else {
                    for(int j=0; j<checkedList.size(); j++){
                        if(checkedList.get(j).getId().equalsIgnoreCase(friends.get(i).getId()))
                            checkedList.remove(j);
                    }
                }
            }
        });

        return view;
    }

    public void setVisibleCheckBox(boolean c){
        check=c;
    }

    public ArrayList<Friend> getCheckedList(){
        return checkedList;
    }
}
