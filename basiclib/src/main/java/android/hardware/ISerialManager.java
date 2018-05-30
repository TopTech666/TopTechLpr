/*     */ package android.hardware;
/*     */ 
/*     */

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ public abstract interface ISerialManager extends IInterface
/*     */ {
/*     */   public abstract String[] getSerialPorts()
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void openSerialPort(String paramString)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void uart_write(String paramString)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract String uart_read()
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements ISerialManager
/*     */   {
/*     */     private static final String DESCRIPTOR = "android.hardware.ISerialManager";
/*     */     static final int TRANSACTION_getSerialPorts = 1;
/*     */     static final int TRANSACTION_openSerialPort = 2;
/*     */     static final int TRANSACTION_uart_write = 3;
/*     */     static final int TRANSACTION_uart_read = 4;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  16 */       attachInterface(this, "android.hardware.ISerialManager");
/*     */     }
/*     */ 
/*     */     public static ISerialManager asInterface(IBinder obj)
/*     */     {
/*  24 */       if (obj == null) {
/*  25 */         return null;
/*     */       }
/*  27 */       IInterface iin = obj.queryLocalInterface("android.hardware.ISerialManager");
/*  28 */       if ((iin != null) && (iin instanceof ISerialManager)) {
/*  29 */         return ((ISerialManager)iin);
/*     */       }
/*  31 */       return new Proxy(obj);
/*     */     }
/*     */ 
/*     */     public IBinder asBinder() {
/*  35 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
/*     */       throws RemoteException
/*     */     {
/*     */       String _arg0;
/*  39 */       switch (code)
/*     */       {
/*     */       case 1598968902:
/*  43 */         reply.writeString("android.hardware.ISerialManager");
/*  44 */         return true;
/*     */       case 1:
/*  48 */         data.enforceInterface("android.hardware.ISerialManager");
/*  49 */         String[] _result = getSerialPorts();
/*  50 */         reply.writeNoException();
/*  51 */         reply.writeStringArray(_result);
/*  52 */         return true;
/*     */       case 2:
/*  56 */         data.enforceInterface("android.hardware.ISerialManager");
/*     */ 
/*  58 */         _arg0 = data.readString();
/*  59 */         openSerialPort(_arg0);
/*  60 */         reply.writeNoException();
/*  61 */         return true;
/*     */       case 3:
/*  65 */         data.enforceInterface("android.hardware.ISerialManager");
/*     */ 
/*  67 */         _arg0 = data.readString();
/*  68 */         uart_write(_arg0);
/*  69 */         reply.writeNoException();
/*  70 */         return true;
/*     */       case 4:
/*  74 */         data.enforceInterface("android.hardware.ISerialManager");
/*  75 */         String _result1 = uart_read();
/*  76 */         reply.writeNoException();
/*  77 */         reply.writeString(_result1);
/*  78 */         return true;
/*     */       }
/*     */ 
/*  81 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */ 
/*     */     private static class Proxy implements ISerialManager {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/*  88 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/*  92 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/*  96 */         return "android.hardware.ISerialManager";
/*     */       }
/*     */ 
/*     */       public String[] getSerialPorts() throws RemoteException
/*     */       {
/* 101 */         Parcel _data = Parcel.obtain();
/* 102 */         Parcel _reply = Parcel.obtain();
/*     */         String[] _result;
/*     */         try {
/* 105 */           _data.writeInterfaceToken("android.hardware.ISerialManager");
/* 106 */           this.mRemote.transact(1, _data, _reply, 0);
/* 107 */           _reply.readException();
/* 108 */           _result = _reply.createStringArray();
/*     */         }
/*     */         finally {
/* 111 */           _reply.recycle();
/* 112 */           _data.recycle();
/*     */         }
/* 114 */         return _result;
/*     */       }
/*     */ 
/*     */       public void openSerialPort(String name) throws RemoteException
/*     */       {
/* 119 */         Parcel _data = Parcel.obtain();
/* 120 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 122 */           _data.writeInterfaceToken("android.hardware.ISerialManager");
/* 123 */           _data.writeString(name);
/* 124 */           this.mRemote.transact(2, _data, _reply, 0);
/* 125 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 128 */           _reply.recycle();
/* 129 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void uart_write(String value) throws RemoteException {
/* 134 */         Parcel _data = Parcel.obtain();
/* 135 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 137 */           _data.writeInterfaceToken("android.hardware.ISerialManager");
/* 138 */           _data.writeString(value);
/* 139 */           this.mRemote.transact(3, _data, _reply, 0);
/* 140 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 143 */           _reply.recycle();
/* 144 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public String uart_read() throws RemoteException {
/* 149 */         Parcel _data = Parcel.obtain();
/* 150 */         Parcel _reply = Parcel.obtain();
/*     */         String _result;
/*     */         try {
/* 153 */           _data.writeInterfaceToken("android.hardware.ISerialManager");
/* 154 */           this.mRemote.transact(4, _data, _reply, 0);
/* 155 */           _reply.readException();
/* 156 */           _result = _reply.readString();
/*     */         }
/*     */         finally {
/* 159 */           _reply.recycle();
/* 160 */           _data.recycle();
/*     */         }
/* 162 */         return _result;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\MyProject\trunk\Andriod\libs\SeriaSDK.jar
 * Qualified Name:     android.hardware.ISerialManager
 * JD-Core Version:    0.5.3
 */