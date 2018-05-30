package android.os;

public abstract interface IServiceManager extends IInterface
{
  public static final String descriptor = "android.os.IServiceManager";
  public static final int GET_SERVICE_TRANSACTION = 1;
  public static final int CHECK_SERVICE_TRANSACTION = 2;
  public static final int ADD_SERVICE_TRANSACTION = 3;
  public static final int LIST_SERVICES_TRANSACTION = 4;
  public static final int CHECK_SERVICES_TRANSACTION = 5;
  public static final int SET_PERMISSION_CONTROLLER_TRANSACTION = 6;

  public abstract IBinder getService(String paramString)
    throws RemoteException;

  public abstract IBinder checkService(String paramString)
    throws RemoteException;

  public abstract void addService(String paramString, IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;

  public abstract String[] listServices()
    throws RemoteException;

  public abstract void setPermissionController(IPermissionController paramIPermissionController)
    throws RemoteException;
}

/* Location:           E:\MyProject\trunk\Andriod\libs\SeriaSDK.jar
 * Qualified Name:     android.os.IServiceManager
 * JD-Core Version:    0.5.3
 */