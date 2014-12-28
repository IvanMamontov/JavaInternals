#include "nativeutils.h"
#include "timers.h"

extern unsigned long read_rdtscp(void);

JNIEXPORT jlong JNICALL Java_edu_NativeUtils_rdtscp(JNIEnv *env, jclass jobj)
{
  return (jlong) read_rdtscp();
}

JNIEXPORT jlong JNICALL JavaCritical_edu_NativeUtils_rdtscp()
{
  return (jlong) read_rdtscp();
}