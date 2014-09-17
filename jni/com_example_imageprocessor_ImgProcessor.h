/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_imageprocessor_ImgProcessor */

#ifndef _Included_com_example_imageprocessor_ImgProcessor
#define _Included_com_example_imageprocessor_ImgProcessor
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_imageprocessor_ImgProcessor
 * Method:    convertARGBToGrayscale
 * Signature: (II[B[B)V
 */
JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_convertARGBToGrayscale
  (JNIEnv *, jclass, jint, jint, jbyteArray, jbyteArray);

/*
 * Class:     com_example_imageprocessor_ImgProcessor
 * Method:    interpolateBilinear
 * Signature: (IIII[B[B)V
 */
JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_interpolateBilinear
  (JNIEnv *, jclass, jint, jint, jint, jint, jbyteArray, jbyteArray);

/*
 * Class:     com_example_imageprocessor_ImgProcessor
 * Method:    interpolateBicubic
 * Signature: (IIII[B[B)V
 */
JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_interpolateBicubic
  (JNIEnv *, jclass, jint, jint, jint, jint, jbyteArray, jbyteArray);

/*
 * Class:     com_example_imageprocessor_ImgProcessor
 * Method:    interpolateSpline
 * Signature: (IIII[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_example_imageprocessor_ImgProcessor_interpolateSpline
  (JNIEnv *, jclass, jint, jint, jint, jint, jbyteArray);

/*
 * Class:     com_example_imageprocessor_ImgProcessor
 * Method:    smoothHistogram
 * Signature: ([I[II)V
 */
JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_smoothHistogram
  (JNIEnv *, jclass, jintArray, jintArray, jint);

/*
 * Class:     com_example_imageprocessor_ImgProcessor
 * Method:    binarizeOtsu
 * Signature: (II[B[B[I)V
 */
JNIEXPORT void JNICALL Java_com_example_imageprocessor_ImgProcessor_binarizeOtsu
  (JNIEnv *, jclass, jint, jint, jbyteArray, jbyteArray, jintArray);

#ifdef __cplusplus
}
#endif
#endif
