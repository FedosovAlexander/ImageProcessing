package com.example.imageprocessor;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
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
                takePicture();
               
            }
		});
		mGalleryButton=(Button)findViewById(R.id.gallery_button);
		mGalleryButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
		});
	}
	
	public void takePicture(){
		TakingPictureFragment mCameraFragment=new TakingPictureFragment();
		getFragmentManager().beginTransaction().replace(R.layout.activity_main, mCameraFragment);
	}

	public class TakingPictureFragment extends Fragment
	{
		private CameraBridgeViewBase mOpenCvCameraView;
	    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getApplicationContext()) {
	        @Override
	        public void onManagerConnected(int status) {
	            switch (status) {
	                case LoaderCallbackInterface.SUCCESS:
	                {
	                    mOpenCvCameraView.enableView();
	                } break;
	                default:
	                {
	                    super.onManagerConnected(status);
	                } break;
	            }
	        }
	    };
		
	    public TakingPictureFragment()
	    {
	    	super();
	    }
		@Override 
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			 mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
			 mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		     mOpenCvCameraView.setCvCameraViewListener((CvCameraViewListener2) getApplicationContext());
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3,getApplicationContext(), mLoaderCallback);
		}
		
		
	}
}
