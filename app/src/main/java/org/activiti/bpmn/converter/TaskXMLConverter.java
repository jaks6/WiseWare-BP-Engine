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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.Task;

/**
 * @author Tijs Rademakers
 */
public class TaskXMLConverter extends BaseBpmnXMLConverter {

  public Class<? extends BaseElement> getBpmnElementType() {
    return Task.class;
  }
  
  @Override
  protected String getXMLElementName() {
    return ELEMENT_TASK;
  }
  
  @Override
  protected BaseElement convertXMLToElement(XmlPullParser xtr, BpmnModel model) throws Exception {
    ManualTask manualTask = new ManualTask();
    BpmnXMLUtil.addXMLLocation(manualTask, xtr);
    parseChildElements(getXMLElementName(), manualTask, model, xtr);
    return manualTask;
  }

  @Override
  protected void writeAdditionalAttributes(BaseElement element, BpmnModel model, XmlSerializer xtw) throws Exception {
  }
  
  @Override
  protected void writeAdditionalChildElements(BaseElement element, BpmnModel model, XmlSerializer xtw) throws Exception {
    
  }
}
