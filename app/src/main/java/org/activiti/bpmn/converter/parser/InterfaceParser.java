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
package org.activiti.bpmn.converter.parser;

import org.xmlpull.v1.XmlPullParser;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Interface;
import org.activiti.bpmn.model.Operation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tijs Rademakers
 */
public class InterfaceParser implements BpmnXMLConstants {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(InterfaceParser.class.getName());
  
  public void parse(XmlPullParser xtr, BpmnModel model) throws Exception {
    
    Interface interfaceObject = new Interface();
    BpmnXMLUtil.addXMLLocation(interfaceObject, xtr);
    interfaceObject.setId(model.getTargetNamespace() + ":" + xtr.getAttributeValue(null, ATTRIBUTE_ID));
    interfaceObject.setName(xtr.getAttributeValue(null, ATTRIBUTE_NAME));
    interfaceObject.setImplementationRef(parseMessageRef(xtr.getAttributeValue(null, ATTRIBUTE_IMPLEMENTATION_REF), model));
    
    boolean readyWithInterface = false;
    Operation operation = null;
    try {
      int eventType = xtr.getEventType();

      while (readyWithInterface == false && eventType != XmlPullParser.END_DOCUMENT) {
//        xtr.next();
        if (eventType == XmlPullParser.START_TAG && ELEMENT_OPERATION.equals(xtr.getName())) {
          operation = new Operation();
          BpmnXMLUtil.addXMLLocation(operation, xtr);
          operation.setId(model.getTargetNamespace() + ":" + xtr.getAttributeValue(null, ATTRIBUTE_ID));
          operation.setName(xtr.getAttributeValue(null, ATTRIBUTE_NAME));
          operation.setImplementationRef(parseMessageRef(xtr.getAttributeValue(null, ATTRIBUTE_IMPLEMENTATION_REF), model));

        } else if (eventType == XmlPullParser.START_TAG && ELEMENT_IN_MESSAGE.equals(xtr.getName())) {
          String inMessageRef = xtr.getText();
          if (operation != null && StringUtils.isNotEmpty(inMessageRef)) {
            operation.setInMessageRef(parseMessageRef(inMessageRef.trim(), model));
          }
          
        } else if (eventType == XmlPullParser.START_TAG && ELEMENT_OUT_MESSAGE.equals(xtr.getName())) {
          String outMessageRef = xtr.getText();
          if (operation != null && StringUtils.isNotEmpty(outMessageRef)) {
            operation.setOutMessageRef(parseMessageRef(outMessageRef.trim(), model));
          }
          
        } else if (eventType == XmlPullParser.END_TAG && ELEMENT_OPERATION.equalsIgnoreCase(xtr.getName())) {
          if (operation != null && StringUtils.isNotEmpty(operation.getImplementationRef())) {
            interfaceObject.getOperations().add(operation);
          }
          
        } else if (eventType == XmlPullParser.END_TAG && ELEMENT_INTERFACE.equals(xtr.getName())) {
          readyWithInterface = true;
        }

        eventType =  xtr.next();
      }
    } catch (Exception e) {
      LOGGER.warn("Error parsing interface child elements", e);
    }
    
    model.getInterfaces().add(interfaceObject);
  }
  
  protected String parseMessageRef(String messageRef, BpmnModel model) {
    String result = null;
    if (StringUtils.isNotEmpty(messageRef)) {
      int indexOfP = messageRef.indexOf(':');
      if (indexOfP != -1) {
        String prefix = messageRef.substring(0, indexOfP);
        String resolvedNamespace = model.getNamespace(prefix);
        messageRef = messageRef.substring(indexOfP + 1);

        if (resolvedNamespace == null) {
          // if it's an invalid prefix will consider this is not a namespace prefix so will be used as part of the stringReference
          messageRef = prefix + ":" + messageRef;
        } else if (!resolvedNamespace.equalsIgnoreCase(model.getTargetNamespace())) {
          //  if it's a valid namespace prefix but it's not the targetNamespace then we'll use it as a valid namespace
          // (even out editor does not support defining namespaces it is still a valid xml file)
          messageRef = resolvedNamespace + ":" + messageRef;
        }
      }
      result = messageRef;
    }
    return result;
  }
}
