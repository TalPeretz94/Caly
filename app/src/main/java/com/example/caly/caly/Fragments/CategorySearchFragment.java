package com.example.caly.caly.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.caly.caly.Adapters.SearchBarAdapter;
import com.example.caly.caly.Business;
import com.example.caly.caly.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategorySearchFragment extends Fragment {

    FirebaseFirestore db;
    String TAG = "TAG";
    ArrayList<Business> list;
    RecyclerView recyclerView;
    String myCategory;


    public CategorySearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_category_search, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myCategory = bundle.getString("CATEGORY_STRING");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recy_view2);

        list = new ArrayList<>();

        view.setOnKeyListener((v, keyCode, event) -> {
            if( keyCode == KeyEvent.KEYCODE_BACK )
            {
                HomeUserFragment huf = new HomeUserFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl1, huf, "TAG");
                //fragmentTransaction.remove(getFragmentManager().findFragmentById(R.id.home_rel_layout));
                fragmentTransaction.commit();
                return true;
            }
            return false;
        });


        db = FirebaseFirestore.getInstance();
        db.collection("Business").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getBoolean("filledUpDetails")) {
                                    if (document.getString("category").equalsIgnoreCase(myCategory)) {
                                        Business b = new Business(document.getString("name"),
                                                document.getString("category"), document.getString("timeOfReservation"),
                                                document.getString("openingTime"), document.getString("closingTime"),
                                                document.getString("address"), document.getString("phone"));
                                        b.setmIdOfUser(document.getString("mIdOfUser"));
                                        list.add(b);
                                    }
                                }
                            }
                            SearchBarAdapter searchBarAdapter = new SearchBarAdapter(list,getView().getContext());
                            recyclerView.setAdapter(searchBarAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        //searchByCategory("Food");
    }





    public static Fragment newInstance() {
        return new CategorySearchFragment();
    }


}



