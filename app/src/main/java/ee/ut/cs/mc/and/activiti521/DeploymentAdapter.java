package ee.ut.cs.mc.and.activiti521;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.activiti.engine.repository.ProcessDefinition;

import ee.ut.cs.mc.and.activiti521.engine.ActivitiServiceManager;
import ee.ut.cs.mc.and.activiti521.engine.EngineStatusDescriber;

/**
 * Created by J-Kool on 04/10/2016.
 */
public class DeploymentAdapter extends ArrayAdapter<ProcessDefinition> {


    private static final String TAG = DeploymentAdapter.class.getName();
    private ProgressBar mProgressbar;
    private ActivitiServiceManager mActivitiManager;

    Handler mHandler;


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            view = inflater.inflate(R.layout.deployment_listitem, parent, false);
        }

        Button runButton = (Button) view.findViewById(R.id.btn_run);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Starting process: "+ getItem(position).getName(), Toast.LENGTH_LONG).show();
                mActivitiManager.getService().startProcess(getItem(position).getKey());
            }
        });


        TextView deploymentId = (TextView) view.findViewById(R.id.tv_key);
        deploymentId.setText(getItem(position).getKey());

        return view;
    }



    public DeploymentAdapter(Context context, View view) {
        super(context, R.layout.deployment_listitem);
        mHandler = new Handler(Looper.getMainLooper());

        mProgressbar = (ProgressBar) view.findViewById(R.id.loadingCircle);


        mActivitiManager = new ActivitiServiceManager(getContext());
        mActivitiManager.startService();
        mActivitiManager.bindToService();
        setUpDynamicBehaviour();
    }

    private void setUpDynamicBehaviour() {

        final HandlerCallback<EngineStatusDescriber> engineStatsCallback =
                new HandlerCallback<EngineStatusDescriber>(mHandler) {
            @Override
            protected void handle(EngineStatusDescriber stats) {
                removeLoadingCircleIfPresent();
                clear();
                addAll(stats.processDefinitions);
                notifyDataSetChanged();
            }
        };
        //runs repeatedly
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mActivitiManager != null && mActivitiManager.isBound()){
                    mActivitiManager.getService().getEngineStatus(engineStatsCallback);
                }
                mHandler.postDelayed(this, 2500);
            }
        }, 2000);

    }

    private void removeLoadingCircleIfPresent() {
        if (mProgressbar != null){
            ((ViewGroup) mProgressbar.getParent()).removeView(mProgressbar);
            mProgressbar = null;
        }
    }


}
