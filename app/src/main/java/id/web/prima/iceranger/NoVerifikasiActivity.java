package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class NoVerifikasiActivity extends AppCompatActivity {
    Button kirimver, logout;ProgressDialog progressDialog;FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_verifikasi);
        kirimver = findViewById(R.id.btn_kirimverifikasi);
        logout = findViewById(R.id.btn_logoutverifikasi);
        mAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                sesiLogin user = new sesiLogin();
                user.logout();
                Toast.makeText(NoVerifikasiActivity.this, "Anda berhasil keluar dari akun!",
                        Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(NoVerifikasiActivity.this, LoginActivity.class));
            }
        });
        kirimver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(NoVerifikasiActivity.this)
                        .setTitle("Ice Ranger")
                        .setMessage("Harap Diperhatikan!!!\nRequest pengiriman ulang email verifikasi yang berlebihan akan membuat akun anda di blokir oleh sistem. Sehingga sebelum mengirim pastikan email anda aktif dan dapat menerima pesan baru. Jangan lupa periksa folder spam apabila email verifikasi tidak terdapat pada kotak masuk anda. Segera klik link verifikasi setelah email verifikasi berhasil diterima. Kirim sekarang?")
                        .setPositiveButton("KIRIM SEKARANG", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                kirimVerifikasi();
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
        });
    }
    private void kirimVerifikasi(){
        progressDialog = new ProgressDialog(NoVerifikasiActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang mengirim verifikasi...");
        progressDialog.show();
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mAuth.signOut();
                    sesiLogin user = new sesiLogin();
                    user.logout();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(NoVerifikasiActivity.this, "Email verifikasi berhasil dikirim!",
                            Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(NoVerifikasiActivity.this, LoginActivity.class));
                }else{
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(NoVerifikasiActivity.this, "Email verifikasi gagal dikirim!\nSilahkan tunggu beberapa saat sebelum mencoba kirim ulang",
                            Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(NoVerifikasiActivity.this, "Email verifikasi gagal dikirim!\nSilahkan tunggu beberapa saat sebelum mencoba kirim ulang",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}