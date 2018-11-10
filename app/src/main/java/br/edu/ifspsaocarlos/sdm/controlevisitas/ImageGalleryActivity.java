package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.adapter.GalleryAdapter;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class ImageGalleryActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private FirebaseVisitsHelper mVisitsHelper;
    private DatabaseReference mDatabase;

    private RecyclerView mRecyclerView;
    private TextView tvEmpty;
    private Visit mVisit;
    private List<String> mImages;
    private String imagesUriWithSeparator;
    public static final int RESULT_GALLERY = 100;
    private GalleryAdapter adapter;

    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        mRecyclerView = findViewById(R.id.rv_gallery);
        tvEmpty = findViewById(R.id.tv_empty_view);
        imagesUriWithSeparator = "";

        //seta o firebasehelper
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mVisitsHelper = new FirebaseVisitsHelper(mDatabase);

        //seta o fab
        FloatingActionButton fab = findViewById(R.id.ac_image_gallery_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(ImageGalleryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        });

        //recupera extras
        if(getIntent().hasExtra(Constants.VISIT_DATA)){
            mVisit = getIntent().getParcelableExtra(Constants.VISIT_DATA);
            if(mVisit == null){
                throw new IllegalArgumentException("Must pass VISIT_DATA");
            }
        } else {
            throw new IllegalArgumentException("Must pass VISIT_DATA");
        }

        //seta o recyclerview
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);

        // cria e seta o adapter
        if(mVisit.getImages_id() != null) {
            mImages = new ArrayList<>(Arrays.asList(mVisit.getImages_id().split(Constants.SEPARATOR)));
        } else {
            mImages = new ArrayList<>();
        }
        adapter = new GalleryAdapter(this, mImages);
        mRecyclerView.setAdapter(adapter);
    }

    private void loadImages(){
        //imagesUriWithSeparator = mVisit.getImages_id();

        //se tiver imagens na visita
        if(!mImages.isEmpty()){
            //setar visibilidade dos items
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);

//            //Uris das imagens estão todos numa string, separados por um "#@#". Tranferindo para lista.
//            mImages = new ArrayList<>(Arrays.asList(imagesUriWithSeparator.split(Constants.SEPARATOR)));

            //avisa o adapter que entraram dados na lista
            adapter.notifyDataSetChanged();

        } else { //não há imagens na visita
            //setar visibilidade dos items
            mRecyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            imagesUriWithSeparator = "";
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadImages();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_GALLERY :
                if (data != null) {
                    //adiciono imageUri pra mImages
                    Uri uri = data.getData();
                    String imageUri;

                    if(uri != null) {
                        imageUri = uri.toString();
                        Toast.makeText(this, imageUri, Toast.LENGTH_SHORT).show();
                        mImages.add(imageUri);

                        //atualiza string das imagens na visita
                        mVisit.setImages_id(mVisit.getImages_id() + imageUri + Constants.SEPARATOR);

                        //salvo no bd
                        mVisitsHelper.addVisit(mVisit, new FirebaseVisitsCallback() {
                            @Override
                            public void onVisitsLoadCallback(ArrayList<Visit> visits) {}

                            @Override
                            public void onVisitAddCallback(Visit visit) {}
                        });
                    } else {
                        Toast.makeText(this, "Nenhuma imagem foi selecionada", Toast.LENGTH_SHORT).show();
                    }


                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    Intent galleryIntent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent , RESULT_GALLERY );

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }
//    private static final int REQUEST_WRITE_PERMISSION = 786;
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            openFilePicker();
//        }
//    }

//    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
//        } else {
//            openFilePicker();
//        }
//    }

//    private void openFilePicker(){
//        Intent galleryIntent = new Intent(
//                Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(galleryIntent , RESULT_GALLERY );
//    }
}
