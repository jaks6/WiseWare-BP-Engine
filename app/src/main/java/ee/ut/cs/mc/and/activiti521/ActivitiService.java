package ee.ut.cs.mc.and.activiti521;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.ArrayList;
import java.util.List;

/** Background-running, starts the Activiti Engine on a new Thread when the service is started */
public class ActivitiService extends Service {
    private static final String TAG = ActivitiService.class.getName();

    EngineThread engineThread;
    // Binder given to clients
    private final IBinder mBinder = new ActivitiServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting ActivitiService");
        engineThread = new EngineThread();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Stopping ActivitiService");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ActivitiServiceBinder extends Binder {
        public Handler getEngineThreadHandler(){
            return engineThread.getHandler();
        }

        public EngineStatusDescriber getEngineStats(){
            return new EngineStatusDescriber(engineThread.getProcessEngine());
        }
    }

    class EngineStatusDescriber{
        public final List<ProcessInstance> runningInstances;
        public final List<Deployment> deployedInstances;

        public EngineStatusDescriber(ProcessEngine engine) {
            if (engine != null){
                this.runningInstances = engine.getRuntimeService().createProcessInstanceQuery().list();
                this.deployedInstances = engine.getRepositoryService().createDeploymentQuery().list();
            } else {
                this.runningInstances = new ArrayList<>();
                this.deployedInstances  = new ArrayList<>();
            }
        }

    }

}
