package ee.ut.cs.mc.and.activiti521.migration;

import android.os.Handler;
import android.util.Log;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiActivityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

/**
 * Created by Jakob on 22.08.2016.
 */

public class MigrationListener implements ActivitiEventListener {
    private static final String TAG = MigrationListener.class.getName();
    private Handler engineHandler;
    boolean migrationRequested = false;

    public MigrationListener(Handler mHandler) {
        engineHandler = mHandler;
    }

    @Override
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {
            case ACTIVITY_STARTED:
                Log.d(TAG, "Activity start event: " + event.getType());
            case ACTIVITY_COMPLETED:
                ActivitiActivityEvent activitiEvent = (ActivitiActivityEvent) event;

                Log.d(TAG, String.format(
                        "BP instance=%s, activitiName=%s",
                        event.getProcessInstanceId(), activitiEvent.getActivityName()
                ));

                handleActivityCompletedEvent(activitiEvent);
                break;
            default:
                Log.i(TAG, "Caught event: "+ event.getType());

        }
    }

    private void handleActivityCompletedEvent(ActivitiActivityEvent event) {
        if (migrationRequested){
            //TODO check if migration really possible/feasible at this point in execution
            Log.i(TAG, "MIGRATION!");
            haltProcessExeuction(event);

            createMigration();
        }
    }

    private void createMigration() {

    }

    private void haltProcessExeuction(ActivitiActivityEvent event) {
        RuntimeService runtimeService = event.getEngineServices().getRuntimeService();

        runtimeService.suspendProcessInstanceById(event.getProcessInstanceId());


    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
