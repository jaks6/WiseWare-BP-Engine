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
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tijs Rademakers
 */
public abstract class BaseChildElementParser implements BpmnXMLConstants {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(BaseChildElementParser.class);

  public abstract String getElementName();
  
  public abstract void parseChildElement(XmlPullParser xtr, BaseElement parentElement, BpmnModel model) throws Exception;
  
  protected void parseChildElements(XmlPullParser xtr, BaseElement parentElement, BpmnModel model, BaseChildElementParser parser) throws Exception {
    boolean readyWithChildElements = false;
    int eventType = xtr.getEventType();
    while (readyWithChildElements == false && eventType != XmlPullParser.END_DOCUMENT) {
//      xtr.next();
      if (eventType == XmlPullParser.START_TAG) {
        if (parser.getElementName().equals(xtr.getName())) {
          parser.parseChildElement(xtr, parentElement, model);
        }

      } else if (eventType == XmlPullParser.END_TAG && getElementName().equalsIgnoreCase(xtr.getName())) {
        readyWithChildElements = true;
      }

      eventType = xtr.next();
    }
  }

  public boolean accepts(BaseElement element){
    return element!=null;
  };
}
