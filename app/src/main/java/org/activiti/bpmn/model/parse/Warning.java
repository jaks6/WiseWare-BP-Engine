package org.activiti.bpmn.model.parse;

import org.xmlpull.v1.XmlPullParser;

import org.activiti.bpmn.model.BaseElement;
import org.xmlpull.v1.XmlPullParser;

public class Warning {

  protected String warningMessage;
  protected String resource;
  protected int line;
  protected int column;
  
  public Warning(String warningMessage, XmlPullParser xtr) {
    this.warningMessage = warningMessage;
    this.resource = xtr.getName();
    this.line = xtr.getLineNumber();
    this.column = xtr.getColumnNumber();
  }
  
  public Warning(String warningMessage, BaseElement element) {
    this.warningMessage = warningMessage;
    this.resource = element.getId();
    line = element.getXmlRowNumber();
    column = element.getXmlColumnNumber();
  }
  
  public String toString() {
    return warningMessage + (resource != null ? " | "+resource : "") + " | line " +line + " | column " + column;
  }
}
