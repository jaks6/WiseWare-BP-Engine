package ee.ut.cs.mc.and.activiti521;

import android.os.Handler;

import ee.ut.cs.mc.and.activiti521.engine.EngineInterface;


public abstract class HandlerCallback<T> implements EngineInterface.RunnableListener<T> {
    private final Handler uiHandler;

    protected abstract void handle(T arg);

    public HandlerCallback(Handler handler) {
        uiHandler = handler;
    }

    @Override
    public void onResult(final T arg) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                handle(arg);
            }
        });
    }
}

