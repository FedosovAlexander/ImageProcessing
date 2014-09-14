#include <jni.h>
#include <com_example_imageprocessor_ImgProcessor.h>


JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_convertARGBToGrayscale
(JNIEnv * env, jobject obj, jint width, jint height, jbyteArray argb,jbyteArray gray){
 int i,result;
 jint arraySize;
 jbyte argbimage[(*env)->GetArrayLength(env,argb)];
 jbyte grayimage[(*env)->GetArrayLength(env,argb)];
 arraySize=(*env)->GetArrayLength(env,argb);

     (*env)->GetByteArrayRegion(env, argb, 0, arraySize, argbimage);
     (*env)->GetByteArrayRegion(env, argb, 0, arraySize, grayimage);
 	 for(i=0;i<=(arraySize-4);i+=4){
 		 grayimage[i]=argbimage[i];
 		 result= ((66 * argbimage[i+1] + 129 * argbimage[i+2] +  25 * argbimage[i+3] + 128) >> 8)+16;
 		 if(result>255){result=255;}
 		 if(result<0){result=0;}
 		 grayimage[i+1]=grayimage[i+2]=grayimage[i+3]=(jbyte)result;
 	 }
 	(*env)->ReleaseByteArrayElements(env, argb, argbimage, 0);
 	(*env)->ReleaseByteArrayElements(env, gray, grayimage, 0);
}


JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_smoothHistogram
  (JNIEnv * env, jobject obj, jintArray hist, jint size){
	int i,j,mean,divider;
	jint* pHist;
	jsize arraySize;
	arraySize=(*env)->GetArrayLength(env,hist);
	pHist=(*env)->GetIntArrayElements(env,hist,0);
	for(i=0;i<arraySize;i++){
		mean=i*pHist[i];
		divider=pHist[i];
		for(j=1;j<(size/2)+1;j++){
			if(i+j<arraySize){mean+=(i+j)*pHist[i+j];divider+=pHist[i+j];}
			if(i-j>=0){mean+=(i-j)*pHist[i-j];divider+=pHist[i-j];}
		}
		pHist[i]=mean/divider;
		}
	(*env)->ReleaseIntArrayElements(env, hist, pHist, 0);
}

