#include <stdio.h>
#include <inttypes.h>
#include "SysTime.h"
#include "Compressor.h"
#include "simdcomp.h"

JNIEXPORT jlong JNICALL Java_edu_SysTime_rdtsc(JNIEnv *env, jclass jobj)
{
  unsigned int lo, hi;
  asm volatile (
     "rdtsc"
   : "=a"(lo), "=d"(hi) /* outputs */
   : "a"(0)             /* inputs */
   : "%ebx", "%ecx");     /* clobbers*/
  long long x = ((unsigned long long)lo) | (((unsigned long long)hi) << 32);
  return (jlong) x;
}

JNIEXPORT jlong JNICALL JavaCritical_edu_SysTime_rdtsc()
{
  unsigned int lo, hi;
  asm volatile (
     "rdtsc"
   : "=a"(lo), "=d"(hi) /* outputs */
   : "a"(0)             /* inputs */
   : "%ebx", "%ecx");     /* clobbers*/
  long long x = ((unsigned long long)lo) | (((unsigned long long)hi) << 32);
  return (jlong) x;
}

JNIEXPORT jlong JNICALL Java_edu_SysTime_cpuidrdtsc(JNIEnv *env, jclass jobj)
{
  unsigned int lo, hi;
  asm volatile (
     "cpuid \n"
     "rdtsc"
   : "=a"(lo), "=d"(hi) /* outputs */
   : "a"(0)             /* inputs */
   : "%ebx", "%ecx");     /* clobbers*/
  long long x = ((unsigned long long)lo) | (((unsigned long long)hi) << 32);
  return (jlong) x;
}

JNIEXPORT jlong JNICALL Java_edu_SysTime_rdtscp(JNIEnv *env, jclass jobj)
{
  unsigned int lo, hi;
  asm volatile (
     "rdtscp"
   : "=a"(lo), "=d"(hi) /* outputs */
   : "a"(0)             /* inputs */
   : "%ebx", "%ecx");     /* clobbers*/
  long long x = ((unsigned long long)lo) | (((unsigned long long)hi) << 32);
  return (jlong) x;
}

/*
 * Class:     edu_SysTime
 * Method:    compress
 * Signature: ([I[B)I
 */
JNIEXPORT jint JNICALL Java_edu_SysTime_compress(JNIEnv *env, jclass jobj, jintArray jdatain, jbyteArray jbuffer)
{
    jboolean isCopy;
    size_t length = ((size_t)(*env)->GetArrayLength(env, jdatain));
    jint *dataArray = (*env)->GetIntArrayElements(env, jdatain, &isCopy);
    jbyte *bufferArray = (*env)->GetByteArrayElements(env, jbuffer, &isCopy);

    jint result = compress((uint32_t *)dataArray, (uint8_t *)bufferArray, length);

    (*env)->ReleaseIntArrayElements(env, jdatain, dataArray, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jbuffer, bufferArray, JNI_COMMIT);

    return result;
}

/*
 * Class:     edu_SysTime
 * Method:    compress
 * Signature: ([I[B)I
 */
JNIEXPORT jint JNICALL JavaCritical_edu_SysTime_compress(jint length, jint* dataArray, jint lengthout, jbyte* bufferArray)
{
    jint result = compress((uint32_t *)dataArray, (uint8_t *)bufferArray, length);
    return result;
}

/*
 * Class:     edu_SysTime
 * Method:    compress
 * Signature: ([I[B)I
 */
JNIEXPORT jint JNICALL Java_edu_SysTime_compressCritical(JNIEnv *env, jclass jobj, jintArray jdatain, jbyteArray jbuffer)
{
    jboolean isCopy;
    size_t length = ((size_t)(*env)->GetArrayLength(env, jdatain));
    jint *dataArray = (jint *)(*env)->GetPrimitiveArrayCritical(env, jdatain, &isCopy);
    jbyte *bufferArray = (jbyte *)(*env)->GetPrimitiveArrayCritical(env, jbuffer, &isCopy);

    jint result = compress((uint32_t *)dataArray, (uint8_t *)bufferArray, length);

    (*env)->ReleasePrimitiveArrayCritical(env, jdatain, dataArray, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, jbuffer, bufferArray, JNI_COMMIT);

    return result;
}

static jint compress(uint32_t * datain, uint8_t *buffer, size_t length) {
    uint32_t offset = 0;
    uint8_t * initout = buffer;

    if(length/SIMDBlockSize*SIMDBlockSize != length) {
        printf("Data length should be a multiple of %i \n", SIMDBlockSize);
    }

    for(size_t k = 0; k < length / SIMDBlockSize; ++k) {
        uint32_t b = simdmaxbitsd1(offset, datain + k * SIMDBlockSize);
    	*buffer++ = b;
    	simdpackwithoutmaskd1(offset, datain + k * SIMDBlockSize, (__m128i *) buffer, b);
        offset = datain[k * SIMDBlockSize + SIMDBlockSize - 1];
        buffer += b * sizeof(__m128i);
    }
    return (jint)(buffer - initout);
}
