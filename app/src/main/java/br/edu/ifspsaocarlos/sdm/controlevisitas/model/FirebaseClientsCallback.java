package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import java.util.ArrayList;

public interface FirebaseClientsCallback {
    void onClientAddCallback(Client client);
    void onClientsLoadCallback(ArrayList<Client> clients);
}
