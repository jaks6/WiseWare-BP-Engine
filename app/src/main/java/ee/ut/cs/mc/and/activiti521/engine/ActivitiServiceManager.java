package ee.ut.cs.mc.and.activiti521.engine;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Handles Service start/stop, binding to service, and implements interface
 * for interacting with the service through the binding.
 */
public class ActivitiServiceManager implements EngineInterface {

    private static final String TAG = ActivitiServiceManager.class.getName();
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceBinderConnection();
    private ActivitiService.ActivitiServiceBinder binder;
    private boolean mBound;
    private Context mContext;
    public ActivitiServiceManager(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void startProcess(String processKey) {
        binder.startProcess(processKey);
    }

    @Override
    public void deployProcess(String resourceName) {
        binder.deployProcess(resourceName);
    }

    @Override
    public EngineStatusDescriber getEngineStatus() {
        return binder.getEngineStats();
    }


    /** Starts service and binds to it */
    public void startService(){
        Intent i = new Intent(mContext, ActivitiService.class);
        mContext.startService(i);
        bindToService();
    }

    public void stopService(){
        Intent i = new Intent(mContext, ActivitiService.class);
        mContext.stopService(i);
    }


    /** Binds to service with flag 0, meaning the bindService call does not try to start the
     *  service if not already running! The service must be previously started
     *  explicitly using startService()
     */
    private void bindToService(){
        Intent intent = new Intent(mContext, ActivitiService.class);
        mContext.bindService(intent, mConnection, 0);
    }
    public void unbindFromService(){
        mContext.unbindService(mConnection);
    }

    public ActivitiService.ActivitiServiceBinder getBinder() {
        return binder;
    }

    public boolean isBound() {
        return mBound;
    }

    private final class ServiceBinderConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            binder = (ActivitiService.ActivitiServiceBinder) iBinder;
            mBound = true;
            Log.d(TAG, "Bound to service");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Unbound from service");
            mBound = false;
        }
    }
}
