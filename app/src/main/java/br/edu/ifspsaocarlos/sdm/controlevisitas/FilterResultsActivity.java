package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.adapter.VisitsAdapter;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class FilterResultsActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private FirebaseVisitsHelper mVisitsHelper;

    private String mClientId;
    private String mKeyword;
    private int mFilterBy;

    private RecyclerView mRecyclerView;
    private VisitsAdapter mVisitsAdapter;
    private ArrayList<Visit> mVisitsList;

    private TextView mNoVisitsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_results);

        mNoVisitsTextView = findViewById(R.id.ac_results_novisits);
        mRecyclerView = findViewById(R.id.ac_results_recyclerview);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mVisitsHelper = new FirebaseVisitsHelper(mDatabaseRef);

        mVisitsList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mFilterBy = bundle.getInt(Constants.FILTERBY);
            mClientId = bundle.getString(Constants.CLIENT_ID);
            mKeyword = bundle.getString(Constants.KEYWORD);
        }

        setRecyclerView();
    }

    private void setRecyclerView(){
        //seta o recyclerview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura um dividr entre linhas para uma melhor visualização.
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // cria e seta o adapter
        mVisitsAdapter = new VisitsAdapter(this, mVisitsList);
        mRecyclerView.setAdapter(mVisitsAdapter);

        //loadimages é chamado no onResume()
    }

    private void loadVisits() {
        mVisitsHelper.loadVisitsByFilter(mFilterBy, mClientId, mKeyword, new FirebaseVisitsCallback() {
            @Override
            public void onVisitsLoadCallback(ArrayList<Visit> visits) {
                mVisitsList.clear();
                mVisitsList.addAll(visits);
                mVisitsAdapter.notifyDataSetChanged();

                if (mVisitsList.isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    mNoVisitsTextView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoVisitsTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onVisitAddCallback(Visit visit) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVisits();
    }
}
