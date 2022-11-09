package com.dam.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //vars globales
    RecyclerView recyclerView;
    TextView tvSongTitle, tvCurrentPos, tvTotalDuration;
    ImageView btnPrev, btnPlay, btnNext;
    SeekBar sbPosition;
    MediaPlayer mediaPlayer;
    ArrayList<ModelSong> songArrayList;
    int currentSongPos = 0;
    long currentPos, totalDuration;
    public static final int PERMISSION_READ = 0;

    //init des composant graphiques

    private void initUi() {
        recyclerView = findViewById(R.id.recyclerview);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        tvCurrentPos = findViewById(R.id.tvCurrentPos);
        tvTotalDuration = findViewById(R.id.tvTotalDuration);
        btnPrev = findViewById(R.id.btnPrev);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        sbPosition = findViewById(R.id.sbPosition);
        mediaPlayer = new MediaPlayer();
        songArrayList = new ArrayList<>();

        //init recycler

        recyclerView.setLayoutManager((new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    // Méthode poour vérifier l'accès aux données situées sur l'espace de stockage extene

    public boolean checkPermission() {
        int READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_READ);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "Please allow storage permission",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        setSong();
                    }
                }
            }
        }
    }

    private void setSong() {
        getAudioFiles();

        //gestion de la seekbar

        sbPosition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentPos = seekBar.getProgress();
                mediaPlayer.seekTo((int) currentPos);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentPos = seekBar.getProgress();
                mediaPlayer.seekTo(((int) currentPos));
            }

        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                currentSongPos++;
                if (currentSongPos < (songArrayList.size())) {
                    playSong(currentSongPos);
                } else {
                    currentSongPos = 0;
                    playSong(currentSongPos);
                }
            }
        });

        if (!songArrayList.isEmpty()) {
            playSong(currentSongPos);
            prevSong();
            nextSong();
            pauseSong();
        }
    }






    private void playSong(int pos) {
        try {

            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, songArrayList.get(pos).getSongUri());
            mediaPlayer.start();
            mediaPlayer.prepare();
            btnPlay.setImageResource(R.drawable.ic_baseline_pause_42);
            tvSongTitle.setText(songArrayList.get(pos).getSongTitle());
            currentSongPos = pos;

        } catch (Exception e) {
            e.printStackTrace();
        }
        setSongProgress();
    }

    private void setSongProgress() {
        currentPos = mediaPlayer.getCurrentPosition();
        totalDuration = mediaPlayer.getDuration();
        tvCurrentPos.setText(timerConvertion((long) currentPos));
        tvTotalDuration.setText(timerConvertion((long) totalDuration));
        sbPosition.setMax((int) totalDuration);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    currentPos = mediaPlayer.getCurrentPosition();
                    tvCurrentPos.setText(timerConvertion((long) currentPos));
                    sbPosition.setProgress((int) currentPos);
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    public String timerConvertion(long value) {
        String songDuration;
        int dur = (int) value; //la duree en ms
        int hrs = dur / 3600000;
        int min = (dur / 60000) % 60000;
        int sec = dur % 60000 / 1000;

        if (hrs > 0) {
            songDuration = String.format("%02d:%02:%02d", hrs, min, sec);
        } else {
            songDuration = String.format("%02:%02d", min, sec);
        }
        return songDuration;
    }

    private void prevSong() {
        btnPrev.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSongPos > 0) {
                    currentSongPos--;
                } else {
                    currentSongPos = songArrayList.size() - 1;
                }
                playSong(currentSongPos);
            }
        }));
    }

    private void nextSong() {
        btnNext.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           if (currentSongPos < (songArrayList.size() - 1)) {
                                               currentSongPos++;
                                           } else {
                                               currentSongPos = 0;
                                           }
                                           playSong(currentSongPos);

                                       }
                                   });
    }

    private void pauseSong() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                } else {
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.ic_baseline_pause_42);
                }
            }
        });
    }



    public void getAudioFiles() {
        ContentResolver contentResolver = getContentResolver();
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;


        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                Uri coverFolder = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(coverFolder, albumId);
//                Log.i(TAG, "getAudioFiles: ");

                //remplissage du model
                ModelSong modelSong = new ModelSong();
                modelSong.setSongTitle(title);
                modelSong.setSongArtiste(artist);
                modelSong.setSongUri(Uri.parse(url));
                modelSong.setSongDuration(duration);
                modelSong.setSongCover(albumArtUri);

                songArrayList.add(modelSong);

            } while (cursor.moveToNext());
        }
        AdapterSong adapterSong = new AdapterSong(this, songArrayList);
        recyclerView.setAdapter(adapterSong);

        adapterSong.setOnItemClickListener(new AdapterSong.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {

            }

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        if (checkPermission()) {
            setSong();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

}
