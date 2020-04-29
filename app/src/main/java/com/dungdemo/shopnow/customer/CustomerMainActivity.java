package com.dungdemo.shopnow.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.LoginActivity;
import com.dungdemo.shopnow.admin.AdminActivity;
import com.dungdemo.shopnow.model.ProductCategory;
import com.dungdemo.shopnow.model.SliderItem;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.admin.StoreManagerFragment;
import com.dungdemo.shopnow.admin.UserManagerFragment;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        , AsyncResponse {
    NavigationView navigationView;
    SliderView sliderView;
    SliderAdapter adapter;
    List<SliderItem> sliderItemList = new ArrayList<>();
    List<ProductCategory> productCategories=new ArrayList<>();
    ListView lvCategory;
    ArrayAdapter<ProductCategory> arrayAdapter;
    EditText edtSearch;
    User user;
    int checkExit=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        lvCategory=findViewById(R.id.lvCategory);
        edtSearch=findViewById(R.id.edtSearch);
        findViewById(R.id.imgSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!"".equals(edtSearch.getText().toString())){
                    Intent t=new Intent(CustomerMainActivity.this,ProductListActivity.class);
                    t.putExtra("search",edtSearch.getText().toString());
                    startActivity(t);
                }else{
                    Toast.makeText(CustomerMainActivity.this, "Vui lòng nhập từ khóa!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadUserInfo();
        loadCategory();
        findViewById(R.id.imgTogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if(drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START);
                }else{
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        loadProductForSlider();
        sliderView = findViewById(R.id.imageSlider);
        adapter = new SliderAdapter(this);
        adapter.renewItems(sliderItemList);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent t =new Intent(CustomerMainActivity.this,ProductListActivity.class);
                t.putExtra("title",productCategories.get(i).getName());
                t.putExtra("category_id",productCategories.get(i).getCategory_id());
                startActivity(t);
            }
        });

    }
    private void loadCategory() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/category";

        Request request = new Request.Builder()
                .url(url).addHeader("Authorization",User.getSavedToken(this))
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = null;
                    json = response.body().string() + "";
                    Type listType = new TypeToken<List<ProductCategory>>() {
                    }.getType();
                    productCategories = new Gson().fromJson(json, listType);
                    CustomerMainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           loadCategoryListView();
                        }
                    });
//
                }
            }
        });
    }

    private void loadCategoryListView() {
        arrayAdapter=new ArrayAdapter<ProductCategory>(CustomerMainActivity.this,R.layout.category_listview_item,productCategories){
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                LayoutInflater layoutInflater=(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v=layoutInflater.inflate(R.layout.category_listview_item,null);
                TextView tvCategoryName=v.findViewById(R.id.tvCategoryName);
                TextView tvCategoryDetail=v.findViewById(R.id.tvCategoryDetail);
                ImageView imageView=v.findViewById(R.id.imgCategory);
                ProductCategory productCategory=productCategories.get(position);
                tvCategoryName.setText(productCategory.getName());
                tvCategoryDetail.setText(productCategory.getDetail());
                Picasso.get().load(HostName.categoryImageUrl+productCategory.getCategory_id()+".png").into(imageView);

                return v;

            }
        };
        lvCategory.setAdapter(arrayAdapter);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lvCategory.getLayoutParams();
        float factor = getApplicationContext().getResources().getDisplayMetrics().density;
        lp.height = (int)(120* factor)*productCategories.size();

        lvCategory.setLayoutParams(lp);

    }

    private void loadUserInfo() {
        Map<String,String > map=new HashMap<>(  );
        map.put("token",User.getSavedToken(getApplication()));
        map.put("method","get");
        TaskConnect task=new TaskConnect(CustomerMainActivity.this, HostName.host+"/user/"+User.getSavedUserId(this));
        task.setMap( map );
        task.execute();
    }

    private void loadProductForSlider() {

        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/product/getProductForSlider";

        Request request = new Request.Builder()
                .url(url).addHeader("Authorization",User.getSavedToken(this))
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = null;
                    json = response.body().string() + "";
                    Type listType = new TypeToken<List<SliderItem>>() {
                    }.getType();
                     sliderItemList= new Gson().fromJson(json, listType);
                    CustomerMainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.renewItems(sliderItemList);
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            startActivity(new Intent(this,CartActivity.class));

        }
        if(id==R.id.nav_order){
            startActivity(new Intent(this,OrderActivity.class));
        }
        if(id==R.id.nav_logout){
            User.logout(this);
            Intent intent = new Intent(CustomerMainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            checkExit++;
            if (checkExit == 2) {
                finishAffinity();
            }
            Toast.makeText(this, "Ấn lại để thoát !", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        if(output!=null){
            if(output.code()==200){

                String json="";
                json=output.getBody();
                Gson gson=new Gson();
                user=gson.fromJson(json,User.class);
                View headerView=navigationView.getHeaderView(0);
                TextView name=headerView.findViewById(R.id.tvName);
                name.setText(user.getName());
                TextView phone=headerView.findViewById(R.id.tvPhone);
                phone.setText(user.getPhone());
            }
            if(output.code()==401){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(CustomerMainActivity.this);
                builder1.setMessage(new Gson().fromJson(output.getBody(),String.class));
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                User.logout(getApplicationContext());
                                Intent intent = new Intent(getApplication(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
            if(output.code()==0){
                Toast.makeText(this, "Kiểm tra lại kết nối !", Toast.LENGTH_SHORT).show();
            }


        }

    }

}
