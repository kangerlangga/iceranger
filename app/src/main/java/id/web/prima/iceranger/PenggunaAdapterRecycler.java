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

public class PenggunaAdapterRecycler extends RecyclerView.Adapter<PenggunaAdapterRecycler.ViewHolder> {
    Context context; ArrayList<ModelPengguna> arrayList;
    public PenggunaAdapterRecycler(Context context, ArrayList<ModelPengguna> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_pengguna_item,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nama.setText(arrayList.get(position).getNama());
        holder.jabatan.setText(arrayList.get(position).getJabatan());
        holder.email.setText(arrayList.get(position).getEmail());
        holder.telepon.setText(arrayList.get(position).getTelepon());
        holder.alamat.setText(arrayList.get(position).getAlamat());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.edit.getContext(), EditPengguna.class);
                intent.putExtra("editNama",arrayList.get(position).getNama());
                intent.putExtra("editJabatan",arrayList.get(position).getJabatan());
                intent.putExtra("editEmail",arrayList.get(position).getEmail());
                intent.putExtra("editTel",arrayList.get(position).getTelepon());
                intent.putExtra("editAlamat",arrayList.get(position).getAlamat());
                intent.putExtra("editId",arrayList.get(position).getId_Doc());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                holder.edit.getContext().startActivity(intent);
                PenggunaActivity pengguna = new PenggunaActivity();
                pengguna.finish();
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, jabatan, email, telepon, alamat;
        Button edit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.nama_listPenggunaItem);
            jabatan = itemView.findViewById(R.id.jabatan_listPenggunaItem);
            email = itemView.findViewById(R.id.email_listPenggunaItem);
            telepon = itemView.findViewById(R.id.tel_listPenggunaItem);
            alamat = itemView.findViewById(R.id.alamat_listPenggunaItem);
            edit = itemView.findViewById(R.id.edit_listPenggunaItem);
        }
    }
}