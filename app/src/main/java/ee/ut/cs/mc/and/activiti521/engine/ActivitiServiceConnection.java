package ee.ut.cs.mc.and.activiti521.engine;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

abstract class ActivitiServiceConnection implements ServiceConnection {
    protected abstract void onActivitiServiceConnected(ActivitiService mService);
    protected abstract void onActivitiServiceDisconnected();
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        // We've bound to LocalService, cast the IBinder and get LocalService instance
        ActivitiService service = ((ActivitiService.ActivitiServiceBinder) iBinder).getService();
        onActivitiServiceConnected(service);
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
        onActivitiServiceDisconnected();
    }
}