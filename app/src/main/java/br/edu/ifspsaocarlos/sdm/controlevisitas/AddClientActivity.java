package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Client;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseClientsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseClientsHelper;

public class AddClientActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseClientsHelper mClientsHelper;

    private EditText nameEditText;
    private EditText contactEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mClientsHelper = new FirebaseClientsHelper(mDatabase);


        nameEditText = findViewById(R.id.ac_addclient_etname);
        contactEditText = findViewById(R.id.ac_addclient_etcontact);
        phoneEditText = findViewById(R.id.ac_addclient_etphone);
        emailEditText = findViewById(R.id.ac_addclient_etemail);
        saveButton = findViewById(R.id.ac_addclient_btsave);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEditText.getText().toString().trim().toUpperCase();
                String contact = contactEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                saveClient(name, contact, phone, email);
            }
        });
    }

    private void saveClient(String name, String contact, String phone, String email){
        Client client = new Client(name, contact, phone, email);

        //insere cliente no firebase
        mClientsHelper.addClient(client, new FirebaseClientsCallback() {
            @Override
            public void onClientAddCallback(Client client) {
                finish();
            }

            @Override
            public void onClientsRetrieveCallback(ArrayList<Client> clients) {}
        });
    }
}
