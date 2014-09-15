#include <jni.h>
#include <com_example_imageprocessor_ImgProcessor.h>
#include <android/log.h>
#include <stdio.h>


JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_convertARGBToGrayscale
(JNIEnv * env, jobject obj, jint width, jint height, jbyteArray argb,jbyteArray gray){
 int i,result;
 jint argbarraySize,grayarraySize;
 jbyte* argbimage;
 jbyte* grayimage;
 argbarraySize=(*env)->GetArrayLength(env,argb);
 grayarraySize=(*env)->GetArrayLength(env,gray);


 	 argbimage=(*env)->GetByteArrayElements(env,argb,NULL);
 	 grayimage=(*env)->GetByteArrayElements(env,gray,NULL);
 	 i=0;
 	 (unsigned char*)argbimage;
 	 (unsigned char*)grayimage;
 	 while(i<=(argbarraySize-4)){
 		 grayimage[i]=argbimage[i];
  	 //	__android_log_write(ANDROID_LOG_DEBUG, "NativeFunction", "accessed successfully");//Or ANDROID_LOG_INFO, ..

 		result= ((66 *(unsigned char) argbimage[i+1] + 129 * (unsigned char)argbimage[i+2] +  25 *(unsigned char) argbimage[i+3] + 128) >> 8)+16;
 		 if(result>255){result=255;}
 		if(result<0){result=0;}
 		grayimage[i+1]=grayimage[i+2]=grayimage[i+3]=(unsigned char)result;
 		i+=4;
 	 }
 	 (jbyte*)grayimage;
 	 (jbyte*)argbimage;
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

