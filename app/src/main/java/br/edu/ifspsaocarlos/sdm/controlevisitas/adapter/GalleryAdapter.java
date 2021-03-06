package br.edu.ifspsaocarlos.sdm.controlevisitas.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.controlevisitas.R;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseMediaHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.VisitImage;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private Context context;
    private List<VisitImage> mImages;
    FirebaseMediaHelper mImageHelper;

    public GalleryAdapter(Context context, List<VisitImage> images, DatabaseReference dbRef, StorageReference stRef){
        this.context = context;
        this.mImages = images;
        mImageHelper = new FirebaseMediaHelper(dbRef, stRef);
    }

    @NonNull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.image_gallery_item, parent, false);
        return new GalleryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.GalleryViewHolder holder, int position) {
        VisitImage visitImage = mImages.get(position);

        Picasso
                .get()
                .load(visitImage.getmImageUri())
                .placeholder(R.drawable.progress_animation)
                .into(holder.imageItemView);

    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        ImageView imageItemView;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            imageItemView = itemView.findViewById(R.id.iv_image_gallery_item);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                final String url = mImages.get(position).getmImageUri();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            final int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                final VisitImage image = mImages.get(position);

                new AlertDialog.Builder(context)
                        .setMessage(context.getResources().getString(R.string.delete_image_alertdialog_message))
                        .setCancelable(false)
                        .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mImageHelper.deleteImage(image);
                            }
                        })
                        .setNegativeButton(context.getResources().getString(R.string.no), null)
                        .show();

            }
            return false;
        }
    }
}
