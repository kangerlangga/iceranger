package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditPengguna extends AppCompatActivity {
    EditText email, nama, alamat, telepon, jabatan; Button edit1,hapus1,batal;
    String id;ProgressDialog progressDialog;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pengguna);
        user = FirebaseAuth.getInstance().getCurrentUser();
        edit1 = findViewById(R.id.btn_editPengguna);
        hapus1 = findViewById(R.id.btn_hapusPengguna);
        batal = findViewById(R.id.btn_batal_editPengguna);
        email = findViewById(R.id.email_editPengguna);
        nama = findViewById(R.id.nama_editPengguna);
        alamat = findViewById(R.id.alamat_editPengguna);
        telepon = findViewById(R.id.tel_editPengguna);
        jabatan = findViewById(R.id.jabatan_editPengguna);
        FirebaseApp.initializeApp(EditPengguna.this);
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetData();
            }
        });
        hapus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(EditPengguna.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Pengecekan data...");
                progressDialog.show();
                if (id.equals(user.getUid())){
                    Toast.makeText(EditPengguna.this, "Untuk menghapus akun anda saat ini, Gunakan menu profil!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else{
                    EnableKomponen();
                    new AlertDialog.Builder(EditPengguna.this)
                            .setTitle("Ice Ranger")
                            .setMessage("Apakah anda yakin ingin menghapus Pengguna? Data pengguna akan dihapus secara permanen.")
                            .setPositiveButton("TIDAK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("HAPUS PENGGUNA", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog = new ProgressDialog(EditPengguna.this);
                                    progressDialog.setCancelable(false);
                                    progressDialog.setMessage("Sedang menghapus data...");
                                    progressDialog.show();
                                    hapusPengguna(id);
                                }
                            })
                            .show();
                }
            }
        });
        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisableKomponen();
                String n, a, t, i, e, j;
                n = nama.getText().toString().trim();
                a = alamat.getText().toString().trim();
                t = telepon.getText().toString().trim();
                e = email.getText().toString().trim();
                j = jabatan.getText().toString().trim();
                i = id;
                if (n.equals("") || a.equals("") || t.equals("") || e.equals("") || j.equals("") || i.equals("") || i.isEmpty()){
                    Toast.makeText(EditPengguna.this, "Seluruh kolom harus di isi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                } else {
                    if (id.equals(user.getUid())){
                        Toast.makeText(EditPengguna.this, "Untuk mengedit akun anda saat ini, Gunakan menu profil!",
                                Toast.LENGTH_LONG).show();
                        EnableKomponen();
                    }else{
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        progressDialog = new ProgressDialog(EditPengguna.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Sedang memperbarui data...");
                        progressDialog.show();
                        editPengguna(i,n,a,t,e,j);
                    }
                }
            }
        });
        email.setFocusable(false);
        jabatan.setFocusable(false);
        resetData();
    }
    private void resetData(){
        email.setText(getIntent().getStringExtra("editEmail").toString());
        nama.setText(getIntent().getStringExtra("editNama").toString());
        alamat.setText(getIntent().getStringExtra("editAlamat").toString());
        telepon.setText(getIntent().getStringExtra("editTel").toString());
        jabatan.setText(getIntent().getStringExtra("editJabatan").toString());
        id = getIntent().getStringExtra("editId".toString());
    }
    private void editPengguna(String id,String n,String a,String t,String e,String j){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (id.length() > 0 || !id.isEmpty()){
            Map<String, Object> user = new HashMap<>();
            user.put("id_pengguna", id);
            user.put("email", e);
            user.put("nama", n);
            user.put("alamat", a);
            user.put("telepon", t);
            user.put("jabatan", j);
            db.collection("pengguna").document(id).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(EditPengguna.this, "Data Pengguna Berhasil Diperbarui!",
                                Toast.LENGTH_LONG).show();
                        EnableKomponen();
                        finishAffinity();
                        startActivity(new Intent(EditPengguna.this, PenggunaActivity.class));
                    }else{
                        Toast.makeText(EditPengguna.this, "Data Pengguna Gagal Diperbarui!",
                                Toast.LENGTH_LONG).show();
                        EnableKomponen();
                        finishAffinity();
                        startActivity(new Intent(EditPengguna.this, PenggunaActivity.class));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditPengguna.this, "Data Pengguna Gagal Diperbarui!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                    finishAffinity();
                    startActivity(new Intent(EditPengguna.this, PenggunaActivity.class));
                }
            });
        }else{
            Toast.makeText(EditPengguna.this, "ID tidak ditemukan!",
                    Toast.LENGTH_LONG).show();
            EnableKomponen();
            finishAffinity();
            startActivity(new Intent(EditPengguna.this, PenggunaActivity.class));
        }
    }
    private void hapusPengguna(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pengguna").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditPengguna.this, "Data Pengguna Berhasil Dihapus!",
                                Toast.LENGTH_LONG).show();
                        EnableKomponen();
                        finishAffinity();
                        startActivity(new Intent(EditPengguna.this, PenggunaActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditPengguna.this, "Data Pengguna Gagal Dihapus!",
                                Toast.LENGTH_LONG).show();
                        EnableKomponen();
                        finishAffinity();
                        startActivity(new Intent(EditPengguna.this, PenggunaActivity.class));
                    }
                });
    }
    private void EnableKomponen(){
        edit1.setEnabled(true);
        email.setFocusableInTouchMode(true);
        nama.setFocusableInTouchMode(true);
        alamat.setFocusableInTouchMode(true);
        telepon.setFocusableInTouchMode(true);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void DisableKomponen(){
        edit1.setEnabled(false);
        email.setFocusable(false);
        nama.setFocusable(false);
        alamat.setFocusable(false);
        telepon.setFocusable(false);
        progressDialog = new ProgressDialog(EditPengguna.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Pengecekan data...");
        progressDialog.show();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(EditPengguna.this)
                .setTitle("Ice Ranger")
                .setMessage("Apakah anda yakin ingin kembali ke Daftar Pengguna? Perubahan tidak akan disimpan.")
                .setPositiveButton("KEMBALI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        startActivity(new Intent(EditPengguna.this, PenggunaActivity.class));
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