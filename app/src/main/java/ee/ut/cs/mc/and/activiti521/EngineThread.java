package ee.ut.cs.mc.and.activiti521;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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

import ee.ut.cs.mc.and.activiti521.migration.MigrationListener;
import ee.ut.cs.mc.and.activiti521.migration.Migrator;

/**
 * Created by Jakob on 16.08.2016.
 */

public class EngineThread extends HandlerThread {
    private static final String TAG = EngineThread.class.getSimpleName();
    public static final int ENGINE_THREAD_MSG_DEPLOY_PROCESS = 1;
    public static final int ENGINE_THREAD_MSG_RUN_PROCESS = 2;
    public static final int ENGINE_CAPTURE_INSTANCE_STATE = 3;
    private static final int ENGINE_LOAD_STATE_TO_DB = 4;

    private static final String PROCESS_KEY = "myProcess";

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
        mHandler =  new Handler(getLooper()) {
            public void handleMessage(Message msg) {
                int command = msg.arg1;
                if (processEngine==null) {
                    Log.e(TAG, "Processs Engine not initialized");
                    return;
                } else {
                    switch (command){
                        case ENGINE_THREAD_MSG_DEPLOY_PROCESS:
                            deployProcess();
                            break;
                        case ENGINE_THREAD_MSG_RUN_PROCESS:
                            if (processEngine!=null) startProcess();
                            break;
                        case ENGINE_CAPTURE_INSTANCE_STATE:
                            if (processEngine!= null)
                                if (migrator == null ){
                                    migrator = new Migrator(con);
                                }
                            try {
                                migrator.captureDbStateToFile();

                                Log.i(TAG, "Activating suspended instance");
                                Thread.sleep(1000);
                                processEngine.getRuntimeService().activateProcessInstanceById("4");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case ENGINE_LOAD_STATE_TO_DB:
                            if (migrator == null ){
                                migrator = new Migrator(con);
                            }
                            Log.i(TAG, "Reading .json and storing to DB...");
                            migrator.loadStateFromFileToDb();
                            Log.i(TAG, "Activating suspended instance");
                            processEngine.getRuntimeService().activateProcessInstanceById("4");
                            Log.i(TAG, "Migration finished!");
                            break;
                    }
                }
            }
        };


    }

    /** Creates the thread and runs it */
    public EngineThread() {
        super(TAG);
        start();
        // Start the engine using a new runnable(thread)
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                startEngine();

                deployProcess();
//                startProcess();

                Log.i(TAG, "Temporary immigration");
                if (migrator == null ){
                    migrator = new Migrator(con);
                }
                try {
                    Thread.sleep(3000);
                    Log.i(TAG, "Reading .json and storing to DB...");
                    migrator.loadStateFromFileToDb();
                    Log.i(TAG, "Activating suspended instance");
                    processEngine.getRuntimeService().activateProcessInstanceById("4");

                    Log.i(TAG, "Migration finished!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    void startEngine(){
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
        Log.i(TAG, "Starting Process Instance using key:" + PROCESS_KEY + " Thread ID=" + Thread.currentThread().getId());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employeeName", "Kermit");
        variables.put("numberOfDays", new Integer(4));
        variables.put("vacationMotivation", "I'm really tired!");
        runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
    }

    public void deployProcess() {
        Log.i(TAG, "No of deployments=" + repositoryService.createProcessDefinitionQuery().list().size());
        Log.i(TAG, "About to create deployment.." + " Thread ID=" + Thread.currentThread().getId());
        repositoryService.createDeployment()
                .addClasspathResource("ActivitiTodoProcess.bpmn")
                .deploy();

        Log.i(TAG, "Process Deployed!");
        Log.i(TAG, "No of deployments=" + repositoryService.createProcessDefinitionQuery().list().size());
        Log.i(TAG, "Deployed proceess name = " + repositoryService.createProcessDefinitionQuery().list().get(0).getName());
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
            Class.forName("org.sqldroid.SQLDroidDriver").newInstance();
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
}
