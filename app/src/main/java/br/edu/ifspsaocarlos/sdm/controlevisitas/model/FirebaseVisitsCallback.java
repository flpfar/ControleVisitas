package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import java.util.ArrayList;

public interface FirebaseVisitsCallback {
    void onVisitsRetrieveCallback(ArrayList<Visit> visits);
    void onVisitAddCallback(Visit visit);
}
