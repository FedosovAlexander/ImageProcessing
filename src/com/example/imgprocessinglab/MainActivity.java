package com.example.imgprocessinglab;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	Button mCameraButton;
	Button mGalleryButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MainActivity","MainActivity created");
		setContentView(R.layout.activity_main);
		
		mCameraButton=(Button)findViewById(R.id.camera_button);
		mCameraButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
            	 Log.d("MainActivity","Pressed CameraButton");
            	   Intent intent=new Intent(getApplicationContext(), CameraActivity.class);
            	startActivity(intent);            
            }
		});
		mGalleryButton=(Button)findViewById(R.id.gallery_button);
		mGalleryButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
            	   Intent intent=new Intent(getApplicationContext(), SelectPictureActivity.class);
               	startActivity(intent);
            }
		});
	}
	


}
