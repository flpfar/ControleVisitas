package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Client;

public class AddClientActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

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

        nameEditText = findViewById(R.id.ac_addclient_etname);
        contactEditText = findViewById(R.id.ac_addclient_etcontact);
        phoneEditText = findViewById(R.id.ac_addclient_etphone);
        emailEditText = findViewById(R.id.ac_addclient_etemail);
        saveButton = findViewById(R.id.ac_addclient_btsave);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String contact = contactEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                saveClient(name, contact, phone, email);
            }
        });
    }

    private void saveClient(String name, String contact, String phone, String email){
        String id = mDatabase.child(Constants.FIREBASE_CLIENTS).push().getKey();
        Client client = new Client(name, contact, email, phone);

        //insere cliente no firebase
        mDatabase.child(Constants.FIREBASE_CLIENTS).child(id).setValue(client);

        finish();
    }
}
