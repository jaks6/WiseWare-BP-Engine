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
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.MessageFlow;
import org.activiti.bpmn.model.SubProcess;
import org.apache.commons.lang3.StringUtils;

public class BPMNDIExport implements BpmnXMLConstants {

  public static void writeBPMNDI(BpmnModel model, XmlSerializer xtw) throws Exception {
    // BPMN DI information
    xtw.startTag(BPMNDI_NAMESPACE, ELEMENT_DI_DIAGRAM);
    
    String processId = null;
    if(!model.getPools().isEmpty()) {
      processId = "Collaboration";
    } else {
      processId = model.getMainProcess().getId();
    }

    xtw.attribute("", ATTRIBUTE_ID, "BPMNDiagram_" + processId);

    xtw.startTag(BPMNDI_NAMESPACE, ELEMENT_DI_PLANE );
    xtw.attribute("", ATTRIBUTE_DI_BPMNELEMENT, processId);
    xtw.attribute("", ATTRIBUTE_ID, "BPMNPlane_" + processId);
    
    for (String elementId : model.getLocationMap().keySet()) {
      
      if (model.getFlowElement(elementId) != null || model.getArtifact(elementId) != null || 
          model.getPool(elementId) != null || model.getLane(elementId) != null) {

        xtw.startTag(BPMNDI_NAMESPACE, ELEMENT_DI_SHAPE);
        xtw.attribute("", ATTRIBUTE_DI_BPMNELEMENT, elementId);
        xtw.attribute("", ATTRIBUTE_ID, "BPMNShape_" + elementId);
        
        GraphicInfo graphicInfo = model.getGraphicInfo(elementId);
        FlowElement flowElement = model.getFlowElement(elementId);
        if (flowElement instanceof SubProcess && graphicInfo.getExpanded() != null) {
          xtw.attribute("", ATTRIBUTE_DI_IS_EXPANDED, String.valueOf(graphicInfo.getExpanded()));

        }

        xtw.startTag(OMGDC_NAMESPACE, ELEMENT_DI_BOUNDS);
        xtw.attribute("", ATTRIBUTE_DI_HEIGHT, "" + graphicInfo.getHeight());
        xtw.attribute("", ATTRIBUTE_DI_WIDTH, "" + graphicInfo.getWidth());
        xtw.attribute("", ATTRIBUTE_DI_X, "" + graphicInfo.getX());
        xtw.attribute("", ATTRIBUTE_DI_Y, "" + graphicInfo.getY());
        xtw.endTag(OMGDC_NAMESPACE, ELEMENT_DI_BOUNDS);

        xtw.endTag(BPMNDI_NAMESPACE, ELEMENT_DI_SHAPE);
      }
    }
    
    for (String elementId : model.getFlowLocationMap().keySet()) {
      
      if (model.getFlowElement(elementId) != null || model.getArtifact(elementId) != null || 
          model.getMessageFlow(elementId) != null) {

        xtw.startTag(BPMNDI_NAMESPACE, ELEMENT_DI_EDGE);
        xtw.attribute("", ATTRIBUTE_DI_BPMNELEMENT, elementId);
        xtw.attribute("", ATTRIBUTE_ID, "BPMNEdge_" + elementId);
        
        List<GraphicInfo> graphicInfoList = model.getFlowLocationGraphicInfo(elementId);
        for (GraphicInfo graphicInfo : graphicInfoList) {
          xtw.startTag(OMGDI_NAMESPACE, ELEMENT_DI_WAYPOINT);
          xtw.attribute("", ATTRIBUTE_DI_X, "" + graphicInfo.getX());
          xtw.attribute("", ATTRIBUTE_DI_Y, "" + graphicInfo.getY());
          xtw.endTag(OMGDI_NAMESPACE, ELEMENT_DI_WAYPOINT);
        }
        
        GraphicInfo labelGraphicInfo = model.getLabelGraphicInfo(elementId);
        FlowElement flowElement = model.getFlowElement(elementId);
        MessageFlow messageFlow = null;
        if (flowElement == null) {
          messageFlow = model.getMessageFlow(elementId);
        }
        
        boolean hasName = false;
        if (flowElement != null && StringUtils.isNotEmpty(flowElement.getName())) {
          hasName = true;
        
        } else if (messageFlow != null && StringUtils.isNotEmpty(messageFlow.getName())) {
          hasName = true;
        }
        
        if (labelGraphicInfo != null && hasName) {
          xtw.startTag(BPMNDI_NAMESPACE, ELEMENT_DI_LABEL);
          xtw.startTag(OMGDC_NAMESPACE, ELEMENT_DI_BOUNDS);
          xtw.attribute("", ATTRIBUTE_DI_HEIGHT, "" + labelGraphicInfo.getHeight());
          xtw.attribute("", ATTRIBUTE_DI_WIDTH, "" + labelGraphicInfo.getWidth());
          xtw.attribute("", ATTRIBUTE_DI_X, "" + labelGraphicInfo.getX());
          xtw.attribute("", ATTRIBUTE_DI_Y, "" + labelGraphicInfo.getY());
          xtw.endTag(OMGDC_NAMESPACE, ELEMENT_DI_BOUNDS);
          xtw.endTag(BPMNDI_NAMESPACE, ELEMENT_DI_LABEL);
        }

        xtw.endTag(BPMNDI_NAMESPACE, ELEMENT_DI_EDGE);
      }
    }
    
    // end BPMN DI elements
    xtw.endTag(BPMNDI_NAMESPACE, ELEMENT_DI_PLANE);
    xtw.endTag(BPMNDI_NAMESPACE, ELEMENT_DI_DIAGRAM);
  }
}
