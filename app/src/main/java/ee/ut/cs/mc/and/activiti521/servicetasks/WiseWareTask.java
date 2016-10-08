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
        Log.i(TAG, String.format("Starting Task %s\t BPinstance=%s\t ThreadID=%s",
                execution.getCurrentActivityName(),
                execution.getProcessInstanceId(),
                Thread.currentThread().getId()));
        Thread.sleep(1000);
        Log.i(TAG, "Finished Task " + execution.getCurrentActivityName());
    }
}
