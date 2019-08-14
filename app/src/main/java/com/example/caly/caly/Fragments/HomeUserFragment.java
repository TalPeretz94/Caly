package com.example.caly.caly.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.caly.caly.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeUserFragment extends Fragment {


    CardView category1,category2,category3,category4,category5,category6;


    public HomeUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        category1 = view.findViewById(R.id.category1);
        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCategoryList(0);
            }
        });
        category2 = view.findViewById(R.id.category2);
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCategoryList(1);
            }
        });
        category3 = view.findViewById(R.id.category3);
        category3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCategoryList(2);
            }
        });
        category4 = view.findViewById(R.id.category4);
        category4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCategoryList(3);
            }
        });
        category5 = view.findViewById(R.id.category5);
        category5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCategoryList(4);
            }
        });

        category6 = view.findViewById(R.id.category6);
        category6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCategoryList(5);
            }
        });


    }

    public static Fragment newInstance() {
        return new HomeUserFragment();
    }


    public void moveToCategoryList(int i){
        String[] categories =  getResources().getStringArray(R.array.category);

        CategorySearchFragment sbf = new CategorySearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("CATEGORY_STRING", categories[i]);
        sbf.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.ll, sbf, "TAG");
        //fragmentTransaction.remove(getFragmentManager().findFragmentById(R.id.home_rel_layout));
        fragmentTransaction.commit();
    }
}
