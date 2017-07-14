#include <traildb.h>
#include "../include/TrailDB.h"


JNIEXPORT void JNICALL Java_traildb_TrailDB_init(JNIEnv *env, jobject obj, jstring root) {
	jclass cls;
	jfieldID fid;

	tdb_error err;
	const char *tgt_root;
	tdb *db;


	// Get strings

	tgt_root = (*env)->GetStringUTFChars(env, root, NULL);

	// Initialize tdb

	db = tdb_init();

	// Open tdb

	if (err = tdb_open(db, tgt_root)) {
		printf("Opening TrailDB failed: %s\n", tdb_error_str(err));
		exit(1);
	}

	// Release strings

	(*env)->ReleaseStringUTFChars(env, root, tgt_root);

	// Store db pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "db", "J");
	if (fid == NULL) {
		return;
	}

	(*env)->SetLongField(env, obj, fid, (long) db);
}

JNIEXPORT void JNICALL Java_traildb_TrailDB_dontNeed(JNIEnv *env, jobject obj) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDB_willNeed(JNIEnv *env, jobject obj) {

}

JNIEXPORT jlong JNICALL Java_traildb_TrailDB_numTrails(JNIEnv *env, jobject obj) {
	jclass cls;
	jfieldID fid;

	tdb *db;
	uint64_t result;

	// Retrieve db pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "db", "J");
	if (fid == NULL) {
		exit(1);
	}
	db = (tdb *) (*env)->GetLongField(env, obj, fid);

	// Get number of trails

	result = tdb_num_trails(db);

	return result;
}

JNIEXPORT jlong JNICALL Java_traildb_TrailDB_numEvents(JNIEnv *env, jobject obj) {

}

JNIEXPORT jlong JNICALL Java_traildb_TrailDB_numFields(JNIEnv *env, jobject obj) {
	jclass cls;
	jfieldID fid;

	tdb *db;

	// Retrieve db pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "db", "J");
	if (fid == NULL) {
		exit(1);
	}
	db = (tdb *) (*env)->GetLongField(env, obj, fid);

	return tdb_num_fields(db);
}

JNIEXPORT jlong JNICALL Java_traildb_TrailDB_minTimestamp(JNIEnv *env, jobject obj) {

}

JNIEXPORT jlong JNICALL Java_traildb_TrailDB_maxTimestamp(JNIEnv *env, jobject obj) {

}

JNIEXPORT jlong JNICALL Java_traildb_TrailDB_version(JNIEnv *env, jobject obj) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDB_setOpt(JNIEnv *env, jobject obj, jobject key, jint value) {

}

JNIEXPORT jint JNICALL Java_traildb_TrailDB_getOpt(JNIEnv *env, jobject obj, jobject key) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDB_setTrailOpt(JNIEnv *env, jobject obj, jlong trail_id, jobject key, jint value) {

}

JNIEXPORT jint JNICALL Java_traildb_TrailDB_getTrailOpt(JNIEnv *env, jobject obj, jlong trail_id, jobject key) {

}

JNIEXPORT jint JNICALL Java_traildb_TrailDB_lexiconSize(JNIEnv *env, jobject obj, jint field) {

}

JNIEXPORT jint JNICALL Java_traildb_TrailDB_getField(JNIEnv *env, jobject obj, jstring field_name) {

}

JNIEXPORT jstring JNICALL Java_traildb_TrailDB_getFieldName(JNIEnv *env, jobject obj, jint field) {

}

JNIEXPORT jobject JNICALL Java_traildb_TrailDB_getItem(JNIEnv *env, jobject obj, jint field, jstring value) {

}

JNIEXPORT jstring JNICALL Java_traildb_TrailDB_getValue(JNIEnv *env, jobject obj, jint field, jlong value) {

}

JNIEXPORT jstring JNICALL Java_traildb_TrailDB_getItemValue(JNIEnv *env, jobject obj, jobject item) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDB_close(JNIEnv *env, jobject obj) {

}

JNIEXPORT jobject JNICALL Java_traildb_TrailDB_getUUID(JNIEnv *env, jobject obj, jint trail_id) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDB_getTrailId(JNIEnv *env, jobject obj, jobject uuid) {

}

JNIEXPORT jobject JNICALL Java_traildb_TrailDB_cursorNew(JNIEnv *env, jobject obj) {
	jclass cls;
	jfieldID fid;
	jmethodID cid;
	jobject cursor_obj;

	const tdb *db;
	const tdb_cursor *cur;

	// Retrieve db pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "db", "J");
	if (fid == NULL) {
		exit(1);
	}
	db = (tdb *) (*env)->GetLongField(env, obj, fid);

	// Make new cursor

	cur = tdb_cursor_new(db);

	// Get class of TrailDBCursor

	cls = (*env)->FindClass(env, "traildb/TrailDBCursor");
	if (cls == NULL) {
		exit(1);
	}

	// cursor_obj = new TrailDBCursor();
	// Get method id of TrailDBCursor constructor

	cid = (*env)->GetMethodID(env, cls, "<init>", "()V");

	// Create cursor

	cursor_obj = (*env)->NewObject(env, cls, cid);

	// Store cur pointer on cursor (cursor_obj.cur = cur)

	fid = (*env)->GetFieldID(env, cls, "cur", "J");
	if (fid == NULL) {
		exit(1);
	}
	(*env)->SetLongField(env, cursor_obj, fid, (long) cur);

	// Store db pointer on cursor (cursor_obj.db = db)

	fid = (*env)->GetFieldID(env, cls, "db", "J");
	if (fid == NULL) {
		exit(1);
	}
	(*env)->SetLongField(env, cursor_obj, fid, (long) db);

	return cursor_obj;
}

JNIEXPORT jobject JNICALL Java_traildb_TrailDB_multiCursorNew(JNIEnv *env, jobject obj) {

}
