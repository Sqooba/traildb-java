#include <traildb.h>
#include <string.h>
#include "traildb-java.h"

jclass CID_traildb_filters_TrailDBRestriction;

jclass CID_traildb_filters_TrailDBTimeRange;

jfieldID FID_traildb_filters_TrailDBEventFilter_f;

jfieldID FID_traildb_TrailDB_db;

jfieldID FID_traildb_filters_TrailDBClause_terms;

jfieldID FID_traildb_filters_TrailDBRestriction_field;

jfieldID FID_traildb_filters_TrailDBRestriction_value;

jfieldID FID_traildb_filters_TrailDBRestriction_negative;

jfieldID FID_traildb_filters_TrailDBTimeRange_start;

jfieldID FID_traildb_filters_TrailDBTimeRange_end;


JNIEXPORT void JNICALL Java_traildb_filters_TrailDBEventFilter_init(JNIEnv *env, jobject obj, jobjectArray clauses, jobject tdb_obj) {
    jobject clause_obj;
    jobject term_obj;
    jobjectArray term_objs;
    jobject tmp_field;
    jobject tmp_value;
    jboolean negative;

    tdb_field field;
    tdb_item term;
    struct tdb_event_filter *filter;
    tdb_error err;
    const tdb *db;
    int num_clauses;
    int num_terms;
    uint64_t start_time;
    uint64_t end_time;
    const char *tgt_field;
    const char *tgt_value;

    // Retrieve db pointer

    db = (tdb *) (*env)->GetLongField(env, tdb_obj, FID_traildb_TrailDB_db);

    // Create a new filter

    filter = tdb_event_filter_new();

    num_clauses = (*env)->GetArrayLength(env, clauses);

    for (int i = 0; i < num_clauses; i++) {
        clause_obj = (*env)->GetObjectArrayElement(env, clauses, i);
        term_objs = (*env)->GetObjectField(env, clause_obj, FID_traildb_filters_TrailDBClause_terms);
        num_terms = (*env)->GetArrayLength(env, term_objs);
        for (int j = 0; j < num_terms; j++) {
            term_obj = (*env)->GetObjectArrayElement(env, term_objs, j);
            if ((*env)->IsInstanceOf(env, term_obj, CID_traildb_filters_TrailDBTimeRange)) {
                // Retrieve start and end time

                start_time = (*env)->GetLongField(env, term_obj, FID_traildb_filters_TrailDBTimeRange_start);
                end_time = (*env)->GetLongField(env, term_obj, FID_traildb_filters_TrailDBTimeRange_end);

                // Add time range term

                err = tdb_event_filter_add_time_range(filter, start_time, end_time);
                if (err) {
                    exit(1);
                }
            } else if ((*env)->IsInstanceOf(env, term_obj, CID_traildb_filters_TrailDBRestriction)) {
                // Get strings

                tmp_field = (*env)->GetObjectField(env, term_obj, FID_traildb_filters_TrailDBRestriction_field);
                tmp_value = (*env)->GetObjectField(env, term_obj, FID_traildb_filters_TrailDBRestriction_value);

                tgt_field = (*env)->GetStringUTFChars(env, tmp_field, NULL);
                tgt_value = (*env)->GetStringUTFChars(env, tmp_field, NULL);

                // Get field id from db

                tdb_get_field(db, tgt_field, &field);

                // Get item which corresponds to field=value

                term = tdb_get_item(db, field, tgt_value, strlen(tgt_value));

                // Get is_negative

                negative = (*env)->GetBooleanField(env, term_obj, FID_traildb_filters_TrailDBRestriction_negative);

                // Add event filter

                err = tdb_event_filter_add_term(filter, term, negative ? 1 : 0);

                // Release strings
                (*env)->ReleaseStringUTFChars(env, tmp_field, tgt_field);
                (*env)->ReleaseStringUTFChars(env, tmp_value, tgt_value);

                if (err) {
                    exit(1);
                }
            } else {
                exit(1);
            }
        }

        // Start a new clause

        tdb_event_filter_new_clause(filter);
    }

    // Save filter to f for later

    (*env)->SetLongField(env, obj, FID_traildb_filters_TrailDBEventFilter_f, (long) filter);
}

JNIEXPORT jobject JNICALL Java_traildb_filters_TrailDBEventFilter_matchNone(JNIEnv *env, jclass cls) {

}

JNIEXPORT jobject JNICALL Java_traildb_filters_TrailDBEventFilter_matchAll(JNIEnv *env, jclass cls) {

}

JNIEXPORT void JNICALL Java_traildb_filters_TrailDBEventFilter_free(JNIEnv *env, jobject obj) {

}

JNIEXPORT void JNICALL Java_traildb_filters_TrailDBEventFilter_addTerm(JNIEnv *env, jobject obj, jobject item, jboolean negative) {

}

JNIEXPORT void JNICALL Java_traildb_filters_TrailDBEventFilter_addTimeRange(JNIEnv *env, jobject obj, jint start_time, jint end_time) {

}

JNIEXPORT void JNICALL Java_traildb_filters_TrailDBEventFilter_newClause(JNIEnv *env, jobject obj) {

}

JNIEXPORT jint JNICALL Java_traildb_filters_TrailDBEventFilter_numClauses(JNIEnv *env, jobject obj) {

}

JNIEXPORT jint JNICALL Java_traildb_filters_TrailDBEventFilter_numTerms(JNIEnv *env, jobject obj, jint clause_index) {

}

JNIEXPORT jboolean JNICALL Java_traildb_filters_TrailDBEventFilter_isNegative(JNIEnv *env, jobject obj, jint clause_index, jint term_index) {

}

JNIEXPORT jobject JNICALL Java_traildb_filters_TrailDBEventFilter_getItem(JNIEnv *env, jobject obj, jint clause_index, jint term_index) {

}

JNIEXPORT jint JNICALL Java_traildb_filters_TrailDBEventFilter_getStartTime(JNIEnv *env, jobject obj, jint clause_index, jint term_index) {

}

JNIEXPORT jint JNICALL Java_traildb_filters_TrailDBEventFilter_getEndTime(JNIEnv *env, jobject obj, jint clause_index, jint term_index) {

}

JNIEXPORT void JNICALL Java_traildb_filters_TrailDBEventFilter_initIDs(JNIEnv *env, jclass cls) {
    jclass traildb_TrailDB = (*env)->FindClass(env, "traildb/TrailDB");

    jclass traildb_filters_TrailDBClause = (*env)->FindClass(env, "traildb/filters/TrailDBClause");

    jclass traildb_filters_TrailDBRestriction = (*env)->FindClass(env, "traildb/filters/TrailDBRestriction");

    jclass traildb_filters_TrailDBTimeRange = (*env)->FindClass(env, "traildb/filters/TrailDBTimeRange");

    CID_traildb_filters_TrailDBRestriction = (jclass) (*env)->NewGlobalRef(env, traildb_filters_TrailDBRestriction);

    CID_traildb_filters_TrailDBTimeRange = (jclass) (*env)->NewGlobalRef(env, traildb_filters_TrailDBTimeRange);

    FID_traildb_filters_TrailDBEventFilter_f = (*env)->GetFieldID(env, cls, "f", "J");

    FID_traildb_TrailDB_db = (*env)->GetFieldID(env, traildb_TrailDB, "db", "J");

    FID_traildb_filters_TrailDBClause_terms = (*env)->GetFieldID(env, traildb_filters_TrailDBClause, "terms", "[Ltraildb/filters/TrailDBTerm;");

    FID_traildb_filters_TrailDBRestriction_value = (*env)->GetFieldID(env, traildb_filters_TrailDBRestriction, "value", "Ljava/lang/String;");

    FID_traildb_filters_TrailDBRestriction_field = (*env)->GetFieldID(env, traildb_filters_TrailDBRestriction, "field", "Ljava/lang/String;");

    FID_traildb_filters_TrailDBRestriction_negative = (*env)->GetFieldID(env, traildb_filters_TrailDBRestriction, "negative", "Z");

    FID_traildb_filters_TrailDBTimeRange_start = (*env)->GetFieldID(env, traildb_filters_TrailDBTimeRange, "start", "J");

    FID_traildb_filters_TrailDBTimeRange_end = (*env)->GetFieldID(env, traildb_filters_TrailDBTimeRange, "end", "J");

    (*env)->DeleteLocalRef(env, traildb_filters_TrailDBRestriction);

    (*env)->DeleteLocalRef(env, traildb_filters_TrailDBTimeRange);

}
