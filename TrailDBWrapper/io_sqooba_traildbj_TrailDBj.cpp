#include "io_sqooba_traildbj_TrailDBj.h"
#include <jni.h>
#include <stdio.h>
#include <iostream>
#include <sys/resource.h>
#include <string.h>

extern "C" {
	#include <traildb.h>
}

JNIEXPORT jobject JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsInit
  (JNIEnv *env, jobject thisObject) 
{

	void *cons = tdb_cons_init();
	jobject bb = env->NewDirectByteBuffer((void*) cons, sizeof(&cons));
    return bb;
}

JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsOpen
  (JNIEnv *env, jobject thisObject, jobject consj, jstring rootj, jobjectArray fieldNamesj, jlong numOfieldsj)
{
	// Convert arguments.
    tdb_cons *cons = (tdb_cons*) env->GetDirectBufferAddress(consj);
    const char *root = env->GetStringUTFChars(rootj, 0);

	jsize length = env->GetArrayLength(fieldNamesj); // Should be equal to numOfields
	const char *ofield_names[numOfieldsj];
	int i;
	for (i = 0; i < length; i++) {
		jstring objString = (jstring)env->GetObjectArrayElement(fieldNamesj, i);
		const char *field = env->GetStringUTFChars(objString, 0);
		ofield_names[i] = field;
    }

    long num_ofields = (long)numOfieldsj;

	// Call lib.
	return tdb_cons_open(cons, root, ofield_names, num_ofields);
}

JNIEXPORT void JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsClose
  (JNIEnv *env, jobject thisObject, jobject consj) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) env->GetDirectBufferAddress(consj);

	// Call lib.
	tdb_cons_close(cons);
}

JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsAdd
	(JNIEnv *env, jobject thisObject, jobject consj, jbyteArray uuidj, jlong timestampj, jobjectArray valuesj, jlongArray valuesLengths) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) env->GetDirectBufferAddress(consj);

	jbyte* dataPtr = env->GetByteArrayElements(uuidj, NULL);
	const uint8_t *uuid = (const uint8_t*)dataPtr;
	
	jsize length = env->GetArrayLength(valuesj);
	const char *values[length];
	int i;
	for (i = 0; i < length; i++) {
		jstring objString = (jstring)env->GetObjectArrayElement(valuesj, i);
		const char *field = env->GetStringUTFChars(objString, 0);
		values[i] = field;
    }

	jlong *val_len_ptr = env->GetLongArrayElements(valuesLengths, 0);
	const uint64_t *values_lengths = (const uint64_t*)val_len_ptr;
	
	// Call lib.
	return tdb_cons_add(cons, uuid, (long) timestampj, values, values_lengths);
}

JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsAppend
  (JNIEnv *env, jobject thisObject, jobject consj, jobject tdbj) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) env->GetDirectBufferAddress(consj);
	tdb *db = (tdb*) env->GetDirectBufferAddress(tdbj);

	// Call lib.
	return tdb_cons_append(cons, db);

}

JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbConsFinalize
  (JNIEnv *env, jobject thisObject, jobject consj) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) env->GetDirectBufferAddress(consj);

	// Call lib.
	return tdb_cons_finalize(cons);

}

JNIEXPORT jobject JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbInit
  (JNIEnv *env, jobject thisObject) 
{

	void *tdb = tdb_init();
	jobject byteBuffer = env->NewDirectByteBuffer((void*) tdb, sizeof(&tdb));
    return byteBuffer; 

}

JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbOpen
  (JNIEnv *env, jobject thisObject, jobject jtdb, jstring jroot) 
{

	// Convert arguments.
    tdb *db = (tdb*) env->GetDirectBufferAddress(jtdb);
    const char *root = env->GetStringUTFChars(jroot, 0);

	// Call lib.
	return tdb_open(db, root);

}

JNIEXPORT void JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbClose
  (JNIEnv *env, jobject thisObject, jobject jdb) 
{

	// Convert arguments.
	tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);

	// Call lib.
	tdb_close(db);

}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbNumTrails
  (JNIEnv *env, jobject thisObject, jobject jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	
	// Call lib.
	return (jlong)tdb_num_trails(db);

}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbNumEvents
  (JNIEnv *env, jobject thisObject, jobject jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	
	// Call lib.
	return (jlong)tdb_num_events(db);
}



JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbNumFields
  (JNIEnv *env, jobject thisObject, jobject jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	
	// Call lib.
	return (jlong)tdb_num_fields(db);

}


JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbMinTimestamp
  (JNIEnv *env, jobject thisObject, jobject jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	
	// Call lib.
	return (jlong)tdb_min_timestamp(db);

}


JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbMaxTimestamp
  (JNIEnv *env, jobject thisObject, jobject jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	
	// Call lib.
	return (jlong)tdb_max_timestamp(db);

}


JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbVersion
  (JNIEnv *env, jobject thisObject, jobject jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	
	// Call lib.
	return (jlong)tdb_version(db);

}

JNIEXPORT jstring JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbErrorStr
  (JNIEnv *env, jobject thisObject, jint jerrcode) 
{

	// Convert arguments.
	int nativeInt = (int)jerrcode;

	// Call lib.
	const char *res = tdb_error_str((tdb_error)nativeInt);

	// COnvert result and return it.
	return env->NewStringUTF(res);

}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbLexiconSize
  (JNIEnv *env, jobject thisObject, jobject jdb, jlong jfield) 
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	int nativeInt = (int)jfield;

	// Call lib.
	return (jlong)tdb_lexicon_size(db, nativeInt);

}

JNIEXPORT jint JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetField
  (JNIEnv *env, jobject thisObject, jobject jdb, jstring jfieldName, jobject jfield) 
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	const char *field_name = env->GetStringUTFChars(jfieldName, 0);
	//tdb_field *field= (tdb_field*) env->GetDirectBufferAddress(jfield);
	tdb_field *field = (tdb_field*)malloc(sizeof(tdb_field));

	jclass jc = env->GetObjectClass(jfield);
	jmethodID mid = env->GetMethodID(jc, "putInt","(I)Ljava/nio/ByteBuffer;");
	

	// Call lib.
	int err = tdb_get_field(db, field_name, field);

	// Store to the buffer the what has been put in the pointer.
	env->CallObjectMethod(jfield, mid, *field);

	free(field);

	return err;
}

JNIEXPORT jstring JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetFieldName
  (JNIEnv *env, jobject thisObject, jobject jdb, jlong jfield) 
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	int nativeField = (int)jfield;

	// Call lib.
	const char *field_name = tdb_get_field_name(db, nativeField);

	// Return res.
	return env->NewStringUTF(field_name);
}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetItem
  (JNIEnv *env, jobject thisObject, jobject jdb, jlong jfield, jstring jvalue) 
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	int field = (int)jfield;
	const char *value = env->GetStringUTFChars(jvalue, 0);

	// Call lib.
	return (jlong)tdb_get_item(db, field, value, (uint64_t)strlen(value));

}

JNIEXPORT jstring JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetValue
  (JNIEnv *env, jobject thisObject, jobject jdb, jlong jfield, jlong jval, jobject jvalueLength)
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	uint32_t field = (uint32_t)jfield;
	uint64_t val = (uint64_t)jval;
	uint64_t *value_length = (uint64_t*)malloc(sizeof(uint64_t));

	jclass jc = env->GetObjectClass(jvalueLength);
	jmethodID mid = env->GetMethodID(jc, "putLong","(J)Ljava/nio/ByteBuffer;");
	

	// Call lib.
	const char* v = tdb_get_value(db, field, val, value_length);

	// Store to the buffer the what has been put in the pointer.
	env->CallObjectMethod(jvalueLength, mid, *value_length);

	free(value_length);

	return env->NewStringUTF(v);
}

JNIEXPORT jstring JNICALL Java_io_sqooba_traildbj_TrailDBj_tdbGetItemValue
  (JNIEnv *env, jobject thisObject, jobject jdb, jlong jitem, jobject jvalueLength)
{

	// Convert arguments.
	const tdb *db = (tdb*) env->GetDirectBufferAddress(jdb);
	uint64_t item = (uint64_t)jitem;
	uint64_t *value_length = (uint64_t*)malloc(sizeof(uint64_t));

	jclass jc = env->GetObjectClass(jvalueLength);
	jmethodID mid = env->GetMethodID(jc, "putLong","(J)Ljava/nio/ByteBuffer;");
	

	// Call lib.
	const char* v = tdb_get_item_value(db, item, value_length);

	// Store to the buffer the what has been put in the pointer.
	env->CallObjectMethod(jvalueLength, mid, *value_length);

	free(value_length);

	return env->NewStringUTF(v);

}


