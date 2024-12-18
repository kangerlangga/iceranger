package id.web.prima.iceranger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DetailPelanggan extends AppCompatActivity implements OnMapReadyCallback {
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
    Double lat_p,long_p;
    TextView ubah,namatokopemilik,telepon,alamat; Marker penanda;
    FirebaseFirestore db; ProgressDialog progressDialog;
    ListenerRegistration registration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_detail_pelanggan);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        ubah = findViewById(R.id.uDetail_pelanggan);
        namatokopemilik = findViewById(R.id.namatoko_detailPelanggan);
        telepon = findViewById(R.id.tel_detailPelanggan);
        alamat = findViewById(R.id.alamat_detailPelanggan);
        cekizinlokasi();
        if (locationPermissionGranted){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.peta_detailpelanggan);
            mapFragment.getMapAsync(this);
        }
        ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = getIntent().getStringExtra("detailId".toString());
                registration.remove();
                finishAffinity();
                startActivity(new Intent(DetailPelanggan.this, EditPelanggan.class).putExtra("editID",id));
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
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Set the position of the map.
        ambilLokasi();
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
                saatini.icon(setIcon(DetailPelanggan.this,R.mipmap.pin_pelanggan_200));
                penanda = map.addMarker(saatini);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(lR,DEFAULT_ZOOM));
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }else{
                Toast.makeText(DetailPelanggan.this, "Lokasi tidak diketahui!",
                        Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void cekizinlokasi(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            Toast.makeText(DetailPelanggan.this, "Akses lokasi ditolak!",
//                    Toast.LENGTH_LONG).show();
            locationPermissionGranted = false;
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }else{
            locationPermissionGranted = true;
        }
    }
    private boolean koneksiTerhubung(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetwork()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void cekInternet(){
        if (!koneksiTerhubung()){
            registration.remove();
            finishAffinity();
            startActivity(new Intent(DetailPelanggan.this, NoInternetActivity.class));
        }
    }
    private void ambilLokasi(){
        cekInternet();
        String id = getIntent().getStringExtra("detailId".toString());
        if (id.equals("") || id.isEmpty()){
            registration.remove();
            finish();
            startActivity(new Intent(DetailPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
        }else{
            ambilDB(id);
        }
    }
    private void ambilDB(String id_doc){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sinkronisasi data pelanggan...");
        progressDialog.show();
        db = FirebaseFirestore.getInstance();
        final DocumentReference dr = db.collection("pelanggan").document(id_doc);
        registration = dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(DetailPelanggan.this, "Detail pelanggan tidak ditemukan!"+error,
                            Toast.LENGTH_LONG).show();
                    registration.remove();
                    finishAffinity();
                    startActivity(new Intent(DetailPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
                }
                if (value != null && value.exists()) {
                    namatokopemilik.setText(value.getString("namatoko")+"("+value.getString("pemilik")+")");
                    telepon.setText(value.getString("telepon"));
                    alamat.setText(value.getString("alamat"));
                    lat_p = Double.parseDouble(value.getString("latitude"));
                    long_p = Double.parseDouble(value.getString("longitude"));
                    LatLng detail = new LatLng(lat_p,long_p);
                    addPenanda(detail);
                } else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(DetailPelanggan.this, "Detail pelanggan tidak ditemukan!",
                            Toast.LENGTH_LONG).show();
                    registration.remove();
                    finishAffinity();
                    startActivity(new Intent(DetailPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
                }
            }
        });
    }
    private BitmapDescriptor setIcon(Context context, int vectorResId)
    {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        registration.remove();
        finishAffinity();
        startActivity(new Intent(DetailPelanggan.this, DashboardActivity.class).putExtra("to","menu_toko"));
    }
}