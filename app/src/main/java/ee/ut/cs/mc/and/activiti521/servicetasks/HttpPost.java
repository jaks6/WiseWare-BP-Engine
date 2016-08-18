package ee.ut.cs.mc.and.activiti521.servicetasks;

import android.util.Log;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * Created by Jakob on 16.06.2016.
 */
public class HttpPost implements JavaDelegate {
    private static final String TAG = HttpPost.class.getName();

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Log.i(TAG, "HttpPost");
    }
}
