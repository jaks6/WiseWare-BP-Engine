package org.activiti.bpmn.converter.child;

import org.xmlpull.v1.XmlPullParser;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;

public class ActivitiFailedjobRetryParser extends BaseChildElementParser {

	@Override
	public String getElementName() {
		return FAILED_JOB_RETRY_TIME_CYCLE;
	}

	@Override
	public void parseChildElement(XmlPullParser xtr,
         BaseElement parentElement, BpmnModel model) throws Exception {
		 if (!(parentElement instanceof Activity)) 
            return;
		 String cycle = xtr.getText();
		 if (cycle == null || cycle.isEmpty())
			 return;
		 ((Activity) parentElement).setFailedJobRetryTimeCycleValue(cycle);
	}

}
