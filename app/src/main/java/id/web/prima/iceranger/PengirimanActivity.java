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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PengirimanActivity extends AppCompatActivity {
    FirebaseFirestore db; RecyclerView pengiriman; ArrayList<ModelPengiriman> arrayList;
    PengirimanAdapterRecycler adapter; ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengiriman);
        db = FirebaseFirestore.getInstance();
        pengiriman = findViewById(R.id.recycler_pengiriman);
        arrayList = new ArrayList<ModelPengiriman>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang mengambil data...");
        progressDialog.show();
        cekInternet();
        pengiriman.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PengirimanAdapterRecycler(PengirimanActivity.this,arrayList);
        pengiriman.setAdapter(adapter);
        TampilkanPengiriman();
    }
    private void TampilkanPengiriman(){
        cekInternet();
        db.collection("pengiriman").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                if (list.size() < 1 || list.isEmpty()){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(PengirimanActivity.this, "Data Pengiriman masih kosong!",
                            Toast.LENGTH_LONG).show();
                }else{
                    for (DocumentSnapshot d:list){
                        ModelPengiriman obj = d.toObject(ModelPengiriman.class);
                        obj.setId_doc(d.getId());
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
                Toast.makeText(PengirimanActivity.this, "Data Pengiriman masih kosong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finishAffinity();
        startActivity(new Intent(PengirimanActivity.this, DashboardActivity.class).putExtra("to","menu_beranda"));
    }
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void cekInternet(){
        if (!koneksiTerhubung()){
            finishAffinity();
            startActivity(new Intent(PengirimanActivity.this, NoInternetActivity.class));
        }
    }
}