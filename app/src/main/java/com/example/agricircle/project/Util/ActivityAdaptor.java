package com.example.agricircle.project.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ActivityAdaptor extends BaseAdapter {
    private static List<Activity> results;
    private static List<Crop> crops;
    private LayoutInflater mInflater;


    public ActivityAdaptor(Context ResultFragment, List<Activity> results){
        this.results = results;
        this.crops = MainScreenActivity.getInstance().controller.getCropsList();
        mInflater = LayoutInflater.from(ResultFragment);
    }






    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return results.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return results.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.workspace_adaptor, parent, false);
            holder = new ViewHolder();
            holder.fieldname = (TextView) convertView.findViewById(R.id.fieldname);
            holder.fieldactivity = (TextView) convertView.findViewById(R.id.activitytype);
            holder.cropPic = (ImageView) convertView.findViewById(R.id.picture);
            holder.play = (ImageView) convertView.findViewById(R.id.startactivity);



            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.fieldname.setText(results.get(position).getFieldname());
        holder.fieldactivity.setText(results.get(position).getActivityType());
        if(results.get(position).getActivityType().equals("Sowing")){
            holder.fieldactivity.setBackground(convertView.getContext().getDrawable(R.drawable.activity_background_sowing));
        }
        if(results.get(position).getActivityType().equals("Spraying")){
            holder.fieldactivity.setBackground(convertView.getContext().getDrawable(R.drawable.activity_background_spraying));
        }
        if(results.get(position).getActivityType().equals("Fertilization")){
            holder.fieldactivity.setBackground(convertView.getContext().getDrawable(R.drawable.activity_background_fertilization));
        }

        if(!results.get(position).getUrl().equals("")){
            Picasso.get().load(results.get(position).getUrl()).into(holder.cropPic);
        }
        else{
            holder.cropPic.setImageDrawable(convertView.getContext().getDrawable(R.drawable.questionmark));
        }







        return convertView;
    }

    static class ViewHolder{
        TextView fieldname, fieldactivity;
        ImageView cropPic, play;


    }

}
