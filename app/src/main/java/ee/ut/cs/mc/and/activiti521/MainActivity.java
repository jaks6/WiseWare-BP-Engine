package ee.ut.cs.mc.and.activiti521;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EngineThreadCommunicator engineCommunicator;

    Switch serviceSwitch;
    TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = (TextView) findViewById(R.id.textView_serviceStatus);
        serviceSwitch = (Switch) findViewById(R.id.switch1);

        serviceSwitch.setOnCheckedChangeListener(new ServiceSwitchChangeListener(this));
        engineCommunicator = new EngineThreadCommunicator(this);
    }

    @Override
    protected void onResume() {
        serviceSwitch.setChecked(Util.isServiceRunning(this, ActivitiService.class));
        super.onResume();
    }

    @Override
    protected void onPause() {
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
