package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LihatProfilActivity extends AppCompatActivity {
    ImageView back; TextView email,nama,alamat,telp,ubahAkun; Button hapus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_profil);
        back = findViewById(R.id.back_profil);
        email = findViewById(R.id.email_profil);
        nama = findViewById(R.id.nama_profil);
        alamat = findViewById(R.id.alamat_profil);
        telp = findViewById(R.id.tel_profil);
        ubahAkun = findViewById(R.id.uDetail_profil);
        hapus = findViewById(R.id.btnhapus_profil);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sinkronisasi data akun...");
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String id = user.getUid();
        reference = firestore.collection("pengguna").document(id);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    email.setText(task.getResult().getString("email"));
                    nama.setText(task.getResult().getString("nama").toUpperCase());
                    telp.setText(task.getResult().getString("telepon"));
                    alamat.setText(task.getResult().getString("alamat").toUpperCase());
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }else{
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(LihatProfilActivity.this, "Informasi akun anda tidak ditemukan!",
                            Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(LihatProfilActivity.this, LoginActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(LihatProfilActivity.this, "Informasi akun anda tidak ditemukan!",
                        Toast.LENGTH_LONG).show();
                mAuth.signOut();
                finish();
                startActivity(new Intent(LihatProfilActivity.this, LoginActivity.class));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LihatProfilActivity.this, DashboardActivity.class).putExtra("to","menu_akun"));
            }
        });
        ubahAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LihatProfilActivity.this, EditAkun.class));
            }
        });
        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LihatProfilActivity.this, HapusAkun.class));
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(LihatProfilActivity.this, DashboardActivity.class).putExtra("to","menu_akun"));
    }
}