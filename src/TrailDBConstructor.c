#include <traildb.h>
#include "../include/TrailDBConstructor.h"


JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_init(JNIEnv *env, jobject obj, jstring root, jobjectArray fields) {
	jclass cls;
	jfieldID fid;
	jobject jobj;

	tdb_error err;
	tdb_cons *cons;
	const char *root_str;
	const char **fields_str;
	uint64_t num_fields;

	// Get Number of fields

	num_fields = (*env)->GetArrayLength(env, fields);

	// Get char pointers

	jobject temp_field;
	fields_str = malloc(num_fields * sizeof(char*));
	for (int i = 0; i < num_fields; i++) {
		temp_field = (*env)->GetObjectArrayElement(env, fields, i);
		fields_str[i] = (*env)->GetStringUTFChars(env, temp_field, NULL);
	}

	root_str = (*env)->GetStringUTFChars(env, root, NULL);

	// Initialize and open tdb

	cons = tdb_cons_init();
	if ((err = tdb_cons_open(cons, root_str, fields_str, 2))) {
		printf("Opening TrailDB constructor failed: %s\n", tdb_error_str(err));
		exit(1);
	}

	// Release strings

	for (int i = 0; i < num_fields; i++) {
		temp_field = (*env)->GetObjectArrayElement(env, fields, i);
		(*env)->ReleaseStringUTFChars(env, temp_field, fields_str[i]);
	}

	free(fields_str);

	(*env)->ReleaseStringUTFChars(env, root, root_str);

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
	jobject bb;

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
