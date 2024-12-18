package id.web.prima.iceranger;

import com.google.firebase.Timestamp;

public class ModelPelanggan {
    private String namatoko,pemilik,telepon,alamat,email_create,nama_create,latitude,longitude,id_Pelanggan;
    private Timestamp created_at;
    public String getNamatoko() {
        return namatoko;
    }
    public void setNamatoko(String namatoko) {
        this.namatoko = namatoko;
    }
    public String getTelepon() {
        return telepon;
    }
    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }
    public String getAlamat() {
        return alamat;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    public String getPemilik() {
        return pemilik;
    }
    public void setPemilik(String pemilik) {
        this.pemilik = pemilik;
    }
    public String getEmail_create() {
        return email_create;
    }
    public void setEmail_create(String email_create) {
        this.email_create = email_create;
    }
    public String getNama_create() {
        return nama_create;
    }
    public void setNama_create(String nama_create) {
        this.nama_create = nama_create;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public Timestamp getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
    public String getId_Pelanggan() {
        return id_Pelanggan;
    }
    public void setId_Pelanggan(String id_Pelanggan) {
        this.id_Pelanggan = id_Pelanggan;
    }
    protected void batal(){
        namatoko=pemilik=telepon=alamat=email_create=nama_create=latitude=longitude=id_Pelanggan = "";
        created_at = null;
    }
}