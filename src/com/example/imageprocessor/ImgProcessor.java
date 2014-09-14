package com.example.imageprocessor;

public class ImgProcessor {
	
	static
	{
		System.loadLibrary("imgprocessor");
	}
	
	public static  native void convertARGBToGrayscale(int width,int height, byte[] argb,byte[] gray);
	public static  native byte[] interpolateBilinear(int prevWidth,int prevHeight,int newWidth,int newHeight,byte[] image);
	public static  native byte[] interpolateBicubic(int prevWidth,int prevHeight,int newWidth,int newHeight,byte[] image);
	public static  native byte[] interpolateSpline(int prevWidth,int prevHeight,int newWidth,int newHeight,byte[] image);
	public static  native void smoothHistogram(int[]histogram,int size);
	public static  native byte[] binarizeOtsu(int width,int height,byte[] image);
	public static native byte[] binarizeOtsu(int width,int height,byte[] image,byte[] hist);


}
