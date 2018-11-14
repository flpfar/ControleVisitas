package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Client;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseClientsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseClientsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class StartVisitActivity extends AppCompatActivity {

    public static final int DIALOG_ID_DATE = 1;
    public static final int DIALOG_ID_TIME = 2;

    private DatabaseReference mDatabase;
    private FirebaseVisitsHelper mVisitsHelper;
    private FirebaseClientsHelper mClientsHelper;

    private TextView clientTextView;
    private Spinner clientSpinner;
    private ImageButton clientAddButton;
    private EditText dateEditText;
    private ImageButton dateImageButton;
    private EditText timeEditText;
    private ImageButton timeImageButton;
    private EditText reasonEditText;
    private Button startVisitButton;
    private LinearLayout clientLinearLayout;
    private TextView cancelScheduledTextView;

    private Visit mVisit;
    private ArrayList<Client> mClientsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_visit);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mVisitsHelper = new FirebaseVisitsHelper(mDatabase);
        mClientsHelper = new FirebaseClientsHelper(mDatabase);

        clientLinearLayout = findViewById(R.id.ac_start_linearlayout1);
        clientTextView = findViewById(R.id.ac_start_tvclienteditable);
        clientSpinner = findViewById(R.id.ac_start_spinnerclient);
        clientAddButton = findViewById(R.id.ac_start_btaddclient);
        dateEditText = findViewById(R.id.ac_start_etdate);
        dateImageButton = findViewById(R.id.ac_start_ibcalendar);
        timeEditText = findViewById(R.id.ac_start_ettime);
        timeImageButton = findViewById(R.id.ac_start_ibclock);
        reasonEditText = findViewById(R.id.ac_start_etreason);
        startVisitButton = findViewById(R.id.ac_start_btstartvisit);
        cancelScheduledTextView = findViewById(R.id.ac_start_tvcancel);

        mClientsList = new ArrayList<>();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setDateAndTimeClickListeners();

        if(getIntent().hasExtra(Constants.VISIT_DATA)) {
            mVisit = getIntent().getParcelableExtra(Constants.VISIT_DATA);
            if(mVisit != null){
                setStartScheduledVisitLayout();
            }
        }else {
            populateClientSpinner();

            startVisitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedClientPosition = clientSpinner.getSelectedItemPosition();
                    String selectedClientId = "";
                    if(selectedClientPosition != 0){
                        selectedClientId = mClientsList.get(selectedClientPosition).getId();
                    }
                    String clientName = clientSpinner.getSelectedItem().toString();
                    String date = dateEditText.getText().toString();
                    String time = timeEditText.getText().toString();
                    String reason = reasonEditText.getText().toString();

                    startVisit(clientName, selectedClientId, date, time, reason, Visit.SITUATION_INPROGRESS);
                }
            });

            clientAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //vai para addclientactivity
                    startActivity(new Intent(StartVisitActivity.this, AddClientActivity.class));
                }
            });
        }
    }

    private void setDateAndTimeClickListeners(){
        //insere data e hora atuais nos EditText
        Calendar calendar = Calendar.getInstance();
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mMonth = calendar.get(Calendar.MONTH)+1;
        int mYear = calendar.get(Calendar.YEAR);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinutes = calendar.get(Calendar.MINUTE);
        String mCurrentDate =  String.format("%02d/%02d/%04d", mDay, mMonth, mYear);
        String mCurrentTime = String.format("%02d:%02d", mHour, mMinutes);
        dateEditText.setText(mCurrentDate);
        timeEditText.setText(mCurrentTime);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        dateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();

            }
        });
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });
        timeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });
    }

    private void openDatePicker(){
        Calendar cal = Calendar.getInstance();
        final int currentYear = cal.get(Calendar.YEAR);
        final int currentMonth = cal.get(Calendar.MONTH);
        final int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        //abre caixa para seleção de data
        DatePickerDialog dialog = new DatePickerDialog(StartVisitActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month, year);
                        dateEditText.setText(selectedDate);
                        if(dayOfMonth > currentDay || month > currentMonth + 1 || year > currentYear){
                            setScheduleLayout();
                        }else{
                            setStartVisitLayout();
                        }
                    }
                }, currentYear, currentMonth, currentDay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    
    private void openTimePicker(){
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(StartVisitActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                timeEditText.setText( String.format("%02d:%02d", selectedHour, selectedMinute));
                if(selectedHour > hour || selectedMinute > minute){
                    setScheduleLayout();
                } else{
                    setStartVisitLayout();
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        //mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void setStartScheduledVisitLayout(){
        clientLinearLayout.setVisibility(View.INVISIBLE);
        clientTextView.setText(mVisit.getClient());
        dateImageButton.setOnClickListener(null);
        dateEditText.setOnClickListener(null);
        timeEditText.setOnClickListener(null);
        timeImageButton.setOnClickListener(null);
        timeEditText.setEnabled(false);
        dateEditText.setEnabled(false);
        setTitle(getResources().getString(R.string.start_visit));
        reasonEditText.setText(mVisit.getReason());
        cancelScheduledTextView.setVisibility(View.VISIBLE);

        startVisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedClientId = mVisit.getClient_id();
                String clientName = mVisit.getClient();
                String date = dateEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String reason = reasonEditText.getText().toString();

                startVisit(clientName, selectedClientId, date, time, reason, Visit.SITUATION_INPROGRESS);
            }
        });

        cancelScheduledTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(StartVisitActivity.this)
                        .setMessage(getResources().getString(R.string.cancel_scheduled_alertdialog))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mVisitsHelper.deleteVisit(mVisit);
                                StartVisitActivity.this.finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();

            }
        });
    }

    public void setScheduleLayout(){
        startVisitButton.setText(getResources().getString(R.string.schedule_visit));
        startVisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedClientPosition = clientSpinner.getSelectedItemPosition();
                String selectedClientId = "";
                if(selectedClientPosition != 0){
                    selectedClientId = mClientsList.get(selectedClientPosition).getId();
                }
                String clientName = clientSpinner.getSelectedItem().toString();
                String date = dateEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String reason = reasonEditText.getText().toString();

                startVisit(clientName, selectedClientId, date, time, reason, Visit.SITUATION_SCHEDULED);
            }
        });
    }

    public void setStartVisitLayout(){
        startVisitButton.setText(getResources().getString(R.string.start_visit));
        startVisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedClientPosition = clientSpinner.getSelectedItemPosition();
                String selectedClientId = "";
                if(selectedClientPosition != 0){
                    selectedClientId = mClientsList.get(selectedClientPosition).getId();
                }
                String clientName = clientSpinner.getSelectedItem().toString();
                String date = dateEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String reason = reasonEditText.getText().toString();

                startVisit(clientName, selectedClientId, date, time, reason, Visit.SITUATION_INPROGRESS);
            }
        });
    }

    private void populateClientSpinner(){
        mClientsHelper.loadClients(new FirebaseClientsCallback() {
            @Override
            public void onClientAddCallback(Client client) {}

            @Override
            public void onClientsLoadCallback(ArrayList<Client> clients) {
                if(clients.isEmpty()){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_clients_toast), Toast.LENGTH_LONG).show();

                    //vai para addclientactivity
                    startActivity(new Intent(StartVisitActivity.this, AddClientActivity.class));
                } else {
                    //limpa lista de clients local
                    mClientsList.clear();

                    //adciona todos clients do banco
                    mClientsList.addAll(clients);

                    ArrayList<String> clientsNames = new ArrayList<>();
                    for(Client client : clients){
                        clientsNames.add(client.getName());
                    }

                    //cria o adapter e seta o spinner
                    ArrayAdapter<String> clientsAdapter =
                            new ArrayAdapter<String>(StartVisitActivity.this, android.R.layout.simple_spinner_item, clientsNames);
                    clientsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    clientSpinner.setAdapter(clientsAdapter);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startVisit(String client, String clientId, String date, String time, String reason, final int situation){
        Visit visit;
        if(mVisit == null) {
             visit = new Visit(client, date, time, reason);
        }else{
            visit = mVisit;
            visit.setDate(date);
            visit.setStartTime(time);
            visit.setReason(reason);
        }
        visit.setSituation(situation);
        visit.setClient_id(clientId);

        //insere visita no firebase
        mVisitsHelper.addVisit(visit, new FirebaseVisitsCallback() {
            @Override
            public void onVisitsLoadCallback(ArrayList<Visit> visits) {}

            @Override
            public void onVisitAddCallback(Visit visit) {
                if(situation == Visit.SITUATION_INPROGRESS) {
                    //vai para detailVisitActivity
                    Intent detailVisitActivityIntent = new Intent(StartVisitActivity.this, DetailVisitActivity.class);
                    detailVisitActivityIntent.putExtra(Constants.VISIT_DATA, visit);
                    startActivity(detailVisitActivityIntent);
                } else if(situation == Visit.SITUATION_SCHEDULED){
                    Toast.makeText(StartVisitActivity.this, getResources().getString(R.string.toast_visit_scheduled), Toast.LENGTH_SHORT).show();
                }

                //após ir para a detailVisitActivity, não deve voltar mais a essa activity
                finish();
            }
        });
    }
}
