package ee.ut.cs.mc.and.activiti521.engine;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import ee.ut.cs.mc.and.activiti521.ExperimentUtils;

/** Background-running, starts the Activiti Engine on a new Thread when the service is started */
public class ActivitiService extends Service {
    private static final String TAG = ActivitiService.class.getName();

    EngineThread engineThread;
    // Binder given to clients
    private final IBinder mBinder = new ActivitiServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting ActivitiService");
        if (engineThread == null)
            engineThread = new EngineThread();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Stopping ActivitiService");
        //TODO nice Thread killing
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ActivitiServiceBinder extends Binder {

        public EngineStatusDescriber getEngineStats(){
            return new EngineStatusDescriber(engineThread.getProcessEngine());
        }

        public void startProcess(String processId){
            engineThread.getHandler().obtainMessage(EngineThreadHandler.ENGINE_THREAD_MSG_RUN_PROCESS).sendToTarget();
        }


        public void deployProcess(String processKey){
            engineThread.getHandler().obtainMessage(EngineThreadHandler.ENGINE_THREAD_MSG_DEPLOY_PROCESS).sendToTarget();
        }
    }


}
