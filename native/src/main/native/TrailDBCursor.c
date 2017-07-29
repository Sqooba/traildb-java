#include <traildb.h>
#include "traildb-java.h"

jfieldID FID_traildb_TrailDBCursor_db;

jfieldID FID_traildb_TrailDBCursor_cur;

jclass CID_traildb_TrailDBEvent;

jmethodID MID_traildb_TrailDBEvent_Constructor;

jfieldID FID_traildb_TrailDBEvent_timestamp;

jfieldID FID_traildb_TrailDBEvent_numItems;

jfieldID FID_traildb_TrailDBEvent_items;

jfieldID FID_traildb_TrailDBEvent_db;


JNIEXPORT void JNICALL Java_traildb_TrailDBCursor_free(JNIEnv *env, jobject obj) {

	tdb_cursor *cursor;

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBCursor_cur);

	// Free cursor

	tdb_cursor_free(cursor);
}

JNIEXPORT void JNICALL Java_traildb_TrailDBCursor_getTrail(JNIEnv *env, jobject obj, jlong trail_id) {
	jclass cls;
	jfieldID fid;

	tdb_cursor *cur;

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBCursor_cur);

	// Get trail from cursor

	tdb_get_trail(cur, trail_id);

}

JNIEXPORT jlong JNICALL Java_traildb_TrailDBCursor_getTrailLength(JNIEnv *env, jobject obj) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDBCursor_setEventFilter(JNIEnv *env, jobject obj, jobject filter) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDBCursor_unsetEventFilter(JNIEnv *env, jobject obj) {

}

JNIEXPORT jobject JNICALL Java_traildb_TrailDBCursor_next(JNIEnv *env, jobject obj) {
	jobject event_obj;

	tdb_cursor *cur;
	const tdb_event *event;
	const tdb *db;

	// Retrieve db pointer

	db = (tdb *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBCursor_db);

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBCursor_cur);

	// Get event

	event = tdb_cursor_next(cur);

	// Check if end of trail

	if (event == NULL) {
		return NULL;
	}

	// Create TrailDBEvent

	event_obj = (*env)->NewObject(env, CID_traildb_TrailDBEvent, MID_traildb_TrailDBEvent_Constructor);

	// Store timestamp

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_timestamp, (long) event->timestamp);

	// Store number of items

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_numItems, (long) event->num_items);

	// Store items pointer

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_items, (long) event->items);

	// Store db pointer on event

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_db, (long) db);

	return event_obj;
}

JNIEXPORT jobject JNICALL Java_traildb_TrailDBCursor_peek(JNIEnv *env, jobject obj) {
	jobject event_obj;

	tdb_cursor *cur;
	const tdb_event *event;
	const tdb *db;

	// Retrieve db pointer

	db = (tdb *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBCursor_db);

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBCursor_cur);

	// Get event

	event = tdb_cursor_peek(cur);

	// Check if end of trail

	if (event == NULL) {
		return NULL;
	}

	// Create TrailDBEvent

	event_obj = (*env)->NewObject(env, CID_traildb_TrailDBEvent, MID_traildb_TrailDBEvent_Constructor);

	// Store timestamp

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_timestamp, (long) event->timestamp);

	// Store number of items

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_numItems, (long) event->num_items);

	// Store items pointer

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_items, (long) event->items);

	// Store db pointer on event

	(*env)->SetLongField(env, event_obj, FID_traildb_TrailDBEvent_db, (long) db);

	return event_obj;
}

JNIEXPORT void JNICALL Java_traildb_TrailDBCursor_initIDs(JNIEnv *env, jclass cls) {
	FID_traildb_TrailDBCursor_db = (*env)->GetFieldID(env, cls, "db", "J");
	FID_traildb_TrailDBCursor_cur = (*env)->GetFieldID(env, cls, "cur", "J");

	jclass traildb_TrailDBEvent = (*env)->FindClass(env, "traildb/TrailDBEvent");
	CID_traildb_TrailDBEvent = (jclass) (*env)->NewGlobalRef(env, traildb_TrailDBEvent);
	(*env)->DeleteLocalRef(env, traildb_TrailDBEvent);

	MID_traildb_TrailDBEvent_Constructor = (*env)->GetMethodID(env, CID_traildb_TrailDBEvent, "<init>", "()V");
	FID_traildb_TrailDBEvent_timestamp = (*env)->GetFieldID(env, CID_traildb_TrailDBEvent, "timestamp", "J");
	FID_traildb_TrailDBEvent_numItems = (*env)->GetFieldID(env, CID_traildb_TrailDBEvent, "numItems", "J");
	FID_traildb_TrailDBEvent_items = (*env)->GetFieldID(env, CID_traildb_TrailDBEvent, "items", "J");
	FID_traildb_TrailDBEvent_db = (*env)->GetFieldID(env, CID_traildb_TrailDBEvent, "db", "J");
}
