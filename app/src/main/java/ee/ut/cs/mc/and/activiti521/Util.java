package ee.ut.cs.mc.and.activiti521;

import android.app.ActivityManager;
import android.content.Context;

import java.io.File;

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

    /** Delete directory and all files contained */
    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }


    public static final boolean PrintActivitiLogs = false;
}
