package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DatabaseReference;

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
    private FlexboxLayout keywordsFlexbox;
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
        keywordsFlexbox = findViewById(R.id.ac_detail_keywordsbox);
        closeVisitButton = findViewById(R.id.ac_detail_btclosevisit);

        Visit visit = getIntent().getParcelableExtra(Constants.VISIT_DATA);

        if(visit == null){
            //sem visita para popular activity. será destruida;
            throw new IllegalArgumentException("Must pass VISIT_DATA");
        }

        clientTextView.setText(visit.getClient());
        String startDateAndTime = visit.getDate() + " - " + visit.getStartTime();
        startTimeTextView.setText(startDateAndTime);
        String visitReason = getResources().getString(R.string.visit_reason) + " " + visit.getReason() + '\n';
        notesEditText.setText(visitReason);

        addKeywordImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView keyword = new TextView(getApplicationContext());
                keyword.setText("Teste");
                keyword.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                keyword.setPadding(8, 4, 8, 4);
                keyword.setTextColor(getResources().getColor(R.color.white));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(4, 4, 4, 4);
                keyword.setLayoutParams(layoutParams);
                keywordsFlexbox.addView(keyword);
            }
        });
    }
}
