/*     */package android.os;

/*     */
/*     */import java.util.ArrayList;

/*     */
/*     */class ServiceManagerProxy
/*     */implements IServiceManager
/*     */{
	/*     */private IBinder mRemote;

	/*     */
	/*     */public ServiceManagerProxy(IBinder remote)
	/*     */{
		/* 111 */this.mRemote = remote;
		/*     */}

	/*     */
	/*     */public IBinder asBinder() {
		/* 115 */return this.mRemote;
		/*     */}

	/*     */
	/*     */public IBinder getService(String name) throws RemoteException {
		/* 119 */Parcel data = Parcel.obtain();
		/* 120 */Parcel reply = Parcel.obtain();
		/* 121 */data.writeInterfaceToken("android.os.IServiceManager");
		/* 122 */data.writeString(name);
		/* 123 */this.mRemote.transact(1, data, reply, 0);
		/* 124 */IBinder binder = reply.readStrongBinder();
		/* 125 */reply.recycle();
		/* 126 */data.recycle();
		/* 127 */return binder;
		/*     */}

	/*     */
	/*     */public IBinder checkService(String name) throws RemoteException {
		/* 131 */Parcel data = Parcel.obtain();
		/* 132 */Parcel reply = Parcel.obtain();
		/* 133 */data.writeInterfaceToken("android.os.IServiceManager");
		/* 134 */data.writeString(name);
		/* 135 */this.mRemote.transact(2, data, reply, 0);
		/* 136 */IBinder binder = reply.readStrongBinder();
		/* 137 */reply.recycle();
		/* 138 */data.recycle();
		/* 139 */return binder;
		/*     */}

	/*     */
	/*     */public void addService(String name, IBinder service,
                                    boolean allowIsolated) throws RemoteException
	/*     */{
		/* 144 */Parcel data = Parcel.obtain();
		/* 145 */Parcel reply = Parcel.obtain();
		/* 146 */data.writeInterfaceToken("android.os.IServiceManager");
		/* 147 */data.writeString(name);
		/* 148 */data.writeStrongBinder(service);
		/* 149 */data.writeInt((allowIsolated) ? 1 : 0);
		/* 150 */this.mRemote.transact(3, data, reply, 0);
		/* 151 */reply.recycle();
		/* 152 */data.recycle();
		/*     */}

	/*     */
	/*     */public String[] listServices() throws RemoteException {
		/* 156 */ArrayList services = new ArrayList();
		/* 157 */int n = 0;
		/*     */while (true) {
			/* 159 */Parcel data = Parcel.obtain();
			/* 160 */Parcel reply = Parcel.obtain();
			/* 161 */data.writeInterfaceToken("android.os.IServiceManager");
			/* 162 */data.writeInt(n);
			/* 163 */++n;
			/*     */try {
				/* 165 */boolean res = this.mRemote.transact(4, data, reply, 0);
				/* 166 */if (!(res)) {
					/* 167 */break;
					/*     */}
				/*     */
				/*     */}
			/*     */catch (RuntimeException e)
			/*     */{
				/* 173 */break;
				/*     */}
			/* 175 */services.add(reply.readString());
			/* 176 */reply.recycle();
			/* 177 */data.recycle();
			/*     */}
		/* 179 */String[] array = new String[services.size()];
		/* 180 */services.toArray(array);
		/* 181 */return array;
		/*     */}

	/*     */
	/*     */public void setPermissionController(IPermissionController controller)
			throws RemoteException
	/*     */{
		/* 186 */Parcel data = Parcel.obtain();
		/* 187 */Parcel reply = Parcel.obtain();
		/* 188 */data.writeInterfaceToken("android.os.IServiceManager");
		/* 189 */data.writeStrongBinder(controller.asBinder());
		/* 190 */this.mRemote.transact(6, data, reply, 0);
		/* 191 */reply.recycle();
		/* 192 */data.recycle();
		/*     */}
	/*     */
}

/*
 * Location: E:\MyProject\trunk\Andriod\libs\SeriaSDK.jar Qualified Name:
 * android.os.ServiceManagerProxy JD-Core Version: 0.5.3
 */