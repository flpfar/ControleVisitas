package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.adapter.AudioGalleryAdapter;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseMediaCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseMediaHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.VisitAudio;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.VisitImage;

public class AudioGalleryActivity extends AppCompatActivity {

    private ImageButton mRecorderButton;
    private ProgressBar mProgressBar;
    private TextView mTvEmpty;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private String mVisitId;

    private FirebaseMediaHelper mMediaHelper;

    private RecyclerView mRecyclerView;
    private AudioGalleryAdapter mAudioAdapter;
    private ArrayList<VisitAudio> mAudios;

    /*variaveis audio*/
    private MediaRecorder mRecorder = null;
    private String mAudioFileName = null;
    private String mAudioFilePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_gallery);


        mRecorderButton = findViewById(R.id.ac_audio_ibrecorder);
        mProgressBar = findViewById(R.id.ac_audio_progressbar);
        mRecyclerView = findViewById(R.id.ac_audio_recyclerview);
        mTvEmpty = findViewById(R.id.ac_audio_tvempty);

        mStorageRef = FirebaseStorage.getInstance().getReference(Constants.FIREBASE_AUDIOS);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_AUDIOS);
        mMediaHelper = new FirebaseMediaHelper(mDatabaseRef);

        //recupera extras
        if (getIntent().hasExtra(Constants.VISIT_ID)) {
            mVisitId = getIntent().getStringExtra(Constants.VISIT_ID);
            if (mVisitId == null) {
                Log.e("AudioGalleryActivity", "Must pass VISIT_ID");
                finish();
            }
        } else {
            Log.e("AudioGalleryActivity", "Must pass VISIT_ID");
            finish();
        }

        mRecorderButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        startRecording();
                        return true;
                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        stopRecording();
                        break;
                }
                return false;
            }
        });

        setRecyclerView();
    }

    private void setRecyclerView(){
        mAudios = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura um dividr entre linhas para uma melhor visualização.
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // cria e seta o adapter
        mAudioAdapter = new AudioGalleryAdapter(this, mAudios, mDatabaseRef, mStorageRef);
        mRecyclerView.setAdapter(mAudioAdapter);
    }

    private void loadAudios(){
        mMediaHelper.loadVisitAudios(mVisitId, new FirebaseMediaCallback() {
            @Override
            public void onImagesLoadCallback(ArrayList<VisitImage> images) {}

            @Override
            public void onAudiosLoadCallback(ArrayList<VisitAudio> audios) {
                //recria a lista local com os dados do banco
                mAudios.clear();
                mAudios.addAll(audios);

                mAudioAdapter.notifyDataSetChanged();

                //seta as visibilidades caso haja ou não imagens
                if(mAudios.isEmpty()){
                    mRecyclerView.setVisibility(View.GONE);
                    mTvEmpty.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mTvEmpty.setVisibility(View.GONE);
                }
            }
        });
    }

    private void startRecording(){
        Toast.makeText(this, getResources().getString(R.string.toast_start_recording), Toast.LENGTH_SHORT).show();
        mAudioFileName = System.currentTimeMillis()+".3gp";
        mAudioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mAudioFilePath += "/" + mAudioFileName;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mAudioFilePath);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    private void stopRecording(){
        Toast.makeText(this, getResources().getString(R.string.toast_stop_recording), Toast.LENGTH_SHORT).show();
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        uploadAudio();
    }

    private void playAudio(String url){
        MediaPlayer mediaPlayer = new MediaPlayer();
        try{
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadAudio(){
        mProgressBar.setVisibility(View.VISIBLE);
        StorageReference storageRef = mStorageRef.child(mAudioFileName);
        final File audioFile = new File(mAudioFilePath);
        Uri uriFromAudio = Uri.fromFile(audioFile);

        storageRef.putFile(uriFromAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AudioGalleryActivity.this, getResources().getString(R.string.toast_audio_saved), Toast.LENGTH_SHORT).show();
                final String firebaseAudioUrl = taskSnapshot.getDownloadUrl().toString();
                final String firebaseAudioId = mDatabaseRef.child(mVisitId).push().getKey();
                final VisitAudio visitAudio = new VisitAudio(mVisitId, firebaseAudioUrl, firebaseAudioId, mAudioFileName);
                mDatabaseRef.child(mVisitId).child(firebaseAudioId).setValue(visitAudio);
                mProgressBar.setVisibility(View.GONE);
                audioFile.delete();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAudios();
    }
}
