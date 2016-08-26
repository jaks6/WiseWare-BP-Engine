package ee.ut.cs.mc.and.activiti521.migration;

import org.activiti.engine.impl.JobQueryImpl;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.runtime.Job;

import java.util.List;

/**
 * Created by Jakob on 24.08.2016.
 */

public class MigrationCmd {
    public static final int SELECT_JOBS = 1;
    public static final int SELECT_VARS = 2;

    private final String processInstanceId;

    public MigrationCmd(String instanceId) {
        this.processInstanceId = instanceId;
    }

    public List execute(CommandContext commandContext, int select) {
        List objs = null;
        switch(select){
            case SELECT_JOBS:
                objs = commandContext
                        .getJobEntityManager()
                        .findJobsByQueryCriteria(
                                new JobQueryImpl(commandContext)
                                        .processInstanceId(processInstanceId), null
                        );
                break;
            case SELECT_VARS:

                break;

        }


        return objs;


    }
}
