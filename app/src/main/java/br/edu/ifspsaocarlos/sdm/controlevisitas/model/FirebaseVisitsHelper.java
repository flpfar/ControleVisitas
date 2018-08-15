package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.controlevisitas.R;
import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.adapter.VisitsAdapter;

public class FirebaseVisitsHelper {
    private DatabaseReference mDatabase;
    private ArrayList<Visit> mVisits;

    public FirebaseVisitsHelper(DatabaseReference db){
        this.mDatabase = db.child(Constants.FIREBASE_VISITS);
        mVisits = new ArrayList<>();
    }

    public void retrieveVisits(final FirebaseVisitsCallback callback){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //recupera as visitas do firebase
                ArrayList<Visit> visits = new ArrayList<>();
                for(DataSnapshot visit : dataSnapshot.getChildren()){
                    Visit someVisit = visit.getValue(Visit.class);
                    visits.add(someVisit);
                }
                callback.onVisitsRetrieveCallback(visits);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //ERROR
            }
        });
    }

//    public String addVisit(Visit visit){
//        String id = mDatabase.push().getKey();
//        visit.setSituation(Visit.SITUATION_INPROGRESS);
//        mDatabase.child(id).setValue(visit);
//
//        return id;
//    }

    public void addVisit(final FirebaseVisitsCallback callback, final Visit visit){
        final String id = mDatabase.push().getKey();
        visit.setSituation(Visit.SITUATION_INPROGRESS);
        visit.setId(id);
        mDatabase.child(id).setValue(visit, new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved. " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                    callback.onVisitAddCallback(visit);
                }
            }
        });
    }

}
