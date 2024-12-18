package id.web.prima.iceranger;

import com.google.firebase.Timestamp;

public class ModelPesanan {
    private String email_create,email_driver,id_driver,id_toko,nama_create,nama_driver,pemilik_toko,status_pesanan,tujuan_toko,id_Doc;
    private Timestamp created_at;
    private Integer jumlah_pesanan;
    protected void batal(){
        email_create=email_driver=id_driver=id_toko=nama_create=nama_driver=pemilik_toko=status_pesanan=tujuan_toko=id_Doc = "";
        created_at = null;
        jumlah_pesanan = null;
    }
    public String getEmail_create() {
        return email_create;
    }
    public void setEmail_create(String email_create) {
        this.email_create = email_create;
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
    public String getId_toko() {
        return id_toko;
    }
    public void setId_toko(String id_toko) {
        this.id_toko = id_toko;
    }
    public String getNama_create() {
        return nama_create;
    }
    public void setNama_create(String nama_create) {
        this.nama_create = nama_create;
    }
    public String getNama_driver() {
        return nama_driver;
    }
    public void setNama_driver(String nama_driver) {
        this.nama_driver = nama_driver;
    }
    public String getPemilik_toko() {
        return pemilik_toko;
    }
    public void setPemilik_toko(String pemilik_toko) {
        this.pemilik_toko = pemilik_toko;
    }
    public String getStatus_pesanan() {
        return status_pesanan;
    }
    public void setStatus_pesanan(String status_pesanan) {
        this.status_pesanan = status_pesanan;
    }
    public String getTujuan_toko() {
        return tujuan_toko;
    }
    public void setTujuan_toko(String tujuan_toko) {
        this.tujuan_toko = tujuan_toko;
    }
    public Timestamp getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
    public Integer getJumlah_pesanan() {
        return jumlah_pesanan;
    }
    public void setJumlah_pesanan(Integer jumlah_pesanan) {
        this.jumlah_pesanan = jumlah_pesanan;
    }
    public String getId_Doc() {
        return id_Doc;
    }
    public void setId_Doc(String id_Doc) {
        this.id_Doc = id_Doc;
    }
}
