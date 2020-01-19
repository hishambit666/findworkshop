package com.example.findworkshopuser;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RatingViewHolder extends RecyclerView.ViewHolder {

    public TextView ratingscale,sendfeedback,userName;
    public RatingBar ratingBar;

    public RatingViewHolder(@NonNull View itemView) {
        super(itemView);

        ratingBar = itemView.findViewById(R.id.ratingbar);
        ratingscale = itemView.findViewById(R.id.ratingscale);
        sendfeedback = itemView.findViewById(R.id.feedback);
        userName = itemView.findViewById(R.id.username);
    }
}
