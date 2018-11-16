package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Client;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseClientsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseClientsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class FilterActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private FirebaseVisitsHelper mVisitsHelper;
    private FirebaseClientsHelper mClientsHelper;

    private TextView mClientTextView;
    private Spinner mClientSpinner;
    private TextView mKeywordTextView;
    private EditText mEtKeyword;
    private Button mBtFilter;

    private int mFilterBy = Constants.FILTERBY_CLIENT;

    private ArrayList<Client> mClientsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        mClientTextView = findViewById(R.id.ac_filter_tvclient);
        mClientSpinner = findViewById(R.id.ac_filter_spinnerclient);
        mKeywordTextView = findViewById(R.id.ac_filter_tvkeyword);
        mEtKeyword = findViewById(R.id.ac_filter_etkeyword);
        mBtFilter = findViewById(R.id.ac_filter_btfilter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mVisitsHelper = new FirebaseVisitsHelper(mDatabaseRef);
        mClientsHelper = new FirebaseClientsHelper(mDatabaseRef);

        mClientsList = new ArrayList<>();

        //seta o spinner
        populateClientSpinner();

        mBtFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FilterActivity.this, FilterResultsActivity.class);
                Bundle bundle = new Bundle();

                switch (mFilterBy){
                    case Constants.FILTERBY_CLIENT:
                        //pega cliente selecionado no spinner
                        int selectedClientPosition = mClientSpinner.getSelectedItemPosition();
                        Client selectedClient = mClientsList.get(selectedClientPosition);

                        bundle.putParcelable(Constants.CLIENT_DATA, selectedClient);
                        bundle.putInt(Constants.FILTERBY, mFilterBy);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;

                    case Constants.FILTERBY_KEYWORD:
                        String keyword = mEtKeyword.getText().toString();
                        if(keyword.isEmpty()){
                            Toast.makeText(FilterActivity.this, getResources().getString(R.string.toast_keyword_needed), Toast.LENGTH_SHORT).show();
                        }else{
                            bundle.putString(Constants.KEYWORD, keyword);
                            bundle.putInt(Constants.FILTERBY, mFilterBy);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        break;

                    case Constants.FILTERBY_SCHEDULED:
                        bundle.putInt(Constants.FILTERBY, mFilterBy);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;

                }
            }
        });

    }

    private void populateClientSpinner() {
        mClientsHelper.loadClients(new FirebaseClientsCallback() {
            @Override
            public void onClientAddCallback(Client client) {
            }

            @Override
            public void onClientsLoadCallback(ArrayList<Client> clients) {
                if (clients.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_clients_toast2), Toast.LENGTH_LONG).show();
                } else {

                    //limpa lista de clients local
                    mClientsList.clear();

                    //adciona todos clients do banco
                    mClientsList.addAll(clients);

                    ArrayList<String> clientsNames = new ArrayList<>();
                    for (Client client : mClientsList) {
                        clientsNames.add(client.getName());
                    }

                    //cria o adapter e seta o spinner
                    ArrayAdapter<String> clientsAdapter =
                            new ArrayAdapter<String>(FilterActivity.this, android.R.layout.simple_spinner_item, clientsNames);
                    clientsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mClientSpinner.setAdapter(clientsAdapter);
                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.ac_filter_rdclient:
                if (checked) {
                    mFilterBy = Constants.FILTERBY_CLIENT;
                    mClientSpinner.setVisibility(View.VISIBLE);
                    mClientTextView.setVisibility(View.VISIBLE);

                    mKeywordTextView.setVisibility(View.GONE);
                    mEtKeyword.setVisibility(View.GONE);
                }
                break;
            case R.id.ac_filter_rdkeyword:
                if (checked) {
                    mFilterBy = Constants.FILTERBY_KEYWORD;

                    mClientSpinner.setVisibility(View.GONE);
                    mClientTextView.setVisibility(View.GONE);

                    mKeywordTextView.setVisibility(View.VISIBLE);
                    mEtKeyword.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ac_filter_rdscheduled:
                if (checked) {
                    mFilterBy = Constants.FILTERBY_SCHEDULED;

                    mClientSpinner.setVisibility(View.GONE);
                    mClientTextView.setVisibility(View.GONE);

                    mKeywordTextView.setVisibility(View.GONE);
                    mEtKeyword.setVisibility(View.GONE);
                }
                break;
        }
    }
}
