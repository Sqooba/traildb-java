#include <traildb.h>
#include "../include/TrailDBCursor.h"

JNIEXPORT void JNICALL Java_traildb_TrailDBCursor_free(JNIEnv *env, jobject obj) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDBCursor_getTrail(JNIEnv *env, jobject obj, jlong trail_id) {
	jclass cls;
	jfieldID fid;

	tdb_cursor *cur;

	// Retrieve cursor pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cur", "J");
	if (fid == NULL) {
		exit(1);
	}
	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, fid);

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
	jclass cls;
	jfieldID fid;
	jmethodID cid;
	jobject event_obj;

	tdb_cursor *cur;
	const tdb_event *event;
	const tdb *db;

	// Retrieve db pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "db", "J");
	if (fid == NULL) {
		exit(1);
	}
	db = (tdb *) (*env)->GetLongField(env, obj, fid);

	// Retrieve cursor pointer

	fid = (*env)->GetFieldID(env, cls, "cur", "J");
	if (fid == NULL) {
		exit(1);
	}
	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, fid);

	// Get event

	event = tdb_cursor_next(cur);

	// Check if end of trail

	if (event == NULL) {
		return NULL;
	}

	// Get class of TrailDBEvent

	cls = (*env)->FindClass(env, "traildb/TrailDBEvent");
	if (cls == NULL) {
		exit(1);
	}

	// event_obj = new TrailDBEvent();
	// Get method id of TrailDBEvent constructor

	cid = (*env)->GetMethodID(env, cls, "<init>", "()V");

	// Create event

	event_obj = (*env)->NewObject(env, cls, cid);

	// Store timestamp

	fid = (*env)->GetFieldID(env, cls, "timestamp", "J");
	if (fid == NULL) {
		exit(1);
	}
	(*env)->SetLongField(env, event_obj, fid, (long) event->timestamp);

	// Store number of item

	fid = (*env)->GetFieldID(env, cls, "numItems", "J");
	if (fid == NULL) {
		exit(1);
	}
	(*env)->SetLongField(env, event_obj, fid, (long) event->num_items);

	// Store items pointer

	fid = (*env)->GetFieldID(env, cls, "items", "J");
	if (fid == NULL) {
		exit(1);
	}
	(*env)->SetLongField(env, event_obj, fid, (long) event->items);

	// Store db pointer on event

	fid = (*env)->GetFieldID(env, cls, "db", "J");
	if (fid == NULL) {
		exit(1);
	}
	(*env)->SetLongField(env, event_obj, fid, (long) db);

	return event_obj;
}

JNIEXPORT jobject JNICALL Java_traildb_TrailDBCursor_peek(JNIEnv *env, jobject obj) {

}