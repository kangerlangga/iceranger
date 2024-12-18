package id.web.prima.iceranger;

import com.google.firebase.Timestamp;

public class ModelRiwayat {
    private String driver,email_driver,id_driver,id_pesanan,jumlah_pesanan,last_lat,last_long,pemesan,toko,id_toko,id_doc;
    private Timestamp created_at;
    public String getDriver() {
        return driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }
    public String getEmail_driver() {
        return email_driver;
    }
    public void setEmail_driver(String email_driver) {
        this.email_driver = email_driver;
    }
    public String getId_driver() {
        return id_driver;
    }
    public void setId_driver(String id_driver) {
        this.id_driver = id_driver;
    }
    public String getId_pesanan() {
        return id_pesanan;
    }
    public void setId_pesanan(String id_pesanan) {
        this.id_pesanan = id_pesanan;
    }
    public String getJumlah_pesanan() {
        return jumlah_pesanan;
    }
    public void setJumlah_pesanan(String jumlah_pesanan) {
        this.jumlah_pesanan = jumlah_pesanan;
    }
    public String getLast_lat() {
        return last_lat;
    }
    public void setLast_lat(String last_lat) {
        this.last_lat = last_lat;
    }
    public String getLast_long() {
        return last_long;
    }
    public void setLast_long(String last_long) {
        this.last_long = last_long;
    }
    public String getPemesan() {
        return pemesan;
    }
    public void setPemesan(String pemesan) {
        this.pemesan = pemesan;
    }
    public String getToko() {
        return toko;
    }
    public void setToko(String toko) {
        this.toko = toko;
    }
    public String getId_toko() {
        return id_toko;
    }
    public void setId_toko(String id_toko) {
        this.id_toko = id_toko;
    }
    public String getId_doc() {
        return id_doc;
    }
    public void setId_doc(String id_doc) {
        this.id_doc = id_doc;
    }
    public Timestamp getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
