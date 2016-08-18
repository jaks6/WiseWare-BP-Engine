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

import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import org.activiti.bpmn.converter.child.BaseChildElementParser;
import org.activiti.bpmn.converter.child.ScriptTextParser;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ScriptTask;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Tijs Rademakers
 */
public class ScriptTaskXMLConverter extends BaseBpmnXMLConverter {
  
  protected Map<String, BaseChildElementParser> childParserMap = new HashMap<String, BaseChildElementParser>();
  
	public ScriptTaskXMLConverter() {
		ScriptTextParser scriptTextParser = new ScriptTextParser();
		childParserMap.put(scriptTextParser.getElementName(), scriptTextParser);
	}
	
  public Class<? extends BaseElement> getBpmnElementType() {
    return ScriptTask.class;
  }
  
  @Override
  protected String getXMLElementName() {
    return ELEMENT_TASK_SCRIPT;
  }
  
  @Override
  protected BaseElement convertXMLToElement(XmlPullParser xtr, BpmnModel model) throws Exception {
    ScriptTask scriptTask = new ScriptTask();
    BpmnXMLUtil.addXMLLocation(scriptTask, xtr);
    scriptTask.setScriptFormat(xtr.getAttributeValue(null, ATTRIBUTE_TASK_SCRIPT_FORMAT));
    scriptTask.setResultVariable(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_SCRIPT_RESULTVARIABLE));
    if (StringUtils.isEmpty(scriptTask.getResultVariable())) {
      scriptTask.setResultVariable(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_SERVICE_RESULTVARIABLE));
    }
    String autoStoreVariables = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_SCRIPT_AUTO_STORE_VARIABLE);
    if (StringUtils.isNotEmpty(autoStoreVariables)) {
      scriptTask.setAutoStoreVariables(Boolean.valueOf(autoStoreVariables));
    }
    parseChildElements(getXMLElementName(), scriptTask, childParserMap, model, xtr);
    return scriptTask;
  }

  @Override
  protected void writeAdditionalAttributes(BaseElement element, BpmnModel model, XmlSerializer xtw) throws Exception {
    ScriptTask scriptTask = (ScriptTask) element;
    writeDefaultAttribute(ATTRIBUTE_TASK_SCRIPT_FORMAT, scriptTask.getScriptFormat(), xtw);
    writeQualifiedAttribute(ATTRIBUTE_TASK_SCRIPT_RESULTVARIABLE, scriptTask.getResultVariable(), xtw);
    writeQualifiedAttribute(ATTRIBUTE_TASK_SCRIPT_AUTO_STORE_VARIABLE, String.valueOf(scriptTask.isAutoStoreVariables()), xtw);
  }
  
  @Override
  protected void writeAdditionalChildElements(BaseElement element, BpmnModel model, XmlSerializer xtw) throws Exception {
    ScriptTask scriptTask = (ScriptTask) element;
    if (StringUtils.isNotEmpty(scriptTask.getScript())) {
      xtw.startTag("",ATTRIBUTE_TASK_SCRIPT_TEXT);
      xtw.cdsect(scriptTask.getScript());
      xtw.endTag("", ATTRIBUTE_TASK_SCRIPT_TEXT);
    }
  }
}
