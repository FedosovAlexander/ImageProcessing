package com.example.imageprocessor;

public class ImgProcessor {
	
	static
	{
		System.loadLibrary("imgprocessor");
	}
	
	public static  native void convertARGBToGrayscale(int width,int height, byte[] argb,byte[] gray);
	public static  native void interpolateBilinear(int prevWidth,int prevHeight,int newWidth,int newHeight,byte[] image,byte[] newImage);
	public static  native void interpolateBicubic(int prevWidth,int prevHeight,int newWidth,int newHeight,byte[] image,byte []newImage);
	public static  native void smoothHistogram(int[]histogram,int[]smoothedhistogram,int size);
	public static  native void binarizeOtsu(int width,int height,byte[] image,byte[] binarizedimage,int[] hist);
	public static  native int getNumberOfPeaks(int [] histogram); 
	public static  native void clusterizeKMeans(byte[] img,byte[]clusterized,int[]histogram,int numofclusters,int shift);
}
