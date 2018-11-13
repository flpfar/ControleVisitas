package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import br.edu.ifspsaocarlos.sdm.controlevisitas.adapter.ClientsAdapter;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Client;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseClientsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseClientsHelper;

public class ClientsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ClientsAdapter mClientAdapter;
    private TextView mNoClientsTextView;

    private DatabaseReference mDatabaseRef;
    private FirebaseClientsHelper mClientsHelper;

    private ArrayList<Client> mClients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        mRecyclerView = findViewById(R.id.ac_clients_recyclerview);
        FloatingActionButton mFab = findViewById(R.id.ac_clients_fab);
        mNoClientsTextView = findViewById(R.id.ac_clients_noclients);

        mClients = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mClientsHelper = new FirebaseClientsHelper(mDatabaseRef);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Configura um dividr entre linhas, para uma melhor visualização.
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mClientAdapter = new ClientsAdapter(this, mClients);
        mRecyclerView.setAdapter(mClientAdapter);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClientsActivity.this, AddClientActivity.class));
            }
        });
    }

    private void loadClients(){
        mClientsHelper.loadClients(new FirebaseClientsCallback() {
            @Override
            public void onClientAddCallback(Client client) {
            }

            @Override
            public void onClientsLoadCallback(ArrayList<Client> clients) {
                mClients.clear();
                mClients.addAll(clients);
                mClientAdapter.notifyDataSetChanged();

                if(mClients.isEmpty()){
                    mRecyclerView.setVisibility(View.GONE);
                    mNoClientsTextView.setVisibility(View.VISIBLE);
                }else{
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoClientsTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClients();
    }
}
