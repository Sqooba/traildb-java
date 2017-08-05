#include <traildb.h>
#include "traildb-java.h"


jfieldID FID_traildb_TrailDB_db;


JNIEXPORT void JNICALL Java_traildb_TrailDB_init(JNIEnv *env, jobject obj, jstring root) {
	jclass exc;

	tdb_error err;
	const char *tgt_root;
	tdb *db;

	// Get strings

	tgt_root = (*env)->GetStringUTFChars(env, root, NULL);

	// Initialize tdb

	db = tdb_init();

	// Open tdb

	if ((err = tdb_open(db, tgt_root))) {
		exc = (*env)->FindClass(env, "java/io/FileNotFoundException");
		if (exc == NULL) {
			/* Could not find the exception - We are in so much trouble right now */
			exit(1);
		}
		(*env)->ThrowNew(env, exc, tdb_error_str(err));
		return;
	}

	// Release strings

	(*env)->ReleaseStringUTFChars(env, root, tgt_root);

	// Store db pointer

	(*env)->SetLongField(env, obj, FID_traildb_TrailDB_db, (long) db);
}

JNIEXPORT void JNICALL Java_traildb_TrailDB_dontNeed(JNIEnv *env, jobject obj) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDB_willNeed(JNIEnv *env, jobject obj) {

}

JNIEXPORT jlong JNICALL Java_traildb_TrailDB_numTrails(JNIEnv *env, jobject obj) {
	jclass cls;
	jfieldID fid;

	const tdb *db;
	uint64_t result;

	// Retrieve db pointer

	db = (tdb *) (*env)->GetLongField(env, obj, FID_traildb_TrailDB_db);

	// Get number of trails

	result = tdb_num_trails(db);

	return result;
}

JNIEXPORT jlong JNICALL Java_traildb_TrailDB_numEvents(JNIEnv *env, jobject obj) {
	const tdb *db;
	uint64_t num_events;

	// Retrieve db pointer

	db = (tdb *) (*env)->GetLongField(env, obj, FID_traildb_TrailDB_db);

	// Get number of events

	num_events = tdb_num_events(db);

	return num_events;
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
	jclass cls;
	jfieldID fid;

	const char *name;
	tdb *db;

	// Retrieve db pointer

	db = (tdb *) (*env)->GetLongField(env, obj, FID_traildb_TrailDB_db);

	// Get field name

	name = tdb_get_field_name(db, field);

	return (*env)->NewStringUTF(env, name);
}

JNIEXPORT jobject JNICALL Java_traildb_TrailDB_getItem(JNIEnv *env, jobject obj, jint field, jstring value) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDB_close(JNIEnv *env, jobject obj) {
	tdb *db;

	// Retrieve db pointer

	db = (tdb *) (*env)->GetLongField(env, obj, FID_traildb_TrailDB_db);

	tdb_close(db);
}

JNIEXPORT jobject JNICALL Java_traildb_TrailDB_getUUID(JNIEnv *env, jobject obj, jint trail_id) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDB_getTrailId(JNIEnv *env, jobject obj, jobject uuid) {

}

/*
 * Class:     traildb_TrailDB
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_traildb_TrailDB_initIDs(JNIEnv *env, jclass cls) {

	FID_traildb_TrailDB_db = (*env)->GetFieldID(env, cls, "db", "J");

}
