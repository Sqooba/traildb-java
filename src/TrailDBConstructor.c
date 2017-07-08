#include <traildb.h>
#include "../include/TrailDBConstructor.h"


JNIEXPORT void JNICALL Java_traildb_TrailDBConstructor_init(JNIEnv *env, jobject obj, jstring root, jobjectArray fields) {
	jclass cls;
	jfieldID fid;
	jobject jobj;

	tdb_error err;
	tdb_cons *cons;
	const char *str;
	const char *field_names[] = {"hello", "world"};
	uint64_t num_fields;

	cons = tdb_cons_init();
	str = (*env)->GetStringUTFChars(env, root, NULL);

	num_fields = (*env)->GetArrayLength(env, fields);

	if ((err = tdb_cons_open(cons, str, field_names, 2))) {
		printf("Opening TrailDB constructor failed: %s\n", tdb_error_str(err));
		exit(1);
	}
	(*env)->ReleaseStringUTFChars(env, root, str);

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cons", "Ljava/lang/Object;");
	if (fid == NULL) {
		return; /* failed to find the field */
	}
	(*env)->SetObjectField(env, obj, fid, (jobject) cons);
}

JNIEXPORT void JNICALL Java_TrailDBConstructor_finalize(JNIEnv *env, jobject obj) {
	jclass cls;
	jfieldID fid;
	jobject jobj;

	tdb_error err;
	tdb_cons *cons;

	cls = (*env)->GetObjectClass(env, obj);
	fid = (*env)->GetFieldID(env, cls, "cons", "Ljava/lang/Object;");

	if (fid == NULL) {
		return; /* failed to find the field */
	}

	jobj = (*env)->GetObjectField(env, obj, fid);
	cons = (tdb_cons *) jobj;

	if ((err = tdb_cons_finalize(cons))) {
		printf("Finalizing TrailDB constructor failed: %s\n", tdb_error_str(err));
		exit(1);
	}
}

JNIEXPORT void JNICALL Java_TrailDBConstructor_add(JNIEnv *env, jobject obj, jobject uuid, jint timestamp, jobjectArray values) {
	return;
}
