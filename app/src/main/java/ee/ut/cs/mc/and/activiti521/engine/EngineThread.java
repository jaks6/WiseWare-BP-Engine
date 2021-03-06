package ee.ut.cs.mc.and.activiti521.engine;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
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
import ee.ut.cs.mc.and.activiti521.Util;
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
                ExperimentUtils.runExperimentIfApplicable(getInstance());
            }
        });
    }

    protected void immigrateProcess(String fileName) {
        if (migrator == null ){
            migrator = new Migrator(con);
        }
        Log.i(TAG, "Reading .json and storing to DB...");
        migrator.loadStateFromFileToDb(fileName);

        experimentLog("Activating loaded instance");
        processEngine.getRuntimeService().activateProcessInstanceById(
                ExperimentUtils.IMMIGRATION_PROC_INST_ID);
        experimentLog("Immigration finished");
    }

    protected void emigrateProcess(String processInstanceId) {
        if (migrator == null) {
            migrator = new Migrator(con);
        }
        migrator.emigrateProcess(processInstanceId);
        ExperimentUtils.finishedMigration(processInstanceId);
    }
    protected void emigrateProcesses(String[] processInstanceIds) {
        if (migrator == null) {
            migrator = new Migrator(con);
        }
        migrator.emigrateProcessList(processInstanceIds);
        ExperimentUtils.finishedMigration(processInstanceIds);
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
//                .setAsyncExecutorActivate(true)
//                .setAsyncExecutorEnabled(true)
                .setJobExecutorActivate(true) //Job executor true throws SQLDroid "not implemented" errors, but it should be ok

                .buildProcessEngine();

        Log.i(TAG, "Process Engine built");

        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();

        //add event listener
        runtimeService.addEventListener(new MigrationListener(mHandler));
    }

    public void startProcess(String processKey) {
        Log.i(TAG, "Starting Process Instance using key:" + processKey + " Thread ID=" + Thread.currentThread().getId());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employeeName", "Kermit");
        variables.put("numberOfDays", new Integer(4));
        variables.put("vacationMotivation", "I'm really tired!");
        runtimeService.startProcessInstanceByKey(processKey, variables);
    }

    public void deployProcess(String classPathResource) {
        repositoryService.createDeployment()
                .addClasspathResource(classPathResource)
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
                File file = new File(dir);
                //TODO remove following?
                if (ExperimentUtils.DELETE_FILES_ON_BOOT){
                    Log.w(TAG, "\n[!] DELETING old migration files and databse!\n");
                    Util.deleteDirectory(file);
                }

                file.mkdirs();
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

    public EngineThread getInstance() {
        return this;
    }
}
