package org.activiti.bpmn.converter.export;

import org.xmlpull.v1.XmlSerializer;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.DataStore;
import org.apache.commons.lang3.StringUtils;

public class DataStoreExport implements BpmnXMLConstants {

  public static void writeDataStores(BpmnModel model, XmlSerializer xtw) throws Exception {
    
    for (DataStore dataStore : model.getDataStores().values()) {
      xtw.startTag("", ELEMENT_DATA_STORE);
      xtw.attribute("", ATTRIBUTE_ID, dataStore.getId());
      xtw.attribute("", ATTRIBUTE_NAME, dataStore.getName());
      if (StringUtils.isNotEmpty(dataStore.getItemSubjectRef())) {
        xtw.attribute("", ATTRIBUTE_ITEM_SUBJECT_REF, dataStore.getItemSubjectRef());
      }
      
      if (StringUtils.isNotEmpty(dataStore.getDataState())) {
        xtw.startTag("", ELEMENT_DATA_STATE);
        xtw.text(dataStore.getDataState());
        xtw.endTag("", ELEMENT_DATA_STATE);

      }

      xtw.endTag("", ELEMENT_DATA_STORE);

    }
  }
}
