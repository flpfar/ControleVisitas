package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;

public class FirebaseVisitsHelper {
    private DatabaseReference mDatabase;
    private ValueEventListener mLoadVisitsByDateEventListener;
    private DatabaseReference mLoadVisitsByDateReference;

    public FirebaseVisitsHelper(DatabaseReference db){
        this.mDatabase = db;
    }

//    public void retrieveAllVisits(final FirebaseVisitsCallback callback){
//        mDatabase.child(Constants.FIREBASE_VISITS).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //recupera as visitas do firebase
//                ArrayList<Visit> visits = new ArrayList<>();
//                for(DataSnapshot visit : dataSnapshot.getChildren()){
//                    Visit someVisit = visit.getValue(Visit.class);
//                    try {
//                        someVisit.setId(visit.getKey());
//                    } catch (NullPointerException e){
//                        Log.e("FireBaseVisitsHelper: ", "Cant get visit key");
//                    }
//                    visits.add(someVisit);
//                }
//                callback.onVisitsRetrieveCallback(visits);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //ERROR
//            }
//        });
//    }

    public void loadVisitsByDate(final String date, final FirebaseVisitsCallback callback){
        mLoadVisitsByDateReference = mDatabase.child(Constants.FIREBASE_DATE_VISITS).child(date.replace("/",""));
        mLoadVisitsByDateEventListener = mLoadVisitsByDateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //recupera as visitas do firebase
                final ArrayList<Visit> visits = new ArrayList<>();
                for(DataSnapshot visit : dataSnapshot.getChildren()){
                    Visit someVisit = visit.getValue(Visit.class);
                    visits.add(someVisit);
                }
                callback.onVisitsLoadCallback(visits);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //ERROR
            }
        });
    }

    public void loadVisitsByFilter(final String clientId, final String startDate, final String endDate, final String keyword, final FirebaseVisitsCallback callback){
        DatabaseReference ref;
        if(clientId.isEmpty()) {
            if (startDate.isEmpty()) {
                //busca por keyword
                ref = mDatabase.child(Constants.FIREBASE_KEYWORDS).child(keyword);
                loadVisitsAux(ref, callback);
            } else if (keyword.isEmpty()) {
                //busca por periodo

            } else {
                //busca por período e keyword
            }
        }else{
            if(keyword.isEmpty()){
                if(startDate.isEmpty()){
                    //busca por cliente
                    ref = mDatabase.child(Constants.FIREBASE_CLIENT_VISITS).child(clientId);
                    loadVisitsAux(ref, callback);
                }else{
                    //busca por cliente e periodo
                }
            } else {
                //busca por cliente, periodo e keyword
            }
        }

    }

    private void loadVisitsAux(DatabaseReference ref, final FirebaseVisitsCallback callback){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<Visit> visits = new ArrayList<>();
                for(DataSnapshot visit : dataSnapshot.getChildren()){
                    Visit someVisit = visit.getValue(Visit.class);
                    visits.add(someVisit);
                }
                callback.onVisitsLoadCallback(visits);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("_FIREBASEVISITSHELPER", databaseError.getMessage());
            }
        });
    }

    public void addVisit(final Visit visit, final FirebaseVisitsCallback callback){
        if(visit.getId() == null) {
            final String id = mDatabase.child(Constants.FIREBASE_VISITS).push().getKey();
            visit.setId(id);
        }

        mDatabase.child(Constants.FIREBASE_VISITS).child(visit.getId()).setValue(visit, new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved. " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                    //quando a visita é salva, deve-se salvá-la também na tabela date e keywords.

                    mDatabase.child(Constants.FIREBASE_DATE_VISITS).child(visit.getDate().replace("/", "")).child(visit.getId()).setValue(visit);

                    if(visit.getKeywords() != null && !visit.getKeywords().isEmpty()) {
                        //lista de keyword está separada por "#@#". aqui eu transformo em uma lista
                        List<String> mKeywordsList = Arrays.asList(visit.getKeywords().split(Constants.SEPARATOR));
                        for(String keyword : mKeywordsList){
                            mDatabase.child(Constants.FIREBASE_KEYWORDS).child(keyword).child(visit.getId()).setValue(visit);
                        }
                    }

                    mDatabase.child(Constants.FIREBASE_CLIENT_VISITS).child(visit.getClient_id()).child(visit.getId()).setValue(visit);

                    callback.onVisitAddCallback(visit);
                }
            }
        });
    }

    public void deleteVisit(Visit visit){
        mDatabase.child(Constants.FIREBASE_VISITS).child(visit.getId()).removeValue();
        mDatabase.child(Constants.FIREBASE_DATE_VISITS).child(visit.getDate().replace("/", "")).child(visit.getId()).removeValue();
        if(visit.getKeywords() != null && !visit.getKeywords().isEmpty()) {
            List<String> mKeywordsList = Arrays.asList(visit.getKeywords().split(Constants.SEPARATOR));
            for (String keyword : mKeywordsList) {
                mDatabase.child(Constants.FIREBASE_KEYWORDS).child(keyword).child(visit.getId()).removeValue();
            }
        }
        mDatabase.child(Constants.FIREBASE_CLIENT_VISITS).child(visit.getClient_id()).child(visit.getId()).removeValue();
        mDatabase.child(Constants.FIREBASE_IMAGES).child(visit.getId()).removeValue();
        //StorageReference stRef = FirebaseStorage.getInstance().getReference();
        //TODO: APAGAR IMAGEM DO STORAGE
    }

    public void deleteClientAndVisits(final String clientId){
        mDatabase.child(Constants.FIREBASE_CLIENT_VISITS).child(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot visit : dataSnapshot.getChildren()){
                    Visit someVisit = visit.getValue(Visit.class);
                    deleteVisit(someVisit);
                }
                mDatabase.child(Constants.FIREBASE_CLIENTS).child(clientId).removeValue();
                mDatabase.child(Constants.FIREBASE_CLIENT_VISITS).child(clientId).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("_FIREBASEVISITSHELPER", databaseError.getMessage());
            }
        });
    }

    public void removeLoadVisitsByDateEventListener(){
        mLoadVisitsByDateReference.removeEventListener(mLoadVisitsByDateEventListener);
    }

    //TODO: UpdateVisit
    //must remember to update visit in date_visits too
}
