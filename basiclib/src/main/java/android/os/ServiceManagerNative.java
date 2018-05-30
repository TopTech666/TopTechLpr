/*     */ package android.os;
/*     */ 
/*     */ public abstract class ServiceManagerNative extends Binder
/*     */   implements IServiceManager
/*     */ {
/*     */   public static IServiceManager asInterface(IBinder obj)
/*     */   {
/*  35 */     if (obj == null) {
/*  36 */       return null;
/*     */     }
/*  38 */     IServiceManager in = (IServiceManager)obj.queryLocalInterface("android.os.IServiceManager");
/*     */ 
/*  40 */     if (in != null) {
/*  41 */       return in;
/*     */     }
/*     */ 
/*  44 */     return new ServiceManagerProxy(obj);
/*     */   }
/*     */ 
/*     */   public ServiceManagerNative()
/*     */   {
/*  49 */     attachInterface(this, "android.os.IServiceManager");
/*     */   }
/*     */ 
/*     */   public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
/*     */   {
/*     */     try
/*     */     {
/*     */       String name;
/*     */       IBinder service;
/*  55 */       switch (code)
/*     */       {
/*     */       case 1:
/*  57 */         data.enforceInterface("android.os.IServiceManager");
/*  58 */         name = data.readString();
/*  59 */         service = getService(name);
/*  60 */         reply.writeStrongBinder(service);
/*  61 */         return true;
/*     */       case 2:
/*  65 */         data.enforceInterface("android.os.IServiceManager");
/*  66 */         name = data.readString();
/*  67 */         service = checkService(name);
/*  68 */         reply.writeStrongBinder(service);
/*  69 */         return true;
/*     */       case 3:
/*  73 */         data.enforceInterface("android.os.IServiceManager");
/*  74 */         name = data.readString();
/*  75 */         service = data.readStrongBinder();
/*  76 */         boolean allowIsolated = data.readInt() != 0;
/*  77 */         addService(name, service, allowIsolated);
/*  78 */         return true;
/*     */       case 4:
/*  82 */         data.enforceInterface("android.os.IServiceManager");
/*  83 */         String[] list = listServices();
/*  84 */         reply.writeStringArray(list);
/*  85 */         return true;
/*     */       case 6:
/*  89 */         data.enforceInterface("android.os.IServiceManager");
/*  90 */         IPermissionController controller = IPermissionController.Stub.asInterface(data.readStrongBinder());
/*     */ 
/*  93 */         setPermissionController(controller);
/*  94 */         return true;
/*     */       case 5:
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (RemoteException e)
/*     */     {
/*     */     }
/*     */ 
/* 100 */     return false;
/*     */   }
/*     */ 
/*     */   public IBinder asBinder()
/*     */   {
/* 105 */     return this;
/*     */   }
/*     */ }

/* Location:           E:\MyProject\trunk\Andriod\libs\SeriaSDK.jar
 * Qualified Name:     android.os.ServiceManagerNative
 * JD-Core Version:    0.5.3
 */