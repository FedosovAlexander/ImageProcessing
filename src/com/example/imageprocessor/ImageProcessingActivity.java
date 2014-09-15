package com.example.imageprocessor;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

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
		//mImageView.setImageBitmap(mBasicImage);
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

		switch (item.getItemId()) {
		case 3: {
			int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
			byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] grayimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			for (int i = 0; i < mBasicImage.getWidth()
					* mBasicImage.getHeight(); i++) {
				grayimage[4 * i + 3] = argbimage[4 * i + 3] = (byte) ((pixels[i]) & 0xFF);
				grayimage[4 * i + 2] = argbimage[4 * i + 2] = (byte) (((pixels[i]) >> 8) & 0xFF);
				grayimage[4 * i + 1] = argbimage[4 * i + 1] = (byte) (((pixels[i]) >> 16) & 0xFF);
				grayimage[4 * i] = argbimage[4 * i] = (byte) (((pixels[i]) >> 24) & 0xFF);
			}
			ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, grayimage);
			int[] img = new int[mBasicImage.getWidth()
					* mBasicImage.getHeight()];
			for (int i = 0; i < mBasicImage.getWidth()
					* mBasicImage.getHeight(); i++) {
				img[i] = grayimage[4 * i + 3] & 0xFF
						| (grayimage[4 * i + 2] & 0xFF) << 8
						| (grayimage[4 * i + 1] & 0xFF) << 16
						| (grayimage[4 * i] & 0xFF) << 24;
			}
			mImageView.setImageBitmap(Bitmap.createBitmap(img,
					mBasicImage.getWidth(), mBasicImage.getHeight(),
					Bitmap.Config.ARGB_8888));
		}
		}
		return super.onOptionsItemSelected(item);
	}
    
}
