package com.example.imagebrower.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imagebrower.R;

import java.util.List;

public class DogAdapter extends RecyclerView.Adapter<DogAdapter.ViewHolder> {

    private List<Bitmap> bitmapList;
    private Context mContext = MyApplication.getContext();

    //将要展示的数据源传进来
    public DogAdapter(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    @NonNull
    @Override
    //加载卡片的布局，并返回并构建一个带有卡片布局的viewhold
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.dog_item, parent ,false);

        return new ViewHolder(view);
    }

    @Override
    //定位到当前屏幕位置（recyclerview）
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bitmap = bitmapList.get(position);
        //Glide.with(mContext).load(bitmap).into(holder.dogImage);
        Log.d("this", "bind view");
        holder.dogImage.setImageBitmap(bitmap);
    }

    @Override
    //返回数据源的长度
    public int getItemCount() {
        return bitmapList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
       CardView cardView;
       ImageView dogImage;


       public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            dogImage = view.findViewById(R.id.dog_image);

        }
   }


}
