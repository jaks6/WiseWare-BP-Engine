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

import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.Process;
import org.apache.commons.lang3.StringUtils;

public class ProcessExport implements BpmnXMLConstants {
  /**
   * default attributes taken from process instance attributes
   */
  public static final List<ExtensionAttribute> defaultProcessAttributes = Arrays.asList(
      new ExtensionAttribute(ATTRIBUTE_ID),
      new ExtensionAttribute(ATTRIBUTE_NAME),
      new ExtensionAttribute(ATTRIBUTE_PROCESS_EXECUTABLE),
      new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_PROCESS_CANDIDATE_USERS),
      new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_PROCESS_CANDIDATE_GROUPS)
  );

  @SuppressWarnings("unchecked")
  public static void writeProcess(Process process, XmlSerializer xtw) throws Exception {
    // start process element
    xtw.startTag("",ELEMENT_PROCESS);
    xtw.attribute("", ATTRIBUTE_ID, process.getId());

    if (StringUtils.isNotEmpty(process.getName())) {
      xtw.attribute("", ATTRIBUTE_NAME, process.getName());
    }

    xtw.attribute("", ATTRIBUTE_PROCESS_EXECUTABLE, ATTRIBUTE_VALUE_TRUE);

    if (!process.getCandidateStarterUsers().isEmpty()) {
      xtw.attribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_PROCESS_CANDIDATE_USERS,
              BpmnXMLUtil.convertToDelimitedString(process.getCandidateStarterUsers()));
    }

    if (!process.getCandidateStarterGroups().isEmpty()) {
      xtw.attribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_PROCESS_CANDIDATE_GROUPS,
              BpmnXMLUtil.convertToDelimitedString(process.getCandidateStarterGroups()));
    }

    // write custom attributes
    BpmnXMLUtil.writeCustomAttributes(process.getAttributes().values(), xtw, defaultProcessAttributes);

    if (StringUtils.isNotEmpty(process.getDocumentation())) {

      xtw.startTag("",ELEMENT_DOCUMENTATION);
      xtw.text(process.getDocumentation());
      xtw.endTag("", ELEMENT_DOCUMENTATION);
    }
    
    boolean didWriteExtensionStartElement = ActivitiListenerExport.writeListeners(process, false, xtw);
    didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(process, didWriteExtensionStartElement, xtw);
    
    if (didWriteExtensionStartElement) {
      // closing extensions element
      xtw.endTag("", ELEMENT_EXTENSIONS);
    }
    
    LaneExport.writeLanes(process, xtw);
  }
}
