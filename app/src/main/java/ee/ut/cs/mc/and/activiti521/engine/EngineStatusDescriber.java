package ee.ut.cs.mc.and.activiti521.engine;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.ArrayList;
import java.util.List;

/*
*   Simple descriptor Object which has lists of instance (running and deployed)
*/
public class EngineStatusDescriber {
    public final List<ProcessInstance> runningInstances;
    public final List<ProcessDefinition> processDefinitions;

    public EngineStatusDescriber(ProcessEngine engine) {
        if (engine != null){
            this.runningInstances = engine.getRuntimeService().createProcessInstanceQuery().list();
            this.processDefinitions = engine.getRepositoryService().createProcessDefinitionQuery().list();
        } else {
            this.runningInstances = new ArrayList<>();
            this.processDefinitions = new ArrayList<>();
        }
    }
}
