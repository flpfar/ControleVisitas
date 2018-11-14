package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseMediaHelper {
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ValueEventListener mLoadVisitImagesListener;
    private ValueEventListener mLoadVisitAudiosListener;

    public FirebaseMediaHelper(DatabaseReference db){
        this.mDatabase = db;
    }
    public FirebaseMediaHelper(DatabaseReference db, StorageReference st){
        this.mDatabase = db;
        this.mStorage = st;
    }

    public void loadVisitImages(String visitId, final FirebaseMediaCallback callback){
        mLoadVisitImagesListener = mDatabase.child(visitId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<VisitImage> visitImages = new ArrayList<>();
                for(DataSnapshot image : dataSnapshot.getChildren()){
                    VisitImage someImage = image.getValue(VisitImage.class);
                    visitImages.add(someImage);
                }
                callback.onImagesLoadCallback(visitImages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadVisitAudios(String visitId, final FirebaseMediaCallback callback){
        mLoadVisitAudiosListener = mDatabase.child(visitId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<VisitAudio> visitAudios = new ArrayList<>();
                for(DataSnapshot audio : dataSnapshot.getChildren()){
                    VisitAudio someAudio = audio.getValue(VisitAudio.class);
                    visitAudios.add(someAudio);
                }
                callback.onAudiosLoadCallback(visitAudios);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteImage(VisitImage image) {
        mDatabase.child(image.getmVisitId()).child(image.getmImageId()).removeValue();
        StorageReference imageRef = mStorage.child(image.getmImageName());
        Log.e("STORAGE: ", imageRef.toString());
        imageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //arquivo deletado
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("FIREBASEMEDIAHELPER", "FALHA AO REMOVER DO STORAGE");
                    }
                });
    }

    public void deleteAudio(VisitAudio audio){
        mDatabase.child(audio.getmVisitId()).child(audio.getmAudioId()).removeValue();
        StorageReference audioRef = mStorage.child(audio.getmAudioName());
        audioRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //arquivo removido
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("FIREBASEMEDIAHELPER", "FALHA AO REMOVER DO STORAGE");
                    }
                });
    }

}
