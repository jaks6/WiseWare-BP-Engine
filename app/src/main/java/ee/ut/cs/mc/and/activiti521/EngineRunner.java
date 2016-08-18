package ee.ut.cs.mc.and.activiti521;

import android.util.Log;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Jakob on 16.08.2016.
 */

public class EngineRunner {

    private static final String TAG = EngineRunner.class.getName();
    private static final String PROCESS_KEY = "myProcess";
    private File dbFile;
    private String url;
    String driver = "org.sqldroid.SQLDroidDriver";
    private Connection con;
    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;


    void startEngine(){

        connectToDb();

        // Get process engine
        ProcessEngines.init();

        processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP)
                .setJdbcDriver(driver)
                .setJdbcUrl(url)
                .setCreateDiagramOnDeploy(false)
//	      		  .setHistory(HistoryLevel.NONE.getKey())
                .setJobExecutorActivate(true)
                .buildProcessEngine();
        Log.i(TAG, "Process Engine built");

        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();

        Log.i(TAG, "No of deployments=" + repositoryService.createProcessDefinitionQuery().list().size());
        Log.i(TAG, "About to create deployment");

        repositoryService.createDeployment()
                .addClasspathResource("juneprocess.bpmn")
                .deploy();

        Log.i(TAG, "Process Deployed!");
        Log.i(TAG, "No of deployments=" + repositoryService.createProcessDefinitionQuery().list().size());
        Log.i(TAG, "Deployed proceess name = " + repositoryService.createProcessDefinitionQuery().list().get(0).getName());

        Log.i(TAG, "Starting Process Instance using key:" + PROCESS_KEY);
        runtimeService.startProcessInstanceByKey(PROCESS_KEY);



    }

    public void connectToDb(){
        try {
            dbFile = File.createTempFile("main.sqlite", null);
        } catch (IOException e2) {
            e2.printStackTrace();
        }


        url = "jdbc:sqlite:/" + dbFile.getPath();

        System.out.println(getClass().getCanonicalName() + ": SQLite path: " + url);
        try {
            Class.forName("org.sqldroid.SQLDroidDriver").newInstance();

            if (dbFile.exists()) {
                dbFile.delete();
            }

            con = DriverManager.getConnection(url);

        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
