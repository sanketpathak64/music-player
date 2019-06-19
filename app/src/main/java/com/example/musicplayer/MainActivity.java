package com.example.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView list;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list=(ListView)findViewById(R.id.mylist);

        runTimePermission();


    }
    public void runTimePermission()
    {
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(File file)
    {
        ArrayList<File> arrayList=new ArrayList<File>();
        File[] files=file.listFiles();
        for(File single:files)
        {
            if(single.isDirectory() && !single.isHidden())
            {
                arrayList.addAll(findSong(single));

            }
            else
            {
                if(single.getName().endsWith(".mp3") || single.getName().endsWith(".wav"))
                {
                    arrayList.add(single);
                }
            }
        }

        return arrayList;
    }
    void display()
    {
        final ArrayList<File> songs=findSong(Environment.getExternalStorageDirectory());
        items=new String[songs.size()];
        for(int i=0;i<songs.size();i++)
        {
            items[i]=songs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        list.setAdapter(myAdapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name=list.getItemAtPosition(position).toString();

                startActivity(new Intent(MainActivity.this,Player.class).putExtra("s",songs)
                        .putExtra("sname",name)
                .putExtra("pos",position));
            }
        });
    }
}
