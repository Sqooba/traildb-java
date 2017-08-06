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
 * Class:     traildb_TrailDBMultiTrail
 * Method:    init
 * Signature: ([Ltraildb/TrailDBTrail;)V
 */
JNIEXPORT void JNICALL Java_traildb_TrailDBMultiTrail_init(JNIEnv *env, jobject obj, jobjectArray cursors) {
	jobject cursor_obj;

	tdb_multi_cursor *multi_cur;
	tdb_cursor **tgt_cursors;

	int num_cursors = (*env)->GetArrayLength(env, cursors);

	tgt_cursors = malloc(num_cursors * sizeof(tdb_cursor *));

	for (int i = 0; i < num_cursors; i++) {
		cursor_obj = (*env)->GetObjectArrayElement(env, cursors, i);
		tgt_cursors[i] = (tdb_cursor *) (*env)->GetLongField(env, cursor_obj, FID_traildb_TrailDBTrail_cur);
	}

	multi_cur = tdb_multi_cursor_new(tgt_cursors, num_cursors);

	// free tgt_cursors;

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_cur, (long) multi_cur);

	// Initialize items to NULL because we haven't called next yet

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_items, 0L);
}


/*
 * Class:     traildb_TrailDBMultiTrail
 * Method:    native_getItem
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_traildb_TrailDBMultiTrail_native_1getItem(JNIEnv *env, jobject obj, jint index) {
  const tdb *db;
	const tdb_item *items;
	const char *value;
	char *tgt_value;
	uint64_t value_length;

	// Retrieve items pointer

	items = (tdb_item *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBMultiTrail_items);

	// Retrieve db pointer

	db = (tdb *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBMultiTrail_db);

	// Get the value of the item

	value = tdb_get_item_value(db, items[index], &value_length);

	// Convert buffer to null-terminated string

	tgt_value = malloc(value_length * sizeof(char) + 1);

	strncpy(tgt_value, value, value_length);

	tgt_value[value_length] = '\0';

	return (*env)->NewStringUTF(env, tgt_value);
}

/*
 * Class:     traildb_TrailDBMultiTrail
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_traildb_TrailDBMultiTrail_free(JNIEnv *env, jobject obj) {

}

/*
 * Class:     traildb_TrailDBMultiTrail
 * Method:    reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_traildb_TrailDBMultiTrail_reset(JNIEnv *env, jobject obj) {

}

/*
 * Class:     traildb_TrailDBMultiTrail
 * Method:    next
 * Signature: ()Ltraildb/TrailDBMultiTrail;
 */
JNIEXPORT jobject JNICALL Java_traildb_TrailDBMultiTrail_next(JNIEnv *env, jobject obj) {

	tdb_multi_cursor *multi_cur;
	const tdb_multi_event *multi_event;
	const tdb_event *event;

	// Get cur pointer

	multi_cur = (tdb_multi_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBMultiTrail_cur);

	// Call multi cursor next

	multi_event = tdb_multi_cursor_next(multi_cur);

	if (multi_event == NULL) {
		(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_timestamp, 0L);
		(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_numItems, 0L);
		(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_items, 0L);

		return NULL;
	}

	event = multi_event->event;

	// Store timestamp

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_timestamp, (long) event->timestamp);

	// Store number of items

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_numItems, (long) event->num_items);

	// Store items pointer

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_items, (long) event->items);

	// Store db pointer

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_db, (long) multi_event->db);

	// Store cursor index

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_cursorIndex, (long) multi_event->cursor_idx);

	return obj;
}

/*
 * Class:     traildb_TrailDBMultiTrail
 * Method:    nextBatch
 * Signature: (I)[Ltraildb/TrailDBMultiTrail;
 */
JNIEXPORT jobjectArray JNICALL Java_traildb_TrailDBMultiTrail_nextBatch(JNIEnv *env, jobject obj, jint max_events) {

}

/*
 * Class:     traildb_TrailDBMultiTrail
 * Method:    peek
 * Signature: ()Ltraildb/TrailDBMultiTrail;
 */
JNIEXPORT jobject JNICALL Java_traildb_TrailDBMultiTrail_peek(JNIEnv *env, jobject obj) {
	tdb_multi_cursor *multi_cur;
	const tdb_multi_event *multi_event;
	const tdb_event *event;

	// Get cur pointer

	multi_cur = (tdb_multi_cursor *) (*env)->GetLongField(env, obj, FID_traildb_TrailDBMultiTrail_cur);

	// Call multi cursor next

	multi_event = tdb_multi_cursor_peek(multi_cur);

	if (multi_event == NULL) {
		(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_timestamp, 0L);
		(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_numItems, 0L);
		(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_items, 0L);

		return NULL;
	}

	event = multi_event->event;

	// Store timestamp

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_timestamp, (long) event->timestamp);

	// Store number of items

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_numItems, (long) event->num_items);

	// Store items pointer

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_items, (long) event->items);

	// Store db pointer

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_db, (long) multi_event->db);

	// Store cursor index

	(*env)->SetLongField(env, obj, FID_traildb_TrailDBMultiTrail_cursorIndex, (long) multi_event->cursor_idx);

	return obj;
}

/*
 * Class:     traildb_TrailDBMultiTrail
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
