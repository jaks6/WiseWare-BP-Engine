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

import android.util.Log;

import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Tijs Rademakers
 */
public class DefinitionsParser implements BpmnXMLConstants {
  
  protected static final List<ExtensionAttribute> defaultAttributes = Arrays.asList(
      new ExtensionAttribute(TYPE_LANGUAGE_ATTRIBUTE), 
      new ExtensionAttribute(EXPRESSION_LANGUAGE_ATTRIBUTE), 
      new ExtensionAttribute(TARGET_NAMESPACE_ATTRIBUTE)
  );
  private static final String TAG = DefinitionsParser.class.getCanonicalName();

  @SuppressWarnings("unchecked")
  public void parse(XmlPullParser xtr, BpmnModel model) throws Exception {
    model.setTargetNamespace(xtr.getAttributeValue(null, TARGET_NAMESPACE_ATTRIBUTE));

    int nsStart = xtr.getNamespaceCount(xtr.getDepth()-1);
    int nsEnd = xtr.getNamespaceCount(xtr.getDepth());
    Log.w(TAG, "parse called with unexpected behaviour (Jakob)");
    for (int i = nsStart; i < nsEnd; i++) {
      String prefix = xtr.getNamespacePrefix(i);
      if (StringUtils.isNotEmpty(prefix)) {
        model.addNamespace(prefix, xtr.getNamespaceUri(i));
      }
    }
//    for (int i = 0; i < xtr.getNamespaceCount(3); i++) {
//      String prefix = xtr.getNamespacePrefix(i);
//      if (StringUtils.isNotEmpty(prefix)) {
//        model.addNamespace(prefix, xtr.getNamespaceUri(i));
//      }
//    }
    
    for (int i = 0; i < xtr.getAttributeCount(); i++) {
      ExtensionAttribute extensionAttribute = new ExtensionAttribute();
      extensionAttribute.setName(xtr.getAttributeName(i));
      extensionAttribute.setValue(xtr.getAttributeValue(i));
      if (StringUtils.isNotEmpty(xtr.getAttributeNamespace(i))) {
        extensionAttribute.setNamespace(xtr.getAttributeNamespace(i));
      }
      if (StringUtils.isNotEmpty(xtr.getAttributePrefix(i))) {
        extensionAttribute.setNamespacePrefix(xtr.getAttributePrefix(i));
      }
      if (!BpmnXMLUtil.isBlacklisted(extensionAttribute, defaultAttributes)) {
        model.addDefinitionsAttribute(extensionAttribute);
      }
    }
  }
}
