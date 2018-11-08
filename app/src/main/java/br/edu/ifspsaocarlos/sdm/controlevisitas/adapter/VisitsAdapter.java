package br.edu.ifspsaocarlos.sdm.controlevisitas.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import br.edu.ifspsaocarlos.sdm.controlevisitas.DetailVisitActivity;
import br.edu.ifspsaocarlos.sdm.controlevisitas.R;
import br.edu.ifspsaocarlos.sdm.controlevisitas.StartVisitActivity;
import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsCallback;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.FirebaseVisitsHelper;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class VisitsAdapter extends RecyclerView.Adapter<VisitsAdapter.VisitsViewHolder> {
    private Context context;
    private ArrayList<Visit> visits;
    private FirebaseVisitsHelper mVisitsHelper = new FirebaseVisitsHelper(FirebaseDatabase.getInstance().getReference());

    public VisitsAdapter(Context context, ArrayList<Visit> visits){
        this.context = context;
        this.visits = visits;
    }

    @NonNull
    @Override
    public VisitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.rvdayvisit_item, parent, false);
        return new VisitsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitsViewHolder holder, int position) {
        Visit visit = visits.get(position);
        holder.clientName.setText(visit.getClient());
        holder.visitDate.setText(visit.getDateAndTime());
        String status = "";
        switch (visit.getSituation()){
            case Visit.SITUATION_COMPLETED:
                status = context.getResources().getString(R.string.status_completed);
                holder.visitStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_completed));
                break;
            case Visit.SITUATION_INPROGRESS:
                status = context.getResources().getString(R.string.status_inprogress);
                holder.visitStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_inprogress));
                break;
            case Visit.SITUATION_SCHEDULED:
                status = context.getResources().getString(R.string.status_scheduled);
                holder.visitStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_scheduled));
                break;
            default:
                break;
        }
        holder.visitStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        return visits.size();
    }

    public class VisitsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView clientName;
        TextView visitDate;
        TextView visitStatus;
        ImageView visitStatusIcon;

        public VisitsViewHolder (View itemView){
            super(itemView);

            clientName = itemView.findViewById(R.id.rv_dayvisit_tvclient);
            visitDate = itemView.findViewById(R.id.rv_dayvisit_tvdate);
            visitStatus = itemView.findViewById(R.id.rv_dayvisit_tvstatus);
            visitStatusIcon = itemView.findViewById(R.id.rv_dayvisit_ivstatus);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                final Visit clickedVisit = visits.get(position);
                if(clickedVisit.getSituation() != Visit.SITUATION_SCHEDULED) {
                    Intent detailVisitActivityIntent = new Intent(context, DetailVisitActivity.class);
                    detailVisitActivityIntent.putExtra(Constants.VISIT_DATA, clickedVisit);
                    context.startActivity(detailVisitActivityIntent);
                }else{
                    clickedVisit.setSituation(Visit.SITUATION_INPROGRESS);
                    Intent startVisitActivityIntent = new Intent(context, StartVisitActivity.class);
                    startVisitActivityIntent.putExtra(Constants.VISIT_DATA, clickedVisit);
                    context.startActivity(startVisitActivityIntent);
                }
            }
        }
    }
}
