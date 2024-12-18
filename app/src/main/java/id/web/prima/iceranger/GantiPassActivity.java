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

public class GantiPassActivity extends AppCompatActivity {
    boolean min8, adaAngka, adaHurufBesar, adaHurufKecil; ImageView back; Button gantipass;
    FirebaseUser user; ProgressDialog progressDialog;
    EditText passNow,newPass,konfNewPass;FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_pass);
        back = findViewById(R.id.back_gantipass);
        gantipass = findViewById(R.id.btn_gantipass);
        passNow = findViewById(R.id.passnow_gantipass);
        newPass = findViewById(R.id.newpass_gantipass);
        konfNewPass = findViewById(R.id.konfnewpass_gantipass);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(GantiPassActivity.this, DashboardActivity.class).putExtra("to","menu_akun"));
            }
        });
        gantipass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisableKomponen();
                String p,np,knp;
                p = passNow.getText().toString().trim();
                np = newPass.getText().toString().trim();
                knp = konfNewPass.getText().toString().trim();
                if (p.equals("") || np.equals("") || knp.equals("")){
                    Toast.makeText(GantiPassActivity.this, "Seluruh kolom harus di isi!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else{
                    cekPassword(np);
                    if (min8){
                        if (min8==true && adaHurufBesar==true && adaHurufKecil==true && adaAngka==true){
                            if (knp.equals(np)){
                                gantiPassword(p,np);
                            }else{
                                Toast.makeText(GantiPassActivity.this, "Konfirmasi password baru tidak sesuai!",
                                        Toast.LENGTH_LONG).show();
                                EnableKomponen();
                            }
                        }else{
                            Toast.makeText(GantiPassActivity.this, "Password harus mengandung Karakter Angka, Huruf Besar dan Huruf Kecil!",
                                    Toast.LENGTH_LONG).show();
                            EnableKomponen();
                        }
                    }else{
                        Toast.makeText(GantiPassActivity.this, "Password minimal 8 karakter!",
                                Toast.LENGTH_LONG).show();
                        EnableKomponen();
                    }
                }
            }
        });
    }
    private void cekPassword(String pa){
        //Pengecekan Min 8
        if (pa.length() >= 8){
            min8 = true;
        }else{
            min8 = false;
        }

        //Ada huruf besar
        if (pa.matches("(.*[A-Z].*)")){
            adaHurufBesar = true;
        }else{
            adaHurufBesar = false;
        }

        //Ada huruf kecil
        if (pa.matches("(.*[a-z].*)")){
            adaHurufKecil = true;
        }else{
            adaHurufKecil = false;
        }

        //Ada angka
        if (pa.matches("(.*[0-9].*)")){
            adaAngka = true;
        }else{
            adaAngka = false;
        }
    }
    private void gantiPassword(String oldpass,String newpass){
        AuthCredential c = EmailAuthProvider.getCredential(user.getEmail(), oldpass);
        user.reauthenticate(c)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            progressDialog = new ProgressDialog(GantiPassActivity.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Sedang memperbarui password...");
                            progressDialog.show();
                            user.updatePassword(newpass)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                EnableKomponen();
                                                Toast.makeText(GantiPassActivity.this, "Password anda berhasil diperbarui!\nSilahkan login ulang dengan password baru",
                                                        Toast.LENGTH_LONG).show();
                                                mAuth.signOut();
                                                sesiLogin userlog = new sesiLogin();
                                                userlog.logout();
                                                finish();
                                                startActivity(new Intent(GantiPassActivity.this, LoginActivity.class));
                                            }else{
                                                EnableKomponen();
                                                Toast.makeText(GantiPassActivity.this, "Password gagal diperbarui!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            EnableKomponen();
                                            Toast.makeText(GantiPassActivity.this, "Password gagal diperbarui!",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }else{
                            EnableKomponen();
                            Toast.makeText(GantiPassActivity.this, "Autentikasi Gagal!\nPassword anda salah!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        EnableKomponen();
                        Toast.makeText(GantiPassActivity.this, "Autentikasi Gagal!\nPassword anda salah!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void EnableKomponen(){
        gantipass.setEnabled(true);
        passNow.setFocusableInTouchMode(true);
        newPass.setFocusableInTouchMode(true);
        konfNewPass.setFocusableInTouchMode(true);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void DisableKomponen(){
        gantipass.setEnabled(false);
        passNow.setFocusable(false);
        newPass.setFocusable(false);
        konfNewPass.setFocusable(false);
        progressDialog = new ProgressDialog(GantiPassActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang melakukan autentikasi...");
        progressDialog.show();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(GantiPassActivity.this, DashboardActivity.class).putExtra("to","menu_akun"));
    }
}