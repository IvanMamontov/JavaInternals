#include "SysTime.h"

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