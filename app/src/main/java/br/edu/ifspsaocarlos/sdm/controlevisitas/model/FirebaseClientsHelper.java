package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;

public class FirebaseClientsHelper {
    private DatabaseReference mDatabase;

    public FirebaseClientsHelper(DatabaseReference db){
        this.mDatabase = db.child(Constants.FIREBASE_CLIENTS);
    }

    public void loadClients(final FirebaseClientsCallback callback){
        mDatabase.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Client> clients = new ArrayList<>();
                for(DataSnapshot client : dataSnapshot.getChildren()){
                    Client someClient = client.getValue(Client.class);
                    clients.add(someClient);
                }
                callback.onClientsLoadCallback(clients);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addClient(final Client client, final FirebaseClientsCallback callback){
        final String id = mDatabase.push().getKey();
        client.setId(id);
        mDatabase.child(id).setValue(client, new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved. " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                    callback.onClientAddCallback(client);
                }
            }
        });
    }
}
