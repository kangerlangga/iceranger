package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Sinkronisasi data...");
                progressDialog.show();
                if (koneksiTerhubung()){
                    try{
                        if (!user.getUid().equals("") || !user.getUid().isEmpty() || user.getUid() != null){
                            sesiLogin userlog = new sesiLogin();
                            userlog.setEmail(user.getEmail().toString());
                            userlog.setUserid(user.getUid().toString());
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            DocumentReference reference;
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            String id = user.getUid();
                            if (mAuth.getCurrentUser().isEmailVerified()){
                                reference = firestore.collection("pengguna").document(id);
                                reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()){
                                            sesiLogin user = new sesiLogin();
                                            user.setNama(task.getResult().getString("nama"));
                                            user.setJabatan(task.getResult().getString("jabatan"));
                                            user.setEmail(task.getResult().getString("email"));
                                            user.setTelepon(task.getResult().getString("telepon"));
                                            user.setUserid(task.getResult().getString(id));
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            finishAffinity();
                                            startActivity(new Intent(MainActivity.this, DashboardActivity.class).putExtra("to","menu_beranda"));
                                        }else{
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            mAuth.signOut();
                                            finishAffinity();
                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                        }
                                    }
                                });
                            }else{
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                mAuth.signOut();
                                finishAffinity();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        }else{
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            finishAffinity();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    }catch(Exception e){
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        finishAffinity();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                }else{
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    finishAffinity();
                    startActivity(new Intent(MainActivity.this, NoInternetActivity.class));
                }
            }
        },3000);
    }
    Context context;
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this)
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
}