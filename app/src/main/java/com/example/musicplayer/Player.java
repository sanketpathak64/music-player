package com.example.musicplayer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static android.graphics.PorterDuff.Mode.SRC;
import static android.graphics.PorterDuff.Mode.SRC_IN;

public class Player extends AppCompatActivity {
    Button next,previous,pause;
    TextView name;
    SeekBar seekBar;
    static MediaPlayer myplayer;
    int pos;
    String sname;
    ArrayList<File> mySongs;
    Thread updateseek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        next=findViewById(R.id.nextl);
        previous=findViewById(R.id.previousl);
        pause=findViewById(R.id.pausel);
        name=findViewById(R.id.namel);
        seekBar=findViewById(R.id.seekl);

        updateseek = new Thread()
        {
            @Override
            public void run()
            {
                int totalDuration= myplayer.getDuration();
                int curr;
                curr = 0;
                while(curr<totalDuration)
                {
                    try
                    {
                        sleep(500);
                        curr=myplayer.getCurrentPosition();
                        seekBar.setProgress(curr);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

            }
        };

        if(myplayer!=null)
        {
            myplayer.stop();
            myplayer.release();
        }
        Intent i=getIntent();
        Bundle b=i.getExtras();


        mySongs =  (ArrayList) i.getParcelableArrayListExtra("s");

        sname= mySongs.get(pos).getName().toString();
        String songname=i.getStringExtra("sname");

        name.setText(songname);
        name.setSelected(true);

        pos=b.getInt("pos",0);
        Uri uri=Uri.parse(mySongs.get(pos).toString());

        myplayer=MediaPlayer.create(getApplicationContext(),uri);
        myplayer.start();
        seekBar.setMax(myplayer.getDuration());
        updateseek.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                    myplayer.seekTo(seekBar.getProgress());
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                seekBar.setMax(myplayer.getDuration());
                seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);
                updateseek.start();

                if(myplayer.isPlaying())
                {
                    pause.setBackgroundResource(R.drawable.icon_play);
                    myplayer.pause();
                }
                else
                {
                    pause.setBackgroundResource(R.drawable.icon_pause);
                    myplayer.start();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myplayer.stop();
                myplayer.release();
                pos++;
                Uri u=Uri.parse(mySongs.get(pos).toString());
                myplayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(pos).getName().toString();
                name.setText(sname);
                myplayer.start();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myplayer.stop();
                myplayer.release();
                if(pos-1>0)
                    pos--;
                else
                    pos=mySongs.size()-1;
                Uri u=Uri.parse(mySongs.get(pos).toString());
                myplayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(pos).getName().toString();
                name.setText(sname);
                myplayer.start();
            }
        });


    }

}
