package ee.ut.cs.mc.and.activiti521.engine;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import ee.ut.cs.mc.and.activiti521.HandlerCallback;

/** Background-running, starts the Activiti Engine on a new Thread when the service is started */
public class ActivitiService extends Service implements EngineInterface {
    private static final String TAG = ActivitiService.class.getName();

    EngineThread engineThread;
    // Binder given to clients
    private final ActivitiServiceBinder mBinder = new ActivitiServiceBinder();


    public void getEngineStatus(final RunnableListener callback){
        engineThread.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        EngineStatusDescriber describer =
                                new EngineStatusDescriber(engineThread.getProcessEngine());
                        callback.onResult(describer);
                    }
                });
    }

    /** Fetching the result using a callback that will run on the UI thread*/
    public void getEngineStatus(final HandlerCallback<EngineStatusDescriber> callback){
        engineThread.getHandler().post(
            new Runnable() {
                @Override
                public void run() {
                    EngineStatusDescriber describer =
                            new EngineStatusDescriber(engineThread.getProcessEngine());
                    callback.onResult(describer);
            }
        });
    }


    public void startProcess(String processKey){
        engineThread.getHandler().obtainMessage(
                EngineThreadHandler.ENGINE_THREAD_MSG_RUN_PROCESS,
                processKey
        ).sendToTarget();
    }


    public void deployProcess(String resourceName){
        engineThread.getHandler().obtainMessage(
                EngineThreadHandler.ENGINE_THREAD_MSG_DEPLOY_PROCESS,
                resourceName
        ).sendToTarget();
    }


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
        ActivitiService getService(){
            return ActivitiService.this;
        }
    }




}
