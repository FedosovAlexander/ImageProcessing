package com.example.imgprocessinglab;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageProcessingActivity extends Activity {

	private ImageView mImageView;
	private Button mRefreshButton;
	private Bitmap mBasicImage;
	private Bitmap mCurrentImage;
	private int mScreenWidth;
	private int mScreenHeight;
	private int mNewHeight;
	private int mNewWidth;
	private int mInterpolation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		Display display=getWindowManager().getDefaultDisplay();
		Point p=new Point();
		display.getSize(p);
		mScreenWidth=p.x;
		mScreenHeight=p.y;
		byte[] byteArray = getIntent().getByteArrayExtra("ImageRGBA");
		mBasicImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		setContentView(R.layout.activity_image_processing);
		mImageView=(ImageView)findViewById(R.id.image_view);
		if(mCurrentImage==null){mCurrentImage=mBasicImage;}
		mImageView.setImageBitmap(mCurrentImage);
		mRefreshButton=(Button)findViewById(R.id.refresh_button);
		mRefreshButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
            	mCurrentImage=mBasicImage;
            	mImageView.setImageBitmap(mCurrentImage);           
            }
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
           
      menu.add(0,1,0,"Show histogram");
      menu.add(0,2,0,"Show smoothed histogram");
      menu.add(0,3,0,"Grayscale");
      menu.add(0,4,0,"Bilinear interpolation");
      menu.add(0,5,0,"Bicubic spline interpolation");
      menu.add(0,6,0,"Binarize Otsu");
      menu.add(0,7,0,"k-means сlusterisation");
      
      return super.onCreateOptionsMenu(menu);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:{
			int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
			byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] grayimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			convertIntArrayToByteArray(pixels,argbimage);
			convertIntArrayToByteArray(pixels,grayimage);
			ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, grayimage);
			int[] histogram=getHistogram(grayimage,1);
			//mCurrentImage=(mScreenWidth>mScreenHeight?(buildHistogram(mScreenWidth,mScreenHeight,histogram)):(buildHistogram(mScreenHeight,mScreenWidth,histogram)));
			mCurrentImage=buildHistogram(mScreenWidth,mScreenHeight,histogram);
			mImageView.setImageBitmap(mCurrentImage);
			break;
		}
		case 2:{
			int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
			byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] grayimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			convertIntArrayToByteArray(pixels,argbimage);
			convertIntArrayToByteArray(pixels,grayimage);
			ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, grayimage);
			int[] histogram=getHistogram(grayimage,1);
			int[] smoothedHistogram=new int[histogram.length];
			ImgProcessor.smoothHistogram(histogram,smoothedHistogram, 3);
			mCurrentImage=buildHistogram(mScreenWidth,mScreenHeight,smoothedHistogram);
			mImageView.setImageBitmap(mCurrentImage);
			break;
		}
		case 3: {
			int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
			byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] grayimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			convertIntArrayToByteArray(pixels,argbimage);
			convertIntArrayToByteArray(pixels,grayimage);
			ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, grayimage);
			int[] img = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			convertByteArrayToIntArray(img,grayimage);
			mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
			break;
		}
		case 4: {
			mInterpolation=1;
			runOnUiThread((new Thread() {
				@Override
				public void run() {
					showInputHeightDialog();
					showInputWidthDialog();
			
				}
			}));
			break;
		}
		case 5:{
			mInterpolation=2;
			runOnUiThread((new Thread() {
				@Override
				public void run() {
					showInputHeightDialog();
					showInputWidthDialog();
			
				}
			}));
			break;
		}
		case 6:{
			int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
			byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] grayimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] binarized = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			convertIntArrayToByteArray(pixels,argbimage);
			convertIntArrayToByteArray(pixels,grayimage);
			ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, grayimage);
			int[] histogram=getHistogram(grayimage,1);
			
			ImgProcessor.binarizeOtsu(mBasicImage.getWidth(), mBasicImage.getHeight(), grayimage, binarized, histogram);
			int[] img = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];	
			convertByteArrayToIntArray(img,binarized);
			mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
			break;

		}
		case 7:{
			int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
			byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] clustimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			convertIntArrayToByteArray(pixels,argbimage);
			convertIntArrayToByteArray(pixels,clustimage);
			//ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(),clustimage, argbimage);
			int[] histogram=getHistogram(argbimage,1);
			int[] smoothedhistogram=getHistogram(argbimage,1);
			//ImgProcessor.clusterizeKMeans(argbimage, clustimage, histogram, ImgProcessor.getNumberOfPeaks(histogram), 0);
			//histogram=getHistogram(argbimage,1);
			//for(int i=0;i<3;i++){ImgProcessor.smoothHistogram(histogram, smoothedhistogram, 3);System.arraycopy( smoothedhistogram, 0, histogram, 0, smoothedhistogram.length );}
			int clusters=ImgProcessor.getNumberOfPeaks(smoothedhistogram);
			Log.d("ImageProcessingActivity","the number of clusters is "+clusters);
			ImgProcessor.clusterizeKMeans(argbimage, clustimage,smoothedhistogram,clusters,1);
			histogram=getHistogram(argbimage,2);
			smoothedhistogram=histogram;
			for(int i=0;i<3;i++){ImgProcessor.smoothHistogram(histogram, smoothedhistogram, 3);System.arraycopy( smoothedhistogram, 0, histogram, 0, smoothedhistogram.length );}
			ImgProcessor.clusterizeKMeans(argbimage, clustimage,smoothedhistogram,clusters,2);
			histogram=getHistogram(argbimage,3);
			smoothedhistogram=histogram;
			for(int i=0;i<3;i++){ImgProcessor.smoothHistogram(histogram, smoothedhistogram, 3);System.arraycopy( smoothedhistogram, 0, histogram, 0, smoothedhistogram.length );}
			ImgProcessor.clusterizeKMeans(argbimage, clustimage,smoothedhistogram,clusters,3);
			int[] img = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];	
			convertByteArrayToIntArray(img,clustimage);
			mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
			
			
			break;}
		}
		return super.onOptionsItemSelected(item);
	}
    public static int[] convertByteArrayToIntArray(int[] intarr,byte[]bytearr){
    	for (int i = 0; i < intarr.length; i++) {
			intarr[i] = bytearr[4 * i + 3] & 0xFF
					| (bytearr[4 * i + 2] & 0xFF) << 8
					| (bytearr[4 * i + 1] & 0xFF) << 16
					| (bytearr[4 * i] & 0xFF) << 24;
		}
    	return intarr;
    }
	public static byte [] convertIntArrayToByteArray(int[] intarr,byte[]bytearr){
		for (int i = 0; i < intarr.length; i++) {
			bytearr[4 * i + 3]  = (byte) ((intarr[i]) & 0xFF);
			bytearr[4 * i + 2] = (byte) (((intarr[i]) >> 8) & 0xFF);
			bytearr[4 * i + 1] = (byte) (((intarr[i]) >> 16) & 0xFF);
			bytearr[4 * i] = (byte) (((intarr[i]) >> 24) & 0xFF);
		}
		return bytearr;			
	}
	public int[] getHistogram(byte[] bytearr,int shift){
		int[] histogram=new int[256];
		Arrays.fill(histogram, 0);	
		int value;
		for(int i=shift;i<bytearr.length;i+=4){
			value=(bytearr[i]&0xFF);
			++histogram[value];
		}
		return histogram;
	}
	
	public Bitmap buildHistogram(int width,int height,int[]hist){
		Bitmap histogram=Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
		if (width > height) {
			int xstep = (width - 257) / 256;
			int max = 0;
			for (int i = 0; i < hist.length; i++) {
				if (hist[i] > max) {
					max = hist[i];
				}
			}
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					histogram.setPixel(i, j, Color.YELLOW);
				}
			}
			int xcoord = 1;
			for (int i = 0; i < 256; i++) {

				for (int xc = 0; xc < xstep; xc++) {
					for (int yc = 0; yc < hist[i] * (height - 10) / max; yc++) {
						histogram.setPixel(xcoord, height - 2 - yc,((i & 0xFF) | ((i & 0xFF) << 8)| ((i & 0xFF) << 16) | (255 << 24)));
					}
					xcoord++;
				}
				++xcoord;
			}
		}
		else{
			int ystep = (height - 257) / 256;
			int max = 0;
			for (int i = 0; i < hist.length; i++) {
				if (hist[i] > max) {
					max = hist[i];
				}
			}
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					histogram.setPixel(i, j, Color.YELLOW);
				}
			}
			int ycoord = 1;
			for (int i = 0; i < 256; i++) {

				for (int yc = 0; yc < ystep; yc++) {
					for (int xc = 0; xc < hist[i] * (width - 10) / max; xc++) {
						histogram.setPixel(xc, ycoord,((i & 0xFF) | ((i & 0xFF) << 8)| ((i & 0xFF) << 16) | (255 << 24)));
					}
					ycoord++;
				}
				++ycoord;
			}
		}
		return histogram;
	}
	
	public void showInputWidthDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Previous width was"+mBasicImage.getWidth());
		builder.setMessage("Enter new width:");

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        mNewWidth = Integer.parseInt(input.getText().toString());
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.show();
	}
	
	public void showInputHeightDialog(){
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Previous Height was"+mBasicImage.getHeight());
	builder.setMessage("Enter new height:");

	// Set up the input
	final EditText input = new EditText(this);
	// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	builder.setView(input);

	// Set up the buttons
	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	    	//Looper.prepare();
	        mNewHeight = Integer.parseInt(input.getText().toString());
	        if(mInterpolation==1){
	        	bilinearInterpolation();
	        }
	        if(mInterpolation==2){
	        	bicubicInterpolation();
	        }
	       // Looper.loop();
	    }
	});
	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        dialog.cancel();
	    }
	});
	builder.show();
	}
	public void bilinearInterpolation(){
		int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
		mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
		byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
		convertIntArrayToByteArray(pixels, argbimage);
		byte[] newargbimage = new byte[mNewWidth * mNewHeight * 4];
		ImgProcessor.interpolateBilinear(mBasicImage.getWidth(), mBasicImage.getHeight(), mNewWidth, mNewHeight,argbimage,newargbimage);
		int[] img = new int[mNewWidth * mNewHeight];
		convertByteArrayToIntArray(img, newargbimage);
		mCurrentImage = Bitmap.createBitmap(img, mNewWidth, mNewHeight,Bitmap.Config.ARGB_8888);
		mImageView.setImageBitmap(mCurrentImage);
	}
	public void bicubicInterpolation(){
		int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
		mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
		byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
		convertIntArrayToByteArray(pixels, argbimage);
		byte[] newargbimage = new byte[mNewWidth * mNewHeight * 4];
		ImgProcessor.interpolateBicubic(mBasicImage.getWidth(), mBasicImage.getHeight(), mNewWidth, mNewHeight,argbimage,newargbimage);
		int[] img = new int[mNewWidth * mNewHeight];
		convertByteArrayToIntArray(img, newargbimage);
		mCurrentImage = Bitmap.createBitmap(img, mNewWidth, mNewHeight,Bitmap.Config.ARGB_8888);
		mImageView.setImageBitmap(mCurrentImage);
	}
}
