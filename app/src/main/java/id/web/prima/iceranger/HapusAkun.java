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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HapusAkun extends AppCompatActivity {
    FirebaseUser user; FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    EditText email,pass; Button hapus; ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hapus_akun);
        back = findViewById(R.id.back_hapusAkun);
        hapus = findViewById(R.id.btn_hapusAkun);
        email = findViewById(R.id.email_hapusAkun);
        pass = findViewById(R.id.pass_hapusAkun);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(HapusAkun.this, LihatProfilActivity.class));
            }
        });
        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisableKomponen();
                String e,p;
                e = email.getText().toString().trim();
                p = pass.getText().toString().trim();
                if (e.equals("") || p.equals("")){
                    Toast.makeText(HapusAkun.this, "Seluruh kolom harus di isi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else{
                    new AlertDialog.Builder(HapusAkun.this)
                            .setTitle("Ice Ranger")
                            .setMessage("Apakah anda yakin ingin menghapus Akun? Data akun akan dihapus secara permanen.")
                            .setPositiveButton("TIDAK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EnableKomponen();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("HAPUS AKUN", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    VerifikasiAkun(e,p);
                                }
                            })
                            .show();
                }
            }
        });
    }
    private void VerifikasiAkun(String e, String p) {
        if (e.equals(user.getEmail())){
            AuthCredential c = EmailAuthProvider.getCredential(user.getEmail(), p);
            user.reauthenticate(c).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(HapusAkun.this, "Autentikasi Berhasil!",
                            Toast.LENGTH_LONG).show();
                    hapusAkun(user.getUid());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HapusAkun.this, "Autentikasi Gagal!\nPassword anda salah!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }
            });
        }else{
            Toast.makeText(HapusAkun.this, "Email anda salah!",
                    Toast.LENGTH_LONG).show();
            EnableKomponen();
        }
    }
    private void hapusAkun(String id){
        progressDialog = new ProgressDialog(HapusAkun.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menghapus akun...");
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pengguna").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(HapusAkun.this, "Akun anda berhasil dihapus!",
                                Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        sesiLogin userlog = new sesiLogin();
                        userlog.logout();
                        finish();
                        startActivity(new Intent(HapusAkun.this, LoginActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HapusAkun.this, "Akun anda gagal dihapus!",
                                Toast.LENGTH_LONG).show();
                        EnableKomponen();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HapusAkun.this, "Informasi akun anda gagal dihapus!",
                        Toast.LENGTH_LONG).show();
                EnableKomponen();
            }
        });
    }
    private void EnableKomponen(){
        hapus.setEnabled(true);
        email.setFocusableInTouchMode(true);
        pass.setFocusableInTouchMode(true);
        pass.setText("");
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void DisableKomponen(){
        hapus.setEnabled(false);
        email.setFocusable(false);
        pass.setFocusable(false);
        progressDialog = new ProgressDialog(HapusAkun.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang melakukan autentikasi...");
        progressDialog.show();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(HapusAkun.this, LihatProfilActivity.class));
    }
}