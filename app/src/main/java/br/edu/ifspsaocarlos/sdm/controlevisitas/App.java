package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //permite armazenar dados em cache
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
