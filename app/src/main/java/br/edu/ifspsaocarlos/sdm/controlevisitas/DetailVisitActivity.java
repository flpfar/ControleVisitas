package br.edu.ifspsaocarlos.sdm.controlevisitas;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class DetailVisitActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseVisitsHelper mVisitsHelper;
    private Visit visit;

    private TextView clientTextView;
    private TextView startTimeTextView;
    private TextView closingTimeTextView; //or situation
    private TextView cancelVisitTextView;
    private EditText notesEditText;
    private ImageButton imagesImageButton;
    private ImageButton audiosImageButton;
    private EditText keywordEditText;
    private ImageButton addKeywordImageButton;
    private FlexboxLayout keywordsFlexbox;
    private Button closeVisitButton;
    private ProgressBar progressBar;
    private String keywordsList;
    private List<String> mKeywordsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_visit);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mVisitsHelper = new FirebaseVisitsHelper(mDatabase);
        keywordsList = "";

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
        progressBar = findViewById(R.id.ac_detail_progressbar);
        cancelVisitTextView = findViewById(R.id.ac_detail_tvcancelvisit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ac_detail_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        visit = getIntent().getParcelableExtra(Constants.VISIT_DATA);

        if(visit == null){
            //sem visita para popular activity. será destruida;
            throw new IllegalArgumentException("Must pass VISIT_DATA");
        }

        setCommonLayout();

        switch (visit.getSituation()){
            case Visit.SITUATION_INPROGRESS:
                setLayoutInProgress();
                break;
            case Visit.SITUATION_COMPLETED:
                setLayoutCompleted();
                break;
            default:
                break;
        }


    }

    public void setLayoutInProgress(){

        addKeywordImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView keyword = new TextView(getApplicationContext());
                String newKeyword = keywordEditText.getText().toString();
                keywordEditText.setText("");
                if(!newKeyword.isEmpty()) {
                    keyword.setText(newKeyword);
                    keyword.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    keyword.setPadding(8, 4, 8, 4);
                    keyword.setTextColor(getResources().getColor(R.color.white));

                    //atualiza a lista de keywords
                    keywordsList = keywordsList + newKeyword + Constants.SEPARATOR;

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(4, 4, 4, 4);
                    keyword.setLayoutParams(layoutParams);
                    keywordsFlexbox.addView(keyword);
                    hideKeyboard(DetailVisitActivity.this);
                }
            }
        });

        cancelVisitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailVisitActivity.this)
                        .setMessage(getResources().getString(R.string.delete_alertdialog_message))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mVisitsHelper.deleteVisit(visit);
                                DetailVisitActivity.this.finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();
            }
        });

        closeVisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailVisitActivity.this)
                        .setMessage(getResources().getString(R.string.closetime_alertdialog_message))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.close_visit), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //setar hora e data de encerramento
                                setCurrentTime();
                                //setar status encerrado
                                visit.setSituation(Visit.SITUATION_COMPLETED);

                                setVisitDataAndSave(51);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .setNeutralButton(getResources().getString(R.string.change_dateandtime), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }

    public void setLayoutCompleted(){
        closingTimeTextView.setText(visit.getClosingTime());
        if(visit.getNotes() == null) {
            String visitReason = getResources().getString(R.string.visit_reason) + " " + visit.getReason() + '\n';
            notesEditText.setText(visitReason);
        } else {
            notesEditText.setText(visit.getNotes());
        }
        notesEditText.setFocusable(false);
        notesEditText.setFocusableInTouchMode(false);
        notesEditText.setClickable(false);
//        ViewGroup.LayoutParams params = notesEditText.getLayoutParams();
//        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        notesEditText.setLayoutParams(params);
        closeVisitButton.setVisibility(View.INVISIBLE);
        cancelVisitTextView.setVisibility(View.INVISIBLE);
        keywordEditText.setVisibility(View.GONE);
        addKeywordImageButton.setVisibility(View.GONE);
        if(visit.getKeywords() == null || visit.getKeywords().isEmpty()){
            TextView keyword = new TextView(getApplicationContext());
            keyword.setText(getResources().getString(R.string.no_keywords));
            keyword.setPadding(8, 4, 8, 4);
            keyword.setTextColor(getResources().getColor(R.color.disabled_edittext));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4, 4, 4, 4);
            keyword.setLayoutParams(layoutParams);
            keywordsFlexbox.addView(keyword);
        }
    }

    public void setCommonLayout(){
        clientTextView.setText(visit.getClient());
        String startDateAndTime = visit.getDate() + " - " + visit.getStartTime();
        startTimeTextView.setText(startDateAndTime);

        imagesImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageGallery = new Intent(DetailVisitActivity.this, ImageGalleryActivity.class);
                imageGallery.putExtra(Constants.VISIT_DATA, visit);
                startActivity(imageGallery);
            }
        });

        if(visit.getNotes() == null) {
            String visitReason = getResources().getString(R.string.visit_reason) + " " + visit.getReason() + '\n';
            notesEditText.setText(visitReason);
        } else {
            notesEditText.setText(visit.getNotes());
        }

        if(visit.getKeywords() != null && !visit.getKeywords().isEmpty()){
            //lista de keyword está separada por "#@#" no bd. aqui eu transformo em uma lista
            mKeywordsList = Arrays.asList(visit.getKeywords().split(Constants.SEPARATOR));
            keywordsList = visit.getKeywords();

            //criado o textview e preenche as keywords salvas da visita
            for (String newKeyword : mKeywordsList) {
                TextView keyword = new TextView(getApplicationContext());
                keyword.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                keyword.setPadding(8, 4, 8, 4);
                keyword.setTextColor(getResources().getColor(R.color.white));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(4, 4, 4, 4);
                keyword.setLayoutParams(layoutParams);
                keyword.setText(newKeyword);
                keywordsFlexbox.addView(keyword);
            }
        }
    }

    public void setCurrentTime(){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        visit.setClosingTime(String.format("%02d:%02d", hour, minute));
    }

    @Override
    public void onBackPressed() {
        if(visit.getSituation() != Visit.SITUATION_COMPLETED){
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.save_changes_dialog))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setVisitDataAndSave(51);
                            Toast.makeText(DetailVisitActivity.this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }else{
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_visit, menu);
        if(visit.getSituation() == Visit.SITUATION_COMPLETED){
            menu.findItem(R.id.menu_item_save_visit).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.menu_item_save_visit:
                setVisitDataAndSave(50);
                break;

            case R.id.menu_item_delete_visit:
                new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.delete_alertdialog_message))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mVisitsHelper.deleteVisit(visit);
                                DetailVisitActivity.this.finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();

                break;

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    public void setVisitDataAndSave(final int requestFrom){
        //set visit data
        visit.setNotes(notesEditText.getText().toString());
        visit.setKeywords(keywordsList);

        //create loader
        progressBar.setVisibility(ProgressBar.VISIBLE);

        //save visit in bd
        mVisitsHelper.addVisit(visit, new FirebaseVisitsCallback() {
            @Override
            public void onVisitsLoadCallback(ArrayList<Visit> visits) {}

            @Override
            public void onVisitAddCallback(Visit visit) {
                //remove loader
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                if(requestFrom == 50) { //requestFrom SAVE button
                    //show toast
                    Toast.makeText(DetailVisitActivity.this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
                } else { //requestFrom CLOSEVisit
                    finish();
                }
            }


        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
