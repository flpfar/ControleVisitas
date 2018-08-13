package br.edu.ifspsaocarlos.sdm.controlevisitas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.ifspsaocarlos.sdm.controlevisitas.R;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Visit;

public class VisitsAdapter extends RecyclerView.Adapter<VisitsAdapter.VisitsViewHolder> {
    private Context context;
    private ArrayList<Visit> visits;

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
        holder.clientName.setText(visits.get(position).getClient());
    }

    @Override
    public int getItemCount() {
        return visits.size();
    }

    public class VisitsViewHolder extends RecyclerView.ViewHolder {
        TextView clientName;

        public VisitsViewHolder (View itemView){
            super(itemView);

            clientName = itemView.findViewById(R.id.rv_dayvisit_tvclient);
        }
    }
}
