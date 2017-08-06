#include <traildb.h>
#include "traildb-java.h"

jfieldID FID_traildb_TrailDBMultiTrail_cur;

jfieldID FID_traildb_TrailDBMultiTrail_timestamp;

jfieldID FID_traildb_TrailDBMultiTrail_numItems;

jfieldID FID_traildb_TrailDBMultiTrail_items;

jfieldID FID_traildb_TrailDBMultiTrail_db;

jfieldID FID_traildb_TrailDBMultiTrail_cursorIndex;

jfieldID FID_traildb_TrailDBTrail_cur;


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
	jobject event_obj;
	jobject multi_event_obj;

	tdb_multi_cursor *multi_cur;
	const tdb_multi_event *multi_event;
	const tdb_event *event;

	// Get cur pointer

	multi_cur = (tdb_multi_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBMultiCursor_cur);

	// Call multi cursor next

	multi_event = tdb_multi_cursor_next(multi_cur);

	if (multi_event == NULL) {
	  return NULL;
	}

	event = multi_event->event;

	// Create TrailDBEvent

	event_obj = (*env)->NewObject(env, CID_traildb_TrailDBEvent, MID_traildb_TrailDBEvent_Constructor);

	// Store timestamp

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_timestamp, (long) event->timestamp);

	// Store number of items

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_numItems, (long) event->num_items);

	// Store items pointer

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_items, (long) event->items);

	// Store db pointer on event

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_db, (long) multi_event->db);

	// Create TrailDBMultiEvent

	multi_event_obj = (*env)->NewObject(env, CID_traildb_TrailDBMultiEvent, MID_traildb_TrailDBMultiEvent_Constructor);

	// Store event in multi event

	(*env)->SetObjectField(env, multi_event_obj, FID_traildb_TrailDBMultiEvent_event, event_obj);

	return multi_event_obj;
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
JNIEXPORT void JNICALL Java_traildb_TrailDBMultiTrail_initIDs(JNIEnv *env, jclass cls) {

	jclass traildb_TrailDBTrail = (*env)->FindClass(env, "traildb/TrailDBTrail");

	FID_traildb_TrailDBMultiTrail_cur = (*env)->GetFieldID(env, cls, "cur", "J");

	FID_traildb_TrailDBMultiTrail_timestamp = (*env)->GetFieldID(env, cls, "timestamp", "J");

	FID_traildb_TrailDBMultiTrail_numItems = (*env)->GetFieldID(env, cls, "numItems", "J");

	FID_traildb_TrailDBMultiTrail_items = (*env)->GetFieldID(env, cls, "items", "J");

	FID_traildb_TrailDBMultiTrail_db = (*env)->GetFieldID(env, cls, "db", "J");

	FID_traildb_TrailDBMultiTrail_cursorIndex = (*env)->GetFieldID(env, cls, "cursorIndex", "J");

	FID_traildb_TrailDBTrail_cur = (*env)->GetFieldID(env, traildb_TrailDBTrail, "cur", "J");
}
