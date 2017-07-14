#include <traildb.h>
#include <string.h>
#include "../include/TrailDBEvent.h"


JNIEXPORT jstring JNICALL Java_traildb_TrailDBEvent_native_1getItem(JNIEnv *env, jobject obj, jint index) {
	jclass cls;
	jfieldID fid;

	const tdb *db;
	const tdb_item *items;
	const char *value;
	char *tgt_value;
	uint64_t value_length;

	// Retrieve items pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "items", "J");
	if (fid == NULL) {
		exit(1);
	}
	items = (tdb_item *) (*env)->GetLongField(env, obj, fid);

	// Retrieve db pointer

	fid = (*env)->GetFieldID(env, cls, "db", "J");
	if (fid == NULL) {
		exit(1);
	}
	db = (tdb *) (*env)->GetLongField(env, obj, fid);

	if (db == NULL || items == NULL) {
		exit(1);
	}

	// Get the value of the item

	value = tdb_get_item_value(db, items[index], &value_length);
	if (value == NULL) {
		// This probably shouldn't be possible since we already check
		// for out of bounds error
		exit(1);
	}

	// Convert buffer to null-terminated string

	tgt_value = malloc(value_length * sizeof(char) + 1);

	strncpy(tgt_value, value, value_length);
	tgt_value[value_length] = '\0';

	return (*env)->NewStringUTF(env, tgt_value);
}
