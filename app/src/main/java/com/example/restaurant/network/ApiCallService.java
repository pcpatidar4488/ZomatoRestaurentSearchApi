package com.example.restaurant.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.restaurant.network.response.RestaurantSearchResponseModel;
import com.example.restaurant.utils.Helper;
import com.example.restaurant.utils.MyProgressDialog;
import com.example.restaurant.utils.ParameterConstant;
import com.example.restaurant.R;

import org.greenrobot.eventbus.EventBus;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ApiCallService extends IntentService {

    static Object request;
    static String id;
    static Activity context;
    static Boolean bool;
    static String stock_in_hand_date;


    public interface Action {
        String BASE_URL = "https://developers.zomato.com/api/";
        String API_KEY = "80f1fcaea917becddde5b56fb44301d0";
        int PERMISSIONS_BUZZ_REQUEST = 0xABC;
        String SERVICE_NAME = "NetworkingService";
        String ERROR = "Some thing went wrong!!!";
        String ACTION_SIGN_UP = "ACTION_SIGN_UP";
        String ACTION_RESTAURANT_LIST = "ACTION_RESTAURANT_LIST";

    }

    public ApiCallService() {
        super(Action.SERVICE_NAME);
    }

    public static void action(Activity ctx, Object request, String action, Boolean bool) {
        if (!Helper.isNetworkAvailable(ctx)){
            getDialog(ctx, ParameterConstant.NO_INTERNET_TITTLE,ParameterConstant.NO_INTERNET_MESSAGE,request,action);
            return;
        }
        if (bool){
            MyProgressDialog.getInstance(ctx).show();
        }
        context = ctx;
        ApiCallService.request = request;
        ApiCallService.bool = bool;
        Intent intent = new Intent(ctx, ApiCallService.class);
        intent.setAction(action);
        ctx.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Api api=ThisApp.getApi(getApplicationContext());
        if (action.equals(ApiCallService.Action.ACTION_SIGN_UP)){
            api.signup((Map) request).enqueue(new Local<RestaurantSearchResponseModel>());
        }else if(action.equals(ApiCallService.Action.ACTION_RESTAURANT_LIST)){
            Map<String,String> map= (Map<String,String>) request;
          //  api.getPoint(ParameterConstant.COMPANY_ID,ParameterConstant.COMPANY_ID, map.get("account_name"), map.get("start_date"), map.get("end_date")).enqueue(new Local<PointResponse>());
           if (!map.get("lan").equals("") && !map.get("lon").equals("")){
               api.getRestaurantListLatLon(map.get("q"),Double.parseDouble(map.get("lan")),
                       Double.parseDouble(map.get("lon")), Integer.parseInt(map.get("start")),
                       Integer.parseInt(map.get("count")))
                       .enqueue(new Local<RestaurantSearchResponseModel>());
           }else {
               api.getRestaurantList(map.get("q"),Integer.parseInt(map.get("start")),
                       Integer.parseInt(map.get("count")))
                       .enqueue(new Local<RestaurantSearchResponseModel>());
           }

        }
    }


    class Local<T> implements Callback<T> {

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            MyProgressDialog.setDismiss();
            if (response.code() == 200) {
                T body = response.body();
                EventBus.getDefault().post(body);
            }else {
                if (response.code() !=204){
                    EventBus.getDefault().post(Action.ERROR + " " + response.code());
                }
            }
        }
        @Override
        public void onFailure(Call<T> call, Throwable t) {
            MyProgressDialog.setDismiss();
            EventBus.getDefault().post(t.getMessage());
        }
    }

    static void getDialog(final Activity ctx, String tittle, String message, final Object request, final String action) {
        new AlertDialog.Builder(ctx)
                .setTitle(tittle)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ApiCallService.action(ctx,request,action,ApiCallService.bool);
                    }
                })

//                .setNegativeButton("Exit", null)
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }





}