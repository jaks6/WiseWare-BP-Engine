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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlSerializer;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.apache.commons.lang3.StringUtils;

public class DefinitionsRootExport implements BpmnXMLConstants {

  /** default namespaces for definitions */
  protected static final Set<String> defaultNamespaces = new HashSet<String>(
      Arrays.asList(XSI_PREFIX, XSD_PREFIX, ACTIVITI_EXTENSIONS_PREFIX, BPMNDI_PREFIX, OMGDC_PREFIX, OMGDI_PREFIX));
  
  protected static final List<ExtensionAttribute> defaultAttributes = Arrays.asList(
      new ExtensionAttribute(TYPE_LANGUAGE_ATTRIBUTE), 
      new ExtensionAttribute(EXPRESSION_LANGUAGE_ATTRIBUTE), 
      new ExtensionAttribute(TARGET_NAMESPACE_ATTRIBUTE)
  );

  @SuppressWarnings("unchecked")
  public static void writeRootElement(BpmnModel model, XmlSerializer xtw, String encoding) throws Exception {

    xtw.startDocument(encoding, true);

    // start definitions root element
//    xtw.writeStartElement(ELEMENT_DEFINITIONS);

    xtw.startTag("",ELEMENT_DEFINITIONS);
//    xtw.setDefaultNamespace(BPMN2_NAMESPACE);
    xtw.setPrefix("", BPMN2_NAMESPACE);
    xtw.setPrefix(XSI_PREFIX, XSI_NAMESPACE);
    xtw.setPrefix(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE);
    xtw.setPrefix(BPMNDI_PREFIX, BPMNDI_NAMESPACE);
    xtw.setPrefix(OMGDC_PREFIX, OMGDC_NAMESPACE);
    xtw.setPrefix(OMGDI_PREFIX, OMGDI_NAMESPACE);
    for (String prefix : model.getNamespaces().keySet()) {
      if (!defaultNamespaces.contains(prefix) && StringUtils.isNotEmpty(prefix))
        xtw.setPrefix(prefix, model.getNamespaces().get(prefix));
    }
    xtw.attribute("", TYPE_LANGUAGE_ATTRIBUTE, SCHEMA_NAMESPACE);
    xtw.attribute("", EXPRESSION_LANGUAGE_ATTRIBUTE, XPATH_NAMESPACE);
    if (StringUtils.isNotEmpty(model.getTargetNamespace())) {
      xtw.attribute("", TARGET_NAMESPACE_ATTRIBUTE, model.getTargetNamespace());
    } else {
      xtw.attribute("", TARGET_NAMESPACE_ATTRIBUTE, PROCESS_NAMESPACE);
    }
    
    BpmnXMLUtil.writeCustomAttributes(model.getDefinitionsAttributes().values(), xtw, model.getNamespaces(), defaultAttributes);
  }
}
