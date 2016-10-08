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
    public static int counter = 0;

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
        if ( ExperimentUtils.AUTO_EMIGRATE && counter >= ExperimentUtils.STEPS_BEFORE_EMIGRATION){
            if (ExperimentUtils.emigratingProcessInstanceList.contains(event.getProcessInstanceId())){
                Log.w(TAG, "Already emigrating process instance, skipping new emigration request. BPID="+event.getProcessInstanceId());
            } else {
                ExperimentUtils.startingMigration(event);
                haltProcessExecution(event);
                //TODO check if migration really possible/feasible at this point in execution
                counter++;
                createMigration(event);
            }
        } else {
            counter++;
        }
    }

    private void haltProcessExecution(ActivitiActivityEvent event) {
        experimentLog("Suspending Process Instance: "+ event.getProcessInstanceId());
        event.getEngineServices().getRuntimeService().suspendProcessInstanceById(event.getProcessInstanceId());
    }


    private void createMigration(ActivitiActivityEvent event) {
        experimentLog("Sending CAPTURE_INSTANCE_STATE msg");
        ExperimentUtils.timings.addSplit("Messaging Engine to Emigrate");
        engineHandler.obtainMessage(EngineThreadHandler.ENGINE_CAPTURE_INSTANCE_STATE, event.getProcessInstanceId())
                .sendToTarget();
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
