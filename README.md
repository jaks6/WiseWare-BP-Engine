# Activiti 5.21 Android port #
This port is based on work done by  [Kashif Dar et al.](http://www.sciencedirect.com/science/article/pii/S1574119214001862), who kindly shared the source code of their port of Activiti 5.13. This project is heavily based on the work done by them.
This is a very experimental project, several modules have been removed or modified, so do not expect all of Activitis features to work.

## Activiti modules included in this project ##

* Activiti Engine
* Activiti Model
* Activiti Converter


-----------------------
# What has been changed to adapt Activiti to Android? #

The following modules have been adapted
* **XML Parsing**: instead of javax.xml.stream.XMLStreamReader; and javax.xml.stream.XMLStreamWriter;, using org.xmlpull.v1.XmlPullParser and org.xmlpull.v1.XmlSerializer
* using **com.googlecode.openbeans** instead of **java.beans**
* SQLDroid is used persistence
* and more


-----------------------
### Removed Classes (Functionality) ###
*The following classes that belong to the Activiti software have been entirely commented out:*

### Business rules ###
* BusinessRuleTaskActivityBehavior.java
* BusinessRuleParseHandler.java
* RulesAgendaFilter.java
* RulesDeployer.java
* RulesHelper.java

### Diagram generation ###
* ProcessDiagramCanvas.java
* ProcessDiagramGenerator.java
* ProcessDiagramLayoutFactory.java

### Script execution ###
* ScriptExecutionListener.java
* ScriptTaskListener.java

### JTA related ###
* JtaTransactionContext.java
* JtaTransactionContextFactory.java
* JtaProcessEngineConfiguration.java
* JtaRetryInterceptor.java
* JtaTransactionInterceptor.java

### Other ###
* SpringBeanFactoryProxyMap.java
* BeansConfigurationHelper.java
* DbSchemaCreate.java
* MailActivityBehavior.java
* Bpmn20NamespaceContext.java