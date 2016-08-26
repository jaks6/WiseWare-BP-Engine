package ee.ut.cs.mc.and.activiti521;

import android.app.NotificationManager;
import android.content.Context;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MSG_PROCESS_INSTANCE_COUNT_UPDATE = 1;

    Handler mHandler;
    private EngineThreadCommunicator engineCommunicator;

    Switch serviceSwitch;
    TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler =  new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_PROCESS_INSTANCE_COUNT_UPDATE:
                        updateProcessInstanceCount(msg.arg1);
                        break;
                    default:
                        Log.i(TAG, msg.toString());
                }
            }
        };

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (engineCommunicator.isBound()) {
                    ActivitiService.EngineStatusDescriber stats = engineCommunicator.binder.getEngineStats();
                    statusTextView.setText(String.format("Running instance count: %s\nDeployed instance count: %s",
                            stats.runningInstances.size(),
                            stats.deployedInstances.size()));
                }
                mHandler.postDelayed(this, 1000);
            }

        }, 1000);

        statusTextView = (TextView) findViewById(R.id.textView_serviceStatus);
        serviceSwitch = (Switch) findViewById(R.id.switch1);


        engineCommunicator = new EngineThreadCommunicator(this);
        engineCommunicator.startService();
    }

    private void updateProcessInstanceCount(int runningInstanceCount) {
        statusTextView.setText("Running instances: " + runningInstanceCount);
    }

    @Override
    protected void onResume() {
        serviceSwitch.setChecked(Util.isServiceRunning(this, ActivitiService.class));
        serviceSwitch.setOnCheckedChangeListener(new ServiceSwitchChangeListener(this));
        super.onResume();
    }

    @Override
    protected void onPause() {
        serviceSwitch.setOnCheckedChangeListener(null);
        engineCommunicator.close();
        super.onStop();
    }

    private class ServiceSwitchChangeListener implements android.widget.CompoundButton.OnCheckedChangeListener{
        private Context context;
        ServiceSwitchChangeListener(Context ctx) {this.context = ctx;}
        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            Log.d(TAG, "switch state change, isChecked=" + isChecked);
            if (isChecked){
                engineCommunicator.startService();
            } else {
                engineCommunicator.stopService();
            }
        }
    }

    public void deployButtonClicked(View v){
        Log.i(TAG, "Deploy button clicked.");
        engineCommunicator.sendMessage(EngineThread.ENGINE_THREAD_MSG_DEPLOY_PROCESS);
    }
    public void runButtonClicked(View v){
        Log.i(TAG, "Run button clicked.");
        engineCommunicator.sendMessage(EngineThread.ENGINE_THREAD_MSG_RUN_PROCESS);
    }


}
