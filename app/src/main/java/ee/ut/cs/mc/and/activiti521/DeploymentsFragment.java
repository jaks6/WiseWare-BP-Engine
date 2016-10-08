package ee.ut.cs.mc.and.activiti521;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Created by J-Kool on 04/10/2016.
 */
public class DeploymentsFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deployments_fragment, container, false);
        DeploymentAdapter adapter = new DeploymentAdapter(getActivity(), view);
        setListAdapter(adapter);
        // Inflate the layout for this fragment
        return view;
    }



}
