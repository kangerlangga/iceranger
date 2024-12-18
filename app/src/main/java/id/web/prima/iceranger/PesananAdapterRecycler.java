package id.web.prima.iceranger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PesananAdapterRecycler extends RecyclerView.Adapter<PesananAdapterRecycler.ViewHolder>{
    Context context; ArrayList<ModelPesanan> arrayList;
    public PesananAdapterRecycler(Context context, ArrayList<ModelPesanan> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public PesananAdapterRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_pesanan_item,parent,false);
        return new PesananAdapterRecycler.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PesananAdapterRecycler.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.status.setText(arrayList.get(position).getStatus_pesanan());
        holder.pemesan.setText("Pemesan : "+arrayList.get(position).getTujuan_toko()+" ("+arrayList.get(position).getPemilik_toko()+")");
        holder.driver.setText("Driver : "+arrayList.get(position).getNama_driver());
        holder.jumlah.setText("Jumlah Pesanan : "+arrayList.get(position).getJumlah_pesanan().toString());
        holder.lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.lihat.getContext(), LihatPesanan.class);
                intent.putExtra("idPesanan",arrayList.get(position).getId_Doc());
                intent.putExtra("idDriver",arrayList.get(position).getId_driver());
                intent.putExtra("idPemesan",arrayList.get(position).getId_toko());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                holder.lihat.getContext().startActivity(intent);
                PesananActivity pesanan = new PesananActivity();
                pesanan.finishAffinity();
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    private String ambilWaktu(Date date) {
        return new SimpleDateFormat("dd MMMM yyyy - hh:mm:ss a", Locale.getDefault()).format(date);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView status, pemesan, driver, jumlah;
        Button lihat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.status_listPesanan);
            pemesan = itemView.findViewById(R.id.tujuan_listPesanan);
            driver = itemView.findViewById(R.id.driver_listPesanan);
            jumlah = itemView.findViewById(R.id.jumlah_listPesanan);
            lihat = itemView.findViewById(R.id.detail_listPesanan);
        }
    }
}
