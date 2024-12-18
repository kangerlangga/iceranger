package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TambahDriver extends AppCompatActivity {
    EditText email, nama, alamat, telepon; Button register, batal;
    ProgressDialog progressDialog;
    String jabatan = "Driver";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_driver);
        register = findViewById(R.id.btn_tambahDriver);
        batal = findViewById(R.id.btn_batal_addDriver);
        email = findViewById(R.id.email_addDriver);
        nama = findViewById(R.id.nama_addDriver);
        alamat = findViewById(R.id.alamat_addDriver);
        telepon = findViewById(R.id.tel_addDriver);
        kosong();
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kosong();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisableKomponen();
                String e, n, a, t, j, p;
                e = email.getText().toString().trim();
                n = nama.getText().toString().trim();
                a = alamat.getText().toString().trim();
                t = telepon.getText().toString().trim();
                j = jabatan.trim();
                p = j+"*IceRanger";
                if (e.equals("") || n.equals("") || a.equals("") || t.equals("") || j.equals("Pilih Jabatan") ||
                        p.equals("")){
                    Toast.makeText(TambahDriver.this, "Seluruh kolom harus di isi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                } else {
                    if (Patterns.EMAIL_ADDRESS.matcher(e).matches()){
                        EnableKomponen();
                        new AlertDialog.Builder(TambahDriver.this)
                                .setTitle("Ice Ranger")
                                .setMessage("Harap Diperhatikan! Password default untuk driver baru ialah "+j+"*IceRanger.\nApakah data driver baru sudah benar?")
                                .setPositiveButton("SUDAH", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DisableKomponen();
                                        tambahDriver(e,p,n,a,t,j);
                                    }
                                })
                                .setNegativeButton("BELUM", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }else{
                        Toast.makeText(TambahDriver.this, "Email tidak valid!",
                                Toast.LENGTH_LONG).show();
                        EnableKomponen();
                    }
                }
            }
        });
    }
    private void tambahDriver(String e,String p,String n,String a,String t,String j){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Map<String, Object> user = new HashMap<>();
                    user.put("id_pengguna", Objects.requireNonNull(mAuth.getCurrentUser().getUid().toString()));
                    user.put("email", Objects.requireNonNull(e));
                    user.put("nama", Objects.requireNonNull(n));
                    user.put("alamat", Objects.requireNonNull(a));
                    user.put("telepon", Objects.requireNonNull(t));
                    user.put("jabatan", Objects.requireNonNull(j));
                    DocumentReference reference = db.collection("pengguna").document(mAuth.getCurrentUser().getUid().toString());
                    reference.set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(TambahDriver.this, "Driver Berhasil Ditambahkan! \nPesan Verifikasi telah dikirim ke email!",
                                                        Toast.LENGTH_LONG).show();
                                                EnableKomponen();
                                                logout();
                                                mAuth.signOut();
                                                kosong();
                                            }else{
                                                Toast.makeText(TambahDriver.this, "Driver Berhasil Ditambahkan! \nPesan Verifikasi gagal dikirim!",
                                                        Toast.LENGTH_LONG).show();
                                                EnableKomponen();
                                                logout();
                                                mAuth.signOut();
                                                kosong();
                                            }
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TambahDriver.this, "Data Driver Gagal Ditambahkan!",
                                            Toast.LENGTH_LONG).show();
                                    EnableKomponen();
                                }
                            });
                }else{
                    Toast.makeText(TambahDriver.this, "Driver Gagal Ditambahkan!\nPastikan koneksi internet anda stabil dan Email belum Terdaftar!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }
            }
        });
    }
    private void kosong(){
        nama.setText("");
        email.setText("");
        alamat.setText("");
        telepon.setText("");
    }
    private void logout(){
        sesiLogin user = new sesiLogin();
        user.logout();
        finish();
        startActivity(new Intent(TambahDriver.this, LoginActivity.class));
    }
    private void EnableKomponen(){
        register.setEnabled(true);
        email.setFocusableInTouchMode(true);
        nama.setFocusableInTouchMode(true);
        alamat.setFocusableInTouchMode(true);
        telepon.setFocusableInTouchMode(true);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void DisableKomponen(){
        register.setEnabled(false);
        email.setFocusable(false);
        nama.setFocusable(false);
        alamat.setFocusable(false);
        telepon.setFocusable(false);
        progressDialog = new ProgressDialog(TambahDriver.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menambahkan data...");
        progressDialog.show();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(TambahDriver.this)
                .setTitle("Ice Ranger")
                .setMessage("Apakah anda yakin ingin kembali ke Dashboard?")
                .setPositiveButton("KEMBALI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(TambahDriver.this, DashboardActivity.class).putExtra("to","menu_beranda"));
                    }
                })
                .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}