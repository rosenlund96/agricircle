package com.example.agricircle.project.Util;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricircle.R;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Fragment.ActivityRecieptFragment;
import com.example.agricircle.project.Fragment.ActivityRun;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MapActivityAdaptor extends RecyclerView.Adapter<MapActivityAdaptor.ViewHolder> {

    private List<Activity> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FragmentActivity cFragment;

    // data is passed into the constructor
    public MapActivityAdaptor(FragmentActivity f, List<Activity> data) {
        this.mInflater = LayoutInflater.from(f);
        this.cFragment = f;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_map_adaptor, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Activity temp = mData.get(position);
        //holder.fieldname.setText(temp.getFieldname());
        holder.fieldactivity.setText(temp.getActivityType());
        if(temp.getActivityType().equals("Sowing")){
            holder.fieldactivity.setBackground(holder.fieldactivity.getContext().getDrawable(R.drawable.activity_background_sowing));
        }
        if(temp.getActivityType().equals("Spraying")){
            holder.fieldactivity.setBackground(holder.fieldactivity.getContext().getDrawable(R.drawable.activity_background_spraying));
        }
        if(temp.getActivityType().equals("Fertilization")){
            holder.fieldactivity.setBackground(holder.fieldactivity.getContext().getDrawable(R.drawable.activity_background_fertilization));
        }
        if(!temp.getUrl().equals("")){
            Picasso.get().load(temp.getUrl()).into(holder.cropPic);
        }
        else{
            holder.cropPic.setImageDrawable(holder.cropPic.getContext().getDrawable(R.drawable.questionmark));
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView fieldname, fieldactivity;
        ImageView cropPic, play;

        ViewHolder(View itemView) {
            super(itemView);
            //fieldname = itemView.findViewById(R.id.fieldNameActivity);
            fieldactivity = itemView.findViewById(R.id.typeOfActivity);
            cropPic = itemView.findViewById(R.id.pictureActivity);
            play = itemView.findViewById(R.id.activityImg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            System.out.println("Der blev trykket: " + mData.get(getAdapterPosition()).getFieldname());
            Activity temp = mData.get(getAdapterPosition());
            if(!temp.getFinished()){
                FragmentTransaction ft =  cFragment.getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ActivityRun fragment2 = new ActivityRun();

                Bundle bundle = new Bundle();
                Activity obj = temp;
                obj.setCameFromMap(true);
                bundle.putSerializable("Activity", obj);
                fragment2.setArguments(bundle);
                ft.replace(R.id.article_fragment, fragment2);
                ft.addToBackStack(null);
                ft.commit();
            }
            else{
                FragmentTransaction ft =  cFragment.getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ActivityRecieptFragment fragment2 = new ActivityRecieptFragment();

                Bundle bundle = new Bundle();
                Activity obj = temp;
                obj.setCameFromMap(true);
                bundle.putSerializable("Activity", obj);
                fragment2.setArguments(bundle);
                ft.replace(R.id.article_fragment, fragment2);
                ft.addToBackStack(null);
                ft.commit();
            }





            if (mClickListener != null){
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    Activity getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}