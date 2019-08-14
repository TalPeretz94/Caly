package com.example.caly.caly.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.caly.caly.Adapters.SearchBarAdapter;
import com.example.caly.caly.Business;
import com.example.caly.caly.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchBarFragment extends Fragment {

    FirebaseFirestore db;
    String TAG = "TAG";
    ArrayList<Business> list;
    RecyclerView recyclerView;
    SearchView searchView;
    Context myContext;

    public SearchBarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_bar, container, false);

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recy_view);
        searchView = view.findViewById(R.id.sea_view);
        list = new ArrayList<>();


        db = FirebaseFirestore.getInstance();
        db.collection("Business").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getBoolean("filledUpDetails")) {
                                    Business b = new Business(document.getString("name"),
                                            document.getString("category"), document.getString("timeOfReservation"),
                                            document.getString("openingTime"), document.getString("closingTime"),
                                            document.getString("address"), document.getString("phone"));
                                    b.setmIdOfUser(document.getString("mIdOfUser"));
                                    list.add(b);
                                }
                            }
                            myContext=view.getContext();
                            SearchBarAdapter searchBarAdapter = new SearchBarAdapter(list,view.getContext());
                            recyclerView.setAdapter(searchBarAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        if (searchView!=null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if(s!=null){
                        search(s);
                    }

                    return true;
                }
            });
        }
    }

    public void search(String str){
        ArrayList<Business> myList = new ArrayList<>();
        for(Business object : list){
            if (object!=null){
                if(object.getName().toLowerCase().contains(str.toLowerCase())){
                    myList.add(object);
                }
            }

        }
        SearchBarAdapter searchBarAdapter = new SearchBarAdapter(myList,myContext);
        recyclerView.setAdapter(searchBarAdapter);
    }

    public void searchByCategory(String str){
        ArrayList<Business> myList = new ArrayList<>();
        for(Business object : list){
            if (object!=null){
                if(object.getCategory().toLowerCase().contains(str.toLowerCase())){
                    myList.add(object);
                }
            }

        }
        SearchBarAdapter searchBarAdapter = new SearchBarAdapter(myList,myContext);
        recyclerView.setAdapter(searchBarAdapter);
    }

    public static Fragment newInstance() {
        return new SearchBarFragment();
    }


}
