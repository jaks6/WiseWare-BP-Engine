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
package org.activiti.bpmn.converter.export;

import org.xmlpull.v1.XmlSerializer;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.MessageFlow;
import org.activiti.bpmn.model.Pool;
import org.apache.commons.lang3.StringUtils;

public class CollaborationExport implements BpmnXMLConstants {

  public static void writePools(BpmnModel model, XmlSerializer xtw) throws Exception {
    if (!model.getPools().isEmpty()) {
      xtw.startTag("", ELEMENT_COLLABORATION);
      xtw.attribute("", ATTRIBUTE_ID, "Collaboration");
      for (Pool pool : model.getPools()) {
        xtw.startTag("", ELEMENT_PARTICIPANT);
        xtw.attribute("", ATTRIBUTE_ID, pool.getId());
        if (StringUtils.isNotEmpty(pool.getName())) {
          xtw.attribute("", ATTRIBUTE_NAME, pool.getName());
        }
        if (StringUtils.isNotEmpty(pool.getProcessRef())) {
          xtw.attribute("", ATTRIBUTE_PROCESS_REF, pool.getProcessRef());
        }
        xtw.endTag("", ELEMENT_PARTICIPANT);
      }
      
      for (MessageFlow messageFlow : model.getMessageFlows().values()) {
        xtw.startTag("", ELEMENT_MESSAGE_FLOW);
        xtw.attribute("", ATTRIBUTE_ID, messageFlow.getId());
        if (StringUtils.isNotEmpty(messageFlow.getName())) {
          xtw.attribute("", ATTRIBUTE_NAME, messageFlow.getName());
        }
        if (StringUtils.isNotEmpty(messageFlow.getSourceRef())) {
          xtw.attribute("", ATTRIBUTE_FLOW_SOURCE_REF, messageFlow.getSourceRef());
        }
        if (StringUtils.isNotEmpty(messageFlow.getTargetRef())) {
          xtw.attribute("", ATTRIBUTE_FLOW_TARGET_REF, messageFlow.getTargetRef());
        }
        if (StringUtils.isNotEmpty(messageFlow.getMessageRef())) {
          xtw.attribute("", ATTRIBUTE_MESSAGE_REF, messageFlow.getMessageRef());
        }
        xtw.endTag("", ELEMENT_MESSAGE_FLOW);
      }

      xtw.endTag("", ELEMENT_COLLABORATION);
    }
  }
}
