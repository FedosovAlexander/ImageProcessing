package com.example.imgprocessinglab;



import java.util.Arrays;




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
import android.widget.TextView;
import android.widget.Toast;
public class ImageProcessingActivity extends Activity {

	private ImageView mImageView;
	private TextView mTextView;
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
		mCurrentImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		setContentView(R.layout.activity_image_processing);
		mImageView=(ImageView)findViewById(R.id.image_view);
		mTextView=(TextView)findViewById(R.id.output);
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
      menu.add(0,8,0,"3d k-means сlusterisation");
      menu.add(0,9,0,"calculate characteristics");
      menu.add(0,10,0,"Haar transform");
      menu.add(0,11,0,"undo Haar transform");
      menu.add(0,12,0,"Clusterize entropy");
      menu.add(0,13,0,"Clusterize energy");
      menu.add(0,14,0,"Clusterize kurtosis");
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
			
			//ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, clustimage);
			//ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), clustimage,argbimage );
			//ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(),clustimage, argbimage);
			int[] rhistogram=getHistogram(clustimage,1);
			int[] rsmoothedhistogram=getHistogram(clustimage,1);
			int[] ghistogram=getHistogram(clustimage,2);
			int[] gsmoothedhistogram=getHistogram(clustimage,2);
			int[] bhistogram=getHistogram(clustimage,3);
			int[] bsmoothedhistogram=getHistogram(clustimage,3);
			for(int i=0;i<1;i++){
			ImgProcessor.smoothHistogram(rhistogram, rsmoothedhistogram, 3);System.arraycopy( rsmoothedhistogram, 0, rhistogram, 0, rsmoothedhistogram.length );
			ImgProcessor.smoothHistogram(ghistogram, gsmoothedhistogram, 3);System.arraycopy( gsmoothedhistogram, 0, ghistogram, 0, gsmoothedhistogram.length );
			ImgProcessor.smoothHistogram(bhistogram, bsmoothedhistogram, 3);System.arraycopy( bsmoothedhistogram, 0, bhistogram, 0, bsmoothedhistogram.length );}
			int clusters1=ImgProcessor.getNumberOfPeaks(rsmoothedhistogram);
			Log.d("ImageProcessingActivity","the number of red clusters is "+clusters1);
			int clusters2=ImgProcessor.getNumberOfPeaks(gsmoothedhistogram);
			Log.d("ImageProcessingActivity","the number of green clusters is "+clusters2);
			int clusters3=ImgProcessor.getNumberOfPeaks(bsmoothedhistogram);
			Log.d("ImageProcessingActivity","the number of blue clusters is "+clusters3);
			ImgProcessor.clusterizeKMeans(argbimage, clustimage,rsmoothedhistogram,clusters1,1);
			ImgProcessor.clusterizeKMeans(argbimage, clustimage,gsmoothedhistogram,clusters2,2);
			ImgProcessor.clusterizeKMeans(argbimage, clustimage,bsmoothedhistogram,clusters3,3);
			int[] img = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];	
			convertByteArrayToIntArray(img,clustimage);
			mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
			
			
			break;}
		case 8:{
			int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
			byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] clustimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			convertIntArrayToByteArray(pixels,argbimage);
			convertIntArrayToByteArray(pixels,clustimage);
			
			//ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, clustimage);
			//ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), clustimage,argbimage );
			//ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(),clustimage, argbimage);
			int[] rhistogram=getHistogram(clustimage,1);
			int[] rsmoothedhistogram=getHistogram(clustimage,1);
			int[] ghistogram=getHistogram(clustimage,2);
			int[] gsmoothedhistogram=getHistogram(clustimage,2);
			int[] bhistogram=getHistogram(clustimage,3);
			int[] bsmoothedhistogram=getHistogram(clustimage,3);
			for(int i=0;i<1;i++){
			ImgProcessor.smoothHistogram(rhistogram, rsmoothedhistogram, 3);System.arraycopy( rsmoothedhistogram, 0, rhistogram, 0, rsmoothedhistogram.length );
			ImgProcessor.smoothHistogram(ghistogram, gsmoothedhistogram, 3);System.arraycopy( gsmoothedhistogram, 0, ghistogram, 0, gsmoothedhistogram.length );
			ImgProcessor.smoothHistogram(bhistogram, bsmoothedhistogram, 3);System.arraycopy( bsmoothedhistogram, 0, bhistogram, 0, bsmoothedhistogram.length );}
			int clusters=ImgProcessor.getNumberOfPeaks3d(rsmoothedhistogram,gsmoothedhistogram,bsmoothedhistogram);
			Log.d("ImageProcessingActivity","the number of clusters is "+clusters);
			ImgProcessor.clusterizeKMeans(argbimage, clustimage,rsmoothedhistogram,clusters,1);
			ImgProcessor.clusterizeKMeans(argbimage, clustimage,gsmoothedhistogram,clusters,2);
			ImgProcessor.clusterizeKMeans(argbimage, clustimage,bsmoothedhistogram,clusters,3);
			int[] img = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];	
			convertByteArrayToIntArray(img,clustimage);
			mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
	
			break;}
		case 9:{
			calculateCharachteristics();
			break;
		}
		case 10: {
			int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			mCurrentImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
			byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] grayimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] transformed = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			convertIntArrayToByteArray(pixels,argbimage);
			convertIntArrayToByteArray(pixels,grayimage);
			ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, grayimage);
			System.arraycopy( grayimage, 0, transformed, 0, grayimage.length );
			ImgProcessor.discreteHaarTransform(grayimage,transformed,mBasicImage.getWidth(),mBasicImage.getHeight());
			int[] img = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];	
			convertByteArrayToIntArray(img,transformed);
			mCurrentImage = Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888);
			mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
			break;
		}
		case 11: {
			int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			mCurrentImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
			byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] grayimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] transformed = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			convertIntArrayToByteArray(pixels,argbimage);
			convertIntArrayToByteArray(pixels,grayimage);
			ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, grayimage);
			System.arraycopy( grayimage, 0, transformed, 0, grayimage.length );
			ImgProcessor.undoDiscreteHaarTransform(grayimage,transformed,mBasicImage.getWidth(),mBasicImage.getHeight());
			int[] img = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];	
			convertByteArrayToIntArray(img,transformed);
			mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
			break;
		}
		case 12:{
			int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
			mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
			byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			byte[] clustimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
			convertIntArrayToByteArray(pixels,argbimage);
			convertIntArrayToByteArray(pixels,clustimage);
			ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, clustimage);
			int[] bhistogram=getHistogram(clustimage,3);
			int[] bsmoothedhistogram=getHistogram(clustimage,3);
			float[] histogram = new float[256];
			for(int i=0;i<1;i++){
			ImgProcessor.smoothHistogram(bhistogram, bsmoothedhistogram, 3);System.arraycopy( bsmoothedhistogram, 0, bhistogram, 0, bsmoothedhistogram.length );}
			int clusters1=ImgProcessor.getNumberOfPeaks(bsmoothedhistogram);
			for(int i=0;i<256;i++){
				histogram[i] = (float)bsmoothedhistogram[i];
			}
			ImgProcessor.buildEntropyHistogram(bsmoothedhistogram, histogram, mBasicImage.getWidth(), mBasicImage.getHeight());
			Log.d("ImageProcessingActivity","the number of clusters is "+clusters1);
			ImgProcessor.clusterizeKMeansHistogram(argbimage, clustimage,histogram,clusters1);
			int[] img = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];	
			convertByteArrayToIntArray(img,clustimage);
			mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
			
			
			break;}
		case 13:{
				int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
				mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
				byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
				byte[] clustimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
				convertIntArrayToByteArray(pixels,argbimage);
				convertIntArrayToByteArray(pixels,clustimage);
				ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, clustimage);
				int[] bhistogram=getHistogram(clustimage,3);
				int[] bsmoothedhistogram=getHistogram(clustimage,3);
				float[] histogram = new float[256];
				for(int i=0;i<1;i++){
				ImgProcessor.smoothHistogram(bhistogram, bsmoothedhistogram, 3);System.arraycopy( bsmoothedhistogram, 0, bhistogram, 0, bsmoothedhistogram.length );}
				int clusters1=ImgProcessor.getNumberOfPeaks(bsmoothedhistogram);
				for(int i=0;i<256;i++){
					histogram[i] = (float)bsmoothedhistogram[i];
				}
				ImgProcessor.buildEnergyHistogram(bsmoothedhistogram, histogram, mBasicImage.getWidth(), mBasicImage.getHeight());
				Log.d("ImageProcessingActivity","the number of clusters is "+clusters1);
				ImgProcessor.clusterizeKMeansHistogram(argbimage, clustimage,histogram,clusters1);
				int[] img = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];	
				convertByteArrayToIntArray(img,clustimage);
				mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
				
				
				break;}
			case 14:{
					int[] pixels = new int[mBasicImage.getWidth()* mBasicImage.getHeight()];
					mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,mBasicImage.getWidth(), mBasicImage.getHeight());
					byte[] argbimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
					byte[] clustimage = new byte[4 * mBasicImage.getWidth()* mBasicImage.getHeight()];
					convertIntArrayToByteArray(pixels,argbimage);
					convertIntArrayToByteArray(pixels,clustimage);
					ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),mBasicImage.getHeight(), argbimage, clustimage);
					int[] bhistogram=getHistogram(clustimage,3);
					int[] bsmoothedhistogram=getHistogram(clustimage,3);
					float[] histogram = new float[256];
					for(int i=0;i<1;i++){
					ImgProcessor.smoothHistogram(bhistogram, bsmoothedhistogram, 3);System.arraycopy( bsmoothedhistogram, 0, bhistogram, 0, bsmoothedhistogram.length );}
					int clusters1=ImgProcessor.getNumberOfPeaks(bsmoothedhistogram);
					for(int i=0;i<256;i++){
						histogram[i] = (float)bsmoothedhistogram[i];
					}
					ImgProcessor.buildKurtosisHistogram(bsmoothedhistogram, histogram, mBasicImage.getWidth(), mBasicImage.getHeight());
					Log.d("ImageProcessingActivity","the number of clusters is "+clusters1);
					ImgProcessor.clusterizeKMeansHistogram(argbimage, clustimage,histogram,clusters1);
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
	
	public void calculateCharachteristics() {
		int[] pixels = new int[mBasicImage.getWidth() * mBasicImage.getHeight()];
		mBasicImage.getPixels(pixels, 0, mBasicImage.getWidth(), 0, 0,
				mBasicImage.getWidth(), mBasicImage.getHeight());
		byte[] argbimage = new byte[4 * mBasicImage.getWidth()
				* mBasicImage.getHeight()];
		byte[] grayimage = new byte[4 * mBasicImage.getWidth()
				* mBasicImage.getHeight()];
		byte[] binarized = new byte[4 * mBasicImage.getWidth()
				* mBasicImage.getHeight()];
		convertIntArrayToByteArray(pixels, argbimage);
		convertIntArrayToByteArray(pixels, grayimage);
		ImgProcessor.convertARGBToGrayscale(mBasicImage.getWidth(),
				mBasicImage.getHeight(), argbimage, grayimage);
		int[] histogram = getHistogram(grayimage, 1);

		ImgProcessor.binarizeOtsu(mBasicImage.getWidth(),
				mBasicImage.getHeight(), grayimage, binarized, histogram);
		int[] img = new int[mBasicImage.getWidth() * mBasicImage.getHeight()];
		// convertByteArrayToIntArray(img,binarized);
		// mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(),
		// mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
		int area = 0;
		int massCenterX,massCenterY;
		massCenterX=massCenterY=0;
		int width = 4 * mBasicImage.getWidth();
		int height = mBasicImage.getHeight();
		for (int i = 0; i < height; i++) {
			for (int j = 1; j < width; j += 4) {
			if (((binarized[i*width+j])&0xff)<200) {
				area++;
				massCenterX+=j/4;
				massCenterY+=i;
			}
		}
	}
		massCenterX/=area;
		massCenterY/=area;
		int threshy, threshx;
		byte[] binarizedcopy = new byte[4 * mBasicImage.getWidth()
				* mBasicImage.getHeight()];
		for (int i = 1; i < height-1; i++) {
			for (int j = 5; j < width-4; j += 4) {
				//if () {
					threshy = 2* (binarized[(i+1)*width+j]&0xff)+ (binarized[(i+1)*width+j- 4]&0xff)+ (binarized[(i+1)*width+j + 4]&0xff)- (2* (binarized[(i-1)*width+j]&0xff)
									+ (binarized[(i-1)* width+j- 4]&0xff) + (binarized[(i - 1)*width+j + 4]&0xff));

					threshx = 2* (binarized[(i)*width+j+4]&0xff)+ (binarized[(i+1)*width+j+4]&0xff)+ (binarized[(i-1)*width+j + 4]&0xff)- (2* (binarized[(i)*width+j]&0xff)
							+ (binarized[(i-1)* width+j- 4]&0xff) + (binarized[(i +1)*width+j + 4]&0xff));

					binarizedcopy[i*width+j] = binarizedcopy[i*width+j+ 1] = binarizedcopy[i*width+j + 2] = (byte) ((Math.sqrt(threshx * threshx + threshy * threshy)) > 0.0001 ? 0: 255);
					binarizedcopy[i*width+j - 1] = binarized[i*width+j - 1];

				//}
			}
		}
		int perimeter = 0;
		for (int i = 1; i < height - 1; i++) {
			for (int j = 5; j < width - 4; j += 4) {
				if ((binarizedcopy[i*width+j] & 0xff) < 200) {
					perimeter++;
				}
			}
		}
		double compactness=perimeter*perimeter/area;
		convertByteArrayToIntArray(img, binarizedcopy);
		mTextView.setText("Perimeter is "+perimeter+"\n"+"area is "+area+"\n"+"compactness is "+compactness+"\n"+"mass center x is "+massCenterX+"\n"+"mass center y is "+massCenterY);
		mImageView.setImageBitmap(Bitmap.createBitmap(img,mBasicImage.getWidth(), mBasicImage.getHeight(),Bitmap.Config.ARGB_8888));
	}
}
