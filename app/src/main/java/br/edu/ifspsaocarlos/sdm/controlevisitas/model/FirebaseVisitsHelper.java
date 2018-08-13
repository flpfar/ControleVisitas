package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import android.support.annotation.NonNull;
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
        this.mDatabase = db;
        mVisits = new ArrayList<>();
    }

    public void retrieveVisits(final ArrayList<Visit> visits, final VisitsAdapter adapter){
        mDatabase.child(Constants.FIREBASE_VISITS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //limpa a lista antes de atualizá-la para não duplicar dados
                visits.clear();

                //recupera as visitas do firebase
                for(DataSnapshot visit : dataSnapshot.getChildren()){
                    Visit someVisit = visit.getValue(Visit.class);
                    visits.add(someVisit);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //ERROR
            }
        });
    }

}
