#include "io_sqooba_traildbj_TrailDBj.h"
#include <jni.h>
#include <stdio.h>
#include <traildb.h>

extern "C"
JNIEXPORT jobject JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsInit
  (JNIEnv *env, jobject thisObject) 
{
	void *cons = tdb_cons_init();
	jobject bb = env->NewDirectByteBuffer((void*) cons, sizeof(&cons));
    return bb;
}

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsOpen
 * Signature: (Ljava/nio/ByteBuffer;Ljava/lang/String;[Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsOpen
  (JNIEnv *env, jobject thisObject, jobject consj, jstring rootj, jobjectArray fieldNamesj, jlong numOfieldsj)
{
	// Convert arguments.
    tdb_cons *cons = (tdb_cons*) env->GetDirectBufferAddress(consj);
    const char *root = env->GetStringUTFChars(rootj, 0);

	jsize length = env->GetArrayLength(fieldNamesj); // Should be equal to numOfields
	const char *ofield_names[numOfieldsj];
	int i;
	for (i = 0; i < length; i++) {
		jstring objString = (jstring)env->GetObjectArrayElement(fieldNamesj, i);
		const char *field = env->GetStringUTFChars(objString, 0);
		ofield_names[i] = field;
    }

    long num_ofields = (long)numOfieldsj;

	// Call lib.
	return tdb_cons_open(cons, root, ofield_names, num_ofields);
}

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsClose
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsClose
  (JNIEnv *env, jobject thisObject, jobject consj) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) env->GetDirectBufferAddress(consj);

	// Call lib.
	tdb_cons_close(cons);
}

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsAdd
 * Signature: (Ljava/lang/Object;[BJ[Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsAdd
	(JNIEnv *env, jobject thisObject, jobject consj, jbyteArray uuidj, jlong timestampj, jobjectArray valuesj, jobject valuesLengths) 
{


	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) env->GetDirectBufferAddress(consj);

	jbyte* dataPtr = env->GetByteArrayElements(uuidj, NULL);
	const uint8_t *uuid = (const uint8_t*)dataPtr;
	
	jsize length = env->GetArrayLength(valuesj);
	const char *values[length];
	int i;
	for (i = 0; i < length; i++) {
		jstring objString = (jstring)env->GetObjectArrayElement(valuesj, i);
		const char *field = env->GetStringUTFChars(objString, 0);
		values[i] = field;
    }

	const uint64_t *values_lengths = (const uint64_t*) env->GetDirectBufferAddress(valuesLengths);
	
	// Call lib.
	return tdb_cons_add(cons, uuid, (long) timestampj, values, values_lengths);

	// Releasing resources.
	env->ReleaseByteArrayElements(uuidj, dataPtr, JNI_ABORT);
}

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsAppend
 * Signature: (Ljava/lang/Object;Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsAppend
  (JNIEnv *env, jobject thisObject, jobject consj, jobject tdbj) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) env->GetDirectBufferAddress(consj);
	tdb *db = (tdb*) env->GetDirectBufferAddress(tdbj);

	// Call lib.
	return tdb_cons_append(cons, db);

}

/*
 * Class:     io_sqooba_traildbj_TrailDBj
 * Method:    tdbConsFinalize
 * Signature: (Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsFinalize
  (JNIEnv *env, jobject thisObject, jobject consj) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) env->GetDirectBufferAddress(consj);

	// Call lib.
	return tdb_cons_finalize(cons);

}
