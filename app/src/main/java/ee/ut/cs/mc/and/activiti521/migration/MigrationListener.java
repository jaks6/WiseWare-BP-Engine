package ee.ut.cs.mc.and.activiti521.migration;

import android.os.Handler;
import android.util.Log;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiActivityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

/**
 * Created by Jakob on 22.08.2016.
 */

public class MigrationListener implements ActivitiEventListener {
    private static final String TAG = MigrationListener.class.getName();
    private Handler engineHandler;
    boolean migrationRequested = true;

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
                        "ACT completed BP#%s, activitiName=%s; type=%s counter=%s",
                        event.getProcessInstanceId(), activitiEvent.getType(), activitiEvent.getActivityName(), counter
                ));

                handleActivityCompletedEvent(activitiEvent);
                break;
            default:
                Log.i(TAG, "Caught event: "+ event.getType() + "coutner="+ counter);

        }
    }

    private void handleActivityCompletedEvent(ActivitiActivityEvent event) {
        if (migrationRequested && counter == 3){
            //TODO check if migration really possible/feasible at this point in execution
            Log.i(TAG, "MIGRATION!");
            haltProcessExecution(event);
            createMigration(event);
        } else {
            counter++;
        }
    }

    private void createMigration(ActivitiActivityEvent event) {
        //TODO
        RuntimeService runtimeService = event.getEngineServices().getRuntimeService();
        RepositoryService repositoryService = event.getEngineServices().getRepositoryService();
        ManagementService managementService = event.getEngineServices().getManagementService();
        TaskService taskService = event.getEngineServices().getTaskService();

    }

    private void haltProcessExecution(ActivitiActivityEvent event) {
        RuntimeService runtimeService = event.getEngineServices().getRuntimeService();
        runtimeService.suspendProcessInstanceById(event.getProcessInstanceId());
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
