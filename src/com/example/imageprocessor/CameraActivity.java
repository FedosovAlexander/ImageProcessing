package com.example.imageprocessor;

import java.io.ByteArrayOutputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;
import org.opencv.android.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class CameraActivity extends Activity implements CvCameraViewListener2
{	
	public Mat mMat;
	
	private Bitmap mImageRGBA;
	private Button mShot;
	private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback= new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                    mShot.setVisibility(View.VISIBLE);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
   
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
		 setContentView(R.layout.camera_activity);
		 mShot=(Button)findViewById(R.id.shot);
		 mShot.setVisibility(View.INVISIBLE);
		 mShot.setOnClickListener( new View.OnClickListener() {
	            public void onClick(View v) {
	            	mImageRGBA= Bitmap.createBitmap(mMat.cols(),mMat.rows(),Bitmap.Config.ARGB_8888);
	            	Utils.matToBitmap(mMat,mImageRGBA);
	            	Intent intent = new Intent(getApplicationContext(), ImageProcessingActivity.class);
	            	intent.putExtra("ImageRGBA",mImageRGBA);
	            	startActivity(intent);
	            }
			});
		 mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
		 mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	     mOpenCvCameraView.setCvCameraViewListener(this);
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3,getApplicationContext(), mLoaderCallback);
	}
	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mMat=inputFrame.rgba();
		return inputFrame.rgba();
	}
	
	
}
