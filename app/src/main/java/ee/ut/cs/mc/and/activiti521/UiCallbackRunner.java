package ee.ut.cs.mc.and.activiti521;

import android.app.Activity;


public abstract class UiCallbackRunner<T> implements RunnableListener<T> {
    protected abstract void uiHandle(T arg);
    Activity mActivity;

    public UiCallbackRunner(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onResult(final T arg) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uiHandle(arg);
            }
        });
    }
}

interface RunnableListener<T> {
    void onResult(T arg);
}