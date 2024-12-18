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

public class PengirimanAdapterRecycler extends RecyclerView.Adapter<PengirimanAdapterRecycler.ViewHolder>{
    Context context; ArrayList<ModelPengiriman> arrayList;
    public PengirimanAdapterRecycler(Context context, ArrayList<ModelPengiriman> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_pengiriman_item,parent,false);
        return new PengirimanAdapterRecycler.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.waktu.setText("Dikirim Pada : "+ambilWaktu(arrayList.get(position).getCreated_at().toDate()));
        holder.pemesan.setText("Pemesan : "+arrayList.get(position).getToko()+" ("+arrayList.get(position).getPemesan()+")");
        holder.driver.setText("Driver : "+arrayList.get(position).getDriver());
        holder.jumlah.setText("Jumlah Pesanan : "+arrayList.get(position).getJumlah_pesanan().toString());
        holder.lacak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.lacak.getContext(), LacakPengiriman.class);
                intent.putExtra("idPengiriman",arrayList.get(position).getId_doc());
                intent.putExtra("idDriver",arrayList.get(position).getId_driver());
                intent.putExtra("idPemesan",arrayList.get(position).getId_toko());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                holder.lacak.getContext().startActivity(intent);
                PengirimanActivity pengiriman = new PengirimanActivity();
                pengiriman.finishAffinity();
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    private String ambilWaktu(Date date) {
        return new SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView waktu, pemesan, driver, jumlah;
        Button lacak;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            waktu = itemView.findViewById(R.id.waktu_tracking);
            pemesan = itemView.findViewById(R.id.pemesan_tracking);
            driver = itemView.findViewById(R.id.driver_tracking);
            jumlah = itemView.findViewById(R.id.jumlah_tracking);
            lacak = itemView.findViewById(R.id.btn_tracking);
        }
    }
}
