package com.cibergoliath.mytxis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SolicitudAdapter
        extends RecyclerView.Adapter<SolicitudAdapter.SolicitudViewHolder> {

    private final List<ViajeResponse> listaViajes;
    private final OnSolicitudClickListener listener;

    public interface OnSolicitudClickListener {

        void onSolicitudClick(ViajeResponse viaje);

    }

    public SolicitudAdapter(
            List<ViajeResponse> listaViajes,
            OnSolicitudClickListener listener) {

        this.listaViajes = listaViajes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_solicitud,
                        parent,
                        false
                );

        return new SolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull SolicitudViewHolder holder,
            int position) {

        ViajeResponse viaje =
                listaViajes.get(position);

        holder.txtItemCliente.setText(
                viaje.getNombre()
        );

        holder.txtItemSolicitud.setText(
                "Solicitud #" + viaje.getId()
        );

        holder.itemView.setOnClickListener(v ->
                listener.onSolicitudClick(viaje)
        );
    }

    @Override
    public int getItemCount() {

        return listaViajes.size();

    }

    static class SolicitudViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtItemCliente;
        TextView txtItemSolicitud;

        public SolicitudViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtItemCliente =
                    itemView.findViewById(
                            R.id.txtItemCliente
                    );

            txtItemSolicitud =
                    itemView.findViewById(
                            R.id.txtItemSolicitud
                    );
        }
    }
}