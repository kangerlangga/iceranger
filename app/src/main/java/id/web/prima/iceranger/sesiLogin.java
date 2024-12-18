package id.web.prima.iceranger;

public class sesiLogin {
    private static String email, userid, nama, jabatan, telepon;
    protected String getEmail() {
        return email;
    }
    protected void setEmail(String email) {
        this.email = email;
    }
    protected String getUserid() {
        return userid;
    }
    protected void setUserid(String userid) {
        this.userid = userid;
    }
    protected String getNama() {
        return nama;
    }
    protected void setNama(String nama) {
        this.nama = nama;
    }
    protected String getJabatan() {
        return jabatan;
    }
    protected void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }
    protected String getTelepon() {
        return telepon;
    }
    protected void setTelepon(String telepon) {
        sesiLogin.telepon = telepon;
    }
    protected void logout(){
        setEmail("");
        setUserid("");
        setJabatan("");
        setNama("");
        setTelepon("");
    }
}