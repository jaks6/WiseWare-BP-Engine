package ee.ut.cs.mc.and.activiti521;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.activiti.engine.repository.ProcessDefinition;

import ee.ut.cs.mc.and.activiti521.engine.ActivitiServiceManager;

/**
 * Created by J-Kool on 04/10/2016.
 */
public class DeploymentAdapter extends ArrayAdapter<ProcessDefinition> {


    private static final String TAG = DeploymentAdapter.class.getName();
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



    public DeploymentAdapter(Context context) {
        super(context, R.layout.deployment_listitem);
        mHandler = new Handler(Looper.getMainLooper());
        setUpDynamicBehaviour();
    }

    private void setUpDynamicBehaviour() {
        mActivitiManager = new ActivitiServiceManager(getContext());
        mActivitiManager.startService();
        mActivitiManager.bindToService();


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mActivitiManager != null && mActivitiManager.isBound()){
                    clear();
                    addAll(mActivitiManager.getService().getEngineStatus().processDefinitions);
                    notifyDataSetChanged();
                }
                mHandler.postDelayed(this, 2500);
            }
        }, 2000);
    }
}
