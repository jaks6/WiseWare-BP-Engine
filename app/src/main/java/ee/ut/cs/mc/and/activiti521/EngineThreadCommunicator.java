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
 * Binds to the Engine Thread and mediates messages to it
 */

public class EngineThreadCommunicator {

    private static final String TAG = EngineThreadCommunicator.class.getName();
    private Handler mEngineHandler;
    private Context mContext;
    private boolean mBound = false;


    public EngineThreadCommunicator(Context mContext) {
        this.mContext = mContext;
    }



    public boolean sendMessage(int msg) {
        if (! mBound) {
            Toast.makeText(mContext, "Not bound to service. attempting to bind..", Toast.LENGTH_SHORT).show();
            bindToService();
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

    public void bindToService(){
        Intent intent = new Intent(mContext, ActivitiService.class);
        mContext.bindService(intent, mConnection, Context.BIND_ABOVE_CLIENT);
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
            ActivitiService.ActivitiServiceBinder binder = (ActivitiService.ActivitiServiceBinder) service;
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
}
