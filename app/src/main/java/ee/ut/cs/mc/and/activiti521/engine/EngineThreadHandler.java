package ee.ut.cs.mc.and.activiti521.engine;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by J-Kool on 29/09/2016.
 */
public class EngineThreadHandler extends Handler {
    public static final int ENGINE_THREAD_MSG_DEPLOY_PROCESS = 1;
    public static final int ENGINE_THREAD_MSG_RUN_PROCESS = 2;
    public static final int ENGINE_CAPTURE_INSTANCE_STATE = 3;
    public static final int ENGINE_LOAD_STATE_TO_DB = 4;
    private final EngineThread mEngineThread;

    public EngineThreadHandler(Looper looper, EngineThread engineThread) {
        super(looper);
        mEngineThread = engineThread;
    }
    public void handleMessage(Message msg) {
        if (!mEngineThread.isEngineInitialized()) return;
        int command = msg.what;

        switch (command){
            case ENGINE_THREAD_MSG_DEPLOY_PROCESS:
                mEngineThread.deployProcess();
                break;
            case ENGINE_THREAD_MSG_RUN_PROCESS:
                mEngineThread.startProcess();
                break;
            case ENGINE_CAPTURE_INSTANCE_STATE:
                mEngineThread.emigrateProcess((String)msg.obj);
                break;
            case ENGINE_LOAD_STATE_TO_DB:
                mEngineThread.immigrateProcess();
                break;
        }
    }
}
