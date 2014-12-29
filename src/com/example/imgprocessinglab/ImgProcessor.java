package com.example.imgprocessinglab;

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
	public static  native int getNumberOfPeaks3d(int [] rhistogram,int [] ghistogram,int [] bhistogram);
	public static  native void clusterizeKMeans(byte[] img,byte[]clusterized,int hist[],int numOfClusters,int shift);
	public static  native void discreteHaarTransform(byte[] img,byte[]modifed,int width, int height);
	public static  native void undoDiscreteHaarTransform(byte[] img,byte[]modifed,int width, int height);	
	public static  native void buildEntropyHistogram(int[]histogram,float[]builthistogram,int width, int height);
	public static  native void clusterizeKMeansHistogram(byte[] img,byte[]clusterized,float hist[],int numOfClusters);
	public static  native void smoothHistogramFloat(float[]histogram,float[]smoothedhistogram,int size);
	public static  native void buildEnergyHistogram(int[]histogram,float[]builthistogram,int width, int height);
	public static  native void buildKurtosisHistogram(int[]histogram,float[]builthistogram,int width, int height);	
}
