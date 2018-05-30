/*    */ package com.android.internal.os;
/*    */ 
/*    */

import android.os.IBinder;
import android.os.SystemClock;
import android.util.EventLog;

import java.lang.ref.WeakReference;

/*    */
/*    */
/*    */

/*    */
/*    */ public class BinderInternal
/*    */ {
/* 38 */   static WeakReference<GcWatcher> mGcWatcher = new WeakReference(new GcWatcher());
/*    */   static long mLastGcTime;
/*    */ 
/*    */   public static final native void joinThreadPool();
/*    */ 
/*    */   public static long getLastGcTime()
/*    */   {
/* 68 */     return mLastGcTime;
/*    */   }
/*    */ 
/*    */   public static final native IBinder getContextObject();
/*    */ 
/*    */   public static final native void disableBackgroundScheduling(boolean paramBoolean);
/*    */ 
/*    */   static final native void handleGc();
/*    */ 
/*    */   public static void forceGc(String reason)
/*    */   {
/* 88 */     EventLog.writeEvent(2741, reason);
/* 89 */     Runtime.getRuntime().gc();
/*    */   }
/*    */ 
/*    */   static void forceBinderGc() {
/* 93 */     forceGc("Binder");
/*    */   }
/*    */ 
/*    */   static final class GcWatcher
/*    */   {
/*    */     protected void finalize()
/*    */       throws Throwable
/*    */     {
/* 45 */       BinderInternal.handleGc();
/* 46 */       BinderInternal.mLastGcTime = SystemClock.uptimeMillis();
/* 47 */       BinderInternal.mGcWatcher = new WeakReference(new GcWatcher());
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\MyProject\trunk\Andriod\libs\SeriaSDK.jar
 * Qualified Name:     com.android.internal.os.BinderInternal
 * JD-Core Version:    0.5.3
 */