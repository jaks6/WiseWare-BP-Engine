package ee.ut.cs.mc.and.activiti521;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Jakob on 21.08.2016.
 */

public class Util {

    public static boolean isServiceRunning(Context context, Class c) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (c.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
