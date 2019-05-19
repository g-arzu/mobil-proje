package com.example.gozde.musicapp;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.gozde.musicapp.Model.SarkiYukle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.gozde.musicapp.R.layout.activity_sarkilari_goster;


public class SarkilariGosterActivity extends AppCompatActivity { //ShowSongs

    RecyclerView recyclerView;
    ProgressBar progressBar;

    List<SarkiYukle>mUpload;
    FirebaseStorage mStorage;
    DatabaseReference databaseReference;

    ValueEventListener valueEventListener;
    MediaPlayer mediaPlayer;

    SarkiAdaptoru adaptoru; //SongAdapter & adapter


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_sarkilari_goster);

        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        progressBar=(ProgressBar)findViewById(R.id.progressBarSarkilariGoster);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUpload=new ArrayList<>();

        adaptoru=new SarkiAdaptoru(SarkilariGosterActivity.this,mUpload);
        recyclerView.setAdapter(adaptoru);


        databaseReference= FirebaseDatabase.getInstance().getReference("sarkilar");        //firebase den sarkilar klasorunden
        valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUpload.clear();
                for(DataSnapshot dss:dataSnapshot.getChildren()){
                    SarkiYukle sarkiYukle=dss.getValue(SarkiYukle.class);
                    sarkiYukle.setmKey(dss.getKey());
                    mUpload.add(sarkiYukle);
                }

                adaptoru.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE); //yüklendikten sonra progressbar kayboluyor
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {  //yükleme iptal edilirse...
                Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE); //iptal durumunda progressbar kayboluyor
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

    public void playSong(List<SarkiYukle> sarkiYukleList, int adapterPosition) throws IOException {
        SarkiYukle sarkiYukle=sarkiYukleList.get(adapterPosition);

        if(mediaPlayer!=null){ //null degilse mediplayer null'a esitle
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }

        String a = sarkiYukle.getSarkiLink();
        mediaPlayer=new MediaPlayer(); //mediaplayer olusturuluyor
        mediaPlayer.setDataSource(sarkiYukle.getSarkiLink()); //linkden alınıyor sarkı
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start(); //mp baslatılıyor
            }
        });

        mediaPlayer.prepareAsync();


    }
}
