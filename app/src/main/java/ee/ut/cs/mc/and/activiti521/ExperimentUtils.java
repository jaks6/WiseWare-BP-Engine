package ee.ut.cs.mc.and.activiti521;

import android.os.Handler;
import android.util.Log;
import android.util.TimingLogger;

import org.activiti.engine.delegate.event.ActivitiActivityEvent;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import ee.ut.cs.mc.and.activiti521.engine.EngineThread;
import ee.ut.cs.mc.and.activiti521.engine.EngineThreadHandler;
import ee.ut.cs.mc.and.activiti521.engine.migration.MigrationListener;

/**
 * Created by J-Kool on 28/09/2016.
 *
 * This class is meant to be used for logging various
 * activities as part of experiments
 * and their performance.
 */
public class ExperimentUtils {

    private static final String TAG = ExperimentUtils.class.getSimpleName();
    public static final int NO_OF_INSTANCES_TO_RUN = 1;
    private static final String TIMER_TAG = "TimerTag";


    public static TimingLogger timings;

    public static void experimentLog(String msg){
        Log.i(TAG, "[WW] "+msg);
    }

    public static final String PROCESS_RESOURCE_NAME_WISEWARE = "GoodsMonitoring.bpmn";
    public static final String PROCESS_KEY_WISEWARE = "WiseWareProcess";

    public static final String PROCESS_RESOURCE_NAME_TODO = "ActivitiTodoProcess.bpmn";
    public static final String PROCESS_KEY_TODO = "myProcess";

    public static final boolean AUTO_DEPLOY_PROCESS = true;
    public static final boolean AUTO_START_PROCESS = false;

    // Immigration (loading process from outside)
    public static final boolean AUTO_IMMIGRATE = false;
    public static final String IMMIGRATION_PROC_INST_ID = "4";

    public static final boolean DELETE_FILES_ON_BOOT = false;

    // Emigration (taking process from Engine and serializing it to file
    public static final boolean AUTO_EMIGRATE = false;
    public static final int STEPS_BEFORE_EMIGRATION = NO_OF_INSTANCES_TO_RUN * 5; //How many tasks to finish before triggering emigration
    //5 emigrates after parallel join




    public static List<String> emigratingProcessInstanceList = new ArrayList<>();
    public static List<String> finishedMigrationsList = new ArrayList<>();

    //TODO remove this from code base
    public static void experimentImmigration(Handler handler) {
            Log.i(TAG, "Temporary emigration (in 3s)");
             try {
                Thread.sleep(3000);
                handler.obtainMessage(0, EngineThreadHandler.ENGINE_LOAD_STATE_TO_DB, 0).sendToTarget();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public static void runExperimentIfApplicable(EngineThread engine) {

        Log.i(TAG, "TIMERTAG ENABLE? "+ Log.isLoggable(TIMER_TAG, Log.VERBOSE));
        timings = new TimingLogger(TIMER_TAG, "timer");

        if (AUTO_DEPLOY_PROCESS){
            timings.addSplit("Starting Deployment");
            engine.deployProcess(PROCESS_RESOURCE_NAME_WISEWARE);
            timings.addSplit("Ended Deployment");
        }
        if (AUTO_START_PROCESS){
            for (int i = 0; i < NO_OF_INSTANCES_TO_RUN; i++) {
                timings.addSplit("Starting Run#"+i);
                engine.startProcess(PROCESS_KEY_WISEWARE);
            }
        }
        if (AUTO_IMMIGRATE) experimentImmigration(engine.getHandler());

        timings.dumpToLog();
        timings.reset();
    }

    public static void finishedMigration(String processInstanceId) {
        finishedMigration(new String[]{processInstanceId});
    }

    public static void startingMigration(ActivitiActivityEvent event) {
        experimentLog("Migration start");
        if (MigrationListener.counter == STEPS_BEFORE_EMIGRATION) timings.reset();
        ExperimentUtils.timings.addSplit("Migration start");
//        ExperimentUtils.emigratingProcessInstanceList.add(event.getProcessInstanceId());
    }

    public static void finishedMigration(String[] processInstanceIds) {
        experimentLog("Finished Migration");
        timings.addSplit("Finished Migration"+ StringUtils.join(processInstanceIds, ";"));
        timings.dumpToLog();
    }

    /** Get a list of running BP instance id's */
    public static String[] getListOfBPsToMigrate(ActivitiActivityEvent event) {
        List<ProcessInstance> instanceList = event.getEngineServices().getRuntimeService()
                .createProcessInstanceQuery().list();

        String[] instanceArray = new String[instanceList.size()];
        for (int i = 0; i < instanceList.size(); i++) {
            instanceArray[i] = instanceList.get(i).getProcessInstanceId();
        }

        return instanceArray;
    }
}
