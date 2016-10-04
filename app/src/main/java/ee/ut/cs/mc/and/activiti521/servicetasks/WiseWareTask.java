package ee.ut.cs.mc.and.activiti521.servicetasks;

import android.util.Log;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * Created by J-Kool on 28/09/2016.
 */
public class WiseWareTask  implements JavaDelegate {
    private static final String TAG = WiseWareTask.class.getName();

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Log.i(TAG, "Starting Task, sleeping 3s, Activity Name ="+ execution.getCurrentActivityName());
        Thread.sleep(3000);
        Log.i(TAG, "Finished Task " + execution.getCurrentActivityName());
    }
}
