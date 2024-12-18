package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LupaSandi extends AppCompatActivity {
    ImageView back; EditText email; Button resetpass; ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_sandi);
        back = findViewById(R.id.back_lupasandi);
        email = findViewById(R.id.email_lupasandi);
        resetpass = findViewById(R.id.btn_lupasandi);
        cekInternet();
        EnableKomponen();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LupaSandi.this, LoginActivity.class));
            }
        });
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisableKomponen();
                String e = email.getText().toString().trim();
                if (TextUtils.isEmpty(e) || e.equals("")){
                    Toast.makeText(LupaSandi.this, "Email tidak boleh kosong!",
                            Toast.LENGTH_LONG).show();
                    EnableKomponen();
                }else{
                    cekEmail(e);
                }
            }
        });
    }
    private void cekEmail(String e){
        progressDialog = new ProgressDialog(LupaSandi.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang mengecek data...");
        progressDialog.show();
        cekInternet();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pengguna").whereEqualTo("email",e).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().getDocuments().size()>0) {
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    resetPass(e);
                }else if (task.getResult().getDocuments().size()<1){
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    EnableKomponen();
                    Toast.makeText(LupaSandi.this, "Akun tidak ditemukan!",
                            Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                EnableKomponen();
                Toast.makeText(LupaSandi.this, "Akun tidak ditemukan!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void resetPass(String e){
        progressDialog = new ProgressDialog(LupaSandi.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Mengirim permintaan tautan...");
        progressDialog.show();
        cekInternet();
        FirebaseAuth.getInstance().sendPasswordResetEmail(e).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                EnableKomponen();
                Toast.makeText(LupaSandi.this, "Tautan untuk Reset Password berhasil dikirim!",
                        Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(LupaSandi.this, LoginActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                EnableKomponen();
                Toast.makeText(LupaSandi.this, "Tautan untuk Reset Password gagal dikirim!\nPastikan koneksi internet anda stabil dan coba beberapa saat lagi.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void cekInternet(){
        if (!koneksiTerhubung()){
            finishAffinity();
            startActivity(new Intent(LupaSandi.this, NoInternetActivity.class));
        }
    }
    private void EnableKomponen(){
        resetpass.setEnabled(true);
        email.setFocusableInTouchMode(true);
    }
    private void DisableKomponen(){
        resetpass.setEnabled(false);
        email.setFocusable(false);
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(LupaSandi.this, LoginActivity.class));
    }
}