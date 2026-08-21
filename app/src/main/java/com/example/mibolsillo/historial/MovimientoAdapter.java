package com.example.mibolsillo.historial;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mibolsillo.Movimiento;
import com.example.mibolsillo.R;
import com.example.mibolsillo.registro.FormularioActivity;

import java.util.List;

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder> {

    private final List<Movimiento> listaMovimientos;

    public MovimientoAdapter(List<Movimiento> listaMovimientos) {
        this.listaMovimientos = listaMovimientos;
    }

    @NonNull
    @Override
    public MovimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento, parent, false);
        return new MovimientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimientoViewHolder holder, int position) {
        Movimiento mov = listaMovimientos.get(position);

        holder.tvTitulo.setText(mov.getTitulo());
        holder.tvTipo.setText(mov.getTipo());
        holder.tvMonto.setText("$ " + mov.getMonto());

        if ("INGRESO".equalsIgnoreCase(mov.getTipo())) {
            holder.tvMonto.setTextColor(Color.parseColor("#4CAF50"));
            holder.ivIcono.setImageResource(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ?
                    android.R.drawable.arrow_up_float : android.R.drawable.ic_menu_compass);
        } else {
            holder.tvMonto.setTextColor(Color.parseColor("#F44336"));
            holder.ivIcono.setImageResource(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ?
                    android.R.drawable.arrow_down_float : android.R.drawable.ic_menu_compass);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FormularioActivity.class);
            intent.putExtra("id", mov.getId());
            intent.putExtra("titulo", mov.getTitulo());
            intent.putExtra("monto", mov.getMonto());
            intent.putExtra("tipo", mov.getTipo());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaMovimientos.size();
    }

    public static class MovimientoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvTipo, tvMonto;
        ImageView ivIcono;

        public MovimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloMovimiento);
            tvTipo = itemView.findViewById(R.id.tvTipoMovimiento);
            tvMonto = itemView.findViewById(R.id.tvMontoMovimiento);
            ivIcono = itemView.findViewById(R.id.ivIconoTipo);
        }
    }
}