package com.example.gozde.musicapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.gozde.musicapp.Model.SarkiYukle;

import java.io.IOException;
import java.util.List;

/**
 * Created by gozde on 17.05.2019.
 */

public class SarkiAdaptoru extends RecyclerView.Adapter<SarkiAdaptoru.SarkiAdaptoruViewHolder>{


    Context context;
    List<SarkiYukle> sarkiYukleList; //sarkı listesi

    public SarkiAdaptoru(Context context,List<SarkiYukle> sarkiYukleList){
        this.context=context;
        this.sarkiYukleList=sarkiYukleList;
    }

    @NonNull
    @Override
    public SarkiAdaptoruViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.sarki_item,viewGroup,false); //sarki item layout'una ulasıyor
        return new SarkiAdaptoruViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SarkiAdaptoruViewHolder holder, int i) {
        SarkiYukle sarkiYukle=sarkiYukleList.get(i); //SarkiYukle metoduna ulasıyor
        holder.baslıkTxt.setText(sarkiYukle.getSarkiBaslik()); //uzunluk bilgisi alınıyor
        holder.uzunlukTxt.setText(sarkiYukle.getSarkiUzunluk());//baslık bilgisi alınıyor

    }

    @Override
    public int getItemCount() {
        return sarkiYukleList.size();
    }

    public class SarkiAdaptoruViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView baslıkTxt,uzunlukTxt; //titleTxt, durationTxt

        public SarkiAdaptoruViewHolder(@NonNull View itemView){
            super(itemView);
            baslıkTxt=(TextView)itemView.findViewById(R.id.sarki_baslik);
            uzunlukTxt=(TextView)itemView.findViewById(R.id.sarki_uzunluk);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //secili sarkıyı calmak için fonksiyon

            try {
                ((SarkilariGosterActivity)context).playSong(sarkiYukleList,getAdapterPosition());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
