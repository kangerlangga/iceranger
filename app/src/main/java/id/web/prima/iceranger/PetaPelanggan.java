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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PetaPelanggan extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener,GoogleMap.OnMarkerDragListener {
    private static final String TAG = TambahPelanggan.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    // A default location and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-7.4937746137812455, 112.72162021065358);
    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    String lat_toko,long_toko,sumber;
    Button batal,gunakan,konf; Marker penanda;
    FirebaseFirestore db;ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        cekData();
        setContentView(R.layout.activity_peta_pelanggan);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        gunakan = findViewById(R.id.btngunakan_petapelanggan);
        konf = findViewById(R.id.btnpilih_petapelanggan);
        batal = findViewById(R.id.batal_petapelanggan);
        reset();
        cekizinlokasi();
        if (locationPermissionGranted){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.peta_pelanggan);
            mapFragment.getMapAsync(this);
        }
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
        gunakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gunakanLokasiSekarang();
            }
        });
        konf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lat_toko.equals("") || long_toko.equals("") || lat_toko.isEmpty() || long_toko.isEmpty()){
                    Toast.makeText(PetaPelanggan.this, "Anda belum memilih lokasi pelanggan!",
                            Toast.LENGTH_LONG).show();
                }else{
                    String n,p,t,a,lat,lng;
                    n = getIntent().getStringExtra("namatoko").toString().trim();
                    p = getIntent().getStringExtra("pemilik").toString().trim();
                    t = getIntent().getStringExtra("telepon").toString().trim();
                    a = getIntent().getStringExtra("alamat").toString().trim();
                    lat = lat_toko.trim();
                    lng = long_toko.trim();
                    new AlertDialog.Builder(PetaPelanggan.this)
                            .setTitle("Ice Ranger")
                            .setMessage("Apakah seluruh informasi yang diberikan sudah sesuai?")
                            .setPositiveButton("SUDAH", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (getIntent().getStringExtra("sumber").equals("Tambah")){
                                        String ec,nc;
                                        ec = getIntent().getStringExtra("email_create").toString().trim();
                                        nc = getIntent().getStringExtra("nama_create").toString().trim();
                                        tambahPelanggan(n,p,t,a,ec,nc,lat,lng);
                                    }else if (getIntent().getStringExtra("sumber").equals("Edit")){
                                        String i,eu;
                                        i = getIntent().getStringExtra("editID").toString().trim();
                                        eu = getIntent().getStringExtra("email_update").toString().trim();
                                        editPelanggan(i,n,p,t,a,eu,lat,lng);
                                    }else{
                                        Toast.makeText(PetaPelanggan.this, "Sumber tidak diketahui!",
                                                Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(PetaPelanggan.this, DashboardActivity.class).putExtra("to","menu_beranda"));
                                        finishAffinity();
                                    }
                                }
                            })
                            .setNegativeButton("BELUM", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map1) {
        this.map = map1;
        map.setOnMapClickListener(this);
        map.setOnMarkerDragListener(this);
        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
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
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
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

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void cekizinlokasi(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted = false;
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }else{
            locationPermissionGranted = true;
        }
    }
    private void gunakanLokasiSekarang(){
        cekInternet();
        try {
            if (lastKnownLocation != null) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude(), 1);
                LatLng lR = new LatLng(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude());
                if (penanda != null){
                    penanda.remove();
                }
                MarkerOptions saatini = new MarkerOptions();
                saatini.title(addresses.get(0).getAddressLine(0));
                saatini.position(lR);
                saatini.draggable(true);
                penanda = map.addMarker(saatini);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(lR,DEFAULT_ZOOM));
                lat_toko = String.valueOf(lastKnownLocation.getLatitude());
                long_toko = String.valueOf(lastKnownLocation.getLongitude());
            }else{
                Toast.makeText(PetaPelanggan.this, "Lokasi tidak diketahui!",
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void addPenanda(LatLng lokasi){
        cekInternet();
        try {
            if (lokasi != null) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lokasi.latitude,
                        lokasi.longitude, 1);
                LatLng lR = new LatLng(lokasi.latitude,
                        lokasi.longitude);
                if (penanda != null){
                    penanda.remove();
                }
                MarkerOptions saatini = new MarkerOptions();
                saatini.title(addresses.get(0).getAddressLine(0));
                saatini.position(lR);
                saatini.draggable(true);
                penanda = map.addMarker(saatini);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(lR,DEFAULT_ZOOM));
                lat_toko = String.valueOf(lokasi.latitude);
                long_toko = String.valueOf(lokasi.longitude);
            }else{
                Toast.makeText(PetaPelanggan.this, "Lokasi tidak diketahui!",
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        cekInternet();
        addPenanda(latLng);
    }
    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
    }
    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        cekInternet();
        addPenanda(marker.getPosition());
    }
    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
    }
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void cekInternet(){
        if (!koneksiTerhubung()){
            finishAffinity();
            startActivity(new Intent(PetaPelanggan.this, NoInternetActivity.class));
        }
    }
    private void reset(){
        cekInternet();
        lat_toko = "";
        long_toko = "";
        if (penanda != null){
            penanda.remove();
        }
    }
    private void cekData(){
        reset();
        sumber = getIntent().getStringExtra("sumber").toString();
        if (sumber.equals("Tambah")){
        }else if (sumber.equals("Edit")){
        }else{
            Toast.makeText(PetaPelanggan.this, "Sumber tidak diketahui!",
                    Toast.LENGTH_LONG).show();
            finishAffinity();
            startActivity(new Intent(PetaPelanggan.this, DashboardActivity.class).putExtra("to","menu_beranda"));
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(PetaPelanggan.this)
                .setTitle("Ice Ranger")
                .setMessage("Apakah anda yakin ingin kembali? Informasi tidak akan disimpan.")
                .setPositiveButton("KEMBALI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getIntent().getStringExtra("sumber").equals("Tambah")){
                            startActivity(new Intent(PetaPelanggan.this, TambahPelanggan.class));
                        }else if (getIntent().getStringExtra("sumber").equals("Edit")){
                            startActivity(new Intent(PetaPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
                        }else{
                            Toast.makeText(PetaPelanggan.this, "Sumber tidak diketahui!",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PetaPelanggan.this, DashboardActivity.class));
                        }
                        finishAffinity();
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
    private void tambahPelanggan(String n,String p,String t,String a,String ec,String nc,String lat,String lng){
        progressDialog = new ProgressDialog(PetaPelanggan.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menambahkan data...");
        progressDialog.show();
        db = FirebaseFirestore.getInstance();
        Map<String, Object> pelanggan = new HashMap<>();
        pelanggan.put("namatoko", Objects.requireNonNull(n));
        pelanggan.put("pemilik", Objects.requireNonNull(p));
        pelanggan.put("telepon", Objects.requireNonNull(t));
        pelanggan.put("alamat", Objects.requireNonNull(a));
        pelanggan.put("email_create", Objects.requireNonNull(ec));
        pelanggan.put("nama_create", Objects.requireNonNull(nc));
        pelanggan.put("latitude", Objects.requireNonNull(lat));
        pelanggan.put("longitude", Objects.requireNonNull(lng));
        pelanggan.put("created_at", FieldValue.serverTimestamp());

        db.collection("pelanggan").add(pelanggan).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(PetaPelanggan.this, "Data pelanggan berhasil ditambahkan!",
                        Toast.LENGTH_LONG).show();
                startActivity(new Intent(PetaPelanggan.this, TambahPelanggan.class));
                finishAffinity();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PetaPelanggan.this, "Data pelanggan gagal ditambahkan!\nPastikan koneksi internet anda stabil.",
                        Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }
    private void editPelanggan(String id,String n,String p,String t,String a,String eu,String lat,String lng){
        progressDialog = new ProgressDialog(PetaPelanggan.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang memperbarui data...");
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> pelanggan = new HashMap<>();
        pelanggan.put("namatoko", Objects.requireNonNull(n));
        pelanggan.put("pemilik", Objects.requireNonNull(p));
        pelanggan.put("telepon", Objects.requireNonNull(t));
        pelanggan.put("alamat", Objects.requireNonNull(a));
        pelanggan.put("latitude", Objects.requireNonNull(lat));
        pelanggan.put("longitude", Objects.requireNonNull(lng));
        pelanggan.put("updated_at", FieldValue.serverTimestamp());
        pelanggan.put("email_update", Objects.requireNonNull(eu));
        db.collection("pelanggan").document(id).update(pelanggan).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PetaPelanggan.this, "Data pelanggan berhasil diperbarui!",
                        Toast.LENGTH_LONG).show();
                finishAffinity();
                startActivity(new Intent(PetaPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PetaPelanggan.this, "Data pelanggan gagal diperbarui!\nPastikan koneksi internet anda stabil.",
                        Toast.LENGTH_LONG).show();
                finishAffinity();
                startActivity(new Intent(PetaPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
            }
        });
    }
}