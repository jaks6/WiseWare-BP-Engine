package ee.ut.cs.mc.and.activiti521;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                EngineRunner engineRunner = new EngineRunner();
                engineRunner.startEngine();
            }
        };



        Log.i(TAG, "Starting Engine Runner");
        logger.info("info Starting Engine Runner");
        logger.warn("warn Starting Engine Runner");
        logger.error("error Starting Engine Runner");

        new Thread(runnable).start();
    }
}
