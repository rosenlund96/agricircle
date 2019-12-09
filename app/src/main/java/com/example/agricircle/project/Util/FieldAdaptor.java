package com.example.agricircle.project.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FieldAdaptor extends BaseAdapter {
    private static List<Field> results;
    private LayoutInflater mInflater;


    public FieldAdaptor(Context ResultFragment, List<Field> results){
        this.results = results;
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
            convertView = mInflater.inflate(R.layout.fields_adaptor, parent, false);
            holder = new ViewHolder();

            holder.owner = (TextView) convertView.findViewById(R.id.fieldOwner);
            holder.size = (TextView) convertView.findViewById(R.id.fieldSize);
            holder.image = (ImageView) convertView.findViewById(R.id.fieldPic);



            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.owner.setText(results.get(position).getDisplay_name());
        holder.size.setText("" +Double.toString(results.get(position).getSurface()) + " HA");
        String url = "https://s3-eu-central-1.amazonaws.com/agricircle-giscockpit-api-production-eu/uploads/crop/image/121/thumb_buchweizen.JPG?X-Amz-Expires=7200&X-Amz-Date=20191209T140756Z&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIV3SDBJFY2AELLWA%2F20191209%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-SignedHeaders=host&X-Amz-Signature=5d9454c67c4a0631801aa39a250cf3d522077b728afeeef9156d7860cba1ac57";

        Picasso.get().load(url).into(holder.image);
        return convertView;
    }

    static class ViewHolder{
        TextView name, owner,size;
        ImageView image;

    }
}
