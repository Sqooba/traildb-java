#include "io_sqooba_traildb_TrailDBNative.h"
#include <jni.h>
#include <stdio.h>
#include <iostream>
#include <sys/resource.h>
#include <string.h>

extern "C" {
	#include <traildb.h>
}

static jclass traildbEvent;
static jfieldID JFID_traildbEvent_timestamp;
static jfieldID JFID_traildbEvent_numItems;
static jfieldID JFID_traildbEvent_items;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    } else {
        jclass tempLocalClassRef =  env->FindClass("io/sqooba/traildb/TrailDBEvent");

        if (tempLocalClassRef == NULL) {
            return JNI_ERR;
        }
        traildbEvent = (jclass) env->NewGlobalRef(tempLocalClassRef);
		env->DeleteLocalRef(tempLocalClassRef);

		JFID_traildbEvent_timestamp = env->GetFieldID(traildbEvent, "timestamp", "J");
		JFID_traildbEvent_numItems = env->GetFieldID(traildbEvent, "numItems", "J");
		JFID_traildbEvent_items = env->GetFieldID(traildbEvent, "items", "J");
    } 

    return JNI_VERSION_1_6;
}

void JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv* env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    } else {
        if (traildbEvent != NULL){
            env->DeleteGlobalRef(traildbEvent);
        }
    }
}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbConsInit
  (JNIEnv *env, jobject thisObject) 
{

	void *cons = tdb_cons_init();
	if(!cons) {
		return -1;
	}

    return (long)cons;
}

JNIEXPORT jint JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbConsOpen
  (JNIEnv *env, jobject thisObject, jlong consj, jstring rootj, jobjectArray fieldNamesj, jlong numOfieldsj)
{
	// Convert arguments.
    tdb_cons *cons = (tdb_cons*) consj;
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

JNIEXPORT void JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbConsClose
  (JNIEnv *env, jobject thisObject, jlong consj) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) consj;

	// Call lib.
	tdb_cons_close(cons);
}

JNIEXPORT jint JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbConsAdd
	(JNIEnv *env, jobject thisObject, jlong consj, jbyteArray uuidj, jlong timestampj, jobjectArray valuesj, jlongArray valuesLengths) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) consj;

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

JNIEXPORT jint JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbConsAppend
  (JNIEnv *env, jobject thisObject, jlong consj, jlong tdbj) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) consj;
	tdb *db = (tdb*) tdbj;

	// Call lib.
	return tdb_cons_append(cons, db);

}

JNIEXPORT jint JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbConsFinalize
  (JNIEnv *env, jobject thisObject, jlong consj) 
{

	// Convert arguments.
	tdb_cons *cons = (tdb_cons*) consj;

	// Call lib.
	return tdb_cons_finalize(cons);

}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbInit
  (JNIEnv *env, jobject thisObject) 
{

	void *tdb = tdb_init();

	if(!tdb) {
		return -1;
	}

    return (long)tdb; 

}

JNIEXPORT jint JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbOpen
  (JNIEnv *env, jobject thisObject, jlong jtdb, jstring jroot) 
{

	// Convert arguments.
    tdb *db = (tdb*) jtdb;
    const char *root = env->GetStringUTFChars(jroot, 0);

	// Call lib.
	return tdb_open(db, root);

}

JNIEXPORT void JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbClose
  (JNIEnv *env, jobject thisObject, jlong jdb) 
{

	// Convert arguments.
	tdb *db = (tdb*)jdb;

	// Call lib.
	tdb_close(db);
}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbNumTrails
  (JNIEnv *env, jobject thisObject, jlong jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	
	// Call lib.
	return (jlong)tdb_num_trails(db);

}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbNumEvents
  (JNIEnv *env, jobject thisObject, jlong jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	
	// Call lib.
	return (jlong)tdb_num_events(db);
}



JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbNumFields
  (JNIEnv *env, jobject thisObject, jlong jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	
	// Call lib.
	return (jlong)tdb_num_fields(db);

}


JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbMinTimestamp
  (JNIEnv *env, jobject thisObject, jlong jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	
	// Call lib.
	return (jlong)tdb_min_timestamp(db);

}


JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbMaxTimestamp
  (JNIEnv *env, jobject thisObject, jlong jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	
	// Call lib.
	return (jlong)tdb_max_timestamp(db);

}


JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbVersion
  (JNIEnv *env, jobject thisObject, jlong jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	
	// Call lib.
	return (jlong)tdb_version(db);

}

JNIEXPORT jstring JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbErrorStr
  (JNIEnv *env, jobject thisObject, jint jerrcode) 
{

	// Convert arguments.
	int nativeInt = (int)jerrcode;

	// Call lib.
	const char *res = tdb_error_str((tdb_error)nativeInt);

	// COnvert result and return it.
	return env->NewStringUTF(res);

}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbLexiconSize
  (JNIEnv *env, jobject thisObject, jlong jdb, jlong jfield) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	int nativeInt = (int)jfield;

	// Call lib.
	return (jlong)tdb_lexicon_size(db, nativeInt);

}

JNIEXPORT jint JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbGetField
  (JNIEnv *env, jobject thisObject, jlong jdb, jstring jfieldName, jobject jfield) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
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

JNIEXPORT jstring JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbGetFieldName
  (JNIEnv *env, jobject thisObject, jlong jdb, jlong jfield) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	int nativeField = (int)jfield;

	// Call lib.
	const char *field_name = tdb_get_field_name(db, nativeField);

	// Return res.
	return env->NewStringUTF(field_name);
}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbGetItem
  (JNIEnv *env, jobject thisObject, jlong jdb, jlong jfield, jstring jvalue) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	int field = (int)jfield;
	const char *value = env->GetStringUTFChars(jvalue, 0);

	// Call lib.
	return (jlong)tdb_get_item(db, field, value, (uint64_t)strlen(value));

}

JNIEXPORT jstring JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbGetValue
  (JNIEnv *env, jobject thisObject, jlong jdb, jlong jfield, jlong jval, jobject jvalueLength)
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
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

JNIEXPORT jstring JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbGetItemValue
  (JNIEnv *env, jobject thisObject, jlong jdb, jlong jitem, jobject jvalueLength)
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
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

JNIEXPORT jobject JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbGetUUID
  (JNIEnv *env, jobject thisObject, jlong jdb, jlong jtrailID) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	uint64_t trail_id = (uint64_t) jtrailID;

	// Call lib.
	const uint8_t *uuid = tdb_get_uuid(db, trail_id);
	jobject bb = env->NewDirectByteBuffer((void *)uuid, 16); // Raw uuid is 16-byte.
    return bb;

}

JNIEXPORT jint JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbGetTrailId
  (JNIEnv *env, jobject thisObject, jlong jdb, jbyteArray juuid, jobject jtraildID) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;

	jbyte* dataPtr = env->GetByteArrayElements(juuid, NULL);
	const uint8_t *uuid = (const uint8_t*)dataPtr;

	uint64_t *trail_id = (uint64_t*)malloc(sizeof(uint64_t));

	jclass jc = env->GetObjectClass(jtraildID);
	jmethodID mid = env->GetMethodID(jc, "putLong","(J)Ljava/nio/ByteBuffer;");

	// Call lib.
	int err = tdb_get_trail_id(db, uuid, trail_id);

	env->CallObjectMethod(jtraildID, mid, *trail_id);

	free(trail_id);

	return err;
}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbCursorNew
  (JNIEnv *env, jobject thisObject, jlong jdb) 
{

	// Convert arguments.
	const tdb *db = (tdb*)jdb;

	// Call lib.
	tdb_cursor *cursor = tdb_cursor_new(db);
	
	if(!cursor) {
		return -1;
	}
    
	return (long)cursor;
}

JNIEXPORT void JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbCursorFree
  (JNIEnv *env, jobject thisObject, jlong jcursor) 
{

	// Convert arguments.
	tdb_cursor *cursor = (tdb_cursor*)jcursor;

	// Call lib.
	tdb_cursor_free(cursor);

}

JNIEXPORT jint JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbGetTrail
  (JNIEnv *env, jobject thisObject, jlong jcursor, jlong jtrailID) 
{

	// Convert arguments.
	tdb_cursor *cursor = (tdb_cursor*)jcursor;
	uint64_t trail_id = (uint64_t) jtrailID;	

	// Call lib.
	return tdb_get_trail(cursor, trail_id);
}

JNIEXPORT jlong JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbGetTrailLength
  (JNIEnv *env, jobject thisObject, jlong jcursor) 
{

	// Convert arguments.
	tdb_cursor *cursor = (tdb_cursor*)jcursor;

	// Call lib.
	return (jlong) tdb_get_trail_length(cursor);
}

JNIEXPORT jint JNICALL Java_io_sqooba_traildb_TrailDBNative_tdbCursorNext
  (JNIEnv *env, jobject thisObject, jlong jcursor, jobject jevent)
{

	// Convert arguments.
	tdb_cursor *cursor = (tdb_cursor*)jcursor;

	// Call lib.
	const tdb_event *event;
	event = tdb_cursor_next(cursor);

	// Check if there is no more events.
	if(!event) {
		return -1;
	}

	// Store field in TrailDBEvent.
	env->SetLongField(jevent, JFID_traildbEvent_timestamp, event->timestamp);
	env->SetLongField(jevent, JFID_traildbEvent_numItems, event->num_items);
	env->SetLongField(jevent, JFID_traildbEvent_items, (long)event->items);

	return 0;
}

JNIEXPORT jstring JNICALL Java_io_sqooba_traildb_TrailDBNative_eventGetItemValue
  (JNIEnv *env, jobject thisObject, jlong jdb, jint jindex, jobject jevent) 
{
	
	// Convert arguments.
	const tdb *db = (tdb*)jdb;
	uint64_t value_length;


	// Get the items pointer.
	const tdb_item *items;
	items = (tdb_item *) env->GetLongField(jevent, JFID_traildbEvent_items);
	
	// Call lib.
	const char* v = tdb_get_item_value(db, items[jindex], &value_length);

	// Create the null-terminated String.
	char buffer[value_length];
	memcpy(buffer, &v[0], value_length);
	buffer[value_length] = '\0';

	return env->NewStringUTF(buffer);
}



