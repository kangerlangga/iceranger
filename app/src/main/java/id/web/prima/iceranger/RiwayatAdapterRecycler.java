package id.web.prima.iceranger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RiwayatAdapterRecycler extends RecyclerView.Adapter<RiwayatAdapterRecycler.ViewHolder>{
    Context context; ArrayList<ModelRiwayat> arrayList;
    public RiwayatAdapterRecycler(Context context, ArrayList<ModelRiwayat> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public RiwayatAdapterRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_riwayat_item,parent,false);
        return new RiwayatAdapterRecycler.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RiwayatAdapterRecycler.ViewHolder holder, int position) {
        holder.waktu.setText("Terkirim : "+ambilWaktu(arrayList.get(position).getCreated_at().toDate()));
        holder.pemesan.setText("Pemesan : "+arrayList.get(position).getToko()+" ("+arrayList.get(position).getPemesan()+")");
        holder.driver.setText("Driver : "+arrayList.get(position).getDriver());
        holder.jumlah.setText("Jumlah Pesanan : "+arrayList.get(position).getJumlah_pesanan().toString());
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            waktu = itemView.findViewById(R.id.waktu_riwayat);
            pemesan = itemView.findViewById(R.id.pemesan_riwayat);
            driver = itemView.findViewById(R.id.driver_riwayat);
            jumlah = itemView.findViewById(R.id.jumlah_riwayat);
        }
    }
}
