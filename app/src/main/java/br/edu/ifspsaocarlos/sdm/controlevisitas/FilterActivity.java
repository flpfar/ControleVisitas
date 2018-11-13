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
    private static final int ALL_PERIOD = 0;
    private static final int SELECT_PERIOD = 1;

    private static final int START_DATE = 0;
    private static final int END_DATE = 1;

    private DatabaseReference mDatabaseRef;
    private FirebaseVisitsHelper mVisitsHelper;
    private FirebaseClientsHelper mClientsHelper;

    private Spinner mClientSpinner;
    private LinearLayout mLnCalendars;
    private LinearLayout mLnCalendar1;
    private LinearLayout mLnCalendar2;
    private EditText mEtCalendar1;
    private EditText mEtCalendar2;
    private ImageButton mBtCalendar1;
    private ImageButton mBtCalendar2;
    private EditText mEtKeyword;
    private Button mBtFilter;

    private int mStartDay;
    private int mStartMonth;
    private int mStartYear;

    private ArrayList<Client> mClientsList;
    private int mDatePeriod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        mClientSpinner = findViewById(R.id.ac_filter_spinnerclient);
        mEtCalendar1 = findViewById(R.id.ac_filter_etdate1);
        mEtCalendar2 = findViewById(R.id.ac_filter_etdate2);
        mBtCalendar1 = findViewById(R.id.ac_filter_ibcalendar1);
        mBtCalendar2 = findViewById(R.id.ac_filter_ibcalendar2);
        mEtKeyword = findViewById(R.id.ac_filter_etkeyword);
        mBtFilter = findViewById(R.id.ac_filter_btfilter);
        mLnCalendars = findViewById(R.id.ac_filter_linearlayout);
        mLnCalendar1 = findViewById(R.id.ac_filter_lndate1);
        mLnCalendar2 = findViewById(R.id.ac_filter_lndate2);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mVisitsHelper = new FirebaseVisitsHelper(mDatabaseRef);
        mClientsHelper = new FirebaseClientsHelper(mDatabaseRef);

        mClientsList = new ArrayList<>();
        mDatePeriod = ALL_PERIOD;
        mStartDay = mStartMonth = mStartYear = 9999;

        //seta o spinner
        populateClientSpinner();

        mBtFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedClientPosition = mClientSpinner.getSelectedItemPosition();
                String selectedClientId = "";

                if(selectedClientPosition != 0){
                    selectedClientId = mClientsList.get(selectedClientPosition).getId();
                    Log.e("_TAG_", selectedClientPosition+mClientsList.get(selectedClientPosition).getId());
                }

                String startDate = mEtCalendar1.getText().toString();
                String endDate = mEtCalendar2.getText().toString();
                String keyword = mEtKeyword.getText().toString();

                if(selectedClientId.isEmpty() && startDate.isEmpty() && keyword.isEmpty()){
                    //nenhum parametro de filtro foi selecionado
                    Toast.makeText(FilterActivity.this, "Não é possível realizar a operação. Selecione algum filtro.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(FilterActivity.this, FilterResultsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.CLIENT_ID, selectedClientId);
                    bundle.putString(Constants.START_DATE, startDate);
                    bundle.putString(Constants.END_DATE, endDate);
                    bundle.putString(Constants.KEYWORD, keyword);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            }
        });

        mBtCalendar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(START_DATE);
            }
        });

        mEtCalendar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(START_DATE);
            }
        });

        mBtCalendar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(END_DATE);
            }
        });

        mEtCalendar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(END_DATE);
            }
        });
    }

    private void openDatePicker(final int startOrEndDate){
        Calendar cal = Calendar.getInstance();
        final int currentYear = cal.get(Calendar.YEAR);
        final int currentMonth = cal.get(Calendar.MONTH);
        final int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        //abre caixa para seleção de data
        DatePickerDialog dialog = new DatePickerDialog(FilterActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month, year);

                        if(startOrEndDate == END_DATE){
                            //checar se a data final é menor que a inicial
                            //se for e se ainda não foi setada a data
//                            if(mStartDay == 9999){
//                                Toast.makeText(FilterActivity.this, "Escolha a data inicial!", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(FilterActivity.this, "Data inicial maior que final.", Toast.LENGTH_SHORT).show();
//                            }
                            mEtCalendar2.setText(selectedDate);
                        } else {
                            //data inicial
                            mStartDay = dayOfMonth;
                            mStartMonth = month;
                            mStartYear = year;
                            mEtCalendar1.setText(selectedDate);
                        }
                    }
                }, currentYear, currentMonth, currentDay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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

                    //adiciona um cliente vazio para spinner sem seleção
                    mClientsList.add(new Client(Constants.NO_CLIENT_SELECTED, "", "", ""));

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
            case R.id.ac_filter_rdperiodall:
                if (checked) {
                    mDatePeriod = ALL_PERIOD;
                    mLnCalendars.setVisibility(View.GONE);
                    mEtCalendar1.setText("");
                    mEtCalendar2.setText("");
                    mStartDay = mStartMonth = mStartYear = 9999;
                }
                break;
            case R.id.ac_filter_rdperiodselect:
                if (checked) {
                    mDatePeriod = SELECT_PERIOD;
                    mLnCalendars.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
