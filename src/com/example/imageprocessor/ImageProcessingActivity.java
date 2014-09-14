package com.example.imageprocessor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class ImageProcessingActivity extends Activity {

	private ImageView mImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		byte[] byteArray = getIntent().getByteArrayExtra("ImageRGBA");
		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		setContentView(R.layout.activity_image_processing);
		mImageView=(ImageView)findViewById(R.id.image_view);
		mImageView.setImageBitmap(bitmap);
	}
}
