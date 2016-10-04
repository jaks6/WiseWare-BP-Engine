package ee.ut.cs.mc.and.activiti521;

import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ee.ut.cs.mc.and.activiti521.engine.ActivitiServiceManager;

/**
 * Created by J-Kool on 04/10/2016.
 */
public class DeploymentsFragment extends ListFragment {

    private ActivitiServiceManager mActivitiManager;

    private MainActivity mActivity;
    Handler mHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity)getActivity();


        DeploymentAdapter adapter = new DeploymentAdapter(getActivity());
        setListAdapter(adapter);
// Inflate the layout for this fragment
        return inflater.inflate(R.layout.deployments_fragment, container, false);
//        return super.onCreateView(inflater, container, savedInstanceState);
    }



}
