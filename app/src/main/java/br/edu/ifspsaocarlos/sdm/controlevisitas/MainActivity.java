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
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.edu.ifspsaocarlos.sdm.controlevisitas.adapter.VisitsAdapter;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class MainActivity extends AppCompatActivity {

    private FirebaseVisitsHelper mVisitsHelper;
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private TextView mDateTextView;
    private TextView mNoVisitsTextView;
    private VisitsAdapter mVisitsAdapter;
    private ArrayList<Visit> mVisitsList;
    private Button mPreviousButton;
    private Button mNextButton;
    private int mDay, mMonth, mYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPreviousButton = findViewById(R.id.tb_date_btback);
        mNextButton = findViewById(R.id.tb_date_btnext);
        mNoVisitsTextView = findViewById(R.id.ac_main_novisits);
        mDateTextView = findViewById(R.id.tb_date_tvdate);
        ImageButton calendarImageButton = findViewById(R.id.tb_date_btcalendar);


        //pega a data atual e mostra no textview
        setToday();
        String currentDate = getCurrentDate();

        mDateTextView.setText(currentDate);

        //seta listener p/ selecao de data
        calendarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVisits(getPreviousDate());
                mDateTextView.setText(getCurrentDate());
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVisits(getNextDate());
                mDateTextView.setText(getCurrentDate());
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

        FloatingActionButton fab = findViewById(R.id.fab);
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

                if(mVisitsList.isEmpty()){
                    mRecyclerView.setVisibility(View.GONE);
                    mNoVisitsTextView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoVisitsTextView.setVisibility(View.GONE);
                }
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
                        Toast.makeText(MainActivity.this, getPreviousDate(), Toast.LENGTH_SHORT).show();
                        mDateTextView.setText(selectedDate);
                    }
                }, mYear, mMonth-1, mDay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private String getCurrentDate(){
        return String.format("%02d/%02d/%04d", mDay, mMonth, mYear);
    }

    public String getPreviousDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(mYear, mMonth-1, mDay);
        cal.add(cal.DATE, -1);
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH)+1;
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        return format.format(cal.getTime());
    }

    public String getNextDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(mYear, mMonth-1, mDay);
        cal.add(cal.DATE, 1);
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH)+1;
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        return format.format(cal.getTime());
    }

    private void setToday(){
        Calendar calendar = Calendar.getInstance();
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mMonth = calendar.get(Calendar.MONTH)+1;
        mYear = calendar.get(Calendar.YEAR);
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

            case R.id.menu_item_filter_visits:
                startActivity(new Intent(MainActivity.this, FilterActivity.class));
                break;

            case R.id.menu_item_manage_clients:
                startActivity(new Intent(MainActivity.this, ClientsActivity.class));
                break;

            case R.id.menu_item_help:
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
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
    protected void onStart() {
        super.onStart();
        loadVisits(getCurrentDate());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVisitsHelper.removeLoadVisitsByDateEventListener();
    }
}
