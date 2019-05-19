package com.example.gozde.musicapp.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageException;

/**
 * Created by gozde on 16.05.2019.
 */
//Sarkı Yukle Fonksiyonu


public class SarkiYukle {
    public String sarkiBaslik,sarkiUzunluk,sarkiLink,mKey; //??

    public SarkiYukle() {
    }

    public SarkiYukle(String sarkiBaslik, String sarkiUzunluk, String sarkiLink) {
        if (sarkiBaslik.trim().equals("")){
            sarkiBaslik="baslık yok";
        }
        this.sarkiBaslik = sarkiBaslik;
        this.sarkiUzunluk = sarkiUzunluk;
        this.sarkiLink = sarkiLink;
        this.mKey = mKey;

    }

    public String getSarkiBaslik() {
        return sarkiBaslik;
    }

    public void setSarkiBaslik(String sarkiBaslik) {
        this.sarkiBaslik = sarkiBaslik;
    }

    public String getSarkiUzunluk() {
        return sarkiUzunluk;
    }

    public void setSarkiUzunluk(String sarkiUzunluk) {
        this.sarkiUzunluk = sarkiUzunluk;
    }

    public String getSarkiLink() {
        return sarkiLink;
    }

    public void setSarkiLink(String sarkiLink) {
        this.sarkiLink = sarkiLink;
    }
    @Exclude
    public String getmKey() {
        return mKey;
    }
    @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
