package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class DetailVisitActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseVisit;

    private TextView clientTextView;
    private TextView startTimeTextView;
    private TextView closingTimeTextView; //or situation
    private EditText notesEditText;
    private ImageButton imagesImageButton;
    private ImageButton audiosImageButton;
    private EditText keywordEditText;
    private ImageButton addKeywordImageButton;
    private TextView keywordsBoxTextView;
    private Button closeVisitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_visit);



        clientTextView = findViewById(R.id.ac_detail_tvclient);
        startTimeTextView = findViewById(R.id.ac_detail_tvstarttime_var);
        closingTimeTextView = findViewById(R.id.ac_detail_tvclosingtime_var);
        notesEditText = findViewById(R.id.ac_detail_etnotes);
        imagesImageButton = findViewById(R.id.ac_detail_ibimages);
        audiosImageButton = findViewById(R.id.ac_detail_ibaudios);
        keywordEditText = findViewById(R.id.ac_detail_etkeyword);
        addKeywordImageButton = findViewById(R.id.ac_detail_ibkeyword);
        keywordsBoxTextView = findViewById(R.id.ac_detail_tvkeywordsbox);
        closeVisitButton = findViewById(R.id.ac_detail_btclosevisit);

        String visitId = getIntent().getStringExtra(StartVisitActivity.VISIT_ID);

        if(visitId == null){
            //sem visita para popular activity. será destruida;
            throw new IllegalArgumentException("Must pass VISIT_ID");
        }

        //referencia bd ao id da visita
        mDatabaseVisit = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_VISITS).child(visitId);

        //lê os dados da visita do bd
        mDatabaseVisit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lê a visita e seta os textviews
                Visit visit = dataSnapshot.getValue(Visit.class);
                clientTextView.setText(visit.getClient());
                startTimeTextView.setText(visit.getDate() + " - " + visit.getStartTime());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
