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

import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.FieldExtension;
import org.apache.commons.lang3.StringUtils;

public class FieldExtensionExport implements BpmnXMLConstants {

  public static boolean writeFieldExtensions(List<FieldExtension> fieldExtensionList, 
      boolean didWriteExtensionStartElement, XmlSerializer xtw) throws Exception {
    
    for (FieldExtension fieldExtension : fieldExtensionList) {
      
      if (StringUtils.isNotEmpty(fieldExtension.getFieldName())) {
        
        if (StringUtils.isNotEmpty(fieldExtension.getStringValue()) || StringUtils.isNotEmpty(fieldExtension.getExpression())) {

          if (didWriteExtensionStartElement == false) {
            xtw.startTag("",ELEMENT_EXTENSIONS);
            didWriteExtensionStartElement = true;
          }

          xtw.startTag(ACTIVITI_EXTENSIONS_NAMESPACE, ELEMENT_FIELD);
          BpmnXMLUtil.writeDefaultAttribute(ATTRIBUTE_FIELD_NAME, fieldExtension.getFieldName(), xtw);
          
          if (StringUtils.isNotEmpty(fieldExtension.getStringValue())) {
            xtw.startTag(ACTIVITI_EXTENSIONS_NAMESPACE, ELEMENT_FIELD_STRING);
            xtw.cdsect(fieldExtension.getStringValue());
            xtw.endTag(ACTIVITI_EXTENSIONS_NAMESPACE, ELEMENT_FIELD_STRING);
          } else {
            xtw.startTag(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_FIELD_EXPRESSION);
            xtw.cdsect(fieldExtension.getExpression());
            xtw.endTag(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_FIELD_EXPRESSION);
          }
          xtw.endTag("",ELEMENT_EXTENSIONS);
          xtw.endTag(ACTIVITI_EXTENSIONS_NAMESPACE, ELEMENT_FIELD);
        }
      }
    }
    return didWriteExtensionStartElement;
  }
}
