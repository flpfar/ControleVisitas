package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    }

    public void removeLoadVisitsByDateEventListener(){
        mLoadVisitsByDateReference.removeEventListener(mLoadVisitsByDateEventListener);
    }

    //TODO: UpdateVisit
    //must remember to update visit in date_visits too

}
