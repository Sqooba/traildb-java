#include "../include/TrailDBEventFilter.h"

JNIEXPORT void JNICALL Java_traildb_TrailDBEventFilter_init(JNIEnv *env, jobject obj) {

}

JNIEXPORT jobject JNICALL Java_traildb_TrailDBEventFilter_matchNone(JNIEnv *env, jclass cls) {

}

JNIEXPORT jobject JNICALL Java_traildb_TrailDBEventFilter_matchAll(JNIEnv *env, jclass cls) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDBEventFilter_free(JNIEnv *env, jobject obj) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDBEventFilter_addTerm(JNIEnv *env, jobject obj, jobject item, jboolean negative) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDBEventFilter_addTimeRange(JNIEnv *env, jobject obj, jint start_time, jint end_time) {

}

JNIEXPORT void JNICALL Java_traildb_TrailDBEventFilter_newClause(JNIEnv *env, jobject obj) {

}

JNIEXPORT jint JNICALL Java_traildb_TrailDBEventFilter_numClauses(JNIEnv *env, jobject obj) {

}

JNIEXPORT jint JNICALL Java_traildb_TrailDBEventFilter_numTerms(JNIEnv *env, jobject obj, jint clause_index) {

}

JNIEXPORT jboolean JNICALL Java_traildb_TrailDBEventFilter_isNegative(JNIEnv *env, jobject obj, jint clause_index, jint term_index) {

}

JNIEXPORT jobject JNICALL Java_traildb_TrailDBEventFilter_getItem(JNIEnv *env, jobject obj, jint clause_index, jint term_index) {

}

JNIEXPORT jint JNICALL Java_traildb_TrailDBEventFilter_getStartTime(JNIEnv *env, jobject obj, jint clause_index, jint term_index) {

}

JNIEXPORT jint JNICALL Java_traildb_TrailDBEventFilter_getEndTime(JNIEnv *env, jobject obj, jint clause_index, jint term_index) {

}
