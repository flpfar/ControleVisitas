package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import br.edu.ifspsaocarlos.sdm.controlevisitas.adapter.VisitsAdapter;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class MainActivity extends AppCompatActivity {

    private FirebaseVisitsHelper mVisitsHelper;
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private TextView mDateTextView;
    private VisitsAdapter mVisitsAdapter;
    private ArrayList<Visit> mVisitsList;
    private int mDay, mMonth, mYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDateTextView = findViewById(R.id.tb_date_tvdate);
        ImageButton calendarImageButton = findViewById(R.id.tb_date_btcalendar);


        //pega a data atual e mostra no textview
        Calendar calendar = Calendar.getInstance();
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mMonth = calendar.get(Calendar.MONTH)+1;
        mYear = calendar.get(Calendar.YEAR);
        String currentDate = getCurrentDate();

        mDateTextView.setText(currentDate);

        //seta listener p/ selecao de data
        calendarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        //seta o firebasehelper
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mVisitsHelper = new FirebaseVisitsHelper(mDatabase);

        mRecyclerView = findViewById(R.id.ac_main_rvdayvisits);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura um dividr entre linhas, para uma melhor visualização.
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // cria e seta o adapter
        mVisitsList = new ArrayList<>();
        mVisitsAdapter = new VisitsAdapter(this, mVisitsList);
        mRecyclerView.setAdapter(mVisitsAdapter);

        // recupera visitas do firebase
        loadVisits(currentDate);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StartVisitActivity.class));
            }
        });
    }

    private void loadVisits(String currentDate){
        mVisitsHelper.loadVisitsByDate(currentDate, new FirebaseVisitsCallback() {
            @Override
            public void onVisitsLoadCallback(ArrayList<Visit> visits) {
                mVisitsList.clear();
                mVisitsList.addAll(visits);
                mVisitsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onVisitAddCallback(Visit visit) {}
        });
    }

    private void openDatePicker(){
        //abre caixa para seleção de data
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //month = month + 1;
                        mMonth = month+1;
                        mDay = dayOfMonth;
                        mYear = year;
                        String selectedDate = getCurrentDate(); //String.format("%02d/%02d/%04d", dayOfMonth, month, year);
                        loadVisits(selectedDate);
                        mDateTextView.setText(selectedDate);
                    }
                }, mYear, mMonth-1, mDay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private String getCurrentDate(){
        return String.format("%02d/%02d/%04d", mDay, mMonth, mYear);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.menu_item_start_visit:
                startActivity(new Intent(MainActivity.this, StartVisitActivity.class));
                break;

            case R.id.menu_item_schedule_visit:
                break;

            case R.id.menu_item_filter_visits:
                break;

            case R.id.menu_item_manage_clients:
                break;

            case R.id.menu_item_about:
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVisitsHelper.removeLoadVisitsByDateEventListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVisitsHelper.removeLoadVisitsByDateEventListener();
    }
}
