/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.bpmn.converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.child.BaseChildElementParser;
import org.activiti.bpmn.converter.export.ActivitiListenerExport;
import org.activiti.bpmn.converter.export.FailedJobRetryCountExport;
import org.activiti.bpmn.converter.export.MultiInstanceExport;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.CancelEventDefinition;
import org.activiti.bpmn.model.CompensateEventDefinition;
import org.activiti.bpmn.model.DataObject;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.EventDefinition;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.FormValue;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.MessageEventDefinition;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SignalEventDefinition;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.TerminateEventDefinition;
import org.activiti.bpmn.model.ThrowEvent;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.activiti.bpmn.model.UserTask;
import org.activiti.bpmn.model.ValuedDataObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public abstract class BaseBpmnXMLConverter implements BpmnXMLConstants {

  protected static final Logger LOGGER = LoggerFactory.getLogger(BaseBpmnXMLConverter.class);
  
  protected static final List<ExtensionAttribute> defaultElementAttributes = Arrays.asList(
      new ExtensionAttribute(ATTRIBUTE_ID),
      new ExtensionAttribute(ATTRIBUTE_NAME)
  );
  
  protected static final List<ExtensionAttribute> defaultActivityAttributes = Arrays.asList(
      new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_ACTIVITY_ASYNCHRONOUS), 
      new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_ACTIVITY_EXCLUSIVE), 
      new ExtensionAttribute(ATTRIBUTE_DEFAULT), 
      new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_ACTIVITY_ISFORCOMPENSATION)
  );
  
  public void convertToBpmnModel(XmlPullParser xtr, BpmnModel model, Process activeProcess,
      List<SubProcess> activeSubProcessList) throws Exception {
    
    String elementId = xtr.getAttributeValue(null, ATTRIBUTE_ID);
    String elementName = xtr.getAttributeValue(null, ATTRIBUTE_NAME);
    boolean async = parseAsync(xtr);
    boolean notExclusive = parseNotExclusive(xtr);
    String defaultFlow = xtr.getAttributeValue(null, ATTRIBUTE_DEFAULT);
    boolean isForCompensation = parseForCompensation(xtr);
    
    BaseElement parsedElement = convertXMLToElement(xtr, model);
    
    if (parsedElement instanceof Artifact) {
      Artifact currentArtifact = (Artifact) parsedElement;
      currentArtifact.setId(elementId);

      if (!activeSubProcessList.isEmpty()) {
        activeSubProcessList.get(activeSubProcessList.size() - 1).addArtifact(currentArtifact);

      } else {
        activeProcess.addArtifact(currentArtifact);
      }
    }
    
    if (parsedElement instanceof FlowElement) {
      
      FlowElement currentFlowElement = (FlowElement) parsedElement;
      currentFlowElement.setId(elementId);
      currentFlowElement.setName(elementName);
      
      if (currentFlowElement instanceof FlowNode) {
        FlowNode flowNode = (FlowNode) currentFlowElement;
        flowNode.setAsynchronous(async);
        flowNode.setNotExclusive(notExclusive);
        
        if (currentFlowElement instanceof Activity) {
          
          Activity activity = (Activity) currentFlowElement;
          activity.setForCompensation(isForCompensation);
          if (StringUtils.isNotEmpty(defaultFlow)) {
            activity.setDefaultFlow(defaultFlow);
          }
        }
        
        if (currentFlowElement instanceof Gateway) {
          Gateway gateway = (Gateway) currentFlowElement;
          if (StringUtils.isNotEmpty(defaultFlow)) {
            gateway.setDefaultFlow(defaultFlow);
          }
        }
      }
      
      if (currentFlowElement instanceof DataObject) {
        if (!activeSubProcessList.isEmpty()) {
          activeSubProcessList.get(activeSubProcessList.size() - 1).getDataObjects().add((ValuedDataObject)parsedElement);
        } else {
          activeProcess.getDataObjects().add((ValuedDataObject)parsedElement);
        }
      }

      if (!activeSubProcessList.isEmpty()) {
        activeSubProcessList.get(activeSubProcessList.size() - 1).addFlowElement(currentFlowElement);
      } else {
        activeProcess.addFlowElement(currentFlowElement);
      }
    }
  }
  
  public void convertToXML(XmlSerializer xtw, BaseElement baseElement, BpmnModel model) throws Exception {
    xtw.startTag("", getXMLElementName());
    boolean didWriteExtensionStartElement = false;
    writeDefaultAttribute(ATTRIBUTE_ID, baseElement.getId(), xtw);
    if (baseElement instanceof FlowElement) {
      writeDefaultAttribute(ATTRIBUTE_NAME, ((FlowElement) baseElement).getName(), xtw);
    }
    
    if (baseElement instanceof FlowNode) {
      final FlowNode flowNode = (FlowNode) baseElement;
      if (flowNode.isAsynchronous()) {
        writeQualifiedAttribute(ATTRIBUTE_ACTIVITY_ASYNCHRONOUS, ATTRIBUTE_VALUE_TRUE, xtw);
        if (flowNode.isNotExclusive()) {
          writeQualifiedAttribute(ATTRIBUTE_ACTIVITY_EXCLUSIVE, ATTRIBUTE_VALUE_FALSE, xtw);
        }
      }
      
      if (baseElement instanceof Activity) {
        final Activity activity = (Activity) baseElement;
        if (activity.isForCompensation()) {
          writeDefaultAttribute(ATTRIBUTE_ACTIVITY_ISFORCOMPENSATION, ATTRIBUTE_VALUE_TRUE, xtw);
        }
        if (StringUtils.isNotEmpty(activity.getDefaultFlow())) {
          FlowElement defaultFlowElement = model.getFlowElement(activity.getDefaultFlow());
          if (defaultFlowElement instanceof SequenceFlow) {
            writeDefaultAttribute(ATTRIBUTE_DEFAULT, activity.getDefaultFlow(), xtw);
          }
        }
      }
      
      if (baseElement instanceof Gateway) {
        final Gateway gateway = (Gateway) baseElement;
        if (StringUtils.isNotEmpty(gateway.getDefaultFlow())) {
          FlowElement defaultFlowElement = model.getFlowElement(gateway.getDefaultFlow());
          if (defaultFlowElement instanceof SequenceFlow) {
            writeDefaultAttribute(ATTRIBUTE_DEFAULT, gateway.getDefaultFlow(), xtw);
          }
        }
      }
    }
    
    writeAdditionalAttributes(baseElement, model, xtw);
    
    if (baseElement instanceof FlowElement) {
      final FlowElement flowElement = (FlowElement) baseElement;
      if (StringUtils.isNotEmpty(flowElement.getDocumentation())) {

        xtw.startTag("",ELEMENT_DOCUMENTATION);
        xtw.text(flowElement.getDocumentation());
        xtw.endTag("", ELEMENT_DOCUMENTATION);
      }
    }
    
    didWriteExtensionStartElement = writeExtensionChildElements(baseElement, didWriteExtensionStartElement, xtw);
    didWriteExtensionStartElement = writeListeners(baseElement, didWriteExtensionStartElement, xtw);
    didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(baseElement, didWriteExtensionStartElement, model.getNamespaces(), xtw);
    if (baseElement instanceof Activity) {
    	final Activity activity = (Activity) baseElement;
        FailedJobRetryCountExport.writeFailedJobRetryCount(activity, xtw);
        
     }
    
    if (didWriteExtensionStartElement) {
      xtw.endTag("", ELEMENT_EXTENSIONS);
    }
    
    if (baseElement instanceof Activity) {
      final Activity activity = (Activity) baseElement;
      MultiInstanceExport.writeMultiInstance(activity, xtw);
      
    }
    
    writeAdditionalChildElements(baseElement, model, xtw);

    xtw.endTag("", getXMLElementName());
  }
  
  protected abstract Class<? extends BaseElement> getBpmnElementType();
  
  protected abstract BaseElement convertXMLToElement(XmlPullParser xtr, BpmnModel model) throws Exception;
  
  protected abstract String getXMLElementName();
  
  protected abstract void writeAdditionalAttributes(BaseElement element, BpmnModel model, XmlSerializer xtw) throws Exception;
  
  protected boolean writeExtensionChildElements(BaseElement element, boolean didWriteExtensionStartElement, XmlSerializer xtw) throws Exception {
    return didWriteExtensionStartElement;
  }
  
  protected abstract void writeAdditionalChildElements(BaseElement element, BpmnModel model, XmlSerializer xtw) throws Exception;
  
  // To BpmnModel converter convenience methods
  
  protected void parseChildElements(String elementName, BaseElement parentElement, BpmnModel model, XmlPullParser xtr) throws Exception {
    parseChildElements(elementName, parentElement, null, model, xtr);
  }
  
  protected void parseChildElements(String elementName, BaseElement parentElement, Map<String, BaseChildElementParser> additionalParsers, 
      BpmnModel model, XmlPullParser xtr) throws Exception {
    
    Map<String, BaseChildElementParser> childParsers = new HashMap<String, BaseChildElementParser>();
    if (additionalParsers != null) {
      childParsers.putAll(additionalParsers);
    }
    BpmnXMLUtil.parseChildElements(elementName, parentElement, xtr, childParsers, model);
  }
  
  @SuppressWarnings("unchecked")
  protected ExtensionElement parseExtensionElement(XmlPullParser xtr) throws Exception {
    ExtensionElement extensionElement = new ExtensionElement();
    extensionElement.setName(xtr.getName());
    if (StringUtils.isNotEmpty(xtr.getNamespace())) {
      extensionElement.setNamespace(xtr.getNamespace());
    }
    if (StringUtils.isNotEmpty(xtr.getPrefix())) {
      extensionElement.setNamespacePrefix(xtr.getPrefix());
    }

    BpmnXMLUtil.addCustomAttributes(xtr, extensionElement, defaultElementAttributes);

    boolean readyWithExtensionElement = false;
    while (readyWithExtensionElement == false && xtr.getEventType() != XmlPullParser.END_DOCUMENT) {
      xtr.next();
      if (xtr.getEventType() == XmlPullParser.TEXT || XmlPullParser.CDSECT == xtr.getEventType()) {
        if (StringUtils.isNotEmpty(xtr.getText().trim())) {
          extensionElement.setElementText(xtr.getText().trim());
        }
      } else if (xtr.getEventType() == XmlPullParser.START_TAG) {
        ExtensionElement childExtensionElement = parseExtensionElement(xtr);
        extensionElement.addChildElement(childExtensionElement);
      } else if (xtr.getEventType() == XmlPullParser.END_TAG && extensionElement.getName().equalsIgnoreCase(xtr.getName())) {
        readyWithExtensionElement = true;
      }
      xtr.next();
    }
    return extensionElement;
  }

  protected boolean parseAsync(XmlPullParser xtr) {
    boolean async = false;
    String asyncString = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_ACTIVITY_ASYNCHRONOUS);
    if (ATTRIBUTE_VALUE_TRUE.equalsIgnoreCase(asyncString)) {
      async = true;
    }
    return async;
  }
  
  protected boolean parseNotExclusive(XmlPullParser xtr) {
    boolean notExclusive = false;
    String exclusiveString = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_ACTIVITY_EXCLUSIVE);
    if (ATTRIBUTE_VALUE_FALSE.equalsIgnoreCase(exclusiveString)) {
      notExclusive = true;
    }
    return notExclusive;
  }
  
  protected boolean parseForCompensation(XmlPullParser xtr) {
    boolean isForCompensation = false;
    String compensationString = xtr.getAttributeValue(null, ATTRIBUTE_ACTIVITY_ISFORCOMPENSATION);
    if (ATTRIBUTE_VALUE_TRUE.equalsIgnoreCase(compensationString)) {
      isForCompensation = true;
    }
    return isForCompensation;
  }
  
  protected List<String> parseDelimitedList(String expression) {
    return BpmnXMLUtil.parseDelimitedList(expression);
  }
  
  // To XML converter convenience methods
  
  protected String convertToDelimitedString(List<String> stringList) {
    return BpmnXMLUtil.convertToDelimitedString(stringList);
  }
  
  protected boolean writeFormProperties(FlowElement flowElement, boolean didWriteExtensionStartElement, XmlSerializer xtw) throws Exception {
    
    List<FormProperty> propertyList = null;
    if (flowElement instanceof UserTask) {
      propertyList = ((UserTask) flowElement).getFormProperties();
    } else if (flowElement instanceof StartEvent) {
      propertyList = ((StartEvent) flowElement).getFormProperties();
    }
    
    if (propertyList != null) {
    
      for (FormProperty property : propertyList) {
        
        if (StringUtils.isNotEmpty(property.getId())) {
          
          if (didWriteExtensionStartElement == false) {
            xtw.startTag("",ELEMENT_EXTENSIONS);
            didWriteExtensionStartElement = true;
          }

            xtw.setPrefix(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE);
            xtw.startTag(ACTIVITI_EXTENSIONS_NAMESPACE, ELEMENT_FORMPROPERTY);
//          xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, ELEMENT_FORMPROPERTY, ACTIVITI_EXTENSIONS_NAMESPACE);
          writeDefaultAttribute(ATTRIBUTE_FORM_ID, property.getId(), xtw);
          
          writeDefaultAttribute(ATTRIBUTE_FORM_NAME, property.getName(), xtw);
          writeDefaultAttribute(ATTRIBUTE_FORM_TYPE, property.getType(), xtw);
          writeDefaultAttribute(ATTRIBUTE_FORM_EXPRESSION, property.getExpression(), xtw);
          writeDefaultAttribute(ATTRIBUTE_FORM_VARIABLE, property.getVariable(), xtw);
          writeDefaultAttribute(ATTRIBUTE_FORM_DEFAULT, property.getDefaultExpression(), xtw);
          writeDefaultAttribute(ATTRIBUTE_FORM_DATEPATTERN, property.getDatePattern(), xtw);
          if (property.isReadable() == false) {
            writeDefaultAttribute(ATTRIBUTE_FORM_READABLE, ATTRIBUTE_VALUE_FALSE, xtw);
          }
          if (property.isWriteable() == false) {
            writeDefaultAttribute(ATTRIBUTE_FORM_WRITABLE, ATTRIBUTE_VALUE_FALSE, xtw);
          }
          if (property.isRequired()) {
            writeDefaultAttribute(ATTRIBUTE_FORM_REQUIRED, ATTRIBUTE_VALUE_TRUE, xtw);
          }
          
          for (FormValue formValue : property.getFormValues()) {
            if (StringUtils.isNotEmpty(formValue.getId())) {
                xtw.setPrefix(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE);
                xtw.startTag(ACTIVITI_EXTENSIONS_NAMESPACE, ELEMENT_VALUE);
//              xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, ELEMENT_VALUE, ACTIVITI_EXTENSIONS_NAMESPACE);
//              xtw.writeAttribute(ATTRIBUTE_ID, formValue.getId());
//              xtw.writeAttribute(ATTRIBUTE_NAME, formValue.getName());
//              xtw.writeEndElement();
                xtw.attribute("", ATTRIBUTE_ID, formValue.getId());
                xtw.attribute("", ATTRIBUTE_NAME, formValue.getName());
                xtw.endTag(ACTIVITI_EXTENSIONS_NAMESPACE, ELEMENT_VALUE);
            }
          }

           xtw.endTag(ACTIVITI_EXTENSIONS_NAMESPACE, ELEMENT_FORMPROPERTY);
        }
      }
    }
    
    return didWriteExtensionStartElement;
  }
  
  protected boolean writeListeners(BaseElement element, boolean didWriteExtensionStartElement, XmlSerializer xtw) throws Exception {
    return ActivitiListenerExport.writeListeners(element, didWriteExtensionStartElement, xtw);
  }
  
  protected void writeEventDefinitions(Event parentEvent, List<EventDefinition> eventDefinitions, BpmnModel model, XmlSerializer xtw) throws Exception {
    for (EventDefinition eventDefinition : eventDefinitions) {
      if (eventDefinition instanceof TimerEventDefinition) {
        writeTimerDefinition(parentEvent, (TimerEventDefinition) eventDefinition, xtw);
      } else if (eventDefinition instanceof SignalEventDefinition) {
        writeSignalDefinition(parentEvent, (SignalEventDefinition) eventDefinition, xtw);
      } else if (eventDefinition instanceof MessageEventDefinition) {
        writeMessageDefinition(parentEvent, (MessageEventDefinition) eventDefinition, model, xtw);
      } else if (eventDefinition instanceof ErrorEventDefinition) {
        writeErrorDefinition(parentEvent, (ErrorEventDefinition) eventDefinition, xtw);
      } else if (eventDefinition instanceof TerminateEventDefinition) {
        writeTerminateDefinition(parentEvent, (TerminateEventDefinition) eventDefinition, xtw);
      } else if (eventDefinition instanceof CancelEventDefinition) {
        writeCancelDefinition(parentEvent, (CancelEventDefinition) eventDefinition, xtw);
      } else if (eventDefinition instanceof CompensateEventDefinition) {
        writeCompensateDefinition(parentEvent, (CompensateEventDefinition) eventDefinition, xtw);
      }
    }
  }
  
  protected void writeTimerDefinition(Event parentEvent, TimerEventDefinition timerDefinition, XmlSerializer xtw) throws Exception {
    xtw.startTag("",ELEMENT_EVENT_TIMERDEFINITION);
    if (StringUtils.isNotEmpty(timerDefinition.getCalendarName())) {
      writeQualifiedAttribute(ATTRIBUTE_CALENDAR_NAME, timerDefinition.getCalendarName(), xtw);
    }
    boolean didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(timerDefinition, false, xtw);
    if (didWriteExtensionStartElement) {
        xtw.endTag("", ELEMENT_EXTENSIONS);
    }
    if (StringUtils.isNotEmpty(timerDefinition.getTimeDate())) {
        xtw.startTag("",ATTRIBUTE_TIMER_DATE);
        xtw.text(timerDefinition.getTimeDate());
        xtw.endTag("", ATTRIBUTE_TIMER_DATE);
      
    } else if (StringUtils.isNotEmpty(timerDefinition.getTimeCycle())) {
        xtw.startTag("",ATTRIBUTE_TIMER_CYCLE);
        xtw.text(timerDefinition.getTimeCycle());

        if (StringUtils.isNotEmpty(timerDefinition.getEndDate())) {
            System.err.println("Not implemented: Timer Event end date");
//        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE,ATTRIBUTE_END_DATE,timerDefinition.getEndDate());
        }
        xtw.endTag("", ATTRIBUTE_TIMER_CYCLE);




      
    } else if (StringUtils.isNotEmpty(timerDefinition.getTimeDuration())) {
        xtw.startTag("",ATTRIBUTE_TIMER_DURATION);
        xtw.text(timerDefinition.getTimeDuration());
        xtw.endTag("", ATTRIBUTE_TIMER_DURATION);
    }

      xtw.endTag("", ELEMENT_EVENT_TIMERDEFINITION);
  }
  
  protected void writeSignalDefinition(Event parentEvent, SignalEventDefinition signalDefinition, XmlSerializer xtw) throws Exception {
      xtw.startTag("",ELEMENT_EVENT_SIGNALDEFINITION);
      writeDefaultAttribute(ATTRIBUTE_SIGNAL_REF, signalDefinition.getSignalRef(), xtw);

    if (parentEvent instanceof ThrowEvent && signalDefinition.isAsync()) {
      BpmnXMLUtil.writeQualifiedAttribute(ATTRIBUTE_ACTIVITY_ASYNCHRONOUS, "true", xtw);
    }
    boolean didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(signalDefinition, false, xtw);
    if (didWriteExtensionStartElement) {
        xtw.endTag("", ELEMENT_EXTENSIONS);
    }
      xtw.endTag("", ELEMENT_EVENT_SIGNALDEFINITION);
  }
  
  protected void writeCancelDefinition(Event parentEvent, CancelEventDefinition cancelEventDefinition, XmlSerializer xtw) throws Exception {
    xtw.startTag("",ELEMENT_EVENT_CANCELDEFINITION);
    boolean didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(cancelEventDefinition, false, xtw);
    if (didWriteExtensionStartElement) {
        xtw.endTag("", ELEMENT_EXTENSIONS);
    }
      xtw.endTag("",ELEMENT_EVENT_CANCELDEFINITION);
  }
  
  protected void writeCompensateDefinition(Event parentEvent, CompensateEventDefinition compensateEventDefinition, XmlSerializer xtw) throws Exception {
      xtw.startTag("",ELEMENT_EVENT_COMPENSATEDEFINITION);
    writeDefaultAttribute(ATTRIBUTE_COMPENSATE_ACTIVITYREF, compensateEventDefinition.getActivityRef(), xtw);
    boolean didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(compensateEventDefinition, false, xtw);
    if (didWriteExtensionStartElement) {
        xtw.endTag("", ELEMENT_EXTENSIONS);
    }
      xtw.endTag("",ELEMENT_EVENT_COMPENSATEDEFINITION);
  }
  
  protected void writeMessageDefinition(Event parentEvent, MessageEventDefinition messageDefinition, BpmnModel model, XmlSerializer xtw) throws Exception {
      xtw.startTag("",ELEMENT_EVENT_MESSAGEDEFINITION);
    
    String messageRef = messageDefinition.getMessageRef();
    if (StringUtils.isNotEmpty(messageRef)) {
      // remove the namespace from the message id if set
      if (messageRef.startsWith(model.getTargetNamespace())) {
        messageRef = messageRef.replace(model.getTargetNamespace(), "");
        messageRef = messageRef.replaceFirst(":", "");
      } else {
        for (String prefix : model.getNamespaces().keySet()) {
          String namespace = model.getNamespace(prefix);
          if (messageRef.startsWith(namespace)) {
            messageRef = messageRef.replace(model.getTargetNamespace(), "");
            messageRef = prefix + messageRef;
          }
        }
      }
    }
    writeDefaultAttribute(ATTRIBUTE_MESSAGE_REF, messageRef, xtw);
    boolean didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(messageDefinition, false, xtw);
    if (didWriteExtensionStartElement) {
        xtw.endTag("", ELEMENT_EXTENSIONS);
    }
      xtw.endTag("",ELEMENT_EVENT_MESSAGEDEFINITION);
  }
  
  protected void writeErrorDefinition(Event parentEvent, ErrorEventDefinition errorDefinition, XmlSerializer xtw) throws Exception {
      xtw.startTag("",ELEMENT_EVENT_ERRORDEFINITION);
    writeDefaultAttribute(ATTRIBUTE_ERROR_REF, errorDefinition.getErrorCode(), xtw);
    boolean didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(errorDefinition, false, xtw);
    if (didWriteExtensionStartElement) {
        xtw.endTag("", ELEMENT_EXTENSIONS);
    }
      xtw.endTag("",ELEMENT_EVENT_ERRORDEFINITION);
  }
  
  protected void writeTerminateDefinition(Event parentEvent, TerminateEventDefinition terminateDefinition, XmlSerializer xtw) throws Exception {
      xtw.startTag("",ELEMENT_EVENT_TERMINATEDEFINITION);
    
    if (terminateDefinition.isTerminateAll()) {
    	writeQualifiedAttribute(ATTRIBUTE_TERMINATE_ALL, "true", xtw);
    }
    
    boolean didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(terminateDefinition, false, xtw);
    if (didWriteExtensionStartElement) {
        xtw.endTag("", ELEMENT_EXTENSIONS);
    }
      xtw.endTag("",ELEMENT_EVENT_TERMINATEDEFINITION);
  }
  
  protected void writeDefaultAttribute(String attributeName, String value, XmlSerializer xtw) throws Exception {
    BpmnXMLUtil.writeDefaultAttribute(attributeName, value, xtw);
  }
  
  protected void writeQualifiedAttribute(String attributeName, String value, XmlSerializer xtw) throws Exception {
    BpmnXMLUtil.writeQualifiedAttribute(attributeName, value, xtw);
  }
}
