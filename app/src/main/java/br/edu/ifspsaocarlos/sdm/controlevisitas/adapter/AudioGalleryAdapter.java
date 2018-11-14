package br.edu.ifspsaocarlos.sdm.controlevisitas.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.controlevisitas.R;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseMediaHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.VisitAudio;

public class AudioGalleryAdapter extends RecyclerView.Adapter<AudioGalleryAdapter.AudioGalleryViewHolder> {

    private Context context;
    private List<VisitAudio> mAudios;
    private FirebaseMediaHelper mAudioHelper;

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
    public void onBindViewHolder(@NonNull final AudioGalleryAdapter.AudioGalleryViewHolder holder, final int position) {
        final VisitAudio visitAudio = mAudios.get(position);
        int realposition = position+1;
        holder.audioName.setText(context.getResources().getString(R.string.audio).toUpperCase() + " " + realposition);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verifica se o arquivo que está sendo deletado está sendo reproduzido
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
                final MediaPlayer mediaPlayer = new MediaPlayer();
                holder.progressPlayButton.setVisibility(View.VISIBLE);

                try{
                    mediaPlayer.setDataSource(visitAudio.getmAudioUri());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(final MediaPlayer mp) {
                            holder.progressPlayButton.setVisibility(View.INVISIBLE);
                            holder.playButton.setVisibility(View.INVISIBLE);
                            holder.stopButton.setVisibility(View.VISIBLE);
                            holder.stopButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.playButton.setVisibility(View.VISIBLE);
                                    holder.stopButton.setVisibility(View.INVISIBLE);
                                    mp.stop();
                                    mp.release();
                                }
                            });
                            mp.start();
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    holder.playButton.setVisibility(View.VISIBLE);
                                    holder.stopButton.setVisibility(View.INVISIBLE);
                                    mp.release();
                                }
                            });
                        }
                    });
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

    public class AudioGalleryViewHolder extends RecyclerView.ViewHolder{
        ImageButton playButton;
        ImageButton stopButton;
        ImageButton deleteButton;
        ProgressBar progressPlayButton;
        TextView audioName;

        public AudioGalleryViewHolder(View itemView) {
            super(itemView);
            playButton = itemView.findViewById(R.id.rv_audio_btplay);
            stopButton = itemView.findViewById(R.id.rv_audio_btstop);
            deleteButton = itemView.findViewById(R.id.rv_audio_btdelete);
            audioName = itemView.findViewById(R.id.rv_audio_tvaudioname);
            progressPlayButton = itemView.findViewById(R.id.rv_audio_pbplay);
        }
    }
}
