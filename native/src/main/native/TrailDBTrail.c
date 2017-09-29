#include <traildb.h>
#include <string.h>
#include "traildb-java.h"

jfieldID FID_traildb_TrailDB_db;

jfieldID FID_traildb_TrailDBTrail_db;

jfieldID FID_traildb_TrailDBTrail_cur;

jfieldID FID_traildb_TrailDBTrail_timestamp;

jfieldID FID_traildb_TrailDBTrail_numItems;

jfieldID FID_traildb_TrailDBTrail_items;

jfieldID FID_traildb_filters_TrailDBEventFilter_f;


JNIEXPORT void JNICALL Java_traildb_TrailDBTrail_init(JNIEnv * env, jobject obj, jobject tdb_obj, jlong trail_id) {
	const tdb *db;
	tdb_cursor *cur;

	// Retrieve db pointer

	db = (tdb *) (*env)->GetLongField(env, tdb_obj, FID_traildb_TrailDB_db);

	// Make new cursor

	cur = tdb_cursor_new(db);

	// Set trail id to initial trail

	tdb_get_trail(cur, trail_id);

	// Store cur pointer on trail (obj.cur = cur)

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_cur, (long) cur);

	// Store db pointer on trail (obj.db = db)

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_db, (long) db);

	// Initialize items to NULL because we haven't called next yet

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_items, 0L);
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

JNIEXPORT void JNICALL Java_traildb_TrailDBTrail_native_1getTrail(JNIEnv *env, jobject obj, jlong trail_id) {
	tdb_cursor *cur;

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBTrail_cur);

	// Get trail from cursor

	tdb_get_trail(cur, trail_id);

	// Initialize items to NULL because we haven't called next yet

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_items, 0L);
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

JNIEXPORT void JNICALL Java_traildb_TrailDBTrail_setEventFilter(JNIEnv *env, jobject obj, jobject filter) {
	jclass exc;

	struct tdb_event_filter *tgt_filter;
	tdb_cursor *cur;
	tdb_error err;

	// Retrieve filter pointer

	tgt_filter = (struct tdb_event_filter *) (*env)->GetLongField(env, filter, FID_traildb_filters_TrailDBEventFilter_f);

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBTrail_cur);

	// Set the event filter on the cursor

	err = tdb_cursor_set_event_filter(cur, tgt_filter);

	if (err) {
		exc = (*env)->FindClass(env, "java/io/IOException");
		if (exc == NULL) {
		/* Could not find the exception - We are in so much trouble right now */
			exit(1);
		}
		(*env)->ThrowNew(env, exc, tdb_error_str(err));
		return;
	}
}

JNIEXPORT void JNICALL Java_traildb_TrailDBTrail_unsetEventFilter(JNIEnv *env, jobject obj) {
	tdb_cursor *cur;

	// Retrieve cursor pointer

	cur = (tdb_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBTrail_cur);

	// Unset event filter

	tdb_cursor_unset_event_filter(cur);

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
	  (*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_items, 0L);

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
	  (*env)->SetLongField(env, obj, FID_traildb_TrailDBTrail_items, 0L);

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

	jclass traildb_filters_TrailDBEventFilter = (*env)->FindClass(env, "traildb/filters/TrailDBEventFilter");

	FID_traildb_TrailDB_db = (*env)->GetFieldID(env, traildb_TrailDB, "db", "J");

	FID_traildb_TrailDBTrail_db = (*env)->GetFieldID(env, cls, "db", "J");

	FID_traildb_TrailDBTrail_cur = (*env)->GetFieldID(env, cls, "cur", "J");

	FID_traildb_TrailDBTrail_timestamp = (*env)->GetFieldID(env, cls, "timestamp", "J");

	FID_traildb_TrailDBTrail_numItems = (*env)->GetFieldID(env, cls, "numItems", "J");

	FID_traildb_TrailDBTrail_items = (*env)->GetFieldID(env, cls, "items", "J");

	FID_traildb_filters_TrailDBEventFilter_f = (*env)->GetFieldID(env, traildb_filters_TrailDBEventFilter, "f", "J");

}
