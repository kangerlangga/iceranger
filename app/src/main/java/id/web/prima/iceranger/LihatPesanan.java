package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LihatPesanan extends AppCompatActivity implements LocationListener {
    TextView pemesan,toko,teltoko,jml,alamat,driver,teldriver,time,create,status;
    Button kirim,selesai,navigasi,lacak,hapus; ProgressDialog progressDialog;
    FirebaseFirestore db; ListenerRegistration registration;
    private LocationManager lm; private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mLocationClient; private Location lastlocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted; private Location lastKnownLocation;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    String id_toko,lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_pesanan);
        pemesan = findViewById(R.id.pemesan_lihatPesanan);
        toko = findViewById(R.id.toko_lihatPesanan);
        teltoko = findViewById(R.id.teltoko_lihatPesanan);
        jml = findViewById(R.id.jumlah_lihatPesanan);
        alamat = findViewById(R.id.alamat_lihatPesanan);
        driver = findViewById(R.id.driver_lihatPesanan);
        teldriver = findViewById(R.id.teldriver_lihatPesanan);
        time = findViewById(R.id.timePenugasan_lihatPesanan);
        create = findViewById(R.id.adminCreate_lihatPesanan);
        status = findViewById(R.id.status_lihatPesanan);
        kirim = findViewById(R.id.btnkirim_lihatPesanan);
        selesai = findViewById(R.id.btnselesai_lihatPesanan);
        navigasi = findViewById(R.id.btnnavigasi_lihatPesanan);
        lacak = findViewById(R.id.btnlacak_lihatPesanan);
        hapus = findViewById(R.id.btnhapus_lihatPesanan);
        kirim.setVisibility(View.GONE);
        selesai.setVisibility(View.GONE);
        navigasi.setVisibility(View.GONE);
        lacak.setVisibility(View.GONE);
        hapus.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sinkronisasi data pesanan...");
        progressDialog.show();
        cekInternet();
        cekAkun();

        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LihatPesanan.this)
                        .setTitle("Ice Ranger")
                        .setMessage("Harap Diperhatikan! Lokasi anda akan dipantau ketika sedang melakukan pengiriman. Pastikan koneksi internet anda stabil\nApakah anda yakin akan melakukan pengiriman sekarang?")
                        .setPositiveButton("IYA NIH", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cekPengiriman(getIntent().getStringExtra("idPesanan"));
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
        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LihatPesanan.this)
                        .setTitle("Ice Ranger")
                        .setMessage("Apakah anda yakin ingin menghapus Pesanan? Data pesanan akan dihapus secara permanen.")
                        .setPositiveButton("TIDAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("HAPUS PESANAN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hapusPesanan(getIntent().getStringExtra("idPesanan"));
                            }
                        })
                        .show();
            }
        });
        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LihatPesanan.this)
                        .setTitle("Ice Ranger")
                        .setMessage("Apakah pesanan telah selesai dikirimkan?")
                        .setPositiveButton("SELESAI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sesiLogin selesai1 = new sesiLogin();
                                if (selesai1.getJabatan().equals("Super Admin") || selesai1.getJabatan().equals("Admin Driver")){
                                    PesananSelesaiAdmin(getIntent().getStringExtra("idPesanan"));
                                }else if (selesai1.getJabatan().equals("Driver")){
                                    PesananSelesai(getIntent().getStringExtra("idPesanan"));
                                }else{
                                    registration.remove();
                                    finishAffinity();
                                    startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                                }
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
        lacak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration.remove();
                finishAffinity();
                startActivity(new Intent(LihatPesanan.this, PengirimanActivity.class));
            }
        });
        navigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q="+lat+","+lng+"&mode=1"));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
    }
    private void cekAkun(){
        sesiLogin user1 = new sesiLogin();
        if (user1.getJabatan().equals("Super Admin") || user1.getJabatan().equals("Admin Driver")){
            cekPesananAdmin(getIntent().getStringExtra("idPesanan".toString()));
        }else if (user1.getJabatan().equals("Driver")){
            cekPesananDriver(getIntent().getStringExtra("idPesanan".toString()));
            izinLokasi();
            mLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            // Prompt the user for permission.
            getLocationPermission();
            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }else{
            registration.remove();
            finishAffinity();
            startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
        }
    }
    private void cekPesananAdmin(String id_pesanan){
        db = FirebaseFirestore.getInstance();
        final DocumentReference dr = db.collection("pesanan").document(id_pesanan);
        registration = dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.d("cekPesananAdmin","Pesanan tidak ditemukan!"+error);
                    registration.remove();
                    finishAffinity();
                    startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                }
                if (value != null && value.exists()) {
                    if (value.getString("status_pesanan").equals("Ditugaskan")){
                        kirim.setVisibility(View.GONE);
                        selesai.setVisibility(View.GONE);
                        navigasi.setVisibility(View.GONE);
                        lacak.setVisibility(View.GONE);
                        hapus.setVisibility(View.VISIBLE);
                        pemesan.setText(value.getString("pemilik_toko"));
                        toko.setText(value.getString("tujuan_toko"));
                        jml.setText(value.get("jumlah_pesanan").toString());
                        driver.setText(value.getString("nama_driver"));
                        time.setText(ambilWaktu(value.getDate("created_at")));
                        create.setText(value.getString("nama_create"));
                        status.setText(value.getString("status_pesanan"));
                        ambilTelAlamatToko(value.getString("id_toko"),id_pesanan);
                        ambilTelDriver(value.getString("id_driver"),id_pesanan);
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }else if (value.getString("status_pesanan").equals("Dalam Pengiriman")){
                        kirim.setVisibility(View.GONE);
                        selesai.setVisibility(View.VISIBLE);
                        navigasi.setVisibility(View.GONE);
                        lacak.setVisibility(View.VISIBLE);
                        hapus.setVisibility(View.GONE);
                        pemesan.setText(value.getString("pemilik_toko"));
                        toko.setText(value.getString("tujuan_toko"));
                        jml.setText(value.get("jumlah_pesanan").toString());
                        driver.setText(value.getString("nama_driver"));
                        time.setText(ambilWaktu(value.getDate("created_at")));
                        create.setText(value.getString("nama_create"));
                        status.setText(value.getString("status_pesanan"));
                        ambilTelAlamatToko(value.getString("id_toko"),id_pesanan);
                        ambilTelDriver(value.getString("id_driver"),id_pesanan);
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }else{
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.d("cekPesananAdmin","Pesanan tidak ditemukan!");
                        registration.remove();
                        finishAffinity();
                        startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                    }
                } else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.d("cekPesananAdmin","Pesanan tidak ditemukan!");
                    registration.remove();
                    finishAffinity();
                    startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                }
            }
        });
    }
    private void cekPesananDriver(String id_pesanan){
        db = FirebaseFirestore.getInstance();
        final DocumentReference dr = db.collection("pesanan").document(id_pesanan);
        registration = dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.d("cekPesananAdmin","Pesanan tidak ditemukan!"+error);
                    registration.remove();
                    finishAffinity();
                    startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                }
                if (value != null && value.exists()) {
                    if (value.getString("status_pesanan").equals("Ditugaskan")){
                        kirim.setVisibility(View.VISIBLE);
                        selesai.setVisibility(View.GONE);
                        navigasi.setVisibility(View.GONE);
                        lacak.setVisibility(View.GONE);
                        hapus.setVisibility(View.GONE);
                        pemesan.setText(value.getString("pemilik_toko"));
                        toko.setText(value.getString("tujuan_toko"));
                        jml.setText(value.get("jumlah_pesanan").toString());
                        driver.setText(value.getString("nama_driver"));
                        time.setText(ambilWaktu(value.getDate("created_at")));
                        create.setText(value.getString("nama_create"));
                        status.setText(value.getString("status_pesanan"));
                        ambilTelAlamatToko(value.getString("id_toko"),id_pesanan);
                        ambilTelDriver(value.getString("id_driver"),id_pesanan);
                        id_toko = value.getString("id_toko");
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }else if (value.getString("status_pesanan").equals("Dalam Pengiriman")){
                        kirim.setVisibility(View.GONE);
                        selesai.setVisibility(View.VISIBLE);
                        navigasi.setVisibility(View.VISIBLE);
                        lacak.setVisibility(View.GONE);
                        hapus.setVisibility(View.GONE);
                        pemesan.setText(value.getString("pemilik_toko"));
                        toko.setText(value.getString("tujuan_toko"));
                        jml.setText(value.get("jumlah_pesanan").toString());
                        driver.setText(value.getString("nama_driver"));
                        time.setText(ambilWaktu(value.getDate("created_at")));
                        create.setText(value.getString("nama_create"));
                        status.setText(value.getString("status_pesanan"));
                        ambilTelAlamatToko(value.getString("id_toko"),id_pesanan);
                        ambilTelDriver(value.getString("id_driver"),id_pesanan);
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        mLocationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                if (locationResult == null) {
                                    return;
                                }
                                lastlocation = locationResult.getLastLocation();
                                updateLokasi();
                            }
                        };
                        getLocationUpdates();
                    }else{
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.d("cekPesananAdmin","Pesanan tidak ditemukan!");
                        registration.remove();
                        finishAffinity();
                        startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                    }
                } else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.d("cekPesananAdmin","Pesanan tidak ditemukan!");
                    registration.remove();
                    finishAffinity();
                    startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                }
            }
        });
    }
    private void ambilTelAlamatToko(String id_toko,String id_pesanan){
        db = FirebaseFirestore.getInstance();
        db.collection("pelanggan").document(id_toko).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    teltoko.setText(task.getResult().getString("telepon"));
                    alamat.setText(task.getResult().getString("alamat"));
                    lat = task.getResult().getString("latitude");
                    lng = task.getResult().getString("longitude");
                }else{
                    hapusPesanan(id_pesanan);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hapusPesanan(id_pesanan);
            }
        });
    }
    private void ambilTelDriver(String id_driver,String id_pesanan){
        db = FirebaseFirestore.getInstance();
        db.collection("pengguna").document(id_driver).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    teldriver.setText(task.getResult().getString("telepon"));
                }else{
                    hapusPesanan(id_pesanan);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hapusPesanan(id_pesanan);
            }
        });
    }
    private String ambilWaktu(Date date) {
        return new SimpleDateFormat("dd MMMM yyyy \nhh:mm:ss a", Locale.getDefault()).format(date);
    }
    private void cekPengiriman(String id_pengiriman){
        cekInternet();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser akun = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("pesanan").whereEqualTo("id_driver",akun.getUid()).whereEqualTo("status_pesanan","Dalam Pengiriman").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().getDocuments().size()>0) {
                    Toast.makeText(LihatPesanan.this, "Anda sedang melakukan pengiriman lain!",
                            Toast.LENGTH_LONG).show();
                    registration.remove();
                    finishAffinity();
                    startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                }else if (task.getResult().getDocuments().size()<1){
                    kirimSekarang(id_pengiriman);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                kirimSekarang(id_pengiriman);
            }
        });
    }
    private void kirimSekarang(String id_pengiriman){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memulai pengiriman pesanan...");
        progressDialog.show();
        cekInternet();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> pesanan = new HashMap<>();
        pesanan.put("status_pesanan", Objects.requireNonNull("Dalam Pengiriman"));
        pesanan.put("dikirim_at", FieldValue.serverTimestamp());
        db.collection("pesanan").document(id_pengiriman).update(pesanan).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                        sesiLogin user = new sesiLogin();
                        FirebaseUser akun = FirebaseAuth.getInstance().getCurrentUser();
                        String lat,lng;
                        lat = String.valueOf(lastKnownLocation.getLatitude());
                        lng = String.valueOf(lastKnownLocation.getLongitude());
                        Map<String, Object> pengiriman = new HashMap<>();
                        pengiriman.put("id_pengiriman", Objects.requireNonNull(id_pengiriman));
                        pengiriman.put("created_at", FieldValue.serverTimestamp());
                        pengiriman.put("update_at", FieldValue.serverTimestamp());
                        pengiriman.put("latitude",Objects.requireNonNull(lat));
                        pengiriman.put("longitude",Objects.requireNonNull(lng));
                        pengiriman.put("pemesan",Objects.requireNonNull(pemesan.getText().toString().trim()));
                        pengiriman.put("toko",Objects.requireNonNull(toko.getText().toString().trim()));
                        pengiriman.put("jumlah_pesanan",Objects.requireNonNull(jml.getText()));
                        pengiriman.put("driver",Objects.requireNonNull(user.getNama()));
                        pengiriman.put("id_driver",Objects.requireNonNull(akun.getUid()));
                        pengiriman.put("email_driver",Objects.requireNonNull(akun.getEmail()));
                        pengiriman.put("id_toko",Objects.requireNonNull(id_toko));
                        DocumentReference reference = db.collection("pengiriman").document(id_pengiriman);
                        reference.set(pengiriman).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Toast.makeText(LihatPesanan.this, "Pengiriman baru berhasil dilaporkan!\nSelamat melakukan perjalanan dan hati-hati...",
                                        Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Toast.makeText(LihatPesanan.this, "Pengiriman baru gagal dilaporkan!\nPastikan koneksi internet anda stabil",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(LihatPesanan.this, "Pengiriman baru gagal dilaporkan!\nPastikan koneksi internet anda stabil",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                            }
                        } else {
                            Log.d("Lokasi Driver", "Current location is null. Using defaults.");
                            Log.e("Lokasi Driver", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    private void updateLokasi(){
        db = FirebaseFirestore.getInstance();
        Map<String, Object> upengiriman = new HashMap<>();
        upengiriman.put("latitude", Objects.requireNonNull(String.valueOf(lastlocation.getLatitude())));
        upengiriman.put("longitude", Objects.requireNonNull(String.valueOf(lastlocation.getLongitude())));
        upengiriman.put("update_at", FieldValue.serverTimestamp());
        db.collection("pengiriman").document(getIntent().getStringExtra("idPesanan")).update(upengiriman).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("updateLokasi","Update Berhasil");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("updateLokasi","Update Gagal"+e);
            }
        });
    }
    private void hapusPesanan(String id_pesanan){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menghapus pesanan...");
        progressDialog.show();
        cekInternet();
        db = FirebaseFirestore.getInstance();
        db.collection("pesanan").document(id_pesanan).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                registration.remove();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(LihatPesanan.this, "Pesanan berhasil dihapus!",
                        Toast.LENGTH_LONG).show();
                finishAffinity();
                startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(LihatPesanan.this, "Pesanan gagal dihapus!\nPastikan koneksi internet anda stabil.",
                        Toast.LENGTH_LONG).show();
                registration.remove();
                finishAffinity();
                startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
            }
        });
    }
    private void selesaiPengiriman(String id_pesanan){
        cekInternet();
        db = FirebaseFirestore.getInstance();
        db.collection("pesanan").document(id_pesanan).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                db.collection("pengiriman").document(id_pesanan).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(LihatPesanan.this, "Pesanan berhasil diselesaikan!",
                                Toast.LENGTH_LONG).show();
                        registration.remove();
                        finishAffinity();
                        startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(LihatPesanan.this, "Pesanan gagal diselesaikan!\nPastikan koneksi internet anda stabil.",
                                Toast.LENGTH_LONG).show();
                        registration.remove();
                        finishAffinity();
                        startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(LihatPesanan.this, "Pesanan gagal diselesaikan!\nPastikan koneksi internet anda stabil.",
                        Toast.LENGTH_LONG).show();
                registration.remove();
                finishAffinity();
                startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
            }
        });
    }
    private void PesananSelesai(String id_pesanan){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Menyelesaikan pesanan anda...");
        progressDialog.show();
        cekInternet();
        mLocationClient.removeLocationUpdates(mLocationCallback);
        registration.remove();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pengiriman").document(id_pesanan).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> selesai = new HashMap<>();
                selesai.put("id_pesanan", Objects.requireNonNull(documentSnapshot.getString("id_pengiriman")));
                selesai.put("created_at", FieldValue.serverTimestamp());
                selesai.put("last_lat",Objects.requireNonNull(documentSnapshot.getString("latitude")));
                selesai.put("last_long",Objects.requireNonNull(documentSnapshot.getString("longitude")));
                selesai.put("pemesan",Objects.requireNonNull(documentSnapshot.getString("pemesan")));
                selesai.put("toko",Objects.requireNonNull(documentSnapshot.getString("toko")));
                selesai.put("jumlah_pesanan",Objects.requireNonNull(documentSnapshot.getString("jumlah_pesanan")));
                selesai.put("driver",Objects.requireNonNull(documentSnapshot.getString("driver")));
                selesai.put("id_driver",Objects.requireNonNull(documentSnapshot.getString("id_driver")));
                selesai.put("email_driver",Objects.requireNonNull(documentSnapshot.getString("email_driver")));
                selesai.put("id_toko",Objects.requireNonNull(documentSnapshot.getString("id_toko")));
                DocumentReference reference = db.collection("riwayat").document(id_pesanan);
                reference.set(selesai).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        selesaiPengiriman(id_pesanan);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(LihatPesanan.this, "Riwayat pesanan gagal ditambahkan!\nPastikan koneksi internet anda stabil.",
                                Toast.LENGTH_LONG).show();
                        registration.remove();
                        finishAffinity();
                        startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(LihatPesanan.this, "Pesanan gagal diselesaikan!\nPastikan koneksi internet anda stabil.",
                        Toast.LENGTH_LONG).show();
                registration.remove();
                finishAffinity();
                startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
            }
        });
    }
    private void PesananSelesaiAdmin(String id_pesanan){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Menyelesaikan pesanan anda...");
        progressDialog.show();
        cekInternet();
        registration.remove();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pengiriman").document(id_pesanan).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> selesai = new HashMap<>();
                selesai.put("id_pesanan", Objects.requireNonNull(documentSnapshot.getString("id_pengiriman")));
                selesai.put("created_at", FieldValue.serverTimestamp());
                selesai.put("last_lat",Objects.requireNonNull(documentSnapshot.getString("latitude")));
                selesai.put("last_long",Objects.requireNonNull(documentSnapshot.getString("longitude")));
                selesai.put("pemesan",Objects.requireNonNull(documentSnapshot.getString("pemesan")));
                selesai.put("toko",Objects.requireNonNull(documentSnapshot.getString("toko")));
                selesai.put("jumlah_pesanan",Objects.requireNonNull(documentSnapshot.getString("jumlah_pesanan")));
                selesai.put("driver",Objects.requireNonNull(documentSnapshot.getString("driver")));
                selesai.put("id_driver",Objects.requireNonNull(documentSnapshot.getString("id_driver")));
                selesai.put("email_driver",Objects.requireNonNull(documentSnapshot.getString("email_driver")));
                selesai.put("id_toko",Objects.requireNonNull(documentSnapshot.getString("id_toko")));
                DocumentReference reference = db.collection("riwayat").document(id_pesanan);
                reference.set(selesai).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        selesaiPengiriman(id_pesanan);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(LihatPesanan.this, "Riwayat pesanan gagal ditambahkan!\nPastikan koneksi internet anda stabil.",
                                Toast.LENGTH_LONG).show();
                        registration.remove();
                        finishAffinity();
                        startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(LihatPesanan.this, "Pesanan gagal diselesaikan!\nPastikan koneksi internet anda stabil.",
                        Toast.LENGTH_LONG).show();
                registration.remove();
                finishAffinity();
                startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        registration.remove();
        finishAffinity();
        startActivity(new Intent(LihatPesanan.this, PesananActivity.class));
    }
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void cekInternet(){
        if (!koneksiTerhubung()){
            registration.remove();
            finishAffinity();
            startActivity(new Intent(LihatPesanan.this, NoInternetActivity.class));
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(LihatPesanan.this, location.getLatitude()+""+location.getLongitude(), Toast.LENGTH_SHORT).show();
    }
    private void izinLokasi() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
    private void getLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10*1000);
        locationRequest.setFastestInterval(9*1000);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }
}