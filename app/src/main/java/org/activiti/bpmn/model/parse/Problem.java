package org.activiti.bpmn.model.parse;

import org.xmlpull.v1.XmlPullParser;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.xmlpull.v1.XmlPullParser;

public class Problem {

  protected String errorMessage;
  protected String resource;
  protected int line;
  protected int column;
  
  public Problem(String errorMessage, XmlPullParser xtr) {
    this.errorMessage = errorMessage;
    this.resource = xtr.getName();
    this.line = xtr.getLineNumber();
    this.column = xtr.getColumnNumber();
  }
  
  public Problem(String errorMessage, BaseElement element) {
    this.errorMessage = errorMessage;
    this.resource = element.getId();
    line = element.getXmlRowNumber();
    column = element.getXmlColumnNumber();
  }
  
  public Problem(String errorMessage, GraphicInfo graphicInfo) {
    this.errorMessage = errorMessage;
    line = graphicInfo.getXmlRowNumber();
    column = graphicInfo.getXmlColumnNumber();
  }
  
  public String toString() {
    return errorMessage + (resource != null ? " | "+resource : "") + " | line " +line + " | column " + column;
  }
}
