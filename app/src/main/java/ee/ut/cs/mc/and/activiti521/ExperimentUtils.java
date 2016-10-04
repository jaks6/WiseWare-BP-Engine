package ee.ut.cs.mc.and.activiti521;

import android.os.Handler;
import android.util.Log;

import ee.ut.cs.mc.and.activiti521.engine.EngineThreadHandler;

/**
 * Created by J-Kool on 28/09/2016.
 *
 * This class is meant to be used for logging various
 * activities as part of experiments
 * and their performance.
 */
public class ExperimentUtils {

    private static final String TAG = ExperimentUtils.class.getName();

    public static void experimentLog(String msg){
        Log.i(TAG, "[WW] "+msg);
    }

    public static final String PROCESS_RESOURCE_NAME_WISEWARE = "GoodsMonitoring.bpmn";
    public static final String PROCESS_KEY = "WiseWareProcess";

    public static final String PROCESS_RESOURCE_NAME_TODO = "ActivitiTodoProcess.bpmn";
//    public static final String PROCESS_KEY = "myProcess";

    public static final boolean AUTO_DEPLOY_PROCESS = true;
    public static final boolean AUTO_START_PROCESS = false;

    // Immigration (loading process from outside)
    public static final boolean AUTO_IMMIGRATE = false;
    public static final String IMMIGRATION_PROC_INST_ID = "4";

    // Emigration (taking process from Engine and serializing it to file
    public static final boolean AUTO_EMIGRATE = false;
    public static final int STEPS_BEFORE_EMIGRATION = 7; //How many tasks to finish before triggering emigration
    //5 emigrates after parallel join

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
}
