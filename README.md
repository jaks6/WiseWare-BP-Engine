As presented in:
J. Mass, C. Chang and S. Srirama, “WiseWare: A Device-to-Device-based Business Process Management System for Industrial Internet of Things,” in the 9th IEEE International Conference on Internet of Things (iThings 2016)
, Chengdu, China (if given), 2016

# Activiti 5.21 Android port #
This port is based on work done by  [Kashif Dar et al.](http://www.sciencedirect.com/science/article/pii/S1574119214001862), who kindly shared the source code of their port of Activiti 5.13. This project is heavily based on the work done by them.
This is a very experimental project, several modules have been removed or modified, so do not expect all of Activitis features to work.

## Activiti modules included in this project ##

* Activiti Engine
* Activiti Model
* Activiti Converter


-----------------------
# What has been changed to adapt Activiti to Android? #

The following modules have been adapted:

* **XML Parsing:** instead of XMLStreamReader and XMLStreamWriter from javax.xml.stream, using XmlPullParser and XmlSerializer from org.xmlpull.v1.
* **Java Beans:** using **com.googlecode.openbeans** instead of **java.beans**
* **SQLDroid** SQLDroid used for persistence
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

Known issues (by no means is this a definitive list):

When querying the DB for process instances, e.g. with:

```
#!Java

List<ProcessInstance> instanceList = event.getEngineServices().getRuntimeService()
                .createProcessInstanceQuery().active().list();
```
The above code should filter process instances with the state "Active" and return a list of them. The filtering method "active()" causes this code to fail with the message:

```
#!java

java.lang.ClassNotFoundException: Didn't find class "java.beans.Introspector" on path: DexPathList.......
```
