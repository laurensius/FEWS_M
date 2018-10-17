package com.laurensius_dede_suhardiman.fireearlywarningsystem.model;

public class Client {

    String id;
    String nama;
    String nama_toko;
    String alamat_toko;
    String api_key;
    String cat_suhu;
    String cat_asap;
    String cat_api;

    public Client(
            String id,
            String nama,
            String nama_toko,
            String alamat_toko,
            String api_key,
            String cat_suhu,
            String cat_asap,
            String cat_api){
        this.id = id;
        this.nama = nama;
        this.nama_toko = nama_toko;
        this.alamat_toko = alamat_toko;
        this.api_key = api_key;
        this.cat_suhu = cat_suhu;
        this.cat_asap = cat_asap;
        this.cat_api = cat_api;
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

    public String getAlamat() {
        return alamat_toko;
    }

    public String getApi_key() {
        return api_key;
    }

    public String getCat_suhu() {
        return cat_suhu;
    }

    public String getCat_asap() {
        return cat_asap;
    }

    public String getCat_api() {
        return cat_api;
    }
}
