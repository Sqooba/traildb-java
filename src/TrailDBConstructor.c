#include <traildb.h>
#include "../include/TrailDBConstructor.h"
#include <string.h>


JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_init(JNIEnv *env, jobject obj, jstring root, jobjectArray fields) {
	jclass cls;
	jfieldID fid;
	jobject jobj;

	tdb_error err;
	tdb_cons *cons;
	const char *tgt_root;
	const char **tgt_fields;
	uint64_t num_fields;

	// Get Number of fields

	num_fields = (*env)->GetArrayLength(env, fields);

	// Get char pointers

	jobject temp_field;
	tgt_fields = malloc(num_fields * sizeof(char*));
	for (int i = 0; i < num_fields; i++) {
		temp_field = (*env)->GetObjectArrayElement(env, fields, i);
		tgt_fields[i] = (*env)->GetStringUTFChars(env, temp_field, NULL);
	}

	tgt_root = (*env)->GetStringUTFChars(env, root, NULL);

	// Initialize and open tdb

	cons = tdb_cons_init();
	if ((err = tdb_cons_open(cons, tgt_root, tgt_fields, num_fields))) {
		printf("Opening TrailDB constructor failed: %s\n", tdb_error_str(err));
		exit(1);
	}

	// Release strings

	for (int i = 0; i < num_fields; i++) {
		temp_field = (*env)->GetObjectArrayElement(env, fields, i);
		(*env)->ReleaseStringUTFChars(env, temp_field, tgt_fields[i]);
	}

	free(tgt_fields);

	(*env)->ReleaseStringUTFChars(env, root, tgt_root);

	// Store cons pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cons", "J");
	if (fid == NULL) {
		return;
	}

	(*env)->SetLongField(env, obj, fid, (long) cons);
}

JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_finalize(JNIEnv *env, jobject obj) {
	jclass cls;
	jfieldID fid;

	tdb_error err;
	tdb_cons *cons;

	// Retrieve cons pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cons", "J");
	if (fid == NULL) {
		return;
	}
	cons = (tdb_cons *) (*env)->GetLongField(env, obj, fid);

	// Finalize tdb

	if ((err = tdb_cons_finalize(cons))) {
		printf("Finalizing TrailDB constructor failed: %s\n", tdb_error_str(err));
		exit(1);
	}
}

JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_nativeAdd(JNIEnv *env, jobject obj, jbyteArray uuid, jint ts, jobjectArray values) {
	jclass cls;
	jfieldID fid;

	jobject temp_value;
	const char **tgt_values;
	uint64_t *tgt_lengths;
	uint8_t tgt_uuid[16];

	tdb_error err;
	tdb_cons *cons;
	uint64_t num_values;

	// Get Number of values

	num_values = (*env)->GetArrayLength(env, values);

	// Get char pointers

	tgt_values = malloc(num_values * sizeof(char*));
	tgt_lengths = malloc(num_values * sizeof(uint64_t));
	for (int i = 0; i < num_values; i++) {
		temp_value = (*env)->GetObjectArrayElement(env, values, i);
		tgt_values[i] = (*env)->GetStringUTFChars(env, temp_value, NULL);
		tgt_lengths[i] = strlen(tgt_values[i]);
	}

	// Marshall UUID to byte array

	(*env)->GetByteArrayRegion(env, uuid, 0, 16, tgt_uuid);

	// Retrieve cons pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cons", "J");
	if (fid == NULL) {
		return;
	}
	cons = (tdb_cons *) (*env)->GetLongField(env, obj, fid);

	// Add event to tdb

	if ((err = tdb_cons_add(cons, tgt_uuid, ts, tgt_values, tgt_lengths))){
		printf("Adding an event failed: %s\n", tdb_error_str(err));
		exit(1);
	}

	// Release strings

	for (int i = 0; i < num_values; i++) {
		temp_value = (*env)->GetObjectArrayElement(env, values, i);
		(*env)->ReleaseStringUTFChars(env, temp_value, tgt_values[i]);
	}

	free(tgt_values);
}

JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_close(JNIEnv *env, jobject obj) {
	jclass cls;
	jfieldID fid;

	tdb_error err;
	tdb_cons *cons;

	// Retrieve cons pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cons", "J");
	if (fid == NULL) {
		return;
	}
	cons = (tdb_cons *) (*env)->GetLongField(env, obj, fid);

	// Close tdb

	tdb_cons_close(cons);
}