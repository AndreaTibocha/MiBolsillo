package com.example.mibolsillo.historial; // Ajusta si el paquete cambia

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mibolsillo.Movimiento;
import com.example.mibolsillo.databinding.ItemMovimientoBinding;
import java.util.ArrayList;

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder> {

    private ArrayList<Movimiento> listaMovimientos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Movimiento movimiento);
    }

    public MovimientoAdapter(ArrayList<Movimiento> listaMovimientos, OnItemClickListener listener) {
        this.listaMovimientos = listaMovimientos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMovimientoBinding binding = ItemMovimientoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MovimientoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimientoViewHolder holder, int position) {
        Movimiento mov = listaMovimientos.get(position);
        holder.binding.txtDescripcion.setText(mov.getDescripcion());
        holder.binding.txtTipo.setText(mov.getTipo());
        holder.binding.txtValor.setText("$" + mov.getValor());

        // Lógica de colores pastel y flechas según el tipo
        if (mov.getTipo().equalsIgnoreCase("Ingreso")) {
            holder.binding.imgIndicador.setImageResource(android.R.drawable.arrow_up_float);
            holder.binding.txtValor.setTextColor(Color.parseColor("#38A169")); // Verde
        } else {
            holder.binding.imgIndicador.setImageResource(android.R.drawable.arrow_down_float);
            holder.binding.txtValor.setTextColor(Color.parseColor("#E53E3E")); // Rojo
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(mov));
    }

    @Override
    public int getItemCount() {
        return listaMovimientos.size();
    }

    public static class MovimientoViewHolder extends RecyclerView.ViewHolder {
        ItemMovimientoBinding binding;
        public MovimientoViewHolder(@NonNull ItemMovimientoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}