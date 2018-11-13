package br.edu.ifspsaocarlos.sdm.controlevisitas.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.ifspsaocarlos.sdm.controlevisitas.AddClientActivity;
import br.edu.ifspsaocarlos.sdm.controlevisitas.R;
import br.edu.ifspsaocarlos.sdm.controlevisitas.Utils.Constants;
import br.edu.ifspsaocarlos.sdm.controlevisitas.model.Client;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClientsViewHolder> {

    private Context context;
    private ArrayList<Client> mClients;

    public ClientsAdapter(Context context, ArrayList<Client> mClients) {
        this.context = context;
        this.mClients = mClients;
    }

    @NonNull
    @Override
    public ClientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.rvclient_item, parent, false);
        return new ClientsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientsViewHolder holder, int position) {
        Client client = mClients.get(position);
        holder.tvClient.setText(client.getName().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return mClients.size();
    }

    public class ClientsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvClient;

        public ClientsViewHolder(View itemView) {
            super(itemView);

            tvClient = itemView.findViewById(R.id.rv_client_tvitem);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Client clickedClient = mClients.get(position);
                Intent clientIntent = new Intent(context, AddClientActivity.class);
                clientIntent.putExtra(Constants.CLIENT_DATA, clickedClient);
                context.startActivity(clientIntent);
            }
        }

    }
}
