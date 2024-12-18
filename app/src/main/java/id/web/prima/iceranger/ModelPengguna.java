package id.web.prima.iceranger;

public class ModelPengguna {
    String alamat, email, jabatan, nama, telepon, id_pengguna, id_Doc;
    public ModelPengguna(){}
    public ModelPengguna(String alamat, String email, String jabatan, String nama, String telepon, String id_pengguna) {
        this.alamat = alamat;
        this.email = email;
        this.jabatan = jabatan;
        this.nama = nama;
        this.telepon = telepon;
        this.id_pengguna = id_pengguna;
    }
    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }
    public String getId_pengguna() {
        return id_pengguna;
    }
    public void setId_pengguna(String id_pengguna) {
        this.id_pengguna = id_pengguna;
    }
    public String getId_Doc() {
        return id_Doc;
    }
    public void setId_Doc(String id_Doc) {
        this.id_Doc = id_Doc;
    }
}
