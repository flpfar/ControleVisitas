package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

    private Spinner clientSpinner;
    private ImageButton clientAddButton;
    private EditText dateEditText;
    private ImageButton dateImageButton;
    private EditText timeEditText;
    private ImageButton timeImageButton;
    private EditText reasonEditText;
    private Button startVisitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_visit);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mVisitsHelper = new FirebaseVisitsHelper(mDatabase);
        mClientsHelper = new FirebaseClientsHelper(mDatabase);

        clientSpinner = findViewById(R.id.ac_start_spinnerclient);
        clientAddButton = findViewById(R.id.ac_start_btaddclient);
        dateEditText = findViewById(R.id.ac_start_etdate);
        dateImageButton = findViewById(R.id.ac_start_ibcalendar);
        timeEditText = findViewById(R.id.ac_start_ettime);
        timeImageButton = findViewById(R.id.ac_start_ibclock);
        reasonEditText = findViewById(R.id.ac_start_etreason);
        startVisitButton = findViewById(R.id.ac_start_btstartvisit);

        populateClientSpinner();

        clientAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vai para addclientactivity
                startActivity(new Intent(StartVisitActivity.this, AddClientActivity.class));
            }
        });

        setDateAndTimeClickListeners();

        startVisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientName = clientSpinner.getSelectedItem().toString();
                String date = dateEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String reason = reasonEditText.getText().toString();

                startVisit(clientName, date, time, reason);
            }
        });
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
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        //abre caixa para seleção de data
        DatePickerDialog dialog = new DatePickerDialog(StartVisitActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month, year);
                        dateEditText.setText(selectedDate);
                    }
                }, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    
    private void openTimePicker(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(StartVisitActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                timeEditText.setText( String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time
        //mTimePicker.setTitle("Select Time");
        mTimePicker.show();
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

        //verifica se a lista está vazia, se estiver mostra toast e envia para 'AddClientActivity';


//        mDatabase.child(Constants.FIREBASE_CLIENTS).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                final List<String> clients = new ArrayList<String>();
//
//                //zera o adapter para sempre pegar atualizações dos dados
//                //clientSpinner.setAdapter(null);
//
//                //pega os clients do firebase e coloca seus nomes numa lista para popular o spinner
//                for(DataSnapshot client : dataSnapshot.getChildren()){
//                    String clientName = client.child("name").getValue(String.class);
//                    clients.add(clientName);
//                }
//
//                //verifica se a lista está vazia, se estiver mostra toast e envia para 'AddClientActivity';
//                if(clients.isEmpty()){
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_clients_toast), Toast.LENGTH_LONG).show();
//
//                    //vai para addclientactivity
//                    startActivity(new Intent(StartVisitActivity.this, AddClientActivity.class));
//                } else {
//                    //cria o adapter e seta o spinner
//                    ArrayAdapter<String> clientsAdapter =
//                            new ArrayAdapter<String>(StartVisitActivity.this, android.R.layout.simple_spinner_item, clients);
//                    clientsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    clientSpinner.setAdapter(clientsAdapter);
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //ERROR
//            }
//        });
    }

    private void startVisit(String client, String date, String time, String reason){
        Visit visit = new Visit(client, date, time, reason);

        //insere visita no firebase
        mVisitsHelper.addVisit(visit, new FirebaseVisitsCallback() {
            @Override
            public void onVisitsLoadCallback(ArrayList<Visit> visits) {}

            @Override
            public void onVisitAddCallback(Visit visit) {
                //vai para detailVisitActivity
                Intent detailVisitActivityIntent = new Intent(StartVisitActivity.this, DetailVisitActivity.class);
                detailVisitActivityIntent.putExtra(Constants.VISIT_DATA, visit);
                startActivity(detailVisitActivityIntent);

                //após ir para a detailVisitActivity, não deve voltar mais a essa activity
                finish();
            }
        });
    }
}
