package com.laurensius_dede_suhardiman.fireearlywarningsystem.model;

public class Notif {
    String id;
    String nama;
    String nama_toko;
    String alamat_toko;
    String notifikasi;

    public Notif(
            String id,
            String nama,
            String nama_toko,
            String alamat_toko,
            String notifikasi
    ){
        this.id = id;
        this.nama = nama;
        this.nama_toko = nama_toko;
        this.alamat_toko = alamat_toko;
        this.notifikasi = notifikasi;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getNama_toko() {
        return nama_toko;
    }

    public String getAlamat_toko() {
        return alamat_toko;
    }

    public String getNotifikasi() {
        return notifikasi;
    }
}
