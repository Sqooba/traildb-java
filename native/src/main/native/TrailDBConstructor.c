#include <traildb.h>
#include <string.h>
#include "traildb-java.h"


JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_init(JNIEnv *env, jobject obj, jstring root, jobjectArray fields) {
	jclass cls;
	jfieldID fid;
	jclass exc;

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
		exc = (*env)->FindClass(env, "java/io/IOException");
		if (exc == NULL) {
			/* Could not find the exception - We are in so much trouble right now */
			exit(1);
		}
		(*env)->ThrowNew(env, exc, tdb_error_str(err));
		return;
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
	jclass exc;

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
		exc = (*env)->FindClass(env, "java/io/IOException");
		if (exc == NULL) {
			/* Could not find the exception - We are in so much trouble right now */
			exit(1);
		}
		(*env)->ThrowNew(env, exc, tdb_error_str(err));
		return;
	}
}

JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_native_1add(JNIEnv *env, jobject obj, jbyteArray uuid, jint ts, jobjectArray values) {
	jclass cls;
	jfieldID fid;
	jclass exc;

	jobject temp_value;
	const char **tgt_values;
	uint64_t *tgt_lengths;
	uint8_t tgt_uuid[16];

	tdb_error err;
	tdb_cons *cons;
	uint64_t num_values;
	char *err_msg;

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

	(*env)->GetByteArrayRegion(env, uuid, 0, 16, (jbyte *) tgt_uuid);

	// Retrieve cons pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cons", "J");
	if (fid == NULL) {
		return;
	}
	cons = (tdb_cons *) (*env)->GetLongField(env, obj, fid);

	// Add event to tdb

	if ((err = tdb_cons_add(cons, tgt_uuid, ts, tgt_values, tgt_lengths))) {
		exc = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
		if (exc == NULL) {
			/* Could not find the exception - We are in so much trouble right now */
			exit(1);
		}
		// Subtract 2 because the %s will be replaced
		err_msg = (char *) malloc(strlen(tdb_error_str(err)) * sizeof(char) + sizeof("Adding an event failed: %s!") - 2);

		sprintf(err_msg, "Adding an event failed: %s!", tdb_error_str(err));

		(*env)->ThrowNew(env, exc, err_msg);

		free(err_msg);
		// Don't return here because we still need to release the strings
	}

	// Release strings

	for (int i = 0; i < num_values; i++) {
		temp_value = (*env)->GetObjectArrayElement(env, values, i);
		(*env)->ReleaseStringUTFChars(env, temp_value, tgt_values[i]);
	}

	free(tgt_values);
}

JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_append(JNIEnv *env, jobject obj, jobject tdbArg) {
	jclass cls;
	jfieldID fid;
	jclass exc;

	tdb *db;
	tdb_cons *cons;
	tdb_error err;

	// Retrieve cons pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cons", "J");
	if (fid == NULL) {
		exit(1);
	}
	cons = (tdb_cons *) (*env)->GetLongField(env, obj, fid);

	// Retrieve db pointer

	cls = (*env)->GetObjectClass(env, tdbArg);
	fid = (*env)->GetFieldID(env, cls, "db", "J");
	if (fid == NULL) {
		exit(1);
	}
	db = (tdb *) (*env)->GetLongField(env, tdbArg, fid);

	// Call constructor append

	if ((err = tdb_cons_append(cons, db))) {
		exc = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
		if (exc == NULL) {
			/* Could not find the exception - We are in so much trouble right now */
			exit(1);
		}
		(*env)->ThrowNew(env, exc, tdb_error_str(err));
	}
}

JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_close(JNIEnv *env, jobject obj) {
	jclass cls;
	jfieldID fid;

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

JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_setOpt(JNIEnv *env, jobject obj, jobject key, jobject value) {
	jclass cls;
	jfieldID fid;
	jclass exc;

	tdb_cons *cons;
	tdb_opt_key key_flag;
	tdb_opt_value value_flag;

	// Retrieve cons pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cons", "J");
	if (fid == NULL) {
		return;
	}
	cons = (tdb_cons *) (*env)->GetLongField(env, obj, fid);

	// Get ordinal of key enum

	jmethodID getValueMethod = (*env)->GetMethodID(env, (*env)->FindClass(env, "traildb/TrailDB$TDB_OPT_CONS_KEY"), "ordinal", "()I");
	jint ord = (*env)->CallIntMethod(env, key, getValueMethod);

	switch (ord) {
		case 0:
			key_flag = TDB_OPT_CONS_OUTPUT_FORMAT; // 1001
			break;
		case 1:
			key_flag = TDB_OPT_CONS_NO_BIGRAMS; // 1002
			break;
		default:
			exc = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
			if (exc == NULL) {
				/* Could not find the exception - We are in so much trouble right now */
				exit(1);
			}
			(*env)->ThrowNew(env, exc, "Unrecognized option key");
			return;
	}

	// Get ordinal of value enum

	getValueMethod = (*env)->GetMethodID(env, (*env)->FindClass(env, "traildb/TrailDB$TDB_OPT_CONS_VALUE"), "ordinal", "()I");
	ord = (*env)->CallIntMethod(env, key, getValueMethod);

	switch (ord) {
		case 0:
			value_flag.value = TDB_OPT_CONS_OUTPUT_FORMAT_DIR; // 0
			break;
		case 1:
			value_flag.value = TDB_OPT_CONS_OUTPUT_FORMAT_DIR; // 0
			break;
		case 2:
			value_flag.value = TDB_OPT_CONS_OUTPUT_FORMAT_PACKAGE; // 1
			break;
		case 3:
			value_flag.value = TDB_OPT_CONS_OUTPUT_FORMAT_PACKAGE; // 1
			break;
		default:
			exc = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
			if (exc == NULL) {
				/* Could not find the exception - We are in so much trouble right now */
				exit(1);
			}
			(*env)->ThrowNew(env, exc, "Unrecognized option value");
			return;
	}

	// Set option

	tdb_cons_set_opt(cons, key_flag, value_flag);
}

JNIEXPORT jobject JNICALL Java_traildb_TrailDBConstructor_getOpt(JNIEnv *env, jobject obj, jobject key) {
	jclass cls;
	jfieldID fid;
	jclass exc;

	tdb_cons *cons;
	tdb_opt_key key_flag;
	tdb_opt_value value_flag;
	char value_name[32]; // 32 is arbitrary

	// Retrieve cons pointer

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cons", "J");
	if (fid == NULL) {
		exit(1);
	}
	cons = (tdb_cons *) (*env)->GetLongField(env, obj, fid);

	// Get ordinal of key enum

	jmethodID getValueMethod = (*env)->GetMethodID(env, (*env)->FindClass(env, "traildb/TrailDB$TDB_OPT_CONS_KEY"), "ordinal", "()I");
	jint value = (*env)->CallIntMethod(env, key, getValueMethod);

	switch (value) {
		case 0:
			key_flag = TDB_OPT_CONS_OUTPUT_FORMAT; // 1001
			break;
		case 1:
			key_flag = TDB_OPT_CONS_NO_BIGRAMS; // 1002
			break;
		default:
			exc = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
			if (exc == NULL) {
				/* Could not find the exception - We are in so much trouble right now */
				exit(1);
			}
			(*env)->ThrowNew(env, exc, "Unrecognized option key");
			return NULL;
	}

	// Get option

	tdb_cons_get_opt(cons, key_flag, &value_flag);

	switch (value_flag.value) {
		case TDB_OPT_CONS_OUTPUT_FORMAT_DIR:
			strcpy(value_name, "TDB_OPT_CONS_OUTPUT_FORMAT_DIR");
			break;
		case TDB_OPT_CONS_OUTPUT_FORMAT_PACKAGE:
			strcpy(value_name, "TDB_OPT_CONS_OUTPUT_FORMAT_PACKAGE");
			break;
		default:
			exc = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
			if (exc == NULL) {
				/* Could not find the exception - We are in so much trouble right now */
				exit(1);
			}
			(*env)->ThrowNew(env, exc, "Unrecognized option value returned from TrailDB");
			return NULL;
	}

	jclass jenum = (*env)->FindClass(env, "traildb/TrailDB$TDB_OPT_CONS_VALUE");
	jfieldID fidEnum = (*env)->GetStaticFieldID(env, jenum , value_name, "LTrailDB$TDB_OPT_CONS_VALUE;");
	return (*env)->GetStaticObjectField(env, jenum, fidEnum);
}
