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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
//import  javax.xml.stream.XMLInputFactory;
//import  javax.xml.stream.XMLOutputFactory;
//import  javax.xml.stream.XMLStreamException;

import org.activiti.bpmn.model.parse.Problem;
import org.activiti.bpmn.model.parse.Warning;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import javax.xml.transform.sax.SAXSource;
import mf.javax.xml.transform.stream.StreamSource;
import mf.javax.xml.validation.Schema;
import mf.javax.xml.validation.SchemaFactory;
import mf.javax.xml.validation.Validator;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.alfresco.AlfrescoStartEventXMLConverter;
import org.activiti.bpmn.converter.alfresco.AlfrescoUserTaskXMLConverter;
import org.activiti.bpmn.converter.child.DocumentationParser;
import org.activiti.bpmn.converter.child.IOSpecificationParser;
import org.activiti.bpmn.converter.child.MultiInstanceParser;
import org.activiti.bpmn.converter.export.ActivitiListenerExport;
import org.activiti.bpmn.converter.export.BPMNDIExport;
import org.activiti.bpmn.converter.export.CollaborationExport;
import org.activiti.bpmn.converter.export.DataStoreExport;
import org.activiti.bpmn.converter.export.DefinitionsRootExport;
import org.activiti.bpmn.converter.export.MultiInstanceExport;
import org.activiti.bpmn.converter.export.ProcessExport;
import org.activiti.bpmn.converter.export.SignalAndMessageDefinitionExport;
import org.activiti.bpmn.converter.parser.BpmnEdgeParser;
import org.activiti.bpmn.converter.parser.BpmnShapeParser;
import org.activiti.bpmn.converter.parser.DataStoreParser;
import org.activiti.bpmn.converter.parser.DefinitionsParser;
import org.activiti.bpmn.converter.parser.ExtensionElementsParser;
import org.activiti.bpmn.converter.parser.ImportParser;
import org.activiti.bpmn.converter.parser.InterfaceParser;
import org.activiti.bpmn.converter.parser.ItemDefinitionParser;
import org.activiti.bpmn.converter.parser.LaneParser;
import org.activiti.bpmn.converter.parser.MessageFlowParser;
import org.activiti.bpmn.converter.parser.MessageParser;
import org.activiti.bpmn.converter.parser.ParticipantParser;
import org.activiti.bpmn.converter.parser.PotentialStarterParser;
import org.activiti.bpmn.converter.parser.ProcessParser;
import org.activiti.bpmn.converter.parser.ResourceParser;
import org.activiti.bpmn.converter.parser.SignalParser;
import org.activiti.bpmn.converter.parser.SubProcessParser;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.converter.util.InputStreamProvider;
import org.activiti.bpmn.exceptions.XMLException;
import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BooleanDataObject;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.DateDataObject;
import org.activiti.bpmn.model.DoubleDataObject;
import org.activiti.bpmn.model.EventSubProcess;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.IntegerDataObject;
import org.activiti.bpmn.model.LongDataObject;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StringDataObject;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.bpmn.model.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public class BpmnXMLConverter implements BpmnXMLConstants {

  protected static final Logger LOGGER = LoggerFactory.getLogger(BpmnXMLConverter.class);
	
  protected static final String BPMN_XSD = "org/activiti/impl/bpmn/parser/BPMN20.xsd";
  protected static final String DEFAULT_ENCODING = "UTF-8";
  
	protected static Map<String, BaseBpmnXMLConverter> convertersToBpmnMap = new HashMap<String, BaseBpmnXMLConverter>();
	protected static Map<Class<? extends BaseElement>, BaseBpmnXMLConverter> convertersToXMLMap = 
	    new HashMap<Class<? extends BaseElement>, BaseBpmnXMLConverter>();
	
	protected ClassLoader classloader;
	protected List<String> userTaskFormTypes;
	protected List<String> startEventFormTypes;
	
	protected BpmnEdgeParser bpmnEdgeParser = new BpmnEdgeParser();
	protected BpmnShapeParser bpmnShapeParser = new BpmnShapeParser();
	protected DefinitionsParser definitionsParser = new DefinitionsParser();
	protected DocumentationParser documentationParser = new DocumentationParser();
	protected ExtensionElementsParser extensionElementsParser = new ExtensionElementsParser();
	protected ImportParser importParser = new ImportParser();
	protected InterfaceParser interfaceParser = new InterfaceParser();
  protected ItemDefinitionParser itemDefinitionParser = new ItemDefinitionParser();
  protected IOSpecificationParser ioSpecificationParser = new IOSpecificationParser();
  protected DataStoreParser dataStoreParser = new DataStoreParser();
  protected LaneParser laneParser = new LaneParser();
  protected MessageParser messageParser = new MessageParser();
  protected MessageFlowParser messageFlowParser = new MessageFlowParser();
  protected MultiInstanceParser multiInstanceParser = new MultiInstanceParser();
  protected ParticipantParser participantParser = new ParticipantParser();
  protected PotentialStarterParser potentialStarterParser = new PotentialStarterParser();
  protected ProcessParser processParser = new ProcessParser();
  protected ResourceParser resourceParser = new ResourceParser();
  protected SignalParser signalParser = new SignalParser();
  protected SubProcessParser subProcessParser = new SubProcessParser();
	
	static {
		// events
	  addConverter(new EndEventXMLConverter());
	  addConverter(new StartEventXMLConverter());
    
    // tasks
	  addConverter(new BusinessRuleTaskXMLConverter());
    addConverter(new ManualTaskXMLConverter());
    addConverter(new ReceiveTaskXMLConverter());
    addConverter(new ScriptTaskXMLConverter());
    addConverter(new ServiceTaskXMLConverter());
    addConverter(new SendTaskXMLConverter());
    addConverter(new UserTaskXMLConverter());
    addConverter(new TaskXMLConverter());
    addConverter(new CallActivityXMLConverter());
    
    // gateways
    addConverter(new EventGatewayXMLConverter());
    addConverter(new ExclusiveGatewayXMLConverter());
    addConverter(new InclusiveGatewayXMLConverter());
    addConverter(new ParallelGatewayXMLConverter());
    addConverter(new ComplexGatewayXMLConverter());
    
    // connectors
    addConverter(new SequenceFlowXMLConverter());
    
    // catch, throw and boundary event
    addConverter(new CatchEventXMLConverter());
    addConverter(new ThrowEventXMLConverter());
    addConverter(new BoundaryEventXMLConverter());
    
    // artifacts
    addConverter(new TextAnnotationXMLConverter());
    addConverter(new AssociationXMLConverter());
    
    // data store reference
    addConverter(new DataStoreReferenceXMLConverter());
    
    // data objects
    addConverter(new ValuedDataObjectXMLConverter(), StringDataObject.class);
    addConverter(new ValuedDataObjectXMLConverter(), BooleanDataObject.class);
    addConverter(new ValuedDataObjectXMLConverter(), IntegerDataObject.class);
    addConverter(new ValuedDataObjectXMLConverter(), LongDataObject.class);
    addConverter(new ValuedDataObjectXMLConverter(), DoubleDataObject.class);
    addConverter(new ValuedDataObjectXMLConverter(), DateDataObject.class);
    
    // Alfresco types
    addConverter(new AlfrescoStartEventXMLConverter());
    addConverter(new AlfrescoUserTaskXMLConverter());
  }
  
  public static void addConverter(BaseBpmnXMLConverter converter) {
    addConverter(converter, converter.getBpmnElementType());
  }
  
  public static void addConverter(BaseBpmnXMLConverter converter, Class<? extends BaseElement> elementType) {
    convertersToBpmnMap.put(converter.getXMLElementName(), converter);
    convertersToXMLMap.put(elementType, converter);
  }
  
  public void setClassloader(ClassLoader classloader) {
    this.classloader = classloader;
  }

  public void setUserTaskFormTypes(List<String> userTaskFormTypes) {
    this.userTaskFormTypes = userTaskFormTypes;
  }
  
  public void setStartEventFormTypes(List<String> startEventFormTypes) {
    this.startEventFormTypes = startEventFormTypes;
  }
  
  public void validateModel(InputStreamProvider inputStreamProvider) throws Exception {
    Schema schema = createSchema();
    
    Validator validator = schema.newValidator();
    validator.validate(new StreamSource(inputStreamProvider.getInputStream()));
  }
  
//  public void validateModel(XMLStreamReader xmlStreamReader) throws Exception {
//    Schema schema = createSchema();
//
//    Validator validator = schema.newValidator();
//    validator.validate(new StAXSource(xmlStreamReader));
//  }

  protected Schema createSchema() throws SAXException {
//    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      ClassLoader cl;
      cl = Thread.currentThread().getContextClassLoader();

      if (cl == null) {
          //cl = ClassLoader.getSystemClassLoader();
          //use the current class loader
          cl = SchemaFactory.class.getClassLoader();
      }

      mf.javax.xml.validation.SchemaFactory factory = mf.javax.xml.validation.SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI, "mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory", cl);
      Schema schema = null;
    if (classloader != null) {
      schema = factory.newSchema(classloader.getResource(BPMN_XSD));
    }
    
    if (schema == null) {
      schema = factory.newSchema(BpmnXMLConverter.class.getClassLoader().getResource(BPMN_XSD));
    }
    
    if (schema == null) {
      throw new XMLException("BPMN XSD could not be found");
    }
    return schema;
  }
  
  public BpmnModel convertToBpmnModel(InputStreamProvider inputStreamProvider, boolean validateSchema, boolean enableSafeBpmnXml) {
    return convertToBpmnModel(inputStreamProvider, validateSchema, enableSafeBpmnXml, DEFAULT_ENCODING);
  }
  
  public BpmnModel convertToBpmnModel(InputStreamProvider inputStreamProvider, boolean validateSchema, boolean enableSafeBpmnXml, String encoding) {
//    XMLInputFactory xif = XMLInputFactory.newInstance();
//
      XmlPullParserFactory xif = null;
      try {
          xif = XmlPullParserFactory.newInstance();
      } catch (XmlPullParserException e1) {
          e1.printStackTrace();
      }
//    if (xif.isPropertySupported(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES)) {
//      xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
//    }
//    if (xif.isPropertySupported(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES)) {
//      xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
//    }
//    if (xif.isPropertySupported(XMLInputFactory.SUPPORT_DTD)) {
//      xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
//    }

      InputStreamReader in = null;
      XmlPullParser xtr = null;
    try {
//      XmlPullParser xtr = xif.createXMLStreamReader(in);
            xif.setNamespaceAware(true);
            xtr = xif.newPullParser();
            xtr.setInput(inputStreamProvider.getInputStream(), encoding);
        } catch (XmlPullParserException e1) {
            e1.printStackTrace();
        }

      try {
        if (validateSchema) {
            validateModel(inputStreamProvider);
//          if (!enableSafeBpmnXml) {
//            validateModel(inputStreamProvider);
//          } else {
//            validateModel(xtr);
//          }
  
          // The input stream is closed after schema validation
//          in = new InputStreamReader(inputStreamProvider.getInputStream(), encoding);
//          xtr = xif.createXMLStreamReader(in);
            xtr = xif.newPullParser();
            xtr.setInput(inputStreamProvider.getInputStream(), encoding);
        }
          return convertToBpmnModel(xtr);
  
      } catch (Exception e) {
        throw new XMLException(e.getMessage(), e);
      }
  
      // XML conversion

//    } catch (UnsupportedEncodingException e) {
//      throw new XMLException("The bpmn 2.0 xml is not UTF8 encoded", e);
//    } catch (XMLStreamException e) {
//      throw new XMLException("Error while reading the BPMN 2.0 XML", e);
//    }
    finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          LOGGER.debug("Problem closing BPMN input stream", e);
        }
      }
    }
  }

	public BpmnModel convertToBpmnModel(XmlPullParser xtr) {

        Problem p = new Problem("p", xtr);
        Warning w = new Warning("w", xtr);

        System.out.println(getClass().getCanonicalName() + ": " + p.toString() + " " + w.toString());

	  BpmnModel model = new BpmnModel();
//	  model.setStartEventFormTypes(startEventFormTypes);
//	  model.setUserTaskFormTypes(userTaskFormTypes);
		try {
			Process activeProcess = null;
			List<SubProcess> activeSubProcessList = new ArrayList<SubProcess>();
            int eventType = xtr.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
//				try {
//					xtr.next();
//				} catch(Exception e) {
//					LOGGER.debug("Error reading XML document", e);
//					throw new XMLException("Error reading XML", e);
//				}

				if (eventType == XmlPullParser.END_TAG  && ELEMENT_SUBPROCESS.equals(xtr.getName())) {
					activeSubProcessList.remove(activeSubProcessList.size() - 1);
				}
				
				if (eventType == XmlPullParser.END_TAG  && ELEMENT_TRANSACTION.equals(xtr.getName())) {
          activeSubProcessList.remove(activeSubProcessList.size() - 1);
        }

				if (xtr.getEventType() == XmlPullParser.START_TAG == false) {
                    eventType = xtr.next();
					continue;
				}

				if (ELEMENT_DEFINITIONS.equals(xtr.getName())) {
				  definitionsParser.parse(xtr, model);
				  
        } else if (ELEMENT_RESOURCE.equals(xtr.getName())) {
          resourceParser.parse(xtr, model);
          
				} else if (ELEMENT_SIGNAL.equals(xtr.getName())) {
					signalParser.parse(xtr, model);
					
				} else if (ELEMENT_MESSAGE.equals(xtr.getName())) {
          messageParser.parse(xtr, model);
          
				} else if (ELEMENT_ERROR.equals(xtr.getName())) {
          
          if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, ATTRIBUTE_ID))) {
            model.addError(xtr.getAttributeValue(null, ATTRIBUTE_ID),
                xtr.getAttributeValue(null, ATTRIBUTE_ERROR_CODE));
          }
          
				} else if (ELEMENT_IMPORT.equals(xtr.getName())) {
				  importParser.parse(xtr, model);
          
				} else if (ELEMENT_ITEM_DEFINITION.equals(xtr.getName())) {
				  itemDefinitionParser.parse(xtr, model);
          
				} else if (ELEMENT_DATA_STORE.equals(xtr.getName())) {
				  dataStoreParser.parse(xtr, model);
				  
				} else if (ELEMENT_INTERFACE.equals(xtr.getName())) {
				  interfaceParser.parse(xtr, model);
				  
				} else if (ELEMENT_IOSPECIFICATION.equals(xtr.getName())) {
				  ioSpecificationParser.parseChildElement(xtr, activeProcess, model);
					
				} else if (ELEMENT_PARTICIPANT.equals(xtr.getName())) {
				  participantParser.parse(xtr, model);
				  
				} else if (ELEMENT_MESSAGE_FLOW.equals(xtr.getName())) {
          messageFlowParser.parse(xtr, model);

				} else if (ELEMENT_PROCESS.equals(xtr.getName())) {
					
				  Process process = processParser.parse(xtr, model);
				  if (process != null) {
            activeProcess = process;	
				  }
				
				} else if (ELEMENT_POTENTIAL_STARTER.equals(xtr.getName())) {
				  potentialStarterParser.parse(xtr, activeProcess);
				  
				} else if (ELEMENT_LANE.equals(xtr.getName())) {
          laneParser.parse(xtr, activeProcess, model);
					
				} else if (ELEMENT_DOCUMENTATION.equals(xtr.getName())) {
				  
					BaseElement parentElement = null;
					if (!activeSubProcessList.isEmpty()) {
						parentElement = activeSubProcessList.get(activeSubProcessList.size() - 1);
					} else if (activeProcess != null) {
						parentElement = activeProcess;
					}
					documentationParser.parseChildElement(xtr, parentElement, model);
				
				} else if (activeProcess == null && ELEMENT_TEXT_ANNOTATION.equals(xtr.getName())) {
				  String elementId = xtr.getAttributeValue(null, ATTRIBUTE_ID);
          TextAnnotation textAnnotation = (TextAnnotation) new TextAnnotationXMLConverter().convertXMLToElement(xtr, model);
          textAnnotation.setId(elementId);
          model.getGlobalArtifacts().add(textAnnotation);
          
				} else if (activeProcess == null && ELEMENT_ASSOCIATION.equals(xtr.getName())) {
          String elementId = xtr.getAttributeValue(null, ATTRIBUTE_ID);
          Association association = (Association) new AssociationXMLConverter().convertXMLToElement(xtr, model);
          association.setId(elementId);
          model.getGlobalArtifacts().add(association);
				
				} else if (ELEMENT_EXTENSIONS.equals(xtr.getName())) {
				  extensionElementsParser.parse(xtr, activeSubProcessList, activeProcess, model);
				
				} else if (ELEMENT_SUBPROCESS.equals(xtr.getName())) {
          subProcessParser.parse(xtr, activeSubProcessList, activeProcess);
          
				} else if (ELEMENT_TRANSACTION.equals(xtr.getName())) {
          subProcessParser.parse(xtr, activeSubProcessList, activeProcess);
					
				} else if (ELEMENT_DI_SHAPE.equals(xtr.getName())) {
          bpmnShapeParser.parse(xtr, model);
				
				} else if (ELEMENT_DI_EDGE.equals(xtr.getName())) {
				  bpmnEdgeParser.parse(xtr, model);

				} else {

					if (!activeSubProcessList.isEmpty() && ELEMENT_MULTIINSTANCE.equalsIgnoreCase(xtr.getName())) {
						
					  multiInstanceParser.parseChildElement(xtr, activeSubProcessList.get(activeSubProcessList.size() - 1), model);
					  
					} else if (convertersToBpmnMap.containsKey(xtr.getName())) {
					  if (activeProcess != null) {
  					  BaseBpmnXMLConverter converter = convertersToBpmnMap.get(xtr.getName());
  					  converter.convertToBpmnModel(xtr, model, activeProcess, activeSubProcessList);
					  }
					}
				}
                eventType = xtr.next();
			}

			for (Process process : model.getProcesses()) {
			  for (Pool pool : model.getPools()) {
			    if (process.getId().equals(pool.getProcessRef())) {
			      pool.setExecutable(process.isExecutable());
			    }
			  }
			  processFlowElements(process.getFlowElements(), process);
			}
		
		} catch (XMLException e) {
		  throw e;
		  
		} catch (Exception e) {
			LOGGER.error("Error processing BPMN document", e);
			throw new XMLException("Error processing BPMN document", e);
		}
		return model;
	}
	
	private void processFlowElements(Collection<FlowElement> flowElementList, BaseElement parentScope) {
	  for (FlowElement flowElement : flowElementList) {
  	  if (flowElement instanceof SequenceFlow) {
        SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
        FlowNode sourceNode = getFlowNodeFromScope(sequenceFlow.getSourceRef(), parentScope);
        if (sourceNode != null) {
          sourceNode.getOutgoingFlows().add(sequenceFlow);
        }
        FlowNode targetNode = getFlowNodeFromScope(sequenceFlow.getTargetRef(), parentScope);
        if (targetNode != null) {
          targetNode.getIncomingFlows().add(sequenceFlow);
        }
      } else if (flowElement instanceof BoundaryEvent) {
        BoundaryEvent boundaryEvent = (BoundaryEvent) flowElement;
        FlowElement attachedToElement = getFlowNodeFromScope(boundaryEvent.getAttachedToRefId(), parentScope);
        if(attachedToElement != null) {
          boundaryEvent.setAttachedToRef((Activity) attachedToElement);
          ((Activity) attachedToElement).getBoundaryEvents().add(boundaryEvent);
        }
      } else if(flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        processFlowElements(subProcess.getFlowElements(), subProcess);
      }
	  }
	}
	
	private FlowNode getFlowNodeFromScope(String elementId, BaseElement scope) {
	  FlowNode flowNode = null;
	  if (StringUtils.isNotEmpty(elementId)) {
  	  if (scope instanceof Process) {
  	    flowNode = (FlowNode) ((Process) scope).getFlowElement(elementId);
  	  } else if (scope instanceof SubProcess) {
  	    flowNode = (FlowNode) ((SubProcess) scope).getFlowElement(elementId);
  	  }
	  }
	  return flowNode;
	}
	
	public byte[] convertToXML(BpmnModel model) {
	  return convertToXML(model, DEFAULT_ENCODING);
	}
	
	public byte[] convertToXML(BpmnModel model, String encoding) {
    try {

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      
//      XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XmlPullParserFactory xof = XmlPullParserFactory.newInstance();
      OutputStreamWriter out = new OutputStreamWriter(outputStream, encoding);

//      XmlSerializer writer = xof.createXmlSerializer(out);
        XmlSerializer writer = xof.newSerializer(); // createXmlSerializer(out);
        writer.setOutput(out);
//      XmlSerializer xtw = new IndentingXmlSerializer(writer);
        XmlSerializer xtw = writer;

      DefinitionsRootExport.writeRootElement(model, xtw, encoding);
      CollaborationExport.writePools(model, xtw);
      DataStoreExport.writeDataStores(model, xtw);
      SignalAndMessageDefinitionExport.writeSignalsAndMessages(model, xtw);
      
      for (Process process : model.getProcesses()) {
        
        if (process.getFlowElements().isEmpty() && process.getLanes().isEmpty()) {
          // empty process, ignore it 
          continue;
        }
      
        ProcessExport.writeProcess(process, xtw);
        
        for (FlowElement flowElement : process.getFlowElements()) {
          createXML(flowElement, model, xtw);
        }
        
        for (Artifact artifact : process.getArtifacts()) {
          createXML(artifact, model, xtw);
        }
        
        // end process element
          xtw.endTag("", ELEMENT_PROCESS);
      }

      BPMNDIExport.writeBPMNDI(model, xtw);

      // end definitions root element
        xtw.endTag("", ELEMENT_DEFINITIONS);
        xtw.endDocument();

      xtw.flush();

      outputStream.close();

//      xtw.close();
      
      return outputStream.toByteArray();
      
    } catch (Exception e) {
      LOGGER.error("Error writing BPMN XML", e);
      throw new XMLException("Error writing BPMN XML", e);
    }
  }

  private void createXML(FlowElement flowElement, BpmnModel model, XmlSerializer xtw) throws Exception {
    
    if (flowElement instanceof SubProcess) {
      
      SubProcess subProcess = (SubProcess) flowElement;
      if (flowElement instanceof Transaction) {
//        xtw.writeStartElement(ELEMENT_TRANSACTION);
          xtw.startTag("", ELEMENT_TRANSACTION);
      } else {
        xtw.startTag("", ELEMENT_SUBPROCESS);
      }

      xtw.attribute("", ATTRIBUTE_ID, subProcess.getId());
      if (StringUtils.isNotEmpty(subProcess.getName())) {
          xtw.attribute("", ATTRIBUTE_NAME, subProcess.getName());
      } else {
          xtw.attribute("", ATTRIBUTE_NAME, "subProcess");
      }
      
      if (subProcess instanceof EventSubProcess) {
          xtw.attribute("", ATTRIBUTE_TRIGGERED_BY, ATTRIBUTE_VALUE_TRUE);
        
      } else if (subProcess instanceof Transaction == false) {
        if (subProcess.isAsynchronous()) {
          BpmnXMLUtil.writeQualifiedAttribute(ATTRIBUTE_ACTIVITY_ASYNCHRONOUS, ATTRIBUTE_VALUE_TRUE, xtw);
          if (subProcess.isNotExclusive()) {
            BpmnXMLUtil.writeQualifiedAttribute(ATTRIBUTE_ACTIVITY_EXCLUSIVE, ATTRIBUTE_VALUE_FALSE, xtw);
          }
        }
      }
      
      if (StringUtils.isNotEmpty(subProcess.getDocumentation())) {

          xtw.startTag("", ELEMENT_DOCUMENTATION);
          xtw.text(subProcess.getDocumentation());
          xtw.endTag("", ELEMENT_DOCUMENTATION);
      }
      
      boolean didWriteExtensionStartElement = ActivitiListenerExport.writeListeners(subProcess, false, xtw);
      
      didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(subProcess, didWriteExtensionStartElement, model.getNamespaces(), xtw);
      if (didWriteExtensionStartElement) {
        // closing extensions element
          xtw.endTag("", ELEMENT_SUBPROCESS);
      }
      
      MultiInstanceExport.writeMultiInstance(subProcess, xtw);
      
      for (FlowElement subElement : subProcess.getFlowElements()) {
        createXML(subElement, model, xtw);
      }
      
      for (Artifact artifact : subProcess.getArtifacts()) {
        createXML(artifact, model, xtw);
      }
      
//      xtw.writeEndElement();
      
    } else {
    
      BaseBpmnXMLConverter converter = convertersToXMLMap.get(flowElement.getClass());
      
      if (converter == null) {
        throw new XMLException("No converter for " + flowElement.getClass() + " found");
      }
      
      converter.convertToXML(xtw, flowElement, model);
    }
  }
  
  private void createXML(Artifact artifact, BpmnModel model, XmlSerializer xtw) throws Exception {
    
    BaseBpmnXMLConverter converter = convertersToXMLMap.get(artifact.getClass());
      
    if (converter == null) {
      throw new XMLException("No converter for " + artifact.getClass() + " found");
    }
      
    converter.convertToXML(xtw, artifact, model);
  }
}
