#include <stdio.h>
#include <inttypes.h>
#include "SysTime.h"
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
    int dataLength = ((int)(*env)->GetArrayLength(env, jdatain));
    const jsize bufferLength = (*env)->GetArrayLength(env, jbuffer);
    jint *dataArray = (*env)->GetIntArrayElements(env, jdatain, &isCopy);
    jbyte *bufferArray = (*env)->GetByteArrayElements(env, jbuffer, &isCopy);

    if(dataLength/SIMDBlockSize*SIMDBlockSize != dataLength) {
        printf("Data length should be a multiple of %i \n", SIMDBlockSize);
    }

    uint32_t * datain = (uint32_t *)dataArray;
    size_t length = (size_t)dataLength;
    uint8_t * buffer = (uint8_t *)bufferArray;

    uint32_t offset = 0;
    uint8_t * initout = buffer;

//    for(size_t k = 0; k < length; ++k) {
//       printf("%i %" PRIu32 "\n", k, datain[k]);
//       printf("%i %" PRIu8 "\n", k, buffer[k]);
//    }

    for(size_t k = 0; k < length / SIMDBlockSize; ++k) {
        uint32_t b = simdmaxbitsd1(offset, datain + k * SIMDBlockSize);
    	*buffer++ = b;
    	simdpackwithoutmaskd1(offset, datain + k * SIMDBlockSize, (__m128i *) buffer, b);
        offset = datain[k * SIMDBlockSize + SIMDBlockSize - 1];
        buffer += b * sizeof(__m128i);
    }

    for(size_t k = 0; k < length; ++k) {
//       printf("%i %" PRIu32 "\n", k, datain[k]);
       printf("%i %" PRIu8 "\n", k, buffer[k]);
    }

    (*env)->ReleaseIntArrayElements(env, jdatain, dataArray, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jbuffer, bufferArray, JNI_COMMIT);

    return (jint)(buffer - initout);
}
