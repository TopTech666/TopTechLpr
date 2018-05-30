/*    */ package android.os;
/*    */ 
/*    */ public abstract interface IPermissionController extends IInterface
/*    */ {
/*    */   public abstract boolean checkPermission(String paramString, int paramInt1, int paramInt2)
/*    */     throws RemoteException;
/*    */ 
/*    */   public static abstract class Stub extends Binder
/*    */     implements IPermissionController
/*    */   {
/*    */     private static final String DESCRIPTOR = "android.os.IPermissionController";
/*    */     static final int TRANSACTION_checkPermission = 1;
/*    */ 
/*    */     public Stub()
/*    */     {
/* 16 */       attachInterface(this, "android.os.IPermissionController");
/*    */     }
/*    */ 
/*    */     public static IPermissionController asInterface(IBinder obj)
/*    */     {
/* 24 */       if (obj == null) {
/* 25 */         return null;
/*    */       }
/* 27 */       IInterface iin = obj.queryLocalInterface("android.os.IPermissionController");
/* 28 */       if ((iin != null) && (iin instanceof IPermissionController)) {
/* 29 */         return ((IPermissionController)iin);
/*    */       }
/* 31 */       return new Proxy(obj);
/*    */     }
/*    */ 
/*    */     public IBinder asBinder() {
/* 35 */       return this;
/*    */     }
/*    */ 
/*    */     public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
/* 39 */       switch (code)
/*    */       {
/*    */       case 1598968902:
/* 43 */         reply.writeString("android.os.IPermissionController");
/* 44 */         return true;
/*    */       case 1:
/* 48 */         data.enforceInterface("android.os.IPermissionController");
/*    */ 
/* 50 */         String _arg0 = data.readString();
/*    */ 
/* 52 */         int _arg1 = data.readInt();
/*    */ 
/* 54 */         int _arg2 = data.readInt();
/* 55 */         boolean _result = checkPermission(_arg0, _arg1, _arg2);
/* 56 */         reply.writeNoException();
/* 57 */         reply.writeInt((_result) ? 1 : 0);
/* 58 */         return true;
/*    */       }
/*    */ 
/* 61 */       return super.onTransact(code, data, reply, flags);
/*    */     }
/*    */ 
/*    */     private static class Proxy implements IPermissionController {
/*    */       private IBinder mRemote;
/*    */ 
/*    */       Proxy(IBinder remote) {
/* 68 */         this.mRemote = remote;
/*    */       }
/*    */ 
/*    */       public IBinder asBinder() {
/* 72 */         return this.mRemote;
/*    */       }
/*    */ 
/*    */       public String getInterfaceDescriptor() {
/* 76 */         return "android.os.IPermissionController";
/*    */       }
/*    */ 
/*    */       public boolean checkPermission(String permission, int pid, int uid) throws RemoteException {
/* 80 */         Parcel _data = Parcel.obtain();
/* 81 */         Parcel _reply = Parcel.obtain();
/*    */         boolean _result;
/*    */         try {
/* 84 */           _data.writeInterfaceToken("android.os.IPermissionController");
/* 85 */           _data.writeString(permission);
/* 86 */           _data.writeInt(pid);
/* 87 */           _data.writeInt(uid);
/* 88 */           this.mRemote.transact(1, _data, _reply, 0);
/* 89 */           _reply.readException();
/* 90 */           _result = 0 != _reply.readInt();
/*    */         }
/*    */         finally {
/* 93 */           _reply.recycle();
/* 94 */           _data.recycle();
/*    */         }
/* 96 */         return _result;
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\MyProject\trunk\Andriod\libs\SeriaSDK.jar
 * Qualified Name:     android.os.IPermissionController
 * JD-Core Version:    0.5.3
 */