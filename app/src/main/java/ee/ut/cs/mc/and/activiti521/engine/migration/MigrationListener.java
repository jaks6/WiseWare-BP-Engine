package ee.ut.cs.mc.and.activiti521.engine.migration;

import android.os.Handler;
import android.util.Log;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiActivityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

import ee.ut.cs.mc.and.activiti521.ExperimentUtils;
import ee.ut.cs.mc.and.activiti521.engine.EngineThreadHandler;

import static ee.ut.cs.mc.and.activiti521.ExperimentUtils.experimentLog;

/**
 * Created by Jakob on 22.08.2016.
 */

public class MigrationListener implements ActivitiEventListener {
    private static final String TAG = MigrationListener.class.getName();

    private Handler engineHandler;

    //temporary counter which is used to trigger migration
    int counter = 0;

    public MigrationListener(Handler mHandler) {
        engineHandler = mHandler;
    }

    @Override
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {
            case ACTIVITY_STARTED:
                Log.d(TAG, "Activity start event: " + event.getType());
                break;
            case ACTIVITY_COMPLETED:
                ActivitiActivityEvent activitiEvent = (ActivitiActivityEvent) event;

                Log.d(TAG, String.format(
                        "ACT completed BP#%s, type=%s; \tactivitiName=%s \tcounter=%s",
                        event.getProcessInstanceId(), activitiEvent.getType(), activitiEvent.getActivityName(), counter
                ));

                handleActivityCompletedEvent(activitiEvent);
                break;
            default:
                Log.v(TAG, "Caught event: "+ event.getType() + " \t\t\tcounter="+ counter);

        }
    }

    private void handleActivityCompletedEvent(ActivitiActivityEvent event) {
        if ( ExperimentUtils.AUTO_EMIGRATE && counter == ExperimentUtils.STEPS_BEFORE_EMIGRATION){
            //TODO check if migration really possible/feasible at this point in execution
            counter++;
            experimentLog("Migration start");
            haltProcessExecution(event);
            createMigration(event);
        } else {
            counter++;
        }
    }



    private void createMigration(ActivitiActivityEvent event) {
        experimentLog("Sending CAPTURE_INSTANCE_STATE msg");
        engineHandler.obtainMessage(0, EngineThreadHandler.ENGINE_CAPTURE_INSTANCE_STATE, 0, event.getProcessInstanceId())
                .sendToTarget();
    }

    private void haltProcessExecution(ActivitiActivityEvent event) {
        experimentLog("Suspending process instance");
        RuntimeService runtimeService = event.getEngineServices().getRuntimeService();
        runtimeService.suspendProcessInstanceById(event.getProcessInstanceId());
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
