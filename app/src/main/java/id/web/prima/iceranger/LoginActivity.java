package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    EditText user, pass; Button login;ProgressDialog progressDialog; TextView lupasandi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.btn_login);
        user = findViewById(R.id.email_login);
        pass = findViewById(R.id.pass_login);
        lupasandi = findViewById(R.id.lupasandi);
        lupasandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LoginActivity.this, LupaSandi.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableKomponen();
                String e,p;
                e = user.getText().toString();
                p = pass.getText().toString();
                if (e.isEmpty() || p.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Seluruh kolom harus dipenuhi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else{
                    mAuth.signInWithEmailAndPassword(e,p).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                                sesiLogin user = new sesiLogin();
                                user.setEmail(mAuth.getCurrentUser().getEmail().toString());
                                user.setUserid(mAuth.getCurrentUser().getUid().toString());
                                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                DocumentReference reference;
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                String id = user1.getUid();
                                reference = firestore.collection("pengguna").document(id);
                                reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()){
                                            if (mAuth.getCurrentUser().isEmailVerified()){
                                                Toast.makeText(LoginActivity.this, "Login Berhasil!",
                                                        Toast.LENGTH_LONG).show();
                                                sesiLogin user = new sesiLogin();
                                                user.setNama(task.getResult().getString("nama"));
                                                user.setJabatan(task.getResult().getString("jabatan"));
                                                user.setEmail(task.getResult().getString("email"));
                                                user.setTelepon(task.getResult().getString("telepon"));
                                                user.setUserid(task.getResult().getString(id));
                                                EnableKomponen();
                                                finish();
                                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class).putExtra("to","menu_beranda"));
                                            }else{
                                                EnableKomponen();
                                                Toast.makeText(LoginActivity.this, "Email belum di verifikasi!",
                                                        Toast.LENGTH_LONG).show();
                                                finish();
                                                startActivity(new Intent(LoginActivity.this, NoVerifikasiActivity.class));
                                            }
                                        }else{
                                            user1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    mAuth.signOut();
                                                    EnableKomponen();
                                                    Toast.makeText(LoginActivity.this, "Akun anda telah dihapus!",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mAuth.signOut();
                                                    EnableKomponen();
                                                    Toast.makeText(LoginActivity.this, "Data Akun anda telah dihapus!",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        user1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                mAuth.signOut();
                                                EnableKomponen();
                                                Toast.makeText(LoginActivity.this, "Akun anda telah dihapus!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mAuth.signOut();
                                                EnableKomponen();
                                                Toast.makeText(LoginActivity.this, "Data Akun anda telah dihapus!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            EnableKomponen();
                            Toast.makeText(LoginActivity.this, "Login Gagal!\nEmail atau Password anda salah!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Ice Ranger")
                .setMessage("Apakah anda yakin ingin keluar dari aplikasi?")
                .setPositiveButton("KELUAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
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
    private void EnableKomponen(){
        cekInternet();
        login.setEnabled(true);
        user.setFocusableInTouchMode(true);
        pass.setFocusableInTouchMode(true);
        pass.setText("");
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void DisableKomponen(){
        cekInternet();
        login.setEnabled(false);
        user.setFocusable(false);
        pass.setFocusable(false);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang melakukan autentikasi...");
        progressDialog.show();
    }
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void cekInternet(){
        if (!koneksiTerhubung()){
            finishAffinity();
            startActivity(new Intent(LoginActivity.this, NoInternetActivity.class));
        }
    }
}