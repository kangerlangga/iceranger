package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PesananActivity extends AppCompatActivity {
    FirebaseFirestore db; RecyclerView pesanan; ArrayList<ModelPesanan> arrayList;
    PesananAdapterRecycler adapter; ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan);
        db = FirebaseFirestore.getInstance();
        pesanan = findViewById(R.id.recycler_pesanan);
        arrayList = new ArrayList<ModelPesanan>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang mengambil data...");
        progressDialog.show();
        cekInternet();
        pesanan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PesananAdapterRecycler(PesananActivity.this,arrayList);
        pesanan.setAdapter(adapter);
        sesiLogin akun = new sesiLogin();
        FirebaseUser akun2 = FirebaseAuth.getInstance().getCurrentUser();
        if (akun.getJabatan().equals("Super Admin") || akun.getJabatan().equals("Admin Driver")){
            TampilkanAdmin();
        }else if (akun.getJabatan().equals("Driver")){
            TampilkanDriver(akun2.getUid());
        }else{
            finishAffinity();
            startActivity(new Intent(PesananActivity.this, DashboardActivity.class).putExtra("to","menu_beranda"));
        }
    }
    private void TampilkanAdmin(){
        cekInternet();
        db.collection("pesanan").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                if (list.size() < 1 || list.isEmpty()){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(PesananActivity.this, "Data Pesanan masih kosong!",
                            Toast.LENGTH_LONG).show();
                }else{
                    for (DocumentSnapshot d:list){
                        ModelPesanan obj = d.toObject(ModelPesanan.class);
                        obj.setId_Doc(d.getId());
                        arrayList.add(obj);
                    }
                    adapter.notifyDataSetChanged();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(PesananActivity.this, "Data Pesanan masih kosong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void TampilkanDriver(String id){
        cekInternet();
        db.collection("pesanan").whereEqualTo("id_driver",id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                if (list.size() < 1 || list.isEmpty()){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(PesananActivity.this, "Tidak ada pengiriman untuk anda!",
                            Toast.LENGTH_LONG).show();
                }else{
                    for (DocumentSnapshot d:list){
                        ModelPesanan obj = d.toObject(ModelPesanan.class);
                        obj.setId_Doc(d.getId());
                        arrayList.add(obj);
                    }
                    adapter.notifyDataSetChanged();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(PesananActivity.this, "Tidak ada pengiriman untuk anda!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finishAffinity();
        startActivity(new Intent(PesananActivity.this, DashboardActivity.class).putExtra("to","menu_beranda"));
    }
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void cekInternet(){
        if (!koneksiTerhubung()){
            finishAffinity();
            startActivity(new Intent(PesananActivity.this, NoInternetActivity.class));
        }
    }
}