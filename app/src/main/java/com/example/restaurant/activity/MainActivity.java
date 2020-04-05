package com.example.restaurant.activity;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurant.adapter.RestaurantsListAdapter;
import com.example.restaurant.base.BaseActivity;
import com.example.restaurant.network.response.RestaurantModel;
import com.example.restaurant.utils.Preferences;
import com.example.restaurant.R;
import com.example.restaurant.network.ApiCallService;
import com.example.restaurant.network.response.RestaurantSearchResponseModel;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private LinearLayout layoutLocation;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private RestaurantsListAdapter mAdapter;
    private List<RestaurantModel> data;
    private AppCompatEditText search;
    private TextView noRecord,tvAddress;
    private ImageView iconFilter,iconClose;
    int pageNo=0, count = 10;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private static int REQUEST_ADDRESS = 1;
    private String id ="",lat="",lon="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = new ArrayList<RestaurantModel>();
        layoutManager = new LinearLayoutManager(this);
        findIds();
        Map map=new HashMap();
        map.put("lan", Preferences.getInstance(MainActivity.this).getLat());
        map.put("lon",Preferences.getInstance(MainActivity.this).getLon());
        map.put("q","");
        map.put("start",""+pageNo);
        map.put("count",""+count);
        ApiCallService.action(this,map,ApiCallService.Action.ACTION_RESTAURANT_LIST,true);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            pageNo+=count;
                            loading = false;
                            Map map=new HashMap();
                            map.put("lan",Preferences.getInstance(MainActivity.this).getLat());
                            map.put("lon",Preferences.getInstance(MainActivity.this).getLon());
                            map.put("q",search.getText().toString());
                            map.put("start",""+pageNo);
                            map.put("count",""+count);
                            ApiCallService.action(MainActivity.this,map,ApiCallService.Action.ACTION_RESTAURANT_LIST,false);
                        }

                    }
                }
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    pageNo = 0;
                    data = new ArrayList<>();
                    loading = true;
                    if (s.length()>2){
                        Map map=new HashMap();
                        map.put("lan",Preferences.getInstance(MainActivity.this).getLat());
                        map.put("lon",Preferences.getInstance(MainActivity.this).getLon());
                        map.put("q",search.getText().toString());
                        map.put("start",""+pageNo);
                        map.put("count",""+count);
                        ApiCallService.action(MainActivity.this,map,ApiCallService.Action.ACTION_RESTAURANT_LIST,false);
                    }else if (s.toString().equals("")){
                        Map map=new HashMap();
                        map.put("lan",""+Preferences.getInstance(MainActivity.this).getLat());
                        map.put("lon",""+Preferences.getInstance(MainActivity.this).getLon());
                        map.put("q","");
                        map.put("start",""+pageNo);
                        map.put("count",""+count);
                        ApiCallService.action(MainActivity.this,map,ApiCallService.Action.ACTION_RESTAURANT_LIST,false);
                    }
                    if (s.length()>0){
                        iconClose.setVisibility(View.VISIBLE);
                    }else {
                        iconClose.setVisibility(View.GONE);
                    }

                } catch (Exception e) {

                }
            }
        });

        iconClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });

        layoutLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddressPickerActivity.class), REQUEST_ADDRESS);
            }
        });

        iconFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(data, new Comparator() {

                    @Override
                    public int compare(Object a1, Object a2) {
                        final RestaurantModel app1 = (RestaurantModel) a1;
                        final RestaurantModel app2 = (RestaurantModel) a2;
                        return app1.getRestaurant().getAggregate_rating().toLowerCase(Locale.getDefault()).compareTo(app2.getRestaurant().getAggregate_rating().toLowerCase(Locale.getDefault()));
                    }
                });

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        if (Preferences.getInstance(MainActivity.this).getAddress().equals("")){
            tvAddress.setText("Select Address");
        }else {
            tvAddress.setText(Preferences.getInstance(MainActivity.this).getAddress());
        }
        super.onResume();
    }

    private void findIds() {
        layoutLocation = findViewById(R.id.layoutLocation);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        search = findViewById(R.id.search);
        noRecord = findViewById(R.id.noRecord);
        tvAddress = findViewById(R.id.tvAddress);
        iconFilter = findViewById(R.id.iconFilter);
        iconClose = findViewById(R.id.iconClose);
    }

    boolean isExit = false;

    @Override
    public void onBackPressed() {
            if (isExit) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
            isExit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_ADDRESS) {
                    String address = data.getStringExtra("address");
                    String[] latLong = data.getStringExtra("latLong").split(",");
                    Preferences.getInstance(MainActivity.this).setLat(latLong[0]);
                    Preferences.getInstance(MainActivity.this).setLon(latLong[1]);
                    Preferences.getInstance(MainActivity.this).setAddress(address);
                    this.tvAddress.setText(address);
                    pageNo = 0;
                    loading = true;
                        Map map=new HashMap();
                        map.put("lan",Preferences.getInstance(MainActivity.this).getLat());
                        map.put("lon",Preferences.getInstance(MainActivity.this).getLon());
                        map.put("q",search.getText().toString());
                        map.put("start",""+pageNo);
                        map.put("count",""+count);
                        ApiCallService.action(MainActivity.this,map,ApiCallService.Action.ACTION_RESTAURANT_LIST,true);
                  //  this.search.setText("Your location has been marked on map");
                //    tick.setImageDrawable(getResources().getDrawable(R.drawable.tick_location));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Subscribe
    public void getRestaurantList(RestaurantSearchResponseModel response) {
        int oldSize = response.getRestaurants().size();
        if (pageNo == 0) {
            data = new ArrayList<>();
            data = response.getRestaurants();
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setFocusable(false);
            mRecyclerView.setNestedScrollingEnabled(true);
            mRecyclerView.setLayoutManager(layoutManager);
            // mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
            mAdapter = new RestaurantsListAdapter(this, this.data);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            data.addAll(response.getRestaurants());
            mAdapter.notifyItemRangeInserted((data.size() - oldSize), data.size());
            // mAdapter.setHasStableIds(true);
            if (response.getRestaurants().size() > 0) {
                loading = true;
            } else {
                loading = false;
            }
        }
        if (data.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            noRecord.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            noRecord.setVisibility(View.GONE);
        }
    }

}