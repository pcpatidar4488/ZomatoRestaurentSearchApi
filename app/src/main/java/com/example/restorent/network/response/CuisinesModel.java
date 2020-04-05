package com.example.restorent.network.response;



public class CuisinesModel {
    private CuisineInfoModel cuisine;

    private CuisineInfoModel getCuisineInfo() {
        return cuisine;
    }

    public int getCuisine_id() {
        return getCuisineInfo().cuisine_id;
    }

    public String getCuisine_name() {
        return getCuisineInfo().cuisine_name;
    }

    private class CuisineInfoModel {
        private int cuisine_id;
        private String cuisine_name;
    }
}
