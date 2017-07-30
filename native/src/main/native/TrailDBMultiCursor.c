#include <traildb.h>
#include "traildb-java.h"


jclass CID_traildb_TrailDBMultiEvent;

jclass CID_traildb_TrailDBEvent;

jmethodID MID_traildb_TrailDBMultiEvent_Constructor;

jmethodID MID_traildb_TrailDBEvent_Constructor;

jfieldID FID_traildb_TrailDBMultiCursor_cur;

jfieldID FID_traildb_TrailDBCursor_cur;

jfieldID FID_traildb_TrailDBMultiEvent_event;

jfieldID FID_traildb_TrailDBEvent_timestamp;

jfieldID FID_traildb_TrailDBEvent_numItems;

jfieldID FID_traildb_TrailDBEvent_items;

jfieldID FID_traildb_TrailDBEvent_db;

/*
 * Class:     traildb_TrailDBMultiCursor
 * Method:    init
 * Signature: ([Ltraildb/TrailDBCursor;)V
 */
JNIEXPORT void JNICALL Java_traildb_TrailDBMultiCursor_init(JNIEnv *env, jobject obj, jobjectArray cursors) {
	jobject cursor_obj;

	tdb_multi_cursor *multi_cur;
	tdb_cursor **tgt_cursors;

	int num_cursors = (*env)->GetArrayLength(env, cursors);

	tgt_cursors = malloc(num_cursors * sizeof(tdb_cursor *));

	for (int i = 0; i < num_cursors; i++) {
		cursor_obj = (*env)->GetObjectArrayElement(env, cursors, i);
		tgt_cursors[i] = (tdb_cursor *) (*env)->GetLongField(env, cursor_obj, FID_traildb_TrailDBCursor_cur);
	}

	multi_cur = tdb_multi_cursor_new(tgt_cursors, num_cursors);

	// free tgt_cursors;

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiCursor_cur, (long) multi_cur);

}

/*
 * Class:     traildb_TrailDBMultiCursor
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_traildb_TrailDBMultiCursor_free(JNIEnv *env, jobject obj) {

}

/*
 * Class:     traildb_TrailDBMultiCursor
 * Method:    reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_traildb_TrailDBMultiCursor_reset(JNIEnv *env, jobject obj) {

}

/*
 * Class:     traildb_TrailDBMultiCursor
 * Method:    next
 * Signature: ()Ltraildb/TrailDBMultiEvent;
 */
JNIEXPORT jobject JNICALL Java_traildb_TrailDBMultiCursor_next(JNIEnv *env, jobject obj) {

}

/*
 * Class:     traildb_TrailDBMultiCursor
 * Method:    nextBatch
 * Signature: (I)[Ltraildb/TrailDBMultiEvent;
 */
JNIEXPORT jobjectArray JNICALL Java_traildb_TrailDBMultiCursor_nextBatch(JNIEnv *env, jobject obj, jint max_events) {

}

/*
 * Class:     traildb_TrailDBMultiCursor
 * Method:    peek
 * Signature: ()Ltraildb/TrailDBMultiEvent;
 */
JNIEXPORT jobject JNICALL Java_traildb_TrailDBMultiCursor_peek(JNIEnv *env, jobject obj) {

}

/*
 * Class:     traildb_TrailDBMultiCursor
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_traildb_TrailDBMultiCursor_initIDs(JNIEnv *env, jclass cls) {
	jclass traildb_TrailDBMultiEvent = (*env)->FindClass(env, "traildb/TrailDBMultiEvent");

	jclass traildb_TrailDBEvent = (*env)->FindClass(env, "traildb/TrailDBEvent");

	jclass traildb_TrailDBCursor = (*env)->FindClass(env, "traildb/TrailDBCursor");

	CID_traildb_TrailDBMultiEvent = (jclass) (*env)->NewGlobalRef(env, traildb_TrailDBMultiEvent);

	CID_traildb_TrailDBEvent = (jclass) (*env)->NewGlobalRef(env, traildb_TrailDBEvent);

	MID_traildb_TrailDBMultiEvent_Constructor = (*env)->GetMethodID(env, CID_traildb_TrailDBMultiEvent, "<init>", "()V");

	MID_traildb_TrailDBEvent_Constructor = (*env)->GetMethodID(env, CID_traildb_TrailDBEvent, "<init>", "()V");

	FID_traildb_TrailDBMultiCursor_cur = (*env)->GetFieldID(env, cls, "cur", "J");

	FID_traildb_TrailDBCursor_cur = (*env)->GetFieldID(env, traildb_TrailDBCursor, "cur", "J");

	FID_traildb_TrailDBMultiEvent_event = (*env)->GetFieldID(env, CID_traildb_TrailDBMultiEvent, "event", "Ltraildb/TrailDBEvent;");

	FID_traildb_TrailDBEvent_timestamp = (*env)->GetFieldID(env, CID_traildb_TrailDBEvent, "timestamp", "J");

	FID_traildb_TrailDBEvent_numItems = (*env)->GetFieldID(env, CID_traildb_TrailDBEvent, "numItems", "J");

	FID_traildb_TrailDBEvent_items = (*env)->GetFieldID(env, CID_traildb_TrailDBEvent, "items", "J");

	FID_traildb_TrailDBEvent_db = (*env)->GetFieldID(env, CID_traildb_TrailDBEvent, "db", "J");

	(*env)->DeleteLocalRef(env, traildb_TrailDBMultiEvent);

	(*env)->DeleteLocalRef(env, traildb_TrailDBEvent);
}
