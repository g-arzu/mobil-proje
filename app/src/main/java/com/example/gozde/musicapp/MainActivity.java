package com.example.gozde.musicapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gozde.musicapp.Model.SarkiYukle;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.gozde.musicapp.R.layout.activity_main;


public class MainActivity extends AppCompatActivity {
//gerekli nesneler tanımlandı
    AppCompatEditText sarkiBaslik1; //editTextTitle
    TextView sarkiSecilmeditxt1; //textViewImage
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference mStorageRef;
    StorageTask mUploadTask;
    DatabaseReference referenceSarkilar;

//layout ile baglantılar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sarkiBaslik1=(AppCompatEditText)findViewById(R.id.sarkiBaslik);
        sarkiSecilmeditxt1=(TextView)findViewById(R.id.sarkiSecilmeditxt);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        referenceSarkilar= FirebaseDatabase.getInstance().getReference().child("sarkilar");
        mStorageRef= FirebaseStorage.getInstance().getReference().child("sarkilar");
    }
//sarkı dosyası acma metodu
    public void sarkiDosyasiAc(View v){ //openAudioFile
        Intent i=new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("sarki/*");
        startActivityForResult(i,101); //yazma izni kodu
    }

    @Override //yazma izni alındıktan sonra dosya ismine ulaşma
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode==RESULT_OK && data.getData()!=null){
            audioUri=data.getData();
            String dosyaIsmi=getFileName(audioUri); //fileName

            sarkiSecilmeditxt1.setText(dosyaIsmi);
        }
    }

    private String getFileName(Uri uri) { //dosyaya ulasma
        String sonuc=null; //result
        if(audioUri.getScheme().equals("content")){
            Cursor cursor=getContentResolver().query(uri,null,null,null,null);

            try{
                if(cursor!=null && cursor.moveToFirst()){
                    sonuc=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }

        }


        if(sonuc==null){ //sonuc döndürülüyor
            sonuc= uri.getPath();
            int cut=sonuc.lastIndexOf('/');
            if(cut!=-1){
                sonuc=sonuc.substring(cut+1);
            }
        }
        return sonuc;
    }

    public void sesiYukle(View v){ //uploadAudioToFireBase  //sesi Firebase e yükleme
        if(sarkiSecilmeditxt1.getText().toString().equals("dosya secilmedi")){
            Toast.makeText(getApplicationContext(),"dosya seciniz",Toast.LENGTH_LONG).show();
        }
        else
        {
            if(mUploadTask!=null && mUploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"sarki yukleme zaten kuyrukta",Toast.LENGTH_LONG).show(); //yükleniyorsa mesaj gosterilir

            }
            else {
                dosyaYukle(); //uploadFile //degilse yuklenir
            }


        }
    }

    private void dosyaYukle() { //yukleme metodu
        if(audioUri!=null){
            String uzulukTxt; //durationTxt
            Toast.makeText(getApplicationContext(),"yukleniyor...",Toast.LENGTH_LONG).show();


            progressBar.setVisibility(View.VISIBLE); //yuklenirken ProgressBar aktifleşir

            final StorageReference storageReference= mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(audioUri));
            int uzunluk=sarkiUzunlugunuBul(audioUri); //durationInMills ve fingSongDuration //uzunluk bilgisi alınır

            if (uzunluk==0){
                uzulukTxt="NA";
            }
            uzulukTxt=getDurationFromMilli(uzunluk); //uzunlugu buluyor

            final String finalUzulukTxt = uzulukTxt;
            mUploadTask=storageReference.putFile(audioUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { //bilgileri alıyor

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    SarkiYukle sarkiYukle=new SarkiYukle(sarkiBaslik1.getText().toString(),
                                            finalUzulukTxt,uri.toString());  //3parametre var ??

                                    String uploadId=referenceSarkilar.push().getKey();
                                    referenceSarkilar.child(uploadId).setValue(sarkiYukle);  //id ye göre şarkı yükle

                                }
                            });


                        }
                    })

            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) { //progressbar ın ilerleme durumu
                    double progress=(100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });

        }
        else
        {
            Toast.makeText(getApplicationContext(),"dosya secilmedi",Toast.LENGTH_LONG).show(); //dosya secilmedi ise hata mesajı
        }
    }

    private String getDurationFromMilli(int uzunluk) {      //uzunluk alma
        Date date=new Date(uzunluk);
        SimpleDateFormat simple=new SimpleDateFormat("mm:ss", Locale.getDefault());
        String zamanim=simple.format(date); //myTime
        return zamanim;

    }

    private int sarkiUzunlugunuBul(Uri audioUri) { //milisaniye cinsinden uzunluk hesaplanıyor
        int miliZaman=0; //timeInMilliSec
        try{
            MediaMetadataRetriever retriever=new MediaMetadataRetriever();
            retriever.setDataSource(this,audioUri);
            String zaman=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); //time
            miliZaman=Integer.parseInt(zaman);

            retriever.release();
            return miliZaman;
        }

        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private String getFileExtension(Uri audioUri) { //dosya uzantısını al
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(audioUri));

    }

    public void openSongsActivity(View view){ //sarkı yurutuluyor
        Intent i=new Intent(MainActivity.this,SarkilariGosterActivity.class);
        startActivity(i);
    }
}
