package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.content.Intent;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import br.edu.ifspsaocarlos.sdm.controlevisitas.adapter.VisitsAdapter;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class MainActivity extends AppCompatActivity {

    private FirebaseVisitsHelper mVisitsHelper;
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private VisitsAdapter mVisitsAdapter;
    private ArrayList<Visit> mVisitsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //seta o firebasehelper
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mVisitsHelper = new FirebaseVisitsHelper(mDatabase);

        mRecyclerView = findViewById(R.id.ac_main_rvdayvisits);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura um dividr entre linhas, para uma melhor visualização.
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mVisitsList = new ArrayList<>();
        mVisitsAdapter = new VisitsAdapter(this, mVisitsList);

        mVisitsHelper.retrieveVisits(mVisitsList, mVisitsAdapter);

        mRecyclerView.setAdapter(mVisitsAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StartVisitActivity.class));
            }
        });
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
}
