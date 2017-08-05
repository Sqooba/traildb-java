#include <traildb.h>
#include <string.h>
#include "traildb-java.h"

jfieldID FID_traildb_TrailDB_db;

jfieldID FID_traildb_TrailDBTrail_db;

jfieldID FID_traildb_TrailDBTrail_cur;

jfieldID FID_traildb_TrailDBTrail_timestamp;

jfieldID FID_traildb_TrailDBTrail_numItems;

jfieldID FID_traildb_TrailDBTrail_items;


JNIEXPORT void JNICALL Java_traildb_TrailDBTrail_init(JNIEnv * env, jobject obj, jobject tdb_obj, jlong traild_id) {
	const tdb *db;
	const tdb_cursor *cur;

	// Retrieve db pointer

	db = (tdb *) (*env)->GetLongField(env, tdb_obj, FID_traildb_TrailDB_db);

	// Make new cursor

	cur = tdb_cursor_new(db);

	// Set trail id to initial trail

	tdb_get_trail(cur, trail_id);

	// Store cur pointer on cursor (obj.cur = cur)

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_cur, (long) cur);

	// Store db pointer on cursor (obj.db = db)

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_db, (long) db);
}

JNIEXPORT jstring JNICALL Java_traildb_TrailDBTrail_native_1getItem(JNIEnv *env, jobject obj, jint index) {
	const tdb *db;
	const tdb_item *items;
	const char *value;
	char *tgt_value;
	uint64_t value_length;

	// Retrieve items pointer

	items = (tdb_item *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBTrail_items);

	// Retrieve db pointer

	db = (tdb *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBTrail_db);

	// Get the value of the item

	value = tdb_get_item_value(db, items[index], &value_length);

	// Convert buffer to null-terminated string

	tgt_value = malloc(value_length * sizeof(char) + 1);

	strncpy(tgt_value, value, value_length);

	tgt_value[value_length] = '\0';

	return (*env)->NewStringUTF(env, tgt_value);
}

JNIEXPORT void JNICALL Java_traildb_TrailDBTrail_getTrail(JNIEnv *env, jobject obj, jlong trail_id) {
	tdb_cursor *cur;

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBTrail_cur);

	// Get trail from cursor

	tdb_get_trail(cur, trail_id);
}

JNIEXPORT jlong JNICALL Java_traildb_TrailDBTrail_getTrailLength(JNIEnv *env, jobject obj) {
	tdb_cursor *cur;
	uint64_t length;

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBTrail_cur);

	// Get trail length

	length = tdb_get_trail_length(cur);

	return length;
}

JNIEXPORT void JNICALL Java_traildb_TrailDBCursor_setEventFilter(JNIEnv *env, jobject obj, jobject filter) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDBCursor_unsetEventFilter(JNIEnv *env, jobject obj) {

}

JNIEXPORT jobject JNICALL Java_traildb_TrailDBTrail_next(JNIEnv *env, jobject obj) {
	jobject event_obj;

	tdb_cursor *cur;
	const tdb_event *event;

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBTrail_cur);

	// Get event

	event = tdb_cursor_next(cur);

	// Check if end of trail

	if (event == NULL) {
	  (*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_timestamp, 0L);
	  (*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_numItems, 0L);
	  (*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_items, NULL);

		return NULL;
	}

	// Store timestamp

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_timestamp, (long) event->timestamp);

	// Store number of items

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_numItems, (long) event->num_items);

	// Store items pointer

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_items, (long) event->items);

  // Return self for convenience

	return obj;
}

JNIEXPORT jobject JNICALL Java_traildb_TrailDBTrail_peek(JNIEnv *env, jobject obj) {
	jobject event_obj;

	tdb_cursor *cur;
	const tdb_event *event;

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBTrail_cur);

	// Get event

	event = tdb_cursor_peek(cur);

	// Check if end of trail

	if (event == NULL) {
	  (*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_timestamp, 0L);
	  (*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_numItems, 0L);
	  (*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_items, NULL);

		return NULL;
	}

	// Store timestamp

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_timestamp, (long) event->timestamp);

	// Store number of items

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_numItems, (long) event->num_items);

	// Store items pointer

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_items, (long) event->items);

  // Return self for convenience

	return obj;
}

JNIEXPORT void JNICALL Java_traildb_TrailDBTrail_initIDs(JNIEnv *env, jclass cls) {

	jclass traildb_TrailDB = (*env)->FindClass(env, "traildb/TrailDB");

	FID_traildb_TrailDB_db = (*env)->GetFieldID(env, traildb_TrailDB, "db", "J");

	FID_traildb_TrailDBTrail_db = (*env)->GetFieldID(env, cls, "db", "J");

	FID_traildb_TrailDBTrail_cur = (*env)->GetFieldID(env, cls, "cur", "J");

	FID_traildb_TrailDBTrail_timestamp = (*env)->GetFieldID(env, cls, "timestamp", "J");

	FID_traildb_TrailDBTrail_numItems = (*env)->GetFieldID(env, cls, "numItems", "J");

	FID_traildb_TrailDBTrail_items = (*env)->GetFieldID(env, cls, "items", "J");

}
