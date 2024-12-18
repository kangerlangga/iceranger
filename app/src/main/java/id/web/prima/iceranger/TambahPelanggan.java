package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class TambahPelanggan extends AppCompatActivity {
    EditText namatoko,pemilik,telepon,alamat;
    Button pilihLokasi,batal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pelanggan);
        namatoko = findViewById(R.id.namatoko_addpelanggan);
        pemilik = findViewById(R.id.namapemilik_addpelanggan);
        telepon = findViewById(R.id.tel_addpelanggan);
        alamat = findViewById(R.id.alamattoko_addpelanggan);
        pilihLokasi = findViewById(R.id.btnlokasi_addpelanggan);
        batal = findViewById(R.id.batal_addpelanggan);
        sesiLogin user1 = new sesiLogin();
        reset();
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
        pilihLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisableKomponen();
                String n,p,t,a,e,nc;
                n = namatoko.getText().toString().trim();
                p = pemilik.getText().toString().trim();
                t = telepon.getText().toString().trim();
                a = alamat.getText().toString().trim();
                e = user1.getEmail().toString().trim();
                nc = user1.getNama().toString().trim();
                if (n.equals("") || p.equals("") || t.equals("") || a.equals("") || e.equals("")){
                    Toast.makeText(TambahPelanggan.this, "Seluruh kolom harus di isi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else{
                    cekToko(n,p,t,a,e,nc);
                }
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finishAffinity();
        startActivity(new Intent(TambahPelanggan.this, DashboardActivity.class).putExtra("to","menu_beranda"));
    }
    private void reset(){
        cekInternet();
        EnableKomponen();
        namatoko.setText("");
        pemilik.setText("");
        telepon.setText("");
        alamat.setText("");
    }
    private void EnableKomponen(){
        pilihLokasi.setEnabled(true);
        namatoko.setFocusableInTouchMode(true);
        pemilik.setFocusableInTouchMode(true);
        alamat.setFocusableInTouchMode(true);
        telepon.setFocusableInTouchMode(true);
    }
    private void DisableKomponen(){
        pilihLokasi.setEnabled(false);
        namatoko.setFocusable(false);
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
            startActivity(new Intent(TambahPelanggan.this, NoInternetActivity.class));
        }
    }
    private void cekToko(String n,String p,String t,String a,String ec,String nc){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pelanggan").whereEqualTo("namatoko",n).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().getDocuments().size()>0) {
                    Toast.makeText(TambahPelanggan.this, "Nama toko sudah dipakai!\nTambahkan keterangan khusus agar tidak sama dengan toko lain",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else if (task.getResult().getDocuments().size()<1){
                    new AlertDialog.Builder(TambahPelanggan.this)
                            .setTitle("Ice Ranger")
                            .setMessage("Anda akan membuka peta, pastikan koneksi internet anda stabil. Aplikasi akan otomatis keluar ketika internet anda tidak stabil untuk menghindari adanya kesalahan dan kebocoran data.")
                            .setPositiveButton("BUKA PETA", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    kirimData(n,p,t,a,ec,nc);
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new AlertDialog.Builder(TambahPelanggan.this)
                        .setTitle("Ice Ranger")
                        .setMessage("Anda akan membuka peta, pastikan koneksi internet anda stabil. Aplikasi akan otomatis keluar ketika internet anda tidak stabil untuk menghindari adanya kesalahan dan kebocoran data.")
                        .setPositiveButton("BUKA PETA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                kirimData(n,p,t,a,ec,nc);
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
        });
    }
    private void kirimData(String n,String p,String t,String a,String ec,String nc){
        Intent kirim = new Intent(TambahPelanggan.this, PetaPelanggan.class);
        kirim.putExtra("sumber","Tambah");
        kirim.putExtra("namatoko",n);
        kirim.putExtra("pemilik",p);
        kirim.putExtra("telepon",t);
        kirim.putExtra("alamat",a);
        kirim.putExtra("email_create",ec);
        kirim.putExtra("nama_create",nc);
        startActivity(kirim);
        finish();
    }
}