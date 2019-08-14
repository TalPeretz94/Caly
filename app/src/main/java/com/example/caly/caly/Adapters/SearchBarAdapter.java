package com.example.caly.caly.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.caly.caly.Business;
import com.example.caly.caly.R;
import com.example.caly.caly.User_BusinessPage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchBarAdapter extends RecyclerView.Adapter<SearchBarAdapter.MyViewHolder> {
    ArrayList<Business> list;
    FirebaseFirestore db;
    String imageUrl;
    String docId;
    Context theCon;

    public SearchBarAdapter(ArrayList<Business> list, Context theCon) {
        this.list = list;
        this.theCon = theCon;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_holder, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final Business currentItem = list.get(i);
        db = FirebaseFirestore.getInstance();

        docId = currentItem.getmIdOfUser();
        myViewHolder.bName.setText(list.get(i).getName());
        myViewHolder.bAddress.setText(list.get(i).getAddress());
        myViewHolder.bDesc.setText(list.get(i).getCategory());
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), User_BusinessPage.class);
                intent.putExtra("BUSINESS_ID", currentItem.getmIdOfUser());
                view.getContext().startActivity(intent);
            }
        });

        if(docId!=null){
        db.collection("Business").document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                imageUrl = documentSnapshot.getString("imageUrl");
                Context c = theCon;
                Glide
                        .with(theCon)
                        .load(imageUrl)
                        .into(myViewHolder.profilePhoto);


            }
        });}


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView bName, bDesc, bAddress;
        CircleImageView profilePhoto;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bName = itemView.findViewById(R.id.businessNameForSearch);
            bAddress = itemView.findViewById(R.id.businessAddressForSearch);
            bDesc = itemView.findViewById(R.id.descriptionForSearch);
            profilePhoto = itemView.findViewById(R.id.cardview_image3);


        }
    }
}
