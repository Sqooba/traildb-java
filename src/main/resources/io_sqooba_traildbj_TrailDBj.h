/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class io_sqooba_traildbj_TrailDBj */

#ifndef _Included_io_sqooba_traildbj_TrailDBj
#define _Included_io_sqooba_traildbj_TrailDBj
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsInit
 * Signature: ()Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsInit
  (JNIEnv *, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsOpen
 * Signature: (Ljava/nio/ByteBuffer;Ljava/lang/String;[Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsOpen
  (JNIEnv *, jobject, jobject, jstring, jobjectArray, jlong);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsClose
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsClose
  (JNIEnv *, jobject, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsAdd
 * Signature: (Ljava/nio/ByteBuffer;[BJ[Ljava/lang/String;[J)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsAdd
  (JNIEnv *, jobject, jobject, jbyteArray, jlong, jobjectArray, jlongArray);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsAppend
 * Signature: (Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsAppend
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsFinalize
 * Signature: (Ljava/nio/ByteBuffer;)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsFinalize
  (JNIEnv *, jobject, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbInit
 * Signature: ()Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbInit
  (JNIEnv *, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbOpen
 * Signature: (Ljava/nio/ByteBuffer;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbOpen
  (JNIEnv *, jobject, jobject, jstring);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbClose
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbClose
  (JNIEnv *, jobject, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbNumTrails
 * Signature: (Ljava/nio/ByteBuffer;)J
 */
JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbNumTrails
  (JNIEnv *, jobject, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbNumEvents
 * Signature: (Ljava/nio/ByteBuffer;)J
 */
JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbNumEvents
  (JNIEnv *, jobject, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbNumFields
 * Signature: (Ljava/nio/ByteBuffer;)J
 */
JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbNumFields
  (JNIEnv *, jobject, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbMinTimestamp
 * Signature: (Ljava/nio/ByteBuffer;)J
 */
JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbMinTimestamp
  (JNIEnv *, jobject, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbMaxTimestamp
 * Signature: (Ljava/nio/ByteBuffer;)J
 */
JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbMaxTimestamp
  (JNIEnv *, jobject, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbVersion
 * Signature: (Ljava/nio/ByteBuffer;)J
 */
JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbVersion
  (JNIEnv *, jobject, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbErrorStr
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbErrorStr
  (JNIEnv *, jobject, jint);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbLexiconSize
 * Signature: (Ljava/nio/ByteBuffer;J)J
 */
JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbLexiconSize
  (JNIEnv *, jobject, jobject, jlong);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbGetField
 * Signature: (Ljava/nio/ByteBuffer;Ljava/lang/String;Ljava/nio/ByteBuffer;)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetField
  (JNIEnv *, jobject, jobject, jstring, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbGetFieldName
 * Signature: (Ljava/nio/ByteBuffer;J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetFieldName
  (JNIEnv *, jobject, jobject, jlong);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbGetItem
 * Signature: (Ljava/nio/ByteBuffer;JLjava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetItem
  (JNIEnv *, jobject, jobject, jlong, jstring);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbGetValue
 * Signature: (Ljava/nio/ByteBuffer;JJLjava/nio/ByteBuffer;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetValue
  (JNIEnv *, jobject, jobject, jlong, jlong, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbGetItemValue
 * Signature: (Ljava/nio/ByteBuffer;JLjava/nio/ByteBuffer;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetItemValue
  (JNIEnv *, jobject, jobject, jlong, jobject);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbGetUUID
 * Signature: (Ljava/nio/ByteBuffer;J)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetUUID
  (JNIEnv *, jobject, jobject, jlong);

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbGetTrailId
 * Signature: (Ljava/nio/ByteBuffer;[BLjava/nio/ByteBuffer;)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetTrailId
  (JNIEnv *, jobject, jobject, jbyteArray, jobject);

#ifdef __cplusplus
}
#endif
#endif
