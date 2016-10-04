package ee.ut.cs.mc.and.activiti521.engine;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import ee.ut.cs.mc.and.activiti521.UiCallbackRunner;

/**
 * Handles Service start/stop, binding to service, provides pointer to service
 * when bound.
 */
public class ActivitiServiceManager {

    private static final String TAG = ActivitiServiceManager.class.getName();

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ActivitiServiceConnection() {
        @Override
        protected void onActivitiServiceConnected(ActivitiService service) {
            mService = service;
            mBound = true;
            Log.d(TAG, "Bound to service");
        }

        @Override
        protected void onActivitiServiceDisconnected() {
            mBound = false;
            Log.d(TAG, "Unbound from service");
        }
    };
    private boolean mBound;
    private Context mContext;
    private ActivitiService mService;
    public ActivitiServiceManager(Context mContext) {
        this.mContext = mContext;
    }

    /** Starts service and binds to it */
    public void startService(){
        Intent i = new Intent(mContext, ActivitiService.class);
        mContext.startService(i);
    }

    public void stopService(){
        Intent i = new Intent(mContext, ActivitiService.class);
        mContext.stopService(i);
    }


    /** Binds to service with flag 0, meaning the bindService call does not try to start the
     *  service if not already running! The service must be previously started
     *  explicitly using startService()
     */
    public void bindToService(){
        Intent intent = new Intent(mContext, ActivitiService.class);
        mContext.bindService(intent, mConnection, 0);
    }
    public void unbindFromService(){
        mContext.unbindService(mConnection);
    }

    public boolean isBound() {
        return mBound;
    }

    public ActivitiService getService() {
        return mService;
    }

}
