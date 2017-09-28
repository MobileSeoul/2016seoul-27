package soongsil.ourbycicle;

// 김수운 - 관광지 카테고리

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

///*
public class SightCategory extends Fragment {
    ArrayList<Sight> sights = new ArrayList<Sight>();
    ArrayList<SCategory> categories = new ArrayList<SCategory>();
    private Fragment fragment;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sight_category, container, false);
       fragment=new SightCategory();
        Bundle bundle = getArguments();
        sights = (ArrayList<Sight>)bundle.getSerializable("sights");
        categories = (ArrayList<SCategory>)bundle.getSerializable("categories");

        ListView listView;
        SCategoryAdapter adapter;
        listView = (ListView)view.findViewById(R.id.SCategoryList);

        adapter = new SCategoryAdapter(getActivity(), R.layout.my_sight_category, categories);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SightList.class);
                // 카테고리 이름 넘기기
                intent.putExtra("category", categories.get(i).getName());
                // 관광지 정보 넘기기
                intent.putExtra("sights", sights);
                startActivity(intent);
            }
        });
        return view;
    }
    public static SightCategory newInstance(Bundle args){
        SightCategory frag = new SightCategory();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if(!fm.isDestroyed()) {
            FragmentTransaction ft = fm.beginTransaction();
            //    ft.hide(fragment);
            ft.remove(fragment);
            ft.commit();
        }
    }

}
//*/
