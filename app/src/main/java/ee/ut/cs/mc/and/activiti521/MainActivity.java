package ee.ut.cs.mc.and.activiti521;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import ee.ut.cs.mc.and.activiti521.engine.ActivitiService;
import ee.ut.cs.mc.and.activiti521.engine.ActivitiServiceManager;
import ee.ut.cs.mc.and.activiti521.engine.EngineStatusDescriber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MSG_PROCESS_INSTANCE_COUNT_UPDATE = 1;

    Handler mHandler;

    private ActivitiServiceManager activitiManager;

    Switch serviceSwitch;
    TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        statusTextView = (TextView) findViewById(R.id.textView_serviceStatus);
        serviceSwitch = (Switch) findViewById(R.id.switch2);

        statusTextView.setText("Initializing...");

        //Start Activiti Service, create client to interact with it
        activitiManager = new ActivitiServiceManager(this);
        activitiManager.startService();
        activitiManager.bindToService();

        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_PROCESS_INSTANCE_COUNT_UPDATE:
                        updateProcessInstanceCount(msg.arg1);
                        break;
                    default:
                        Log.i(TAG, msg.toString());
                }
            }
        };

        setUpPeriodicEngineStatsUpdates();

        Log.i(TAG, "Initialized MainActivity on Thread="+Thread.currentThread().getId());
    }

    private void setUpPeriodicEngineStatsUpdates() {
        //Defines the behaviour on UI thread when an @EngineStatusDescriber is received
        final HandlerCallback<EngineStatusDescriber> engineStatsCallback =
                new HandlerCallback<EngineStatusDescriber>(mHandler){
            @Override
            protected void handle(EngineStatusDescriber stats) {
                statusTextView.setText(String.format("Running instance count:\t%s\nProcess definition count:\t%s",
                        stats.runningInstances.size(),
                        stats.processDefinitions.size()));
            }
        };

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activitiManager.isBound()) {
                    activitiManager.getService().getEngineStatus(engineStatsCallback);
                }
                mHandler.postDelayed(this, 3000);
            }
        }, 1000);
    }

    private void updateProcessInstanceCount(int runningInstanceCount) {
        statusTextView.setText("Running instances: " + runningInstanceCount);
    }

    @Override
    protected void onResume() {
        serviceSwitch.setChecked(Util.isServiceRunning(this, ActivitiService.class));
        serviceSwitch.setOnCheckedChangeListener(new ServiceSwitchChangeListener());
        super.onResume();
    }

    @Override
    protected void onPause() {
        serviceSwitch.setOnCheckedChangeListener(null);
//        activitiManager.close();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private class ServiceSwitchChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            if (isChecked) {
                activitiManager.startService();
            } else {
                activitiManager.stopService();
            }
        }
    }

    public void deployButtonClicked(View v) {
        Log.i(TAG, "Deploy button clicked.");
        activitiManager.getService().deployProcess(ExperimentUtils.PROCESS_RESOURCE_NAME_WISEWARE);
    }

    public void runButtonClicked(View v) {
        Log.i(TAG, "Run button clicked.");
        activitiManager.getService().startProcess(ExperimentUtils.PROCESS_KEY_WISEWARE);
    }
}
