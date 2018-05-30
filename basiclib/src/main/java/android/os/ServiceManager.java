/*     */ package android.os;
/*     */ 
/*     */

import android.util.Log;

import com.android.internal.os.BinderInternal;

import java.util.HashMap;
import java.util.Map;

/*     */
/*     */
/*     */

/*     */
/*     */ public final class ServiceManager
/*     */ {
/*     */   private static final String TAG = "ServiceManager";
/*     */   private static IServiceManager sServiceManager;
/*  31 */   private static HashMap<String, IBinder> sCache = new HashMap();
/*     */ 
/*     */   private static IServiceManager getIServiceManager() {
/*  34 */     if (sServiceManager != null) {
/*  35 */       return sServiceManager;
/*     */     }
/*     */ 
/*  39 */     sServiceManager = ServiceManagerNative.asInterface(BinderInternal.getContextObject());
/*  40 */     return sServiceManager;
/*     */   }
/*     */ 
/*     */   public static IBinder getService(String name)
/*     */   {
/*     */     try
/*     */     {
/*  51 */       IBinder service = (IBinder)sCache.get(name);
/*  52 */       if (service != null) {
/*  53 */         return service;
/*     */       }
/*  55 */       return getIServiceManager().getService(name);
/*     */     }
/*     */     catch (RemoteException e) {
/*  58 */       Log.e("ServiceManager", "error in getService", e);
/*     */     }
/*  60 */     return null;
/*     */   }
/*     */ 
/*     */   public static void addService(String name, IBinder service)
/*     */   {
/*     */     try
/*     */     {
/*  72 */       getIServiceManager().addService(name, service, false);
/*     */     } catch (RemoteException e) {
/*  74 */       Log.e("ServiceManager", "error in addService", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void addService(String name, IBinder service, boolean allowIsolated)
/*     */   {
/*     */     try
/*     */     {
/*  89 */       getIServiceManager().addService(name, service, allowIsolated);
/*     */     } catch (RemoteException e) {
/*  91 */       Log.e("ServiceManager", "error in addService", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static IBinder checkService(String name)
/*     */   {
/*     */     try
/*     */     {
/* 101 */       IBinder service = (IBinder)sCache.get(name);
/* 102 */       if (service != null) {
/* 103 */         return service;
/*     */       }
/* 105 */       return getIServiceManager().checkService(name);
/*     */     }
/*     */     catch (RemoteException e) {
/* 108 */       Log.e("ServiceManager", "error in checkService", e); }
/* 109 */     return null;
/*     */   }
/*     */ 
/*     */   public static String[] listServices()
/*     */     throws RemoteException
/*     */   {
/*     */     try
/*     */     {
/* 118 */       return getIServiceManager().listServices();
/*     */     } catch (RemoteException e) {
/* 120 */       Log.e("ServiceManager", "error in listServices", e); }
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   public static void initServiceCache(Map<String, IBinder> cache)
/*     */   {
/* 134 */     if (sCache.size() != 0) {
/* 135 */       throw new IllegalStateException("setServiceCache may only be called once");
/*     */     }
/* 137 */     sCache.putAll(cache);
/*     */   }
/*     */ }

/* Location:           E:\MyProject\trunk\Andriod\libs\SeriaSDK.jar
 * Qualified Name:     android.os.ServiceManager
 * JD-Core Version:    0.5.3
 */