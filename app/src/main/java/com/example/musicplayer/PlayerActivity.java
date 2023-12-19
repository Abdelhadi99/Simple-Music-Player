package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlayerActivity extends AppCompatActivity {
    private TextView txtSongName, txtSongStart, txtSongStop;
    private Button btnPlay, btnNext, btnPrev, btnFastRewind, btnFastForward;
    private SeekBar seekBar;

    String songName;
    static MediaPlayer mediaPlayer;
    ImageView imageView;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initData();

        ////////////////////////////////////////////////////
        //start of the work here//

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        // get intent that have the Array list of songs

        Intent intent = getIntent();
        ArrayList<File> mySongs = (ArrayList<File>) intent.getExtras().get("songs");

        // Sort the ArrayList based on last modified timestamp (newer to older)
        Collections.sort(mySongs, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                // Compare last modified timestamps in descending order
                return Long.compare(file2.lastModified(), file1.lastModified());
            }
        });
        position = intent.getIntExtra("pos", 0);


        txtSongName.setSelected(true);

        Uri uri = Uri.parse(mySongs.get(position).toString());
        songName = mySongs.get(position).getName();
        txtSongName.setText(songName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        String endTime = createTime(mediaPlayer.getDuration());
        txtSongStop.setText(endTime);

        // Initialize the SeekBar max value
        seekBar.setMax(mediaPlayer.getDuration());

        // Initialize Handler
        Handler handler = new Handler();

        // ...

        // This handler updates the SeekBar progress periodically
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);

                    String currentTime = createTime(currentPosition);
                    txtSongStart.setText(currentTime);

                    // Schedule the handler to run again after a delay
                    handler.postDelayed(this, 1000); // Delayed by 1 second
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }, 1000); // Delayed by 1 second

        // ...


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });


        // work with the txt song start that show progress of duration of the song

        final Handler handler2 = new Handler();
        final int delay = 1000;

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {

                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtSongStart.setText(currentTime);
                handler.postDelayed(this, delay);

            }
        }, delay);


        //


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_play));
                    mediaPlayer.pause();

                } else {
                    btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_pause));
                    mediaPlayer.start();

                }
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1) % mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                songName = mySongs.get(position).getName();
                txtSongName.setText(songName);
                mediaPlayer.start();
                btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_pause));
                startAnimation(imageView);

                String endTime = createTime(mediaPlayer.getDuration());
                txtSongStop.setText(endTime);
                seekBar.setProgress(mediaPlayer.getCurrentPosition());

            }
        });

        // next listener   :::   if the song is complete the next one play

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                seekBar.setProgress(100);
                btnNext.performClick();


            }
        });


        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                songName = mySongs.get(position).getName();
                txtSongName.setText(songName);
                mediaPlayer.start();
                btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_pause));
                startAnimation(imageView);

                String endTime = createTime(mediaPlayer.getDuration());
                txtSongStop.setText(endTime);
                seekBar.setProgress(mediaPlayer.getCurrentPosition());

            }
        });

        btnFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        });

        btnFastRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        });

    }

    void initData() {
        imageView = findViewById(R.id.imgSong);
        txtSongName = findViewById(R.id.txtSong);
        txtSongStart = findViewById(R.id.txtSongStart);
        txtSongStop = findViewById(R.id.txtSongStop);

        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnFastForward = findViewById(R.id.btnFastForward);
        btnFastRewind = findViewById(R.id.btnFastRewind);

        seekBar = findViewById(R.id.seekBar);

    }

    public void startAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();

    }

    // this method for make the duration of the song be in minutes and seconds
    public String createTime(int duration) {
        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        time += min + ":";
        if (sec < 10) {
            time += "0";
        }
        time += sec;

        return time;
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }
}