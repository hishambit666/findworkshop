package com.example.findworkshopuser;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirebaseViewHolder extends RecyclerView.ViewHolder {

    public TextView workshopname,address,spintext,contact, distance;
    public ImageView Image,whatsapp;

    public FirebaseViewHolder(@NonNull View itemView) {
        super(itemView);

        contact = itemView.findViewById(R.id.contact);
        workshopname = itemView.findViewById(R.id.workshopname);
        address = itemView.findViewById(R.id.address);
        spintext = itemView.findViewById(R.id.spintext);
        Image = itemView.findViewById(R.id.images);
        whatsapp = itemView.findViewById(R.id.whatsapp);
        distance = itemView.findViewById(R.id.distance);
    }
}

//class FirebasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//     ImageView imageWhatsapp;
//
//    public FirebasViewHolder(View itemView) {
//        super(itemView);
//        imageWhatsapp = (ImageView) itemView.findViewById(R.id.whatsapp);
//        imageWhatsapp.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        String url = "https://api.whatsapp.com/send?phone=+6"+model.getContact();
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setData(Uri.parse(url));
//        startActivity(i);

//    }
//}
