package ee.ut.cs.mc.and.activiti521.engine;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ee.ut.cs.mc.and.activiti521.ExperimentUtils;
import ee.ut.cs.mc.and.activiti521.engine.migration.MigrationListener;
import ee.ut.cs.mc.and.activiti521.engine.migration.Migrator;

import static ee.ut.cs.mc.and.activiti521.ExperimentUtils.experimentLog;

/**
 * Created by Jakob on 16.08.2016.
 */

public class EngineThread extends HandlerThread {
    private static final String TAG = EngineThread.class.getSimpleName();

    private static final String driver = "org.sqldroid.SQLDroidDriver";

    private File dbFile;
    private String url;
    private Connection con;

    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;
    private Migrator migrator;

    public Handler mHandler;
    private LocalBroadcastManager broadcastManager;

    @Override
    public synchronized void start() {
        super.start();
        //init handler after looper has been initialized
        mHandler =  new EngineThreadHandler(getLooper(), this);

        // Start the engine using a new runnable(thread)
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                startEngine();
                if (ExperimentUtils.AUTO_DEPLOY_PROCESS) deployProcess();
                if (ExperimentUtils.AUTO_START_PROCESS) startProcess();
                if (ExperimentUtils.AUTO_IMMIGRATE) ExperimentUtils.experimentImmigration(mHandler);
            }
        });
    }

    protected void immigrateProcess() {
        if (migrator == null ){
            migrator = new Migrator(con);
        }
        Log.i(TAG, "Reading .json and storing to DB...");
        migrator.loadStateFromFileToDb();

        experimentLog("Activating loaded instance");
        processEngine.getRuntimeService().activateProcessInstanceById(
                ExperimentUtils.IMMIGRATION_PROC_INST_ID);
        experimentLog("Immigration finished");
    }

    protected void emigrateProcess(String processInstanceId) {
        if (migrator == null ){
            migrator = new Migrator(con);
        }
        try {
            migrator.captureDbStateToFile(processInstanceId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        experimentLog("Finished Migration");
    }

    public EngineThread() {
        super(TAG);
        start();
    }

    private void startEngine(){
        Log.d(TAG, "starting Engine on thread="+ Thread.currentThread().getId());
        connectToDb();

        // Get process engine
        ProcessEngines.init();

        processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP)
                .setJdbcDriver(driver)
                .setJdbcUrl(url)
                .setCreateDiagramOnDeploy(false)
//	      		  .setHistory(HistoryLevel.NONE.getKey())
                .setJobExecutorActivate(true) //Job executor true throws SQLDroid "not implemented" errors
                .buildProcessEngine();
        Log.i(TAG, "Process Engine built");

        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();

        //add event listener
        runtimeService.addEventListener(new MigrationListener(mHandler));
    }

    public void startProcess() {
        Log.i(TAG, "Starting Process Instance using key:" + ExperimentUtils.PROCESS_KEY + " Thread ID=" + Thread.currentThread().getId());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employeeName", "Kermit");
        variables.put("numberOfDays", new Integer(4));
        variables.put("vacationMotivation", "I'm really tired!");
        runtimeService.startProcessInstanceByKey(ExperimentUtils.PROCESS_KEY, variables);
    }

    public void deployProcess() {
        repositoryService.createDeployment()
                .addClasspathResource(ExperimentUtils.PROCESS_RESOURCE_NAME_WISEWARE)
                .deploy();
        Log.i(TAG, "Process Deployed! name = " + repositoryService.createProcessDefinitionQuery().list().get(0).getName());

        repositoryService.createDeployment()
                .addClasspathResource(ExperimentUtils.PROCESS_RESOURCE_NAME_TODO)
                .deploy();
        Log.i(TAG, "Process Deployed! name = " + repositoryService.createProcessDefinitionQuery().list().get(0).getName());

    }

    public void connectToDb(){
        try {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                Log.e(TAG, "No SD Card");
            } else {
                String dir = Environment.getExternalStorageDirectory()+File.separator+"myDirectory";
                //create folder
                new File(dir).mkdirs();
                //create file
                dbFile = new File(dir, "main.sqlite");
            }

            url = "jdbc:sqlite:/" + dbFile.getPath();

            System.out.println(getClass().getCanonicalName() + ": SQLite path: " + url);
            Class.forName(driver).newInstance();
            if (dbFile.exists()) {
                dbFile.delete();
            }
            con = DriverManager.getConnection(url);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Handler getHandler() {
        return mHandler;
    }
    public Connection getConnection() { return con; }
    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public boolean isEngineInitialized() {
        return processEngine != null;
    }
}
