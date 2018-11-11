package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import java.util.ArrayList;

public interface FirebaseMediaCallback {
    void onImagesLoadCallback(ArrayList<VisitImage> images);
}
