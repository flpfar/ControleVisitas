package br.edu.ifspsaocarlos.sdm.controlevisitas.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.controlevisitas.R;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseMediaHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.VisitAudio;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.VisitImage;

public class AudioGalleryAdapter extends RecyclerView.Adapter<AudioGalleryAdapter.AudioGalleryViewHolder> {

    private Context context;
    private List<VisitAudio> mAudios;
    FirebaseMediaHelper mAudioHelper;
    MediaPlayer mMediaPlayer;

    public AudioGalleryAdapter(Context context, List<VisitAudio> audios, DatabaseReference dbRef, StorageReference stRef){
        this.context = context;
        this.mAudios = audios;
        mAudioHelper = new FirebaseMediaHelper(dbRef, stRef);
    }

    @NonNull
    @Override
    public AudioGalleryAdapter.AudioGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.rvaudio_item, parent, false);
        return new AudioGalleryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioGalleryAdapter.AudioGalleryViewHolder holder, int position) {
        final VisitAudio visitAudio = mAudios.get(position);
        int realposition = position+1;
        holder.audioName.setText(context.getResources().getString(R.string.audio).toUpperCase() + " " + realposition);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage(context.getResources().getString(R.string.delete_audio_alertdialog_message))
                        .setCancelable(false)
                        .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAudioHelper.deleteAudio(visitAudio);
                            }
                        })
                        .setNegativeButton(context.getResources().getString(R.string.no), null)
                        .show();
            }
        });

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer = new MediaPlayer();
                try{
                    mMediaPlayer.setDataSource(visitAudio.getmAudioUri());
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            mp.setOnCompletionListener(mCompletionListener);
                        }
                    });

                    mMediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mAudios.size();
    }

    //onCompletionListener method
    MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.release();
            mMediaPlayer = null;
        }
    };

    public class AudioGalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageButton playButton;
        ImageButton pauseButton;
        ImageButton deleteButton;
        TextView audioName;

        public AudioGalleryViewHolder(View itemView) {
            super(itemView);
            playButton = itemView.findViewById(R.id.rv_audio_btplay);
            pauseButton = itemView.findViewById(R.id.rv_audio_btpause);
            deleteButton = itemView.findViewById(R.id.rv_audio_btdelete);
            audioName = itemView.findViewById(R.id.rv_audio_tvaudioname);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                //what?

            }
        }
    }
}
