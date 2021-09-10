package com.example.Objetos;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {

    View view;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        view = itemView;
    }

    public void setDetails(Context context, String image){
        ImageView mImagetv = view.findViewById(R.id.rImageView);

        Picasso.with(context).load(image).into(mImagetv);



    }
}
