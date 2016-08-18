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

import org.xmlpull.v1.XmlPullParser;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.GraphicInfo;

/**
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public class BpmnShapeParser implements BpmnXMLConstants {
  
  public void parse(XmlPullParser xtr, BpmnModel model) throws Exception {
    
  	String id = xtr.getAttributeValue(null, ATTRIBUTE_DI_BPMNELEMENT);
  	GraphicInfo graphicInfo = new GraphicInfo();
  	
  	String strIsExpanded = xtr.getAttributeValue(null, ATTRIBUTE_DI_IS_EXPANDED);
    if ("true".equalsIgnoreCase(strIsExpanded)) {
      graphicInfo.setExpanded(true);
    }
  	
    BpmnXMLUtil.addXMLLocation(graphicInfo, xtr);
	  int eventType = xtr.getEventType();

	  while (eventType != XmlPullParser.END_DOCUMENT) {
//			xtr.next();
			if (eventType == XmlPullParser.START_TAG && ELEMENT_DI_BOUNDS.equalsIgnoreCase(xtr.getName())) {
				graphicInfo.setX(Double.valueOf(xtr.getAttributeValue(null, ATTRIBUTE_DI_X)));
				graphicInfo.setY(Double.valueOf(xtr.getAttributeValue(null, ATTRIBUTE_DI_Y)));
				graphicInfo.setWidth(Double.valueOf(xtr.getAttributeValue(null, ATTRIBUTE_DI_WIDTH)));
				graphicInfo.setHeight(Double.valueOf(xtr.getAttributeValue(null, ATTRIBUTE_DI_HEIGHT)));
				
				model.addGraphicInfo(id, graphicInfo);
				break;
			} else if (xtr.getEventType() == XmlPullParser.END_TAG && ELEMENT_DI_SHAPE.equalsIgnoreCase(xtr.getName())) {
				break;
			}
		  eventType = xtr.next();
		}
  }
  
  public BaseElement parseElement() {
  	return null;
  }
}
