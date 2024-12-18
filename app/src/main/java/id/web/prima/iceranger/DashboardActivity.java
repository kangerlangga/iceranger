package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import id.web.prima.iceranger.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ActivityDashboardBinding binding;
    ProgressDialog progressDialog; String tujuan = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cekizinlokasi();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sinkronisasi data...");
        progressDialog.show();
        if (!koneksiTerhubung()){
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            finish();
            startActivity(new Intent(DashboardActivity.this, NoInternetActivity.class));
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sesiLogin userlog = new sesiLogin();
        userlog.setEmail(user.getEmail().toString());
        userlog.setUserid(user.getUid().toString());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String id = user.getUid();
        reference = firestore.collection("pengguna").document(id);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    sesiLogin user = new sesiLogin();
                    user.setNama(task.getResult().getString("nama"));
                    user.setJabatan(task.getResult().getString("jabatan"));
                    user.setEmail(task.getResult().getString("email"));
                    user.setUserid(task.getResult().getString(id));
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }else{
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(DashboardActivity.this, "Informasi akun anda tidak ditemukan!",
                            Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(DashboardActivity.this, "Informasi akun anda tidak ditemukan!",
                        Toast.LENGTH_LONG).show();
                mAuth.signOut();
                finish();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            }
        });

        BerandaFragment1 berandaFragment1 = new BerandaFragment1();
        BerandaFragment2 berandaFragment2 = new BerandaFragment2();
        BerandaFragment3 berandaFragment3 = new BerandaFragment3();
        PengirimanFragment pengirimanFragment = new PengirimanFragment();
        PelangganFragment pelangganFragment = new PelangganFragment();
        AkunFragment akunFragment = new AkunFragment();
        sesiLogin user1 = new sesiLogin();
        BottomNavigationView menubawah = findViewById(R.id.navigasiBawah);
        menubawah.setOnNavigationItemSelectedListener(this);
        tujuan = getIntent().getStringExtra("to");
        if (tujuan.length() > 0 || !tujuan.isEmpty() || !tujuan.equals("")){
            if (tujuan.equals("menu_akun")){
                menubawah.setSelectedItemId(R.id.menu_akun);
            }else if (tujuan.equals("menu_toko")){
                menubawah.setSelectedItemId(R.id.menu_toko);
            }else if (tujuan.equals("menu_beranda")){
                menubawah.setSelectedItemId(R.id.menu_beranda);
            }
        }else{
            if (user1.getJabatan().equals("Super Admin")){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment3).commit();
            }else if (user1.getJabatan().equals("Admin Driver")){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment2).commit();
            }else if (user1.getJabatan().equals("Driver")){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment1).commit();
            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment1).commit();
            }
        }

        binding.navigasiBawah.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_beranda){
                    if (user1.getJabatan().equals("Super Admin")){
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment3).commit();
                    }else if (user1.getJabatan().equals("Admin Driver")){
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment2).commit();
                    }else if (user1.getJabatan().equals("Driver")){
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment1).commit();
                    }else{
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment1).commit();
                    }
                    return true;
                }else if(id == R.id.menu_riwayat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, pengirimanFragment).commit();
                    return true;
                }else if(id == R.id.menu_toko){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, pelangganFragment).commit();
                    return true;
                }else if(id == R.id.menu_akun){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, akunFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void cekizinlokasi(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(DashboardActivity.this)
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        BerandaFragment1 berandaFragment1 = new BerandaFragment1();
        BerandaFragment2 berandaFragment2 = new BerandaFragment2();
        BerandaFragment3 berandaFragment3 = new BerandaFragment3();
        PengirimanFragment pengirimanFragment = new PengirimanFragment();
        PelangganFragment pelangganFragment = new PelangganFragment();
        AkunFragment akunFragment = new AkunFragment();
        sesiLogin user1 = new sesiLogin();
        int id = item.getItemId();
        if (id == R.id.menu_beranda){
            if (user1.getJabatan().equals("Super Admin")){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment3).commit();
            }else if (user1.getJabatan().equals("Admin Driver")){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment2).commit();
            }else if (user1.getJabatan().equals("Driver")){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment1).commit();
            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, berandaFragment1).commit();
            }
            return true;
        }else if(id == R.id.menu_riwayat){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, pengirimanFragment).commit();
            return true;
        }else if(id == R.id.menu_toko){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, pelangganFragment).commit();
            return true;
        }else if(id == R.id.menu_akun){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_dashboard, akunFragment).commit();
            return true;
        }
        return false;
    }
}