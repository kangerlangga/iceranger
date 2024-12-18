package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PenggunaActivity extends AppCompatActivity {
    FirebaseFirestore db; RecyclerView pengguna; ArrayList<ModelPengguna> arrayList;
    PenggunaAdapterRecycler adapter; ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengguna);
        db = FirebaseFirestore.getInstance();
        pengguna = findViewById(R.id.recycler_pengguna);
        arrayList = new ArrayList<ModelPengguna>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang mengambil data...");
        progressDialog.show();

        pengguna.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PenggunaAdapterRecycler(PenggunaActivity.this,arrayList);
        pengguna.setAdapter(adapter);
        TampilkanDataPengguna();
    }

    private void TampilkanDataPengguna() {
        db.collection("pengguna").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                if (list.size() < 1 || list.isEmpty()){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(PenggunaActivity.this, "Data Pengguna tidak tersedia!",
                            Toast.LENGTH_LONG).show();
                }else{
                    for (DocumentSnapshot d:list){
                        ModelPengguna obj = d.toObject(ModelPengguna.class);
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
                Toast.makeText(PenggunaActivity.this, "Data Pengguna tidak tersedia!"+e,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(PenggunaActivity.this, DashboardActivity.class).putExtra("to","menu_beranda"));
    }
}