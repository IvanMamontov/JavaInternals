/**
 * A thread can be at a safepoint or not be at a safepoint.
 * When at a safepoint, the thread's representation of it's
 * Java machine state is well described, and can be safely
 * manipulated and observed by other threads in the JVM.
 * When not at a safepoint, the thread's representation of
 * the java machine state will not be manipulated by other
 * threads in the JVM. The JVM may choose to reach a global
 * safepoint(aka Stop-The-World), where all threads are at a
 * safepoint and can't leave the safepoint until the JVM
 * decides to let it do so. This is useful for doing all
 * sorts of work (like certain GC operations, deoptimization
 * during class loading, etc.) that require all threads to be
 * at a well described state. Oracle JDK currently use a
 * simple global "go to safepoint" indicator in the form of
 * a page that is protected when a safepoint is needed, and
 * unprotected otherwise. The safepoint polling for this mechanism
 * amounts to a load from a fixed address in that page. If the
 * load traps with a SEGV, the thread knows it needs to go to
 * enter a safepoint.
 */
package edu.jvm.runtime.safepoint;