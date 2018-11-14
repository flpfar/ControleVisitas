package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import java.util.ArrayList;

public interface FirebaseMediaCallback {
    void onImagesLoadCallback(ArrayList<VisitImage> images);
    void onAudiosLoadCallback(ArrayList<VisitAudio> audios);
}
