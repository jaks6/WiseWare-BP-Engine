package ee.ut.cs.mc.and.activiti521;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import static android.R.id.message;

/**
 * Created by Jakob on 21.08.2016.
 *
 * This class has two responsibilitites:
 * Starts/Stops the service which launches the Engine Thread.
 * Binds to the Engine Thread and mediates messages to it
 */
public class EngineThreadCommunicator {

    private static final String TAG = EngineThreadCommunicator.class.getName();
    private Handler mActivityHandler;
    private Handler mEngineHandler;
    private Context mContext;
    ActivitiService.ActivitiServiceBinder binder;
    private boolean mBound = false;

    public EngineThreadCommunicator(Context mContext) {
        this.mContext = mContext;
    }

    public boolean sendMessage(int msg) {
        if (! mBound) {
            Toast.makeText(mContext, "Not bound to service. is the service running?", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            sendMessageToHandler(msg);
            return true;
        }
    }

    private void sendMessageToHandler(int msg) {
        Message.obtain(
                mEngineHandler,
                0,//not used here
                msg,
                0 //not used here
        ).sendToTarget();
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
    public void bindToService(){
        Intent intent = new Intent(mContext, ActivitiService.class);
        mContext.bindService(intent, mConnection, 0);
    }


    public void unbindFromService(){
        mContext.unbindService(mConnection);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            binder = (ActivitiService.ActivitiServiceBinder) service;
            mEngineHandler = binder.getEngineThreadHandler();
            mBound = true;

            Log.d(TAG, "Bound to service");
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void close() {
        if (mBound) {
            unbindFromService();
        }
    }

    ActivitiService.EngineStatusDescriber getStatusUpdate(){
        if (mBound){
            return binder.getEngineStats();
        }
        else {
            Log.e(TAG, "not bound to service!");
            return null;
        }
    }

    public boolean isBound() {
        return mBound;
    }
}
