package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Client;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseClientsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseClientsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsHelper;

public class AddClientActivity extends AppCompatActivity {

    private static final int MENU_NEW = 1;
    private static final int MENU_SHOW = 2;
    private static final int MENU_EDIT = 3;


    private DatabaseReference mDatabase;
    private FirebaseClientsHelper mClientsHelper;
    private FirebaseVisitsHelper mVisitsHelper;

    private EditText nameEditText;
    private EditText contactEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private Button saveButton;

    private Client mClient;

    private int mMenuState = MENU_SHOW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mClientsHelper = new FirebaseClientsHelper(mDatabase);
        mVisitsHelper = new FirebaseVisitsHelper(mDatabase);


        nameEditText = findViewById(R.id.ac_addclient_etname);
        contactEditText = findViewById(R.id.ac_addclient_etcontact);
        phoneEditText = findViewById(R.id.ac_addclient_etphone);
        emailEditText = findViewById(R.id.ac_addclient_etemail);
        saveButton = findViewById(R.id.ac_addclient_btsave);
        mClient = new Client();

        Toolbar toolbar = findViewById(R.id.ac_addclient_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if(getIntent().hasExtra(Constants.CLIENT_DATA)){
            mClient = getIntent().getParcelableExtra(Constants.CLIENT_DATA);
            setClientDataInLayout();
        }else{
            mMenuState = MENU_NEW;
            invalidateOptionsMenu();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEditText.getText().toString().trim().toUpperCase();

                if(name.isEmpty()){
                    Toast.makeText(AddClientActivity.this, getResources().getString(R.string.client_name_toast), Toast.LENGTH_SHORT).show();
                }else {
                    String contact = contactEditText.getText().toString().trim();
                    String phone = phoneEditText.getText().toString().trim();
                    String email = emailEditText.getText().toString().trim();

                    saveClient(name, contact, phone, email);
                }
            }
        });
    }

    private void saveClient(String name, String contact, String phone, String email){
        mClient.setName(name);
        mClient.setContact(contact);
        mClient.setPhone(phone);
        mClient.setEmail(email);

        //insere cliente no firebase
        mClientsHelper.addClient(mClient, new FirebaseClientsCallback() {
            @Override
            public void onClientAddCallback(Client client) {
                finish();
            }

            @Override
            public void onClientsLoadCallback(ArrayList<Client> clients) {}
        });
    }

    private void setClientDataInLayout(){
        nameEditText.setText(mClient.getName());
        contactEditText.setText(mClient.getContact());
        phoneEditText.setText(mClient.getPhone());
        emailEditText.setText(mClient.getEmail());
        contactEditText.setHint("");
        phoneEditText.setHint("");
        emailEditText.setHint("");
        getSupportActionBar().setTitle(getResources().getString(R.string.client_data));

        nameEditText.setEnabled(false);
        contactEditText.setEnabled(false);
        phoneEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        saveButton.setVisibility(View.GONE);
    }

    private void setModifyLayout(){
        nameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddClientActivity.this, getResources().getString(R.string.no_modify_client_name), Toast.LENGTH_SHORT);
            }
        });
        contactEditText.setEnabled(true);
        phoneEditText.setEnabled(true);
        emailEditText.setEnabled(true);
        saveButton.setVisibility(View.VISIBLE);
        mMenuState = MENU_EDIT;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client, menu);
        switch (mMenuState){
            case MENU_NEW:
                menu.findItem(R.id.menu_item_modify_client).setVisible(false);
                menu.findItem(R.id.menu_item_delete_client).setVisible(false);
                break;
            case MENU_SHOW:
                break;
            case MENU_EDIT:
                menu.findItem(R.id.menu_item_modify_client).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.menu_item_modify_client:
                setModifyLayout();
                break;

            case R.id.menu_item_delete_client:
                new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.delete_alertdialog_message))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mVisitsHelper.deleteClientAndVisits(mClient.getId());
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();

                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
