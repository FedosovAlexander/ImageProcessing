package com.example.imageprocessor;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageProcessingActivity extends Activity {

	private ImageView mImageView;
	private Button mRefreshButton;
	private Bitmap mBasicImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		byte[] byteArray = getIntent().getByteArrayExtra("ImageRGBA");
		mBasicImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		setContentView(R.layout.activity_image_processing);
		mImageView=(ImageView)findViewById(R.id.image_view);
		mImageView.setImageBitmap(mBasicImage);
		mRefreshButton=(Button)findViewById(R.id.refresh_button);
		mRefreshButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
            	mImageView.setImageBitmap(mBasicImage);           
            }
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
           
      menu.add(0,1,0,"Show histogram");
      menu.add(0,2,0,"Show smoothed histogram");
      menu.add(0,3,0,"Grayscale");
      menu.add("Bilinear interpolation");
      menu.add("Binarize Otsu");
      menu.add("Spline ");
      
      return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     
    	switch (item.getItemId()){
    	case 3:{ 
    	      Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
    		ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        	mBasicImage.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        	byte[] byteArray = bStream.toByteArray();
        	byte[] arr=byteArray;
    		ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(), mBasicImage.getHeight(), byteArray, arr);
    		mImageView.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));           
    	}
    	}
      return super.onOptionsItemSelected(item);
    }
    
}
