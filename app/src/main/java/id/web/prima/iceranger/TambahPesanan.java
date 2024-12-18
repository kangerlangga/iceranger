package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TambahPesanan extends AppCompatActivity {
    Spinner pelangganspin,driverspin; EditText jml; Button tambah,batal;
    ProgressDialog progressDialog; FirebaseFirestore db;
    ArrayAdapter<String> adapterpelanggan; ArrayAdapter<String> adapterdriver;
    ArrayList<String> spelanggan = new ArrayList<>(); ArrayList<String> sdriver = new ArrayList<>();
    ArrayList<String> edriver = new ArrayList<>();
    String pilihandriver = "Pilih Driver", pilihanpelanggan = "Pilih Pelanggan";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pesanan);
        pelangganspin = findViewById(R.id.spemesan_addpesanan);
        driverspin = findViewById(R.id.sdriver_addpesanan);
        jml = findViewById(R.id.jml_addpesanan);
        tambah = findViewById(R.id.btn_addpesanan);
        batal = findViewById(R.id.batal_addpesanan);
        ambilData();
        reset();

        spelanggan.add("Pilih Pelanggan");
        adapterpelanggan = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spelanggan);
        adapterpelanggan.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        pelangganspin.setAdapter(adapterpelanggan);

        sdriver.add("Pilih Driver");
        edriver.add("Tidak ada email");
        adapterdriver = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sdriver);
        adapterdriver.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        driverspin.setAdapter(adapterdriver);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
        pelangganspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pilihanpelanggan = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
            }
        });
        driverspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int i = parent.getSelectedItemPosition();
                if (i > 0){
                    pilihandriver = edriver.get(i).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
            }
        });
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisableKomponen();
                String d,p,j;
                d = pilihandriver.trim();
                p = pilihanpelanggan.trim();
                j = jml.getText().toString().trim();
                if (d.equals("Pilih Driver") || p.equals("Pilih Pelanggan") || j.equals("")
                        || pelangganspin.getSelectedItemPosition() == 0 || driverspin.getSelectedItemPosition() == 0){
                    Toast.makeText(TambahPesanan.this, "Seluruh kolom harus di isi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else{
                    int jumlah = Integer.parseInt(j);
                    sinkDriver(d,p,jumlah);
                }
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(TambahPesanan.this, DashboardActivity.class).putExtra("to","menu_beranda"));
    }
    private void reset(){
        pelangganspin.setSelection(0);
        driverspin.setSelection(0);
        jml.setText("");
        cekInternet();
    }
    private void EnableKomponen(){
        pelangganspin.setEnabled(true);
        driverspin.setEnabled(true);
        jml.setFocusableInTouchMode(true);
        tambah.setEnabled(true);
        batal.setEnabled(true);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void DisableKomponen(){
        pelangganspin.setEnabled(false);
        driverspin.setEnabled(false);
        jml.setFocusable(false);
        tambah.setEnabled(false);
        batal.setEnabled(false);
    }
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void cekInternet(){
        if (!koneksiTerhubung()){
            finishAffinity();
            startActivity(new Intent(TambahPesanan.this, NoInternetActivity.class));
        }
    }
    private void ambilDataPelanggan(){
        db = FirebaseFirestore.getInstance();
        db.collection("pelanggan").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size()>0){
                    for (DocumentSnapshot doc : queryDocumentSnapshots){
                        spelanggan.add(doc.getString("namatoko"));
                    }
                    adapterpelanggan.notifyDataSetChanged();
                    EnableKomponen();
                }else{
                    Toast.makeText(TambahPesanan.this, "Data pelanggan tidak tersedia!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TambahPesanan.this, "Data pelanggan tidak tersedia!",
                        Toast.LENGTH_LONG).show();
                EnableKomponen();
            }
        });
    }
    private void ambilDataDriver(){
        db = FirebaseFirestore.getInstance();
        db.collection("pengguna").whereEqualTo("jabatan","Driver")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size()>0){
                    for (DocumentSnapshot doc : queryDocumentSnapshots){
                        sdriver.add(doc.getString("nama"));
                        edriver.add(doc.getString("email"));
                    }
                    adapterdriver.notifyDataSetChanged();
                    EnableKomponen();
                }else{
                    Toast.makeText(TambahPesanan.this, "Data driver tidak tersedia!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TambahPesanan.this, "Data driver tidak tersedia!",
                        Toast.LENGTH_LONG).show();
                EnableKomponen();
            }
        });
    }
    private void ambilData(){
        progressDialog = new ProgressDialog(TambahPesanan.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang mengambil data...");
        progressDialog.show();
        ambilDataPelanggan();
        ambilDataDriver();
    }
    private void sinkDriver(String d,String p,Integer j){
        progressDialog = new ProgressDialog(TambahPesanan.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sinkronisasi data...");
        progressDialog.show();
        db = FirebaseFirestore.getInstance();
        db.collection("pengguna").whereEqualTo("jabatan","Driver")
                .whereEqualTo("email",d).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    String ed,nd,id_d;
                    ed = task.getResult().getDocuments().get(0).getString("email");
                    nd = task.getResult().getDocuments().get(0).getString("nama");
                    id_d = task.getResult().getDocuments().get(0).getId();
                    sinkPelanggan(ed,nd,id_d,p,j);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TambahPesanan.this, "Sinkronisasi Gagal!\nData driver tidak ditemukan",
                        Toast.LENGTH_LONG).show();
                EnableKomponen();
            }
        });
    }
    private void sinkPelanggan(String ed,String nd,String id_d,String p,Integer j){
        db = FirebaseFirestore.getInstance();
        db.collection("pelanggan").whereEqualTo("namatoko",p)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            String np,pt,id_p;
                            np = task.getResult().getDocuments().get(0).getString("namatoko");
                            pt = task.getResult().getDocuments().get(0).getString("pemilik");
                            id_p = task.getResult().getDocuments().get(0).getId();
                            tambahPesanan(ed,nd,id_d,np,pt,id_p,j);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TambahPesanan.this, "Sinkronisasi Gagal!\nData pelanggan tidak ditemukan",
                                Toast.LENGTH_LONG).show();
                        EnableKomponen();
                    }
                });
    }
    private void tambahPesanan(String ed,String nd,String id_d,String np,String pt,String id_p,Integer j){
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = new ProgressDialog(TambahPesanan.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menambahkan pesanan...");
        progressDialog.show();
        sesiLogin akun = new sesiLogin();
        db = FirebaseFirestore.getInstance();
        Map<String, Object> pesanan = new HashMap<>();
        pesanan.put("email_driver", Objects.requireNonNull(ed));
        pesanan.put("nama_driver", Objects.requireNonNull(nd));
        pesanan.put("id_driver", Objects.requireNonNull(id_d));
        pesanan.put("tujuan_toko", Objects.requireNonNull(np));
        pesanan.put("pemilik_toko", Objects.requireNonNull(pt));
        pesanan.put("id_toko", Objects.requireNonNull(id_p));
        pesanan.put("jumlah_pesanan", Objects.requireNonNull(j));
        pesanan.put("status_pesanan", "Ditugaskan");
        pesanan.put("email_create", Objects.requireNonNull(akun.getEmail()));
        pesanan.put("nama_create", Objects.requireNonNull(akun.getNama()));
        pesanan.put("created_at", FieldValue.serverTimestamp());

        db.collection("pesanan").add(pesanan).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(TambahPesanan.this, "Pesanan berhasil ditambahkan!",
                        Toast.LENGTH_LONG).show();
                EnableKomponen();
                reset();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TambahPesanan.this, "Pesanan gagal ditambahkan!\nPastikan koneksi internet anda stabil",
                        Toast.LENGTH_LONG).show();
                EnableKomponen();
            }
        });
    }
}