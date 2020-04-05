package com.example.restaurant.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.example.restaurant.R;

public class MyProgressDialog extends Dialog {
    static MyProgressDialog progressDialog;
    private com.victor.loading.rotate.RotateLoading rotateLoading;

    public MyProgressDialog(Context a) {
        super(a);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.custom_progress_dialog);
        this.rotateLoading = (com.victor.loading.rotate.RotateLoading) findViewById(R.id.loading_spinner);
        this.rotateLoading.start();
    }

    public static MyProgressDialog getInstance(Activity activity) {
        progressDialog = new MyProgressDialog(activity);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static MyProgressDialog getInstance(Activity activity, boolean b) {
        progressDialog = new MyProgressDialog(activity);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static void setDismiss() {
        try {
            progressDialog.dismiss();
        }catch (Exception e){
        }

    }
}
