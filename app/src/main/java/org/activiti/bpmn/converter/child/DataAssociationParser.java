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

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.Assignment;
import org.activiti.bpmn.model.DataAssociation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataAssociationParser implements BpmnXMLConstants {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(DataAssociationParser.class.getName());

  public static void parseDataAssociation(DataAssociation dataAssociation, String elementName, XmlPullParser xtr) {
    boolean readyWithDataAssociation = false;
    Assignment assignment = null;
    try {
      
      dataAssociation.setId(xtr.getAttributeValue(null, "id"));

      int eventType = xtr.getEventType();
      while (readyWithDataAssociation == false && eventType != XmlPullParser.END_DOCUMENT) {

//        xtr.next();
        if (eventType == XmlPullParser.START_TAG && ELEMENT_SOURCE_REF.equals(xtr.getName())) {
          String sourceRef = xtr.nextText();
          if (StringUtils.isNotEmpty(sourceRef)) {
            dataAssociation.setSourceRef(sourceRef.trim());
          }

        } else if (eventType == XmlPullParser.START_TAG && ELEMENT_TARGET_REF.equals(xtr.getName())) {
          String targetRef = xtr.nextText();
          if (StringUtils.isNotEmpty(targetRef)) {
            dataAssociation.setTargetRef(targetRef.trim());
          }
          
        } else if (eventType == XmlPullParser.START_TAG && ELEMENT_TRANSFORMATION.equals(xtr.getName())) {
          String transformation = xtr.nextText();
          if (StringUtils.isNotEmpty(transformation)) {
            dataAssociation.setTransformation(transformation.trim());
          }
          
        } else if (eventType == XmlPullParser.START_TAG && ELEMENT_ASSIGNMENT.equals(xtr.getName())) {
          assignment = new Assignment();
          BpmnXMLUtil.addXMLLocation(assignment, xtr);
          
        } else if (eventType == XmlPullParser.START_TAG && ELEMENT_FROM.equals(xtr.getName())) {
          String from = xtr.nextText();
          if (assignment != null && StringUtils.isNotEmpty(from)) {
            assignment.setFrom(from.trim());
          }
          
        } else if (eventType == XmlPullParser.START_TAG && ELEMENT_TO.equals(xtr.getName())) {
          String to = xtr.nextText();
          if (assignment != null && StringUtils.isNotEmpty(to)) {
            assignment.setTo(to.trim());
          }
          
        } else if (eventType == XmlPullParser.END_TAG && ELEMENT_ASSIGNMENT.equals(xtr.getName())) {
          if (StringUtils.isNotEmpty(assignment.getFrom()) && StringUtils.isNotEmpty(assignment.getTo())) {
            dataAssociation.getAssignments().add(assignment);
          }
          
        } else if (eventType == XmlPullParser.END_TAG && elementName.equals(xtr.getName())) {
          readyWithDataAssociation = true;
        }
      }
    } catch (Exception e) {
      LOGGER.warn("Error parsing data association child elements", e);
    }
  }
}
