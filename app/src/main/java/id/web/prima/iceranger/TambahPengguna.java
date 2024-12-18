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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TambahPengguna extends AppCompatActivity {
    EditText email, nama, alamat, telepon; Button register, batal;
    Spinner jabatanspin; ProgressDialog progressDialog;
    String pilihanJabatan = "Pilih Jabatan";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pengguna);
        register = findViewById(R.id.btn_tambahPengguna);
        batal = findViewById(R.id.btn_batal_addPengguna);
        email = findViewById(R.id.email_addPengguna);
        nama = findViewById(R.id.nama_addPengguna);
        alamat = findViewById(R.id.alamat_addPengguna);
        telepon = findViewById(R.id.tel_addPengguna);
        jabatanspin = findViewById(R.id.jabatan_addPengguna);
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
                j = pilihanJabatan.toString();
                p = j+"*IceRanger";
                if (e.equals("") || n.equals("") || a.equals("") || t.equals("") || j.equals("Pilih Jabatan") ||
                        p.equals("")){
                    Toast.makeText(TambahPengguna.this, "Seluruh kolom harus di isi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                } else {
                    if (Patterns.EMAIL_ADDRESS.matcher(e).matches()){
                        EnableKomponen();
                        new AlertDialog.Builder(TambahPengguna.this)
                                .setTitle("Ice Ranger")
                                .setMessage("Harap Diperhatikan! Password default untuk pengguna baru ialah (jabatan)*IceRanger. Sebagai contoh : [Admin Driver*IceRanger]\nApakah data pengguna baru sudah benar?")
                                .setPositiveButton("SUDAH", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DisableKomponen();
                                        tambahPengguna(e,p,n,a,t,j);
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
                        Toast.makeText(TambahPengguna.this, "Email tidak valid!",
                                Toast.LENGTH_LONG).show();
                        EnableKomponen();
                    }
                }
            }
        });
        jabatanspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pilihanJabatan = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
            }
        });
        ArrayList<String> jabatan = new ArrayList<>();
        jabatan.add("Pilih Jabatan");
        jabatan.add("Driver");
        jabatan.add("Admin Driver");
        jabatan.add("Super Admin");
        ArrayAdapter<String> adapterJabatan =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jabatan);
        adapterJabatan.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        jabatanspin.setAdapter(adapterJabatan);
    }
    private void tambahPengguna(String e,String p,String n,String a,String t,String j){
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
                                                Toast.makeText(TambahPengguna.this, "Pengguna Berhasil Ditambahkan! \nPesan Verifikasi telah dikirim ke email!",
                                                        Toast.LENGTH_LONG).show();
                                                EnableKomponen();
                                                logout();
                                                mAuth.signOut();
                                                kosong();
                                            }else{
                                                Toast.makeText(TambahPengguna.this, "Pengguna Berhasil Ditambahkan! \nPesan Verifikasi gagal dikirim!",
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
                                    Toast.makeText(TambahPengguna.this, "Data Pengguna Gagal Ditambahkan!",
                                            Toast.LENGTH_LONG).show();
                                    EnableKomponen();
                                }
                            });
                }else{
                    Toast.makeText(TambahPengguna.this, "Pengguna Gagal Ditambahkan!\nPastikan koneksi internet anda stabil dan Email belum Terdaftar!",
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
        jabatanspin.setSelection(0);
    }
    private void logout(){
        sesiLogin user = new sesiLogin();
        user.logout();
        finish();
        startActivity(new Intent(TambahPengguna.this, LoginActivity.class));
    }
    private void EnableKomponen(){
        register.setEnabled(true);
        email.setFocusableInTouchMode(true);
        nama.setFocusableInTouchMode(true);
        alamat.setFocusableInTouchMode(true);
        telepon.setFocusableInTouchMode(true);
        jabatanspin.setEnabled(true);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void DisableKomponen(){
        register.setEnabled(false);
        email.setFocusable(false);
        nama.setFocusable(false);
        alamat.setFocusable(false);
        telepon.setFocusable(false);
        jabatanspin.setEnabled(false);
        progressDialog = new ProgressDialog(TambahPengguna.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menambahkan data...");
        progressDialog.show();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(TambahPengguna.this)
                .setTitle("Ice Ranger")
                .setMessage("Apakah anda yakin ingin kembali ke Dashboard?")
                .setPositiveButton("KEMBALI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(TambahPengguna.this, DashboardActivity.class).putExtra("to","menu_beranda"));
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