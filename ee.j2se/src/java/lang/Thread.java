/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang;
public class Thread implements java.lang.Runnable {
	public enum State {
		BLOCKED,
		NEW,
		RUNNABLE,
		TERMINATED,
		TIMED_WAITING,
		WAITING;
	}
	public interface UncaughtExceptionHandler {
		void uncaughtException(java.lang.Thread var0, java.lang.Throwable var1);
	}
	public final static int MAX_PRIORITY = 10;
	public final static int MIN_PRIORITY = 1;
	public final static int NORM_PRIORITY = 5;
	public Thread() { } 
	public Thread(java.lang.Runnable var0) { } 
	public Thread(java.lang.Runnable var0, java.lang.String var1) { } 
	public Thread(java.lang.String var0) { } 
	public Thread(java.lang.ThreadGroup var0, java.lang.Runnable var1) { } 
	public Thread(java.lang.ThreadGroup var0, java.lang.Runnable var1, java.lang.String var2) { } 
	public Thread(java.lang.ThreadGroup var0, java.lang.Runnable var1, java.lang.String var2, long var3) { } 
	public Thread(java.lang.ThreadGroup var0, java.lang.String var1) { } 
	public static int activeCount() { return 0; }
	public final void checkAccess() { }
	/** @deprecated */
	@java.lang.Deprecated
	public int countStackFrames() { return 0; }
	public static java.lang.Thread currentThread() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public void destroy() { }
	public static void dumpStack() { }
	public static int enumerate(java.lang.Thread[] var0) { return 0; }
	public static java.util.Map<java.lang.Thread,java.lang.StackTraceElement[]> getAllStackTraces() { return null; }
	public java.lang.ClassLoader getContextClassLoader() { return null; }
	public static java.lang.Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() { return null; }
	public long getId() { return 0l; }
	public final java.lang.String getName() { return null; }
	public final int getPriority() { return 0; }
	public java.lang.StackTraceElement[] getStackTrace() { return null; }
	public java.lang.Thread.State getState() { return null; }
	public final java.lang.ThreadGroup getThreadGroup() { return null; }
	public java.lang.Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() { return null; }
	public static boolean holdsLock(java.lang.Object var0) { return false; }
	public void interrupt() { }
	public static boolean interrupted() { return false; }
	public final boolean isAlive() { return false; }
	public final boolean isDaemon() { return false; }
	public boolean isInterrupted() { return false; }
	public final void join() throws java.lang.InterruptedException { }
	public final void join(long var0) throws java.lang.InterruptedException { }
	public final void join(long var0, int var1) throws java.lang.InterruptedException { }
	/** @deprecated */
	@java.lang.Deprecated
	public final void resume() { }
	public void run() { }
	public void setContextClassLoader(java.lang.ClassLoader var0) { }
	public final void setDaemon(boolean var0) { }
	public static void setDefaultUncaughtExceptionHandler(java.lang.Thread.UncaughtExceptionHandler var0) { }
	public final void setName(java.lang.String var0) { }
	public final void setPriority(int var0) { }
	public void setUncaughtExceptionHandler(java.lang.Thread.UncaughtExceptionHandler var0) { }
	public static void sleep(long var0) throws java.lang.InterruptedException { }
	public static void sleep(long var0, int var1) throws java.lang.InterruptedException { }
	public void start() { }
	/** @deprecated */
	@java.lang.Deprecated
	public final void stop() { }
	/** @deprecated */
	@java.lang.Deprecated
	public final void stop(java.lang.Throwable var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public final void suspend() { }
	public static void yield() { }
}
