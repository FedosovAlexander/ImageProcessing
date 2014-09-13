package com.example.imageprocessor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class ImageProcessingActivity extends Activity {

	private ImageView mImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bitmap bitmap = (Bitmap)getIntent().getParcelableExtra("ImageRGBA");
		setContentView(R.layout.activity_image_processing);
		mImageView=(ImageView)findViewById(R.id.image_view);
		mImageView.setImageBitmap(bitmap);
	}
}
