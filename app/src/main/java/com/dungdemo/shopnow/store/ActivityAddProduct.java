package com.dungdemo.shopnow.store;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dungdemo.shopnow.R;

public class ActivityAddProduct extends AppCompatActivity {
    ImageView imageView1,imageView2,imageView3,imageView4;
    Bitmap thumbnail1,thumbnail2,thumbnail3,thumbnail4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setTitle("Thêm sản phẩm");
        imageView1=findViewById(R.id.img1);
        imageView2=findViewById(R.id.img2);
        imageView3=findViewById(R.id.img3);
        imageView4=findViewById(R.id.img4);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 1);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 2);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 3);
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 4);
            }
        });
        imageView1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(thumbnail1!=null){
                    thumbnail1=thumbnail2;
                    thumbnail2=thumbnail3;
                    thumbnail3=thumbnail4;
                    thumbnail4=null;
                    loadImagePosition();
                }
                return true;
            }
        });
        imageView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(thumbnail2!=null){
                    thumbnail2=thumbnail3;
                    thumbnail3=thumbnail4;
                    thumbnail4=null;
                    loadImagePosition();
                }
                return true;
            }
        });
        imageView3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(thumbnail3!=null){
                    thumbnail3=thumbnail4;
                    thumbnail4=null;
                    loadImagePosition();
                }
                return true;
            }
        });
        imageView4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(thumbnail4!=null){
                    thumbnail4=null;
                    loadImagePosition();
                }
                return true;
            }
        });
    }

    private void loadImagePosition() {
        if(thumbnail1==null){
                imageView1.setImageResource(R.drawable.icon_add_image);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
        }else
        if(thumbnail2==null){
            imageView1.setImageBitmap(thumbnail1);
            imageView2.setImageResource(R.drawable.icon_add_image);
            imageView2.setVisibility(View.VISIBLE);
            imageView3.setVisibility(View.INVISIBLE);
            imageView4.setVisibility(View.INVISIBLE);
        }else
        if(thumbnail3==null){
            imageView1.setImageBitmap(thumbnail1);
            imageView2.setImageBitmap(thumbnail2);
            imageView3.setImageResource(R.drawable.icon_add_image);
            imageView3.setVisibility(View.VISIBLE);
            imageView4.setVisibility(View.INVISIBLE);
        }else
        if(thumbnail4==null){
            imageView1.setImageBitmap(thumbnail1);
            imageView2.setImageBitmap(thumbnail2);
            imageView3.setImageBitmap(thumbnail3);
            imageView4.setImageResource(R.drawable.icon_add_image);
            imageView4.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1) {
         if(data!=null){
             Uri selectedImageUri = data.getData();
             String filestring = selectedImageUri.getPath();
             thumbnail1 = BitmapFactory.decodeFile(filestring, new BitmapFactory.Options());
             imageView1.setImageBitmap(thumbnail1);
             imageView2.setVisibility(View.VISIBLE);
         }
        }
        if (requestCode == 2) {
            if(data!=null){
                Uri selectedImageUri = data.getData();
                String filestring = selectedImageUri.getPath();
                thumbnail2 = BitmapFactory.decodeFile(filestring, new BitmapFactory.Options());
                imageView2.setImageBitmap(thumbnail2);
                imageView3.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == 3) {
            if(data!=null){
                Uri selectedImageUri = data.getData();
                String filestring = selectedImageUri.getPath();
                thumbnail3 = BitmapFactory.decodeFile(filestring, new BitmapFactory.Options());
                imageView3.setImageBitmap(thumbnail3);
                imageView4.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == 4) {
            if(data!=null){
                Uri selectedImageUri = data.getData();
                String filestring = selectedImageUri.getPath();
                thumbnail4 = BitmapFactory.decodeFile(filestring, new BitmapFactory.Options());
                imageView4.setImageBitmap(thumbnail4);
            }
        }
    }
}
