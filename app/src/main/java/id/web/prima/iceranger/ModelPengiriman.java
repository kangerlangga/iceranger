package id.web.prima.iceranger;

import com.google.firebase.Timestamp;

public class ModelPengiriman {
    private String driver,email_driver,id_driver,id_pengiriman,jumlah_pesanan,latitude,longitude,pemesan,toko,id_toko,id_doc;
    private Timestamp created_at,update_at;

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
    public String getId_pengiriman() {
        return id_pengiriman;
    }
    public void setId_pengiriman(String id_pengiriman) {
        this.id_pengiriman = id_pengiriman;
    }
    public String getJumlah_pesanan() {
        return jumlah_pesanan;
    }
    public void setJumlah_pesanan(String jumlah_pesanan) {
        this.jumlah_pesanan = jumlah_pesanan;
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
    public Timestamp getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
    public Timestamp getUpdate_at() {
        return update_at;
    }
    public void setUpdate_at(Timestamp update_at) {
        this.update_at = update_at;
    }
    public String getId_doc() {
        return id_doc;
    }
    public void setId_doc(String id_doc) {
        this.id_doc = id_doc;
    }
    public String getId_toko() {
        return id_toko;
    }
    public void setId_toko(String id_toko) {
        this.id_toko = id_toko;
    }
}
