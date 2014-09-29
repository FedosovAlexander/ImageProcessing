#include <jni.h>
#include <com_example_imageprocessor_ImgProcessor.h>
#include <android/log.h>
#include <stdio.h>
#include <math.h>
#include <time.h>
#include <float.h>
#include <stdlib.h>
JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_convertARGBToGrayscale
(JNIEnv * env, jobject obj, jint width, jint height, jbyteArray argb,jbyteArray gray) {
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
	while(i<=(argbarraySize-4)) {
		grayimage[i]=argbimage[i];
		//	__android_log_write(ANDROID_LOG_DEBUG, "NativeFunction", "accessed successfully");//Or ANDROID_LOG_INFO, ..

		result= ((66 *(unsigned char) argbimage[i+1] + 129 * (unsigned char)argbimage[i+2] + 25 *(unsigned char) argbimage[i+3] + 128) >> 8)+16;
		if(result>255) {result=255;}
		if(result<0) {result=0;}
		grayimage[i+1]=grayimage[i+2]=grayimage[i+3]=(unsigned char)result;
		i+=4;
	}
	(jbyte*)grayimage;
	(jbyte*)argbimage;
	(*env)->ReleaseByteArrayElements(env, argb, argbimage, 0);
	(*env)->ReleaseByteArrayElements(env, gray, grayimage, 0);
}

JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_smoothHistogram
(JNIEnv * env, jobject obj, jintArray hist,jintArray smoothedhist, jint size) {
	jint i,j;
	jlong mean,divider;
	jint* pHist;
	jint* pSmoothedHist;
	jsize arraySize,smoothedArraySize;
	arraySize=(*env)->GetArrayLength(env,hist);
	smoothedArraySize=(*env)->GetArrayLength(env,smoothedhist);
	pHist=(*env)->GetIntArrayElements(env,hist,0);
	pSmoothedHist=(*env)->GetIntArrayElements(env,smoothedhist,0);
	if(pSmoothedHist!=NULL) {
		for(i=0;i<arraySize;i++) {
			mean=pHist[i];
			divider=1;
			for(j=1;j<(size/2)+1;j++) {
				if(i+j<arraySize) {mean+=pHist[i+j];divider++;}
				if(i-j>=0) {mean+=pHist[i-j];divider++;}
			}
			if(divider>0) {
				pSmoothedHist[i]=(jint)(mean/divider);
			}
		}
	}
	(*env)->ReleaseIntArrayElements(env, hist, pHist, 0);
	(*env)->ReleaseIntArrayElements(env, smoothedhist, pSmoothedHist, 0);
}

JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_interpolateBilinear
(JNIEnv *env, jclass obj, jint prevWidth, jint prevHeight, jint newWidth, jint newHeight, jbyteArray prevImage,jbyteArray newImage) {
	jbyte* pPrevImage,* pNewImage;
	jsize prevSize,newSize;
	jfloat xRatio,yRatio,xDelta,yDelta;
	jint Xp,Yp,Xn,Yn,index;
	jint upleft,downleft,upright,downright;
	prevSize=(*env)->GetArrayLength(env,prevImage);
	newSize=(*env)->GetArrayLength(env,newImage);
	pPrevImage=(*env)->GetByteArrayElements(env,prevImage,0);
	pNewImage=(*env)->GetByteArrayElements(env,newImage,0);
	xRatio=((jfloat)(prevWidth-1)/newWidth);
	yRatio=((jfloat)(prevHeight-1)/newHeight);
	(unsigned char*)pPrevImage;
	(unsigned char*)pNewImage;
	for(Yn=0;Yn<newHeight;Yn++) {
		for(Xn=0;Xn<newWidth;Xn++) {
			Xp=(jint)(Xn*xRatio);
			Yp=(jint)(Yn*yRatio);
			xDelta=(Xn*xRatio)-Xp;
			yDelta=(Yn*yRatio)-Yp;
			index=Yp*prevWidth*4+Xp*4;
			upleft=index;
			upright=index+4;
			downleft=index+4*prevWidth;
			downright=index+4*prevWidth+4;
			//calculate Alpha component
			pNewImage[Yn*newWidth*4+Xn*4]=(unsigned char)((pPrevImage[upleft]&0xFF)*(1-xDelta)*(1-yDelta)+(pPrevImage[downleft]&0xFF)*(xDelta)*(1-yDelta)+(pPrevImage[upright]&0xFF)*(1-xDelta)*(yDelta)+(pPrevImage[downright]&0xFF)*(xDelta)*(yDelta));
			//calculate Red component
			pNewImage[Yn*newWidth*4+Xn*4+1]=(unsigned char)((pPrevImage[upleft+1]&0xFF)*(1-xDelta)*(1-yDelta)+(pPrevImage[downleft+1]&0xFF)*(xDelta)*(1-yDelta)+(pPrevImage[upright+1]&0xFF)*(1-xDelta)*(yDelta)+(pPrevImage[downright+1]&0xFF)*(xDelta)*(yDelta));
			//calculate Green component
			pNewImage[Yn*newWidth*4+Xn*4+2]=(unsigned char)((pPrevImage[upleft+2]&0xFF)*(1-xDelta)*(1-yDelta)+(pPrevImage[downleft+2]&0xFF)*(xDelta)*(1-yDelta)+(pPrevImage[upright+2]&0xFF)*(1-xDelta)*(yDelta)+(pPrevImage[downright+2]&0xFF)*(xDelta)*(yDelta));
			//calculate Blue component
			pNewImage[Yn*newWidth*4+Xn*4+3]=(unsigned char)((pPrevImage[upleft+3]&0xFF)*(1-xDelta)*(1-yDelta)+(pPrevImage[downleft+3]&0xFF)*(xDelta)*(1-yDelta)+(pPrevImage[upright+3]&0xFF)*(1-xDelta)*(yDelta)+(pPrevImage[downright+3]&0xFF)*(xDelta)*(yDelta));

		}
	}
	(jbyte*)pPrevImage;
	(jbyte*)pNewImage;
	(*env)->ReleaseByteArrayElements(env, prevImage, pPrevImage, 0);
	(*env)->ReleaseByteArrayElements(env, newImage, pNewImage, 0);
}

JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_interpolateBicubic
(JNIEnv *env, jclass obj, jint prevWidth, jint prevHeight, jint newWidth, jint newHeight, jbyteArray prevImage,jbyteArray newImage) {
	jbyte* pPrevImage,* pNewImage;
	jsize prevSize,newSize;
	jfloat xRatio,yRatio,xDelta,yDelta,curAlpha,curRed,curGreen,curBlue;
	//a00,a01,a02,a03,  a10,a11,a12,a13,  a20,a21,a22,a23,  a30,a31,a32,a33
	jfloat alphasa[16];
	jfloat alphasr[16];
	jfloat alphasg[16];
	jfloat alphasb[16];
	//f(-1,-1),f(0,-1),f(1,-1),f(2,-1),  f(-1,0),f(0,0),f(1,0),f(2,0),  f(-1,1),f(0,1),f(1,1),f(2,1),  f(-1,2),f(0,2),f(1,2),f(2,2)
	jbyte funcAValues[16];
	jbyte funcRValues[16];
	jbyte funcGValues[16];
	jbyte funcBValues[16];
	jint Xp,Yp,Xn,Yn,index,prevXp,prevYp;
	jint i,j;
	prevSize=(*env)->GetArrayLength(env,prevImage);
	newSize=(*env)->GetArrayLength(env,newImage);
	pPrevImage=(*env)->GetByteArrayElements(env,prevImage,0);
	pNewImage=(*env)->GetByteArrayElements(env,newImage,0);
	xRatio=((jfloat)(prevWidth-1)/newWidth);
	yRatio=((jfloat)(prevHeight-1)/newHeight);
	prevXp=prevYp=0;
	for(Yn=1;Yn<newHeight-2;Yn++) {
		for(Xn=1;Xn<newWidth-2;Xn++) {
			Xp=(jint)(Xn*xRatio);
			Yp=(jint)(Yn*yRatio);
			xDelta=(Xn*xRatio)-Xp;
			yDelta=(Yn*yRatio)-Yp;
			index=Yp*prevWidth*4+Xp*4; //f(0,0)
			if((Xp!=prevXp)||(Yp!=prevYp)) {
				funcAValues[0]=pPrevImage[index-4*prevWidth-4];
				funcRValues[0]=pPrevImage[index-4*prevWidth-3];
				funcGValues[0]=pPrevImage[index-4*prevWidth-2];
				funcBValues[0]=pPrevImage[index-4*prevWidth-1];

				funcAValues[1]=pPrevImage[index-4];
				funcRValues[1]=pPrevImage[index-3];
				funcGValues[1]=pPrevImage[index-2];
				funcBValues[1]=pPrevImage[index-1];

				funcAValues[2]=pPrevImage[index+4*prevWidth-4];
				funcRValues[2]=pPrevImage[index+4*prevWidth-3];
				funcGValues[2]=pPrevImage[index+4*prevWidth-2];
				funcBValues[2]=pPrevImage[index+4*prevWidth-1];

				funcAValues[3]=pPrevImage[index+8*prevWidth-4];
				funcRValues[3]=pPrevImage[index+8*prevWidth-3];
				funcGValues[3]=pPrevImage[index+8*prevWidth-2];
				funcBValues[3]=pPrevImage[index+8*prevWidth-1];

				funcAValues[4]=pPrevImage[index-4*prevWidth];
				funcRValues[4]=pPrevImage[index-4*prevWidth+1];
				funcGValues[4]=pPrevImage[index-4*prevWidth+2];
				funcBValues[4]=pPrevImage[index-4*prevWidth+3];

				funcAValues[5]=pPrevImage[index];
				funcRValues[5]=pPrevImage[index+1];
				funcGValues[5]=pPrevImage[index+2];
				funcBValues[5]=pPrevImage[index+3];

				funcAValues[6]=pPrevImage[index+4*prevWidth];
				funcRValues[6]=pPrevImage[index+4*prevWidth+1];
				funcGValues[6]=pPrevImage[index+4*prevWidth+2];
				funcBValues[6]=pPrevImage[index+4*prevWidth+3];

				funcAValues[7]=pPrevImage[index+8*prevWidth];
				funcRValues[7]=pPrevImage[index+8*prevWidth+1];
				funcGValues[7]=pPrevImage[index+8*prevWidth+2];
				funcBValues[7]=pPrevImage[index+8*prevWidth+3];

				funcAValues[8]=pPrevImage[index-4*prevWidth+4];
				funcRValues[8]=pPrevImage[index-4*prevWidth+5];
				funcGValues[8]=pPrevImage[index-4*prevWidth+6];
				funcBValues[8]=pPrevImage[index-4*prevWidth+7];

				funcAValues[9]=pPrevImage[index+4];
				funcRValues[9]=pPrevImage[index+5];
				funcGValues[9]=pPrevImage[index+6];
				funcBValues[9]=pPrevImage[index+7];

				funcAValues[10]=pPrevImage[index+4*prevWidth+4];
				funcRValues[10]=pPrevImage[index+4*prevWidth+5];
				funcGValues[10]=pPrevImage[index+4*prevWidth+6];
				funcBValues[10]=pPrevImage[index+4*prevWidth+7];

				funcAValues[11]=pPrevImage[index+8*prevWidth+4];
				funcRValues[11]=pPrevImage[index+8*prevWidth+5];
				funcGValues[11]=pPrevImage[index+8*prevWidth+6];
				funcBValues[11]=pPrevImage[index+8*prevWidth+7];

				funcAValues[12]=pPrevImage[index-4*prevWidth+8];
				funcRValues[12]=pPrevImage[index-4*prevWidth+9];
				funcGValues[12]=pPrevImage[index-4*prevWidth+10];
				funcBValues[12]=pPrevImage[index-4*prevWidth+11];

				funcAValues[13]=pPrevImage[index+8];
				funcRValues[13]=pPrevImage[index+9];
				funcGValues[13]=pPrevImage[index+10];
				funcBValues[13]=pPrevImage[index+11];

				funcAValues[14]=pPrevImage[index+4*prevWidth+8];
				funcRValues[14]=pPrevImage[index+4*prevWidth+9];
				funcGValues[14]=pPrevImage[index+4*prevWidth+10];
				funcBValues[14]=pPrevImage[index+4*prevWidth+11];

				funcAValues[15]=pPrevImage[index+8*prevWidth+8];
				funcRValues[15]=pPrevImage[index+8*prevWidth+9];
				funcGValues[15]=pPrevImage[index+8*prevWidth+10];
				funcBValues[15]=pPrevImage[index+8*prevWidth+11];

				alphasa[0]=funcAValues[5]&0xff;
				alphasa[1]=((jfloat)(-12*(funcAValues[1]&0xff)-18*(funcAValues[5]&0xff)+36*(funcAValues[9]&0xff)-6*(funcAValues[13]&0xff)))/36.0;
				alphasa[2]=((jfloat)(18*(funcAValues[1]&0xff)-36*(funcAValues[5]&0xff)+18*(funcAValues[9]&0xff)))/36.0;
				alphasa[3]=((jfloat)(-6*(funcAValues[1]&0xff)+18*(funcAValues[5]&0xff)-18*(funcAValues[9]&0xff)+6*(funcAValues[13]&0xff)))/36.0;
				alphasa[4]=((jfloat)(-12*(funcAValues[4]&0xff)-18*(funcAValues[5]&0xff)+36*(funcAValues[6]&0xff)-6*(funcAValues[7]&0xff)))/36.0;
				alphasa[5]=((jfloat)(4*(funcAValues[0]&0xff)+6*(funcAValues[1]&0xff)-12*(funcAValues[2]&0xff)+2*(funcAValues[3]&0xff)+6*(funcAValues[4]&0xff)+9*(funcAValues[5]&0xff)-18*(funcAValues[6]&0xff)+3*(funcAValues[7]&0xff)-12*(funcAValues[8]&0xff)-18*(funcAValues[9]&0xff)+36*(funcAValues[10]&0xff)-6*(funcAValues[11]&0xff)+2*(funcAValues[12]&0xff)+3*(funcAValues[13]&0xff)-6*(funcAValues[14]&0xff)+(funcAValues[15]&0xff)))/36.0;
				alphasa[6]=((jfloat)(-6*(funcAValues[0]&0xff)-9*(funcAValues[1]&0xff)+18*(funcAValues[2]&0xff)-3*(funcAValues[3]&0xff)+12*(funcAValues[4]&0xff)+18*(funcAValues[5]&0xff)-36*(funcAValues[6]&0xff)+6*(funcAValues[7]&0xff)-6*(funcAValues[8]&0xff)-9*(funcAValues[9]&0xff)+18*(funcAValues[10]&0xff)-3*(funcAValues[11]&0xff)))/36.0;
				alphasa[7]=((jfloat)(2*(funcAValues[0]&0xff)+3*(funcAValues[1]&0xff)-6*(funcAValues[2]&0xff)+(funcAValues[3]&0xff)-6*(funcAValues[4]&0xff)-9*(funcAValues[5]&0xff)+18*(funcAValues[6]&0xff)-3*(funcAValues[7]&0xff)+6*(funcAValues[8]&0xff)+9*(funcAValues[9]&0xff)-18*(funcAValues[10]&0xff)+3*(funcAValues[11]&0xff)-2*(funcAValues[12]&0xff)-3*(funcAValues[13]&0xff)+6*(funcAValues[14]&0xff)-(funcAValues[15]&0xff)))/36.0;
				alphasa[8]=((jfloat)(18*(funcAValues[4]&0xff)-36*(funcAValues[5]&0xff)+18*(funcAValues[6]&0xff)))/36.0;
				alphasa[9]=((jfloat)(-6*(funcAValues[0]&0xff)+12*(funcAValues[1]&0xff)-6*(funcAValues[2]&0xff)-9*(funcAValues[4]&0xff)+18*(funcAValues[5]&0xff)-9*(funcAValues[6]&0xff)+18*(funcAValues[8]&0xff)-36*(funcAValues[9]&0xff)+18*(funcAValues[10]&0xff)-3*(funcAValues[12]&0xff)+6*(funcAValues[13]&0xff)-3*(funcAValues[14]&0xff)))/36.0;
				alphasa[10]=((jfloat)(9*(funcAValues[0]&0xff)-18*(funcAValues[1]&0xff)+9*(funcAValues[2]&0xff)-18*(funcAValues[4]&0xff)+36*(funcAValues[5]&0xff)-18*(funcAValues[6]&0xff)+9*(funcAValues[8]&0xff)-18*(funcAValues[9]&0xff)+9*(funcAValues[10]&0xff)))/36.0;
				alphasa[11]=((jfloat)(-3*(funcAValues[0]&0xff)+6*(funcAValues[1]&0xff)-3*(funcAValues[2]&0xff)+9*(funcAValues[4]&0xff)-18*(funcAValues[5]&0xff)+9*(funcAValues[6]&0xff)-9*(funcAValues[8]&0xff)+18*(funcAValues[9]&0xff)-9*(funcAValues[10]&0xff)+3*(funcAValues[12]&0xff)-6*(funcAValues[13]&0xff)+3*(funcAValues[14]&0xff)))/36.0;
				alphasa[12]=((jfloat)(-6*(funcAValues[4]&0xff)+18*(funcAValues[5]&0xff)-18*(funcAValues[6]&0xff)+6*(funcAValues[7]&0xff)))/36.0;
				alphasa[13]=((jfloat)(2*(funcAValues[0]&0xff)-6*(funcAValues[1]&0xff)+6*(funcAValues[2]&0xff)-2*(funcAValues[3]&0xff)+3*(funcAValues[4]&0xff)-9*(funcAValues[5]&0xff)+9*(funcAValues[6]&0xff)-3*(funcAValues[7]&0xff)-6*(funcAValues[8]&0xff)+18*(funcAValues[9]&0xff)-18*(funcAValues[10]&0xff)+6*(funcAValues[11]&0xff)+(funcAValues[12]&0xff)-3*(funcAValues[13]&0xff)+3*(funcAValues[14]&0xff)-(funcAValues[15]&0xff)))/36.0;
				alphasa[14]=((jfloat)(-3*(funcAValues[0]&0xff)+9*(funcAValues[1]&0xff)-9*(funcAValues[2]&0xff)+3*(funcAValues[3]&0xff)+6*(funcAValues[4]&0xff)-18*(funcAValues[5]&0xff)+18*(funcAValues[6]&0xff)-6*(funcAValues[7]&0xff)-3*(funcAValues[8]&0xff)+9*(funcAValues[9]&0xff)-9*(funcAValues[10]&0xff)+3*(funcAValues[11]&0xff)))/36.0;
				alphasa[15]=((jfloat)((funcAValues[0]&0xff)-3*(funcAValues[1]&0xff)+3*(funcAValues[2]&0xff)-(funcAValues[3]&0xff)-3*(funcAValues[4]&0xff)+9*(funcAValues[5]&0xff)-9*(funcAValues[6]&0xff)+3*(funcAValues[7]&0xff)+3*(funcAValues[8]&0xff)-9*(funcAValues[9]&0xff)+9*(funcAValues[10]&0xff)-3*(funcAValues[11]&0xff)-(funcAValues[12]&0xff)+3*(funcAValues[13]&0xff)-3*(funcAValues[14]&0xff)+(funcAValues[15]&0xff)))/36.0;

				alphasr[0]=funcRValues[5]&0xff;
				alphasr[1]=((jfloat)(-12*(funcRValues[1]&0xff)-18*(funcRValues[5]&0xff)+36*(funcRValues[9]&0xff)-6*(funcRValues[13]&0xff)))/36.0;
				alphasr[2]=((jfloat)(18*(funcRValues[1]&0xff)-36*(funcRValues[5]&0xff)+18*(funcRValues[9]&0xff)))/36.0;
				alphasr[3]=((jfloat)(-6*(funcRValues[1]&0xff)+18*(funcRValues[5]&0xff)-18*(funcRValues[9]&0xff)+6*(funcRValues[13]&0xff)))/36.0;
				alphasr[4]=((jfloat)(-12*(funcRValues[4]&0xff)-18*(funcRValues[5]&0xff)+36*(funcRValues[6]&0xff)-6*(funcRValues[7]&0xff)))/36.0;
				alphasr[5]=((jfloat)(4*(funcRValues[0]&0xff)+6*(funcRValues[1]&0xff)-12*(funcRValues[2]&0xff)+2*(funcRValues[3]&0xff)+6*(funcRValues[4]&0xff)+9*(funcRValues[5]&0xff)-18*(funcRValues[6]&0xff)+3*(funcRValues[7]&0xff)-12*(funcRValues[8]&0xff)-18*(funcRValues[9]&0xff)+36*(funcRValues[10]&0xff)-6*(funcRValues[11]&0xff)+2*(funcRValues[12]&0xff)+3*(funcRValues[13]&0xff)-6*(funcRValues[14]&0xff)+(funcRValues[15]&0xff)))/36.0;
				alphasr[6]=((jfloat)(-6*(funcRValues[0]&0xff)-9*(funcRValues[1]&0xff)+18*(funcRValues[2]&0xff)-3*(funcRValues[3]&0xff)+12*(funcRValues[4]&0xff)+18*(funcRValues[5]&0xff)-36*(funcRValues[6]&0xff)+6*(funcRValues[7]&0xff)-6*(funcRValues[8]&0xff)-9*(funcRValues[9]&0xff)+18*(funcRValues[10]&0xff)-3*(funcRValues[11]&0xff)))/36.0;
				alphasr[7]=((jfloat)(2*(funcRValues[0]&0xff)+3*(funcRValues[1]&0xff)-6*(funcRValues[2]&0xff)+(funcRValues[3]&0xff)-6*(funcRValues[4]&0xff)-9*(funcRValues[5]&0xff)+18*(funcRValues[6]&0xff)-3*(funcRValues[7]&0xff)+6*(funcRValues[8]&0xff)+9*(funcRValues[9]&0xff)-18*(funcRValues[10]&0xff)+3*(funcRValues[11]&0xff)-2*(funcRValues[12]&0xff)-3*(funcRValues[13]&0xff)+6*(funcRValues[14]&0xff)-(funcRValues[15]&0xff)))/36.0;
				alphasr[8]=((jfloat)(18*(funcRValues[4]&0xff)-36*(funcRValues[5]&0xff)+18*(funcRValues[6]&0xff)))/36.0;
				alphasr[9]=((jfloat)(-6*(funcRValues[0]&0xff)+12*(funcRValues[1]&0xff)-6*(funcRValues[2]&0xff)-9*(funcRValues[4]&0xff)+18*(funcRValues[5]&0xff)-9*(funcRValues[6]&0xff)+18*(funcRValues[8]&0xff)-36*(funcRValues[9]&0xff)+18*(funcRValues[10]&0xff)-3*(funcRValues[12]&0xff)+6*(funcRValues[13]&0xff)-3*(funcRValues[14]&0xff)))/36.0;
				alphasr[10]=((jfloat)(9*(funcRValues[0]&0xff)-18*(funcRValues[1]&0xff)+9*(funcRValues[2]&0xff)-18*(funcRValues[4]&0xff)+36*(funcRValues[5]&0xff)-18*(funcRValues[6]&0xff)+9*(funcRValues[8]&0xff)-18*(funcRValues[9]&0xff)+9*(funcRValues[10]&0xff)))/36.0;
				alphasr[11]=((jfloat)(-3*(funcRValues[0]&0xff)+6*(funcRValues[1]&0xff)-3*(funcRValues[2]&0xff)+9*(funcRValues[4]&0xff)-18*(funcRValues[5]&0xff)+9*(funcRValues[6]&0xff)-9*(funcRValues[8]&0xff)+18*(funcRValues[9]&0xff)-9*(funcRValues[10]&0xff)+3*(funcRValues[12]&0xff)-6*(funcRValues[13]&0xff)+3*(funcRValues[14]&0xff)))/36.0;
				alphasr[12]=((jfloat)(-6*(funcRValues[4]&0xff)+18*(funcRValues[5]&0xff)-18*(funcRValues[6]&0xff)+6*(funcRValues[7]&0xff)))/36.0;
				alphasr[13]=((jfloat)(2*(funcRValues[0]&0xff)-6*(funcRValues[1]&0xff)+6*(funcRValues[2]&0xff)-2*(funcRValues[3]&0xff)+3*(funcRValues[4]&0xff)-9*(funcRValues[5]&0xff)+9*(funcRValues[6]&0xff)-3*(funcRValues[7]&0xff)-6*(funcRValues[8]&0xff)+18*(funcRValues[9]&0xff)-18*(funcRValues[10]&0xff)+6*(funcRValues[11]&0xff)+(funcRValues[12]&0xff)-3*(funcRValues[13]&0xff)+3*(funcRValues[14]&0xff)-(funcRValues[15]&0xff)))/36.0;
				alphasr[14]=((jfloat)(-3*(funcRValues[0]&0xff)+9*(funcRValues[1]&0xff)-9*(funcRValues[2]&0xff)+3*(funcRValues[3]&0xff)+6*(funcRValues[4]&0xff)-18*(funcRValues[5]&0xff)+18*(funcRValues[6]&0xff)-6*(funcRValues[7]&0xff)-3*(funcRValues[8]&0xff)+9*(funcRValues[9]&0xff)-9*(funcRValues[10]&0xff)+3*(funcRValues[11]&0xff)))/36.0;
				alphasr[15]=((jfloat)((funcRValues[0]&0xff)-3*(funcRValues[1]&0xff)+3*(funcRValues[2]&0xff)-(funcRValues[3]&0xff)-3*(funcRValues[4]&0xff)+9*(funcRValues[5]&0xff)-9*(funcRValues[6]&0xff)+3*(funcRValues[7]&0xff)+3*(funcRValues[8]&0xff)-9*(funcRValues[9]&0xff)+9*(funcRValues[10]&0xff)-3*(funcRValues[11]&0xff)-(funcRValues[12]&0xff)+3*(funcRValues[13]&0xff)-3*(funcRValues[14]&0xff)+(funcRValues[15]&0xff)))/36.0;

				alphasg[0]=funcGValues[5]&0xff;
				alphasg[1]=((jfloat)(-12*(funcGValues[1]&0xff)-18*(funcGValues[5]&0xff)+36*(funcGValues[9]&0xff)-6*(funcGValues[13]&0xff)))/36.0;
				alphasg[2]=((jfloat)(18*(funcGValues[1]&0xff)-36*(funcGValues[5]&0xff)+18*(funcGValues[9]&0xff)))/36.0;
				alphasg[3]=((jfloat)(-6*(funcGValues[1]&0xff)+18*(funcGValues[5]&0xff)-18*(funcGValues[9]&0xff)+6*(funcGValues[13]&0xff)))/36.0;
				alphasg[4]=((jfloat)(-12*(funcGValues[4]&0xff)-18*(funcGValues[5]&0xff)+36*(funcGValues[6]&0xff)-6*(funcGValues[7]&0xff)))/36.0;
				alphasg[5]=((jfloat)(4*(funcGValues[0]&0xff)+6*(funcGValues[1]&0xff)-12*(funcGValues[2]&0xff)+2*(funcGValues[3]&0xff)+6*(funcGValues[4]&0xff)+9*(funcGValues[5]&0xff)-18*(funcGValues[6]&0xff)+3*(funcGValues[7]&0xff)-12*(funcGValues[8]&0xff)-18*(funcGValues[9]&0xff)+36*(funcGValues[10]&0xff)-6*(funcGValues[11]&0xff)+2*(funcGValues[12]&0xff)+3*(funcGValues[13]&0xff)-6*(funcGValues[14]&0xff)+(funcGValues[15]&0xff)))/36.0;
				alphasg[6]=((jfloat)(-6*(funcGValues[0]&0xff)-9*(funcGValues[1]&0xff)+18*(funcGValues[2]&0xff)-3*(funcGValues[3]&0xff)+12*(funcGValues[4]&0xff)+18*(funcGValues[5]&0xff)-36*(funcGValues[6]&0xff)+6*(funcGValues[7]&0xff)-6*(funcGValues[8]&0xff)-9*(funcGValues[9]&0xff)+18*(funcGValues[10]&0xff)-3*(funcGValues[11]&0xff)))/36.0;
				alphasg[7]=((jfloat)(2*(funcGValues[0]&0xff)+3*(funcGValues[1]&0xff)-6*(funcGValues[2]&0xff)+(funcGValues[3]&0xff)-6*(funcGValues[4]&0xff)-9*(funcGValues[5]&0xff)+18*(funcGValues[6]&0xff)-3*(funcGValues[7]&0xff)+6*(funcGValues[8]&0xff)+9*(funcGValues[9]&0xff)-18*(funcGValues[10]&0xff)+3*(funcGValues[11]&0xff)-2*(funcGValues[12]&0xff)-3*(funcGValues[13]&0xff)+6*(funcGValues[14]&0xff)-(funcGValues[15]&0xff)))/36.0;
				alphasg[8]=((jfloat)(18*(funcGValues[4]&0xff)-36*(funcGValues[5]&0xff)+18*(funcGValues[6]&0xff)))/36.0;
				alphasg[9]=((jfloat)(-6*(funcGValues[0]&0xff)+12*(funcGValues[1]&0xff)-6*(funcGValues[2]&0xff)-9*(funcGValues[4]&0xff)+18*(funcGValues[5]&0xff)-9*(funcGValues[6]&0xff)+18*(funcGValues[8]&0xff)-36*(funcGValues[9]&0xff)+18*(funcGValues[10]&0xff)-3*(funcGValues[12]&0xff)+6*(funcGValues[13]&0xff)-3*(funcGValues[14]&0xff)))/36.0;
				alphasg[10]=((jfloat)(9*(funcGValues[0]&0xff)-18*(funcGValues[1]&0xff)+9*(funcGValues[2]&0xff)-18*(funcGValues[4]&0xff)+36*(funcGValues[5]&0xff)-18*(funcGValues[6]&0xff)+9*(funcGValues[8]&0xff)-18*(funcGValues[9]&0xff)+9*(funcGValues[10]&0xff)))/36.0;
				alphasg[11]=((jfloat)(-3*(funcGValues[0]&0xff)+6*(funcGValues[1]&0xff)-3*(funcGValues[2]&0xff)+9*(funcGValues[4]&0xff)-18*(funcGValues[5]&0xff)+9*(funcGValues[6]&0xff)-9*(funcGValues[8]&0xff)+18*(funcGValues[9]&0xff)-9*(funcGValues[10]&0xff)+3*(funcGValues[12]&0xff)-6*(funcGValues[13]&0xff)+3*(funcGValues[14]&0xff)))/36.0;
				alphasg[12]=((jfloat)(-6*(funcGValues[4]&0xff)+18*(funcGValues[5]&0xff)-18*(funcGValues[6]&0xff)+6*(funcGValues[7]&0xff)))/36.0;
				alphasg[13]=((jfloat)(2*(funcGValues[0]&0xff)-6*(funcGValues[1]&0xff)+6*(funcGValues[2]&0xff)-2*(funcGValues[3]&0xff)+3*(funcGValues[4]&0xff)-9*(funcGValues[5]&0xff)+9*(funcGValues[6]&0xff)-3*(funcGValues[7]&0xff)-6*(funcGValues[8]&0xff)+18*(funcGValues[9]&0xff)-18*(funcGValues[10]&0xff)+6*(funcGValues[11]&0xff)+(funcGValues[12]&0xff)-3*(funcGValues[13]&0xff)+3*(funcGValues[14]&0xff)-(funcGValues[15]&0xff)))/36.0;
				alphasg[14]=((jfloat)(-3*(funcGValues[0]&0xff)+9*(funcGValues[1]&0xff)-9*(funcGValues[2]&0xff)+3*(funcGValues[3]&0xff)+6*(funcGValues[4]&0xff)-18*(funcGValues[5]&0xff)+18*(funcGValues[6]&0xff)-6*(funcGValues[7]&0xff)-3*(funcGValues[8]&0xff)+9*(funcGValues[9]&0xff)-9*(funcGValues[10]&0xff)+3*(funcGValues[11]&0xff)))/36.0;
				alphasg[15]=((jfloat)((funcGValues[0]&0xff)-3*(funcGValues[1]&0xff)+3*(funcGValues[2]&0xff)-(funcGValues[3]&0xff)-3*(funcGValues[4]&0xff)+9*(funcGValues[5]&0xff)-9*(funcGValues[6]&0xff)+3*(funcGValues[7]&0xff)+3*(funcGValues[8]&0xff)-9*(funcGValues[9]&0xff)+9*(funcGValues[10]&0xff)-3*(funcGValues[11]&0xff)-(funcGValues[12]&0xff)+3*(funcGValues[13]&0xff)-3*(funcGValues[14]&0xff)+(funcGValues[15]&0xff)))/36.0;

				alphasb[0]=funcBValues[5]&0xff;
				alphasb[1]=((jfloat)(-12*(funcBValues[1]&0xff)-18*(funcBValues[5]&0xff)+36*(funcBValues[9]&0xff)-6*(funcBValues[13]&0xff)))/36.0;
				alphasb[2]=((jfloat)(18*(funcBValues[1]&0xff)-36*(funcBValues[5]&0xff)+18*(funcBValues[9]&0xff)))/36.0;
				alphasb[3]=((jfloat)(-6*(funcBValues[1]&0xff)+18*(funcBValues[5]&0xff)-18*(funcBValues[9]&0xff)+6*(funcBValues[13]&0xff)))/36.0;
				alphasb[4]=((jfloat)(-12*(funcBValues[4]&0xff)-18*(funcBValues[5]&0xff)+36*(funcBValues[6]&0xff)-6*(funcBValues[7]&0xff)))/36.0;
				alphasb[5]=((jfloat)(4*(funcBValues[0]&0xff)+6*(funcBValues[1]&0xff)-12*(funcBValues[2]&0xff)+2*(funcBValues[3]&0xff)+6*(funcBValues[4]&0xff)+9*(funcBValues[5]&0xff)-18*(funcBValues[6]&0xff)+3*(funcBValues[7]&0xff)-12*(funcBValues[8]&0xff)-18*(funcBValues[9]&0xff)+36*(funcBValues[10]&0xff)-6*(funcBValues[11]&0xff)+2*(funcBValues[12]&0xff)+3*(funcBValues[13]&0xff)-6*(funcBValues[14]&0xff)+(funcBValues[15]&0xff)))/36.0;
				alphasb[6]=((jfloat)(-6*(funcBValues[0]&0xff)-9*(funcBValues[1]&0xff)+18*(funcBValues[2]&0xff)-3*(funcBValues[3]&0xff)+12*(funcBValues[4]&0xff)+18*(funcBValues[5]&0xff)-36*(funcBValues[6]&0xff)+6*(funcBValues[7]&0xff)-6*(funcBValues[8]&0xff)-9*(funcBValues[9]&0xff)+18*(funcBValues[10]&0xff)-3*(funcBValues[11]&0xff)))/36.0;
				alphasb[7]=((jfloat)(2*(funcBValues[0]&0xff)+3*(funcBValues[1]&0xff)-6*(funcBValues[2]&0xff)+(funcBValues[3]&0xff)-6*(funcBValues[4]&0xff)-9*(funcBValues[5]&0xff)+18*(funcBValues[6]&0xff)-3*(funcBValues[7]&0xff)+6*(funcBValues[8]&0xff)+9*(funcBValues[9]&0xff)-18*(funcBValues[10]&0xff)+3*(funcBValues[11]&0xff)-2*(funcBValues[12]&0xff)-3*(funcBValues[13]&0xff)+6*(funcBValues[14]&0xff)-(funcBValues[15]&0xff)))/36.0;
				alphasb[8]=((jfloat)(18*(funcBValues[4]&0xff)-36*(funcBValues[5]&0xff)+18*(funcBValues[6]&0xff)))/36.0;
				alphasb[9]=((jfloat)(-6*(funcBValues[0]&0xff)+12*(funcBValues[1]&0xff)-6*(funcBValues[2]&0xff)-9*(funcBValues[4]&0xff)+18*(funcBValues[5]&0xff)-9*(funcBValues[6]&0xff)+18*(funcBValues[8]&0xff)-36*(funcBValues[9]&0xff)+18*(funcBValues[10]&0xff)-3*(funcBValues[12]&0xff)+6*(funcBValues[13]&0xff)-3*(funcBValues[14]&0xff)))/36.0;
				alphasb[10]=((jfloat)(9*(funcBValues[0]&0xff)-18*(funcBValues[1]&0xff)+9*(funcBValues[2]&0xff)-18*(funcBValues[4]&0xff)+36*(funcBValues[5]&0xff)-18*(funcBValues[6]&0xff)+9*(funcBValues[8]&0xff)-18*(funcBValues[9]&0xff)+9*(funcBValues[10]&0xff)))/36.0;
				alphasb[11]=((jfloat)(-3*(funcBValues[0]&0xff)+6*(funcBValues[1]&0xff)-3*(funcBValues[2]&0xff)+9*(funcBValues[4]&0xff)-18*(funcBValues[5]&0xff)+9*(funcBValues[6]&0xff)-9*(funcBValues[8]&0xff)+18*(funcBValues[9]&0xff)-9*(funcBValues[10]&0xff)+3*(funcBValues[12]&0xff)-6*(funcBValues[13]&0xff)+3*(funcBValues[14]&0xff)))/36.0;
				alphasb[12]=((jfloat)(-6*(funcBValues[4]&0xff)+18*(funcBValues[5]&0xff)-18*(funcBValues[6]&0xff)+6*(funcBValues[7]&0xff)))/36.0;
				alphasb[13]=((jfloat)(2*(funcBValues[0]&0xff)-6*(funcBValues[1]&0xff)+6*(funcBValues[2]&0xff)-2*(funcBValues[3]&0xff)+3*(funcBValues[4]&0xff)-9*(funcBValues[5]&0xff)+9*(funcBValues[6]&0xff)-3*(funcBValues[7]&0xff)-6*(funcBValues[8]&0xff)+18*(funcBValues[9]&0xff)-18*(funcBValues[10]&0xff)+6*(funcBValues[11]&0xff)+(funcBValues[12]&0xff)-3*(funcBValues[13]&0xff)+3*(funcBValues[14]&0xff)-(funcBValues[15]&0xff)))/36.0;
				alphasb[14]=((jfloat)(-3*(funcBValues[0]&0xff)+9*(funcBValues[1]&0xff)-9*(funcBValues[2]&0xff)+3*(funcBValues[3]&0xff)+6*(funcBValues[4]&0xff)-18*(funcBValues[5]&0xff)+18*(funcBValues[6]&0xff)-6*(funcBValues[7]&0xff)-3*(funcBValues[8]&0xff)+9*(funcBValues[9]&0xff)-9*(funcBValues[10]&0xff)+3*(funcBValues[11]&0xff)))/36.0;
				alphasb[15]=((jfloat)((funcBValues[0]&0xff)-3*(funcBValues[1]&0xff)+3*(funcBValues[2]&0xff)-(funcBValues[3]&0xff)-3*(funcBValues[4]&0xff)+9*(funcBValues[5]&0xff)-9*(funcBValues[6]&0xff)+3*(funcBValues[7]&0xff)+3*(funcBValues[8]&0xff)-9*(funcBValues[9]&0xff)+9*(funcBValues[10]&0xff)-3*(funcBValues[11]&0xff)-(funcBValues[12]&0xff)+3*(funcBValues[13]&0xff)-3*(funcBValues[14]&0xff)+(funcBValues[15]&0xff)))/36.0;
			}
			prevXp=Xp;
			prevYp=Yp;
			curAlpha=curRed=curGreen=curBlue=0;
			for(i=0;i<4;i++) {
				for(j=0;j<4;j++) {
					curAlpha+=alphasa[i*4+j]*pow(xDelta,i)*pow(yDelta,j);
					curRed+=alphasr[i*4+j]*pow(xDelta,i)*pow(yDelta,j);
					curGreen+=alphasg[i*4+j]*pow(xDelta,i)*pow(yDelta,j);
					curBlue+=alphasb[i*4+j]*pow(xDelta,i)*pow(yDelta,j);
				}
			}

			curAlpha=roundf(curAlpha);
			curRed=roundf(curRed);
			curGreen=roundf(curGreen);
			curBlue=roundf(curBlue);
			curAlpha=(curAlpha<255?curAlpha:255);
			curAlpha=(curAlpha>0?curAlpha:0);
			curRed=(curRed<255?curRed:255);
			curRed=(curRed>0?curRed:0);
			curGreen=(curGreen<255?curGreen:255);
			curGreen=(curGreen>0?curGreen:0);
			curBlue=(curBlue<255?curBlue:255);
			curBlue=(curBlue>0?curBlue:0);
			pNewImage[Yn*newWidth*4+Xn*4]=(unsigned char)(curAlpha);
			pNewImage[Yn*newWidth*4+Xn*4+1]=(unsigned char)(curRed);
			pNewImage[Yn*newWidth*4+Xn*4+2]=(unsigned char)(curGreen);
			pNewImage[Yn*newWidth*4+Xn*4+3]=(unsigned char)(curBlue);
		}
	}

	(jbyte*)pPrevImage;
	(jbyte*)pNewImage;
	(*env)->ReleaseByteArrayElements(env, prevImage, pPrevImage, 0);
	(*env)->ReleaseByteArrayElements(env, newImage, pNewImage, 0);
}

JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_binarizeOtsu
(JNIEnv *env, jclass obj, jint width, jint height, jbyteArray image, jbyteArray binarized, jintArray hist) {
	jbyte* pImage,*pBinarized;
	jint* pHist;
	jint threshold,i,j,k;
	jsize histSize,imgSize,binSize;
	jdouble sum1,sum2,divider1,divider2,mean1,mean2,squaredDifference;
	imgSize=(*env)->GetArrayLength(env,image);
	binSize=(*env)->GetArrayLength(env,binarized);
	histSize=(*env)->GetArrayLength(env,hist);
	pBinarized=(*env)->GetByteArrayElements(env,binarized,0);
	pImage=(*env)->GetByteArrayElements(env,image,0);
	pHist=(*env)->GetIntArrayElements(env,hist,0);
	threshold=0;
	(unsigned char*)pImage;
	(unsigned char*)pBinarized;
	sum1=sum2=mean1=mean2=squaredDifference=0.0;
	for(i=1;i<histSize-2;i++) {
		sum1=sum2=mean1=mean2=0;
		for(j=1;j<=i;j++) {
			sum1+=pHist[j]*j;
			divider1+=pHist[j];
		}
		mean1=sum1/divider1;
		for(j=i+1;j<histSize-1;j++) {
			sum2+=pHist[j]*j;
			divider2+=pHist[j];
		}
		mean2=sum2/divider2;
		if(((mean1-mean2)*(mean1-mean2))>squaredDifference){
			squaredDifference=((mean1-mean2)*(mean1-mean2));
			threshold=i;
		}
	}
	for(k=0;k<imgSize;k+=4){
		pBinarized[k]=pImage[k];
		pBinarized[k+1]=pBinarized[k+2]=pBinarized[k+3]=(pImage[k+1]>threshold? 0:(histSize-1));
	}
	(jbyte*)pImage;
	(jbyte*)pBinarized;
	(*env)->ReleaseByteArrayElements(env, image,pImage, 0);
	(*env)->ReleaseByteArrayElements(env,binarized,pBinarized, 0);
	(*env)->ReleaseIntArrayElements(env,hist,pHist,0);
}


JNIEXPORT jint JNICALL Java_com_example_imageprocessor_ImgProcessor_getNumberOfPeaks
  (JNIEnv *env, jclass obj, jintArray hist){
	jint i,result,tmpcount,weight,curLeftBorder,curRightBorder,curTop,numOfPeaks=0;
	jint flag=0;
	jfloat threshold,peaks[4*256];
	jsize histSize;
	jint* pHist;
	histSize=(*env)->GetArrayLength(env,hist);
	pHist=(*env)->GetIntArrayElements(env,hist,0);
	curTop=pHist[0];
	i=1;
	curLeftBorder=0;
	curRightBorder=0;
	result=0;
	while(i<histSize){
		if(pHist[i]>=curTop){curTop=pHist[i];peaks[numOfPeaks*4]=i;flag=0;}
		else{
			curRightBorder=i;
			peaks[numOfPeaks*4+1]=curLeftBorder;
			peaks[numOfPeaks*4+2]=curRightBorder;
			weight=0;
			for(tmpcount=curLeftBorder;tmpcount<=curRightBorder;tmpcount++){
				weight+=pHist[tmpcount];
			}
			peaks[numOfPeaks*4+3]=(1-(curLeftBorder+curRightBorder)/(2*curTop))*(1-(weight/((curRightBorder-curLeftBorder)*curTop)));
			curLeftBorder=curRightBorder;
			curTop=pHist[curLeftBorder];
			numOfPeaks++;flag=1;}
		i++;
	}
	if(flag==0){numOfPeaks++;}
	if(numOfPeaks==1){(*env )->ReleaseIntArrayElements(env,hist,pHist,0);return numOfPeaks;}
	threshold=0;
	for(i=0;i<numOfPeaks;i+=4){
		threshold+=peaks[i+3];
	}
	threshold/=numOfPeaks;
	threshold*=0.6;
	for(i=0;i<numOfPeaks;i+=4){
		if(peaks[i+3]>threshold){result++;}
	}
	(*env )->ReleaseIntArrayElements(env,hist,pHist,0);
	return result;
}


JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_clusterizeKMeans
  (JNIEnv *env, jclass obj, jbyteArray img, jbyteArray clusterized,jintArray hist,jint numOfClusters, jint shift){
	jsize sizeOfHist,sizeOfImg,sizeOfClusterized;
	jbyte *pImg,*pClusterized;
	jint *pHist;
	jint centroids[numOfClusters],bestClusterization[numOfClusters];
	jint i,j,k,counter,flag,divider;
	jdouble distance,vectorLength,averageDistance,delta,xRatio,bestMeanDistance,bestDelta;
	jdouble averages[numOfClusters];
	jint *nearestCentroids,*bestNearestCentroids;
	sizeOfHist=(*env)->GetArrayLength(env,hist);
	sizeOfImg=(*env)->GetArrayLength(env,img);
	sizeOfClusterized=(*env)->GetArrayLength(env,clusterized);
	pHist=(*env)->GetIntArrayElements(env,hist,0);
	pImg=(*env)->GetByteArrayElements(env,img,0);
	pClusterized=(*env)->GetByteArrayElements(env,clusterized,0);
	(unsigned char*)pImg;
	(unsigned char*)pClusterized;
	nearestCentroids=malloc(sizeOfHist*sizeof(int));
	bestNearestCentroids=malloc(sizeOfHist*sizeof(int));
	bestDelta=DBL_MAX;
	bestMeanDistance=DBL_MAX;
	k=pHist[0];
	for(i=0;i<sizeOfHist;i++){if(pHist[i]>k){k=pHist[i];}}
	xRatio=((jdouble)k)/sizeOfHist;
	for(k=0;k<100;k++){
		__android_log_write(ANDROID_LOG_DEBUG, "NativeFunction", "going to call srand");//Or ANDROID_LOG_INFO, ..
	srand(time(NULL));
	__android_log_write(ANDROID_LOG_DEBUG, "NativeFunction", "accessed successfully");//Or ANDROID_LOG_INFO, ..
	for(i=0;i<numOfClusters;i++){
		centroids[i]=rand()%256;
		do{
			flag=0;
			for(counter=0;counter<i;counter++){
				if(centroids[counter]==centroids[i]){
					flag++;
					centroids[i]=rand()%256;
				}
			}
		}while(flag>0);
	}
	for(counter=0;counter<10;counter++){
		for(i=0;i<sizeOfHist;i++){
			distance=DBL_MAX;
			for(j=0;j<numOfClusters;j++){
				vectorLength=sqrt(((i-centroids[j])*(i-centroids[j])*xRatio*xRatio+(pHist[i]-pHist[centroids[j]])*(pHist[i]-pHist[centroids[j]])));
				if(distance>vectorLength){distance=vectorLength;nearestCentroids[i]=centroids[j];}
			}
		}
		for(j=0;j<numOfClusters;j++){
			divider=0;averageDistance=0.0;
			for(i=0;i<sizeOfHist;i++){
				if(nearestCentroids[i]==centroids[j]){
					divider++;averageDistance+=sqrt(((i-centroids[j])*(i-centroids[j])*xRatio*xRatio+(pHist[i]-pHist[centroids[j]])*(pHist[i]-pHist[centroids[j]])));
				}
			}
			averageDistance/=divider;
			averages[j]=averageDistance;
			delta=averageDistance;
			for(i=0;i<sizeOfHist;i++){
				vectorLength=sqrt(((i-centroids[j])*(i-centroids[j])*xRatio*xRatio+(pHist[i]-pHist[centroids[j]])*(pHist[i]-pHist[centroids[j]])));
				if(delta>fabs(averageDistance-vectorLength)){delta=fabs(averageDistance-vectorLength);centroids[j]=i;}
				}
			}
		}
	averageDistance=0.0;
	for(i=0;i<numOfClusters;i++){
		averageDistance+=averages[j];
	}
	averageDistance/=numOfClusters;
	if(bestMeanDistance-averageDistance>0&&(bestMeanDistance-averageDistance<bestDelta)){
		bestDelta=bestMeanDistance-averageDistance;bestMeanDistance=averageDistance;
		for(i=0;i<numOfClusters;i++){bestClusterization[i]=centroids[i];}
		for(i=0;i<sizeOfHist;i++){bestNearestCentroids[i]=nearestCentroids[i];}
	}
	}

for(i=shift;i<sizeOfImg;i+=4){
pClusterized[i]=bestNearestCentroids[pImg[i]];
}
free(nearestCentroids);
free(bestNearestCentroids);
(jbyte*)pImg;
(jbyte*)pClusterized;
(*env)->ReleaseByteArrayElements(env,img,pImg,0);
(*env)->ReleaseByteArrayElements(env,clusterized,pClusterized,0);
(*env)->ReleaseIntArrayElements(env,hist,pHist,0);
}



