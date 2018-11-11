package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.adapter.GalleryAdapter;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseMediaCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseMediaHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.VisitImage;

public class ImageGalleryActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int GALLERY_REQUEST = 100;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int THUMBSIZE = 360;
    private static final String TAG = "_ImageGalleryActivity";

    private FirebaseMediaHelper mImagesHelper;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    private RecyclerView mRecyclerView;
    private GalleryAdapter mGalleryAdapter;
    private ProgressBar mProgressBar;

    private String mVisitId;
    private Uri mImageUri;


    private TextView mTvEmpty;

    private List<VisitImage> mImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        mRecyclerView = findViewById(R.id.rv_gallery);
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mProgressBar = findViewById(R.id.ac_image_gallery_progressbar);
        FloatingActionButton fab = findViewById(R.id.ac_image_gallery_fab);

        mImages = new ArrayList<>();

        //seta o firebase
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_IMAGES);
        mStorageRef = FirebaseStorage.getInstance().getReference(Constants.FIREBASE_IMAGES);
        mImagesHelper = new FirebaseMediaHelper(mDatabaseRef);


        //recupera extras
        if(getIntent().hasExtra(Constants.VISIT_ID)){
            mVisitId = getIntent().getStringExtra(Constants.VISIT_ID);
            if(mVisitId == null){
                Log.e(TAG, "Must pass VISIT_ID");
                finish();
            }
        } else {
            Log.e(TAG, "Must pass VISIT_ID");
            finish();
        }

        //seta o fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //requisita permissao de acesso a galeria
                requestPermission();
            }
        });

        setRecyclerView();
    }

    private void setRecyclerView(){
        //seta o recyclerview
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);

        mGalleryAdapter = new GalleryAdapter(this, mImages, mDatabaseRef, mStorageRef);
        mRecyclerView.setAdapter(mGalleryAdapter);

        //loadimages é chamado no onResume()
    }

    private void loadImages(){
        mImagesHelper.loadVisitImages(mVisitId, new FirebaseMediaCallback() {
            @Override
            public void onImagesLoadCallback(ArrayList<VisitImage> images) {
                //recria a lista local com os dados do banco
                mImages.clear();
                mImages.addAll(images);

                //atualiza o adapter
                mGalleryAdapter.notifyDataSetChanged();

                //seta as visibilidades caso haja ou não imagens
                if(mImages.isEmpty()){
                    mRecyclerView.setVisibility(View.GONE);
                    mTvEmpty.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mTvEmpty.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GALLERY_REQUEST :
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    mImageUri = data.getData();
                    mProgressBar.setVisibility(View.VISIBLE);
                    uploadFileToFirebase();
                }
                break;
            default:
                break;
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFileToFirebase(){
        if(mImageUri != null){
            //comprime a imagem
            final byte[] compressedImage = compressImage();

            //seta nome para o arquivo no firebase
            final String imageName = System.currentTimeMillis()+"."+getFileExtension(mImageUri);
            final StorageReference fileRef = mStorageRef.child(imageName);

            //upload imagem
            fileRef.putBytes(compressedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String fireBaseImageUri = taskSnapshot.getDownloadUrl().toString();
                            final String firebaseImageId = mDatabaseRef.child(mVisitId).push().getKey();
                            final VisitImage visitImage = new VisitImage(mVisitId, fireBaseImageUri, firebaseImageId, imageName);
                            mDatabaseRef.child(mVisitId).child(firebaseImageId).setValue(visitImage);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ImageGalleryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Nenhuma imagem foi selecionada", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] compressImage(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Bitmap imageBitmap = SiliCompressor.with(this).getCompressBitmap(getRealPathFromUri(this, mImageUri));
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    private String getRealPathFromUri(Context context, Uri contentUri) {
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFilePicker();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            openFilePicker();
        }
    }

    private void openFilePicker(){
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent , GALLERY_REQUEST );
    }
}
