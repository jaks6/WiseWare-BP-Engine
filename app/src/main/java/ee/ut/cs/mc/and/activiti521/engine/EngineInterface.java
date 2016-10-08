package ee.ut.cs.mc.and.activiti521.engine;

/**
 * Created by J-Kool on 30/09/2016.
 */
public interface EngineInterface {

    void startProcess(String processId);
    void deployProcess(String processKey);
    void getEngineStatus(RunnableListener callback);

    interface RunnableListener<T> {
        void onResult(T arg);
    }

}
