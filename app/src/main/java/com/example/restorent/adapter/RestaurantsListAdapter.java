package com.example.restorent.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restorent.R;
import com.example.restorent.network.response.RestaurantModel;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class RestaurantsListAdapter extends RecyclerView.Adapter<RestaurantsListAdapter.ViewHolder>{

    private Context context;
    public  List<RestaurantModel> dataList;
    public  List<RestaurantModel> filteredDataList;

    public RestaurantsListAdapter(Context context, List<RestaurantModel> data) {
        this.context = context;
        this.dataList = data;
        this.filteredDataList = data;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_restaurants_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(dataList.get(position).getRestaurant().getName());
        viewHolder.tvCuisines.setText(dataList.get(position).getRestaurant().getCuisines());
        viewHolder.tvAverageCost.setText("₹"+dataList.get(position).getRestaurant().getAverage_cost_for_two()+" per person");
        viewHolder.tvRating.setText(dataList.get(position).getRestaurant().getAggregate_rating());
        viewHolder.tvAddress.setText(dataList.get(position).getRestaurant().getLocality());

        if (dataList.get(position).getRestaurant().getThumb() != null) {
            new BitmapWorkerTask(viewHolder.mImageView).execute(dataList.get(position).getRestaurant().getThumb());
//            String[] s = dataList.get(position).getThumb().split("\\?");
//            Glide.with(context)
//                    .load(s[0])
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .into(viewHolder.mImageView);

//            Transformation<Bitmap> circleCrop = new CircleCrop();
//            Glide.with(context)
//                    .load(s[0])
//                    .optionalTransform(circleCrop)
//                    .optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(circleCrop))
//                    .into(viewHolder.mImageView);
        }
        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String jsonString = new Gson().toJson(data.get(position));
//                Intent intent = new Intent(context, ProductListActivity.class);
//                intent.putExtra("id", dataList.get(position).getId());
//                intent.putExtra("type_of_business", dataList.get(position).getAttributes().getType_of_business());
//                intent.putExtra("branchName", dataList.get(position).getAttributes().getName());
//                intent.putExtra("imageUrl", dataList.get(position).getAttributes().getLogo().getCover().getUrl());
//                search.setText("");
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainLayout;
        TextView name, tvCuisines, tvAverageCost, tvRating,tvAddress;
        ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            name = itemView.findViewById(R.id.name);
            mImageView = itemView.findViewById(R.id.mImageView);
            tvCuisines = itemView.findViewById(R.id.tvCuisines);
            tvAverageCost = itemView.findViewById(R.id.tvAverageCost);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvAddress = itemView.findViewById(R.id.tvAddress);

        }
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//                String charString = charSequence.toString();
//                if (charString.isEmpty()) {
//                    filteredDataList = dataList;
//                } else {
//                    ArrayList<RestaurantData> filteredDataList = new ArrayList<>();
//                    for (RestaurantData branchResponse : dataList) {
//                        if (branchResponse.getAttributes().getName().toLowerCase().startsWith(charString) /*|| androidVersion.getMobile().startsWith(charString)*/) {
//                            filteredDataList.add(branchResponse);
//                        }
//                    }
//                    RestaurantsListAdapter.filteredDataList = filteredDataList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = filteredDataList;
//                return filterResults;
//                return null;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                filteredDataList = (ArrayList<RestaurantData>) filterResults.values;
//                dataList = filteredDataList;
//                notifyDataSetChanged();
//            }
//        };
//    }


    // ----------------------------------------------------
    // Load bitmap in AsyncTask
    // ref:
    // http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String imageUrl;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage
            // collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            return LoadImage(imageUrl);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        private Bitmap LoadImage(String URL) {
            Bitmap bitmap = null;
            InputStream in = null;
            try {
                in = OpenHttpConnection(URL);
                if (in!=null){
                    bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        private InputStream OpenHttpConnection(String strURL)
                throws IOException {
            InputStream inputStream = null;
            URL url = new URL(strURL);
            URLConnection conn = url.openConnection();

            try {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = httpConn.getInputStream();
                }
            } catch (Exception ex) {
            }
            return inputStream;
        }
    }
}