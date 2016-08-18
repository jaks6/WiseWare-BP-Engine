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
package org.activiti.bpmn.converter.child;

import org.xmlpull.v1.XmlPullParser;

import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.SendTask;
import org.activiti.bpmn.model.ServiceTask;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Tijs Rademakers
 */
public class FieldExtensionParser extends BaseChildElementParser {

  public String getElementName() {
    return ELEMENT_FIELD;
  }

  public boolean accepts(BaseElement element){
    return ((element instanceof ActivitiListener)
        || (element instanceof ServiceTask)
        || (element instanceof SendTask));
  }

  public void parseChildElement(XmlPullParser xtr, BaseElement parentElement, BpmnModel model) throws Exception {
    
    if (!accepts(parentElement)) return;
    
    FieldExtension extension = new FieldExtension();
    BpmnXMLUtil.addXMLLocation(extension, xtr);
    extension.setFieldName(xtr.getAttributeValue(null, ATTRIBUTE_FIELD_NAME));
    
    if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, ATTRIBUTE_FIELD_STRING))) {
      extension.setStringValue(xtr.getAttributeValue(null, ATTRIBUTE_FIELD_STRING));

    } else if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, ATTRIBUTE_FIELD_EXPRESSION))) {
      extension.setExpression(xtr.getAttributeValue(null, ATTRIBUTE_FIELD_EXPRESSION));

    } else {
      boolean readyWithFieldExtension = false;
      try {
        int eventType = xtr.getEventType();
        while (readyWithFieldExtension == false && eventType != XmlPullParser.END_DOCUMENT) {
          if (eventType == XmlPullParser.START_TAG && ELEMENT_FIELD_STRING.equalsIgnoreCase(xtr.getName())) {
            extension.setStringValue(xtr.nextText().trim());

          } else if (eventType == XmlPullParser.START_TAG && ATTRIBUTE_FIELD_EXPRESSION.equalsIgnoreCase(xtr.getName())) {
            extension.setExpression(xtr.nextText().trim());

          } else if (eventType == XmlPullParser.END_TAG && getElementName().equalsIgnoreCase(xtr.getName())) {
            readyWithFieldExtension = true;
          }
          eventType = xtr.next();
        }
      } catch (Exception e) {
        LOGGER.warn("Error parsing field extension child elements", e);
      }
    }
    
    if (parentElement instanceof ActivitiListener) {
      ((ActivitiListener) parentElement).getFieldExtensions().add(extension);
    } else if (parentElement instanceof ServiceTask) {
      ((ServiceTask) parentElement).getFieldExtensions().add(extension);
    } else {
      ((SendTask) parentElement).getFieldExtensions().add(extension);
    }
  }
}
