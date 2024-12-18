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

import java.util.ArrayList;

public class PelangganAdapterRecycler extends RecyclerView.Adapter<PelangganAdapterRecycler.ViewHolder> {
    Context context; ArrayList<ModelPelanggan> arrayList;
    public PelangganAdapterRecycler(Context context, ArrayList<ModelPelanggan> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_pelanggan_item,parent,false);
        return new PelangganAdapterRecycler.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.namatoko.setText(arrayList.get(position).getNamatoko());
        holder.pemilik.setText(arrayList.get(position).getPemilik());
        holder.telepon.setText(arrayList.get(position).getTelepon());
        holder.alamat.setText(arrayList.get(position).getAlamat());
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.detail.getContext(), DetailPelanggan.class);
                intent.putExtra("detailNamaToko",arrayList.get(position).getNamatoko());
                intent.putExtra("detailPemilik",arrayList.get(position).getPemilik());
                intent.putExtra("detailTelepon",arrayList.get(position).getTelepon());
                intent.putExtra("detailAlamat",arrayList.get(position).getAlamat());
                intent.putExtra("detailLatitude",arrayList.get(position).getLatitude());
                intent.putExtra("detailLongitude",arrayList.get(position).getLongitude());
                intent.putExtra("detailId",arrayList.get(position).getId_Pelanggan());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                holder.detail.getContext().startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView namatoko, pemilik, telepon, alamat;
        Button detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namatoko = itemView.findViewById(R.id.namatoko_listPelangganItem);
            pemilik = itemView.findViewById(R.id.pemilik_listPelangganItem);
            telepon = itemView.findViewById(R.id.tel_listPelangganItem);
            alamat = itemView.findViewById(R.id.alamat_listPelangganItem);
            detail = itemView.findViewById(R.id.detail_listPelangganItem);
        }
    }
}