package br.edu.ifspsaocarlos.sdm.controlevisitas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.controlevisitas.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    public static int THUMBSIZE = 64;
    private Context context;
    private List<String> mImages;

    public GalleryAdapter(Context context, List<String> images){
        this.context = context;
        this.mImages = images;
    }

    @NonNull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.image_gallery_item, parent, false);
        return new GalleryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.GalleryViewHolder holder, int position) {
        String imageUri = mImages.get(position);
        Log.d("1GALLERY ADAPTER: ", imageUri);
        //Bitmap bitmap = BitmapFactory.decodeFile(imageUri);
        //Picasso.get().load(imageUri).into(holder.imageItemView);

        //Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageUri),THUMBSIZE, THUMBSIZE);

//        try {
//            final Bitmap b = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(imageUri)), null,null);
//            holder.imageItemView.setImageBitmap(b);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        try{
//            String path = getRealPathFromUri(context, Uri.parse(imageUri));
//            File f = new File(path, "");
//            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            holder.imageItemView.setImageBitmap(b);
//        }catch (FileNotFoundException e){
//            Log.e("1GA", "loadImageFromStorage: FileNotFoundException: " + e.getMessage() );
//        }



//        holder.imageItemView.setImageBitmap(bitmap);
//        holder.imageItemView.invalidate();
        holder.imageItemView.setImageURI(Uri.parse(imageUri));
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageItemView;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            imageItemView = itemView.findViewById(R.id.iv_image_gallery_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
