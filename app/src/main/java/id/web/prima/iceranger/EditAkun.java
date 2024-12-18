package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditAkun extends AppCompatActivity {
    EditText email,nama,telepon,jabatan,alamat,pass; Button editakun;
    ImageView back; FirebaseAuth mAuth;
    FirebaseUser user; ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_akun);
        email = findViewById(R.id.email_editakun);
        email.setFocusable(false);
        jabatan = findViewById(R.id.jabatan_editakun);
        jabatan.setFocusable(false);
        nama = findViewById(R.id.nama_editakun);
        telepon = findViewById(R.id.telepon_editakun);
        alamat = findViewById(R.id.alamat_editakun);
        pass = findViewById(R.id.pass_editakun);
        editakun = findViewById(R.id.btn_editakun);
        back = findViewById(R.id.back_editakun);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        String id = user.getUid();
        tampilData(id);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(EditAkun.this, LihatProfilActivity.class));
            }
        });
        editakun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisableKomponen();
                String e,p,n,a,j,t;
                e = user.getEmail().toString().trim();
                n = nama.getText().toString().trim();
                a = alamat.getText().toString().trim();
                j = jabatan.getText().toString().trim();
                t = telepon.getText().toString().trim();
                p = pass.getText().toString().trim();
                if (e.equals("") || n.equals("") || a.equals("") || j.equals("") || t.equals("") || p.equals("")){
                    Toast.makeText(EditAkun.this, "Seluruh kolom harus di isi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else{
                    cekPass(e,p,n,a,j,t);
                }
            }
        });
    }
    private void cekPass(String e,String p,String n,String a,String j,String t){
        AuthCredential c = EmailAuthProvider.getCredential(user.getEmail(), p);
        user.reauthenticate(c).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    String id = user.getUid();
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();}
                        Toast.makeText(EditAkun.this, "Autentikasi Berhasil!",
                                Toast.LENGTH_LONG).show();
                        ubahDetail(id,e,n,a,j,t);
                }else{
                    EnableKomponen();
                    Toast.makeText(EditAkun.this, "Autentikasi Gagal!\nPassword anda salah.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                EnableKomponen();
                Toast.makeText(EditAkun.this, "Autentikasi Gagal!\nPassword anda salah.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void ubahDetail(String id_akun,String e,String n,String a,String j,String t){
        progressDialog = new ProgressDialog(EditAkun.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang memperbarui akun...");
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (id_akun.length() > 0 || !id_akun.isEmpty()){
                        Map<String, Object> user1 = new HashMap<>();
                        user1.put("id_pengguna", user.getUid());
                        user1.put("email", e);
                        user1.put("nama", n);
                        user1.put("alamat", a);
                        user1.put("telepon", t);
                        user1.put("jabatan", j);
                        db.collection("pengguna").document(id_akun).set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    sesiLogin sesi = new sesiLogin();
                                    sesi.setNama(n);
                                    sesi.setTelepon(t);
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    Toast.makeText(EditAkun.this, "Informasi Akun Berhasil Diperbarui!",
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(new Intent(EditAkun.this, LihatProfilActivity.class));
                                }else{
                                    Toast.makeText(EditAkun.this, "Informasi Akun Gagal Diperbarui!",
                                            Toast.LENGTH_LONG).show();
                                    EnableKomponen();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditAkun.this, "Informasi Akun Gagal Diperbarui!",
                                        Toast.LENGTH_LONG).show();
                                EnableKomponen();
                            }
                        });
                    }else{
            EnableKomponen();
            Toast.makeText(EditAkun.this, "Akun anda tidak ditemukan!\nAnda telah keluar akun",
                    Toast.LENGTH_LONG).show();
            mAuth.signOut();
            sesiLogin user = new sesiLogin();
            user.logout();
            finish();
            startActivity(new Intent(EditAkun.this, LoginActivity.class));
                    }

    }
    private void tampilData(String id_akun) {
        progressDialog = new ProgressDialog(EditAkun.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sinkronisasi data akun...");
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (id_akun.length() > 0 || !id_akun.isEmpty()) {
            DocumentReference reference = db.collection("pengguna").document(id_akun);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        email.setText(task.getResult().getString("email"));
                        nama.setText(task.getResult().getString("nama"));
                        alamat.setText(task.getResult().getString("alamat"));
                        jabatan.setText(task.getResult().getString("jabatan"));
                        telepon.setText(task.getResult().getString("telepon"));
                        EnableKomponen();
                    } else {
                        EnableKomponen();
                        Toast.makeText(EditAkun.this, "Sinkronisasi data akun gagal!\nAnda telah keluar akun",
                                Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        sesiLogin user = new sesiLogin();
                        user.logout();
                        finish();
                        startActivity(new Intent(EditAkun.this, LoginActivity.class));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    EnableKomponen();
                    Toast.makeText(EditAkun.this, "Sinkronisasi data akun gagal!\nAnda telah keluar akun",
                            Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    sesiLogin user = new sesiLogin();
                    user.logout();
                    finish();
                    startActivity(new Intent(EditAkun.this, LoginActivity.class));
                }
            });
        } else {
            EnableKomponen();
            Toast.makeText(EditAkun.this, "Akun anda tidak ditemukan!\nAnda telah keluar akun",
                    Toast.LENGTH_LONG).show();
            mAuth.signOut();
            sesiLogin user = new sesiLogin();
            user.logout();
            finish();
            startActivity(new Intent(EditAkun.this, LoginActivity.class));
        }
    }
    private void EnableKomponen(){
        editakun.setEnabled(true);
        nama.setFocusableInTouchMode(true);
        telepon.setFocusableInTouchMode(true);
        alamat.setFocusableInTouchMode(true);
        pass.setFocusableInTouchMode(true);
        pass.setText("");
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    private void DisableKomponen(){
        editakun.setEnabled(false);
        nama.setFocusable(false);
        telepon.setFocusable(false);
        alamat.setFocusable(false);
        pass.setFocusable(false);
        progressDialog = new ProgressDialog(EditAkun.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang melakukan autentikasi...");
        progressDialog.show();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(EditAkun.this, LihatProfilActivity.class));
    }
}