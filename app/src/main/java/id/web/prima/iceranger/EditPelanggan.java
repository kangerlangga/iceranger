package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditPelanggan extends AppCompatActivity {
    ProgressDialog progressDialog;
    EditText namatoko,pemilik,telepon,alamat;
    Button gantiLokasi,edit,hapus,batal;String lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pelanggan);
        namatoko = findViewById(R.id.namatoko_editpelanggan);
        pemilik = findViewById(R.id.namapemilik_editpelanggan);
        telepon = findViewById(R.id.tel_editpelanggan);
        alamat = findViewById(R.id.alamattoko_editpelanggan);
        gantiLokasi = findViewById(R.id.btnlokasi_editpelanggan);
        edit = findViewById(R.id.btn_editpelanggan);
        hapus = findViewById(R.id.btnhapus_editpelanggan);
        hapus.setVisibility(View.GONE);
        batal = findViewById(R.id.batal_editpelanggan);
        namatoko.setFocusable(false);
        sesiLogin user1 = new sesiLogin();
        String id = getIntent().getStringExtra("editID").toString();
        resetData(id);
        cekAkun();
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetData(id);
            }
        });
        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EditPelanggan.this)
                        .setTitle("Ice Ranger")
                        .setMessage("Apakah anda yakin ingin menghapus Pelanggan? Data pelanggan akan dihapus secara permanen.")
                        .setPositiveButton("TIDAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("HAPUS PELANGGAN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DisableKomponen();
                                cekHapus(id);
                            }
                        })
                        .show();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableKomponen();
                String n,p,t,a,i,eu;
                n = namatoko.getText().toString().trim();
                p = pemilik.getText().toString().trim();
                t = telepon.getText().toString().trim();
                a = alamat.getText().toString().trim();
                i = getIntent().getStringExtra("editID").toString().trim();
                eu = user1.getEmail().toString().trim();
                if (n.equals("") || p.equals("") || t.equals("") || a.equals("") || i.equals("") || eu.equals("")){
                    Toast.makeText(EditPelanggan.this, "Seluruh kolom harus di isi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else{
                    editPelanggan(i,n,p,t,a,eu);
                }
            }
        });
        gantiLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableKomponen();
                String n,p,t,a,i,eu,lat_g,lng_g;
                n = namatoko.getText().toString().trim();
                p = pemilik.getText().toString().trim();
                t = telepon.getText().toString().trim();
                a = alamat.getText().toString().trim();
                i = getIntent().getStringExtra("editID").toString().trim();
                eu = user1.getEmail().toString().trim();
                lat_g = lat;
                lng_g = lng;
                if (n.equals("") || p.equals("") || t.equals("") || a.equals("") || i.equals("") || eu.equals("") || lat_g.equals("") || lng_g.equals("")){
                    Toast.makeText(EditPelanggan.this, "Seluruh kolom harus di isi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else{
                    new AlertDialog.Builder(EditPelanggan.this)
                            .setTitle("Ice Ranger")
                            .setMessage("Anda akan membuka peta, pastikan koneksi internet anda stabil. Aplikasi akan otomatis keluar ketika internet anda tidak stabil untuk menghindari adanya kesalahan dan kebocoran data.")
                            .setPositiveButton("BUKA PETA", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    kirimData(i,n,p,t,a,eu,lat_g,lng_g);
                                }
                            })
                            .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    EnableKomponen();
                                }
                            })
                            .show();
                }
            }
        });
    }
    private void cekAkun(){
        sesiLogin user1 = new sesiLogin();
        if (user1.getJabatan().equals("Super Admin") || user1.getJabatan().equals("Admin Driver")){
            hapus.setVisibility(View.VISIBLE);
        }else if (user1.getJabatan().equals("Driver")){
            hapus.setVisibility(View.GONE);
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finishAffinity();
        startActivity(new Intent(EditPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
    }
    private void resetData(String id){
        cekInternet();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sinkronisasi data pelanggan...");
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pelanggan").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        namatoko.setText(document.getString("namatoko"));
                        pemilik.setText(document.getString("pemilik"));
                        telepon.setText(document.getString("telepon"));
                        alamat.setText(document.getString("alamat"));
                        lat = document.getString("latitude");
                        lng = document.getString("longitude");
                        EnableKomponen();
                    } else {
                        Toast.makeText(EditPelanggan.this, "Detail pelanggan tidak ditemukan!",
                                Toast.LENGTH_LONG).show();
                        finishAffinity();
                        startActivity(new Intent(EditPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
                    }
                } else {
                    Toast.makeText(EditPelanggan.this, "Detail pelanggan tidak ditemukan!",
                            Toast.LENGTH_LONG).show();
                    finishAffinity();
                    startActivity(new Intent(EditPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditPelanggan.this, "Detail pelanggan tidak ditemukan!",
                        Toast.LENGTH_LONG).show();
                finishAffinity();
                startActivity(new Intent(EditPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
            }
        });
    }
    private void cekHapus(String id_pelanggan){
        cekInternet();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pesanan").whereEqualTo("id_toko",id_pelanggan).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().getDocuments().size()>0) {
                    Toast.makeText(EditPelanggan.this, "Sedang ada pesanan!",
                            Toast.LENGTH_LONG).show();
                }else if (task.getResult().getDocuments().size()<1){
                    hapusPelanggan(id_pelanggan);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hapusPelanggan(id_pelanggan);
            }
        });
    }
    private void hapusPelanggan(String id){
        progressDialog = new ProgressDialog(EditPelanggan.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menghapus data...");
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pelanggan").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditPelanggan.this, "Data pelanggan berhasil dihapus!",
                        Toast.LENGTH_LONG).show();
                finishAffinity();
                startActivity(new Intent(EditPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditPelanggan.this, "Data pelanggan gagal dihapus!",
                        Toast.LENGTH_LONG).show();
                finishAffinity();
                startActivity(new Intent(EditPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
            }
        });
    }
    private void editPelanggan(String id,String n,String p,String t,String a,String eu){
        progressDialog = new ProgressDialog(EditPelanggan.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang memperbarui data...");
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> pelanggan = new HashMap<>();
        pelanggan.put("namatoko", Objects.requireNonNull(n));
        pelanggan.put("pemilik", Objects.requireNonNull(p));
        pelanggan.put("telepon", Objects.requireNonNull(t));
        pelanggan.put("alamat", Objects.requireNonNull(a));
        pelanggan.put("updated_at", FieldValue.serverTimestamp());
        pelanggan.put("email_update", Objects.requireNonNull(eu));
        db.collection("pelanggan").document(id).update(pelanggan).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditPelanggan.this, "Data pelanggan berhasil diperbarui!",
                        Toast.LENGTH_LONG).show();
                finishAffinity();
                startActivity(new Intent(EditPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditPelanggan.this, "Data pelanggan gagal diperbarui!\nPastikan koneksi internet anda stabil.",
                        Toast.LENGTH_LONG).show();
                finishAffinity();
                startActivity(new Intent(EditPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
            }
        });
    }
    private void kirimData(String id,String n,String p,String t,String a,String eu,String la,String ln) {
        Intent kirim = new Intent(EditPelanggan.this, PetaPelanggan.class);
        kirim.putExtra("sumber", "Edit");
        kirim.putExtra("namatoko", n);
        kirim.putExtra("pemilik", p);
        kirim.putExtra("telepon", t);
        kirim.putExtra("alamat", a);
        kirim.putExtra("lat_pelanggan", la);
        kirim.putExtra("lng_pelanggan", ln);
        kirim.putExtra("email_update", eu);
        kirim.putExtra("editID",id);
        startActivity(kirim);
        finish();
    }
    private void EnableKomponen(){
        gantiLokasi.setEnabled(true);
        edit.setEnabled(true);
        hapus.setEnabled(true);
        batal.setEnabled(true);
        pemilik.setFocusableInTouchMode(true);
        alamat.setFocusableInTouchMode(true);
        telepon.setFocusableInTouchMode(true);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void DisableKomponen(){
        gantiLokasi.setEnabled(false);
        edit.setEnabled(false);
        hapus.setEnabled(false);
        batal.setEnabled(false);
        pemilik.setFocusable(false);
        alamat.setFocusable(false);
        telepon.setFocusable(false);
    }
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void cekInternet(){
        if (!koneksiTerhubung()){
            finishAffinity();
            startActivity(new Intent(EditPelanggan.this, NoInternetActivity.class));
        }
    }
}