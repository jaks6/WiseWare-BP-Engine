package ee.ut.cs.mc.and.activiti521.engine.migration;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ee.ut.cs.mc.and.activiti521.ExperimentUtils;

import static ee.ut.cs.mc.and.activiti521.ExperimentUtils.experimentLog;

/**
 * Created by Jakob on 22.08.2016.
 */

public class Migrator {

    private static final String TAG = Migrator.class.getName();
    private final Connection mConnection;


    public Migrator(Connection con) {
        mConnection = con;
    }

    public void loadStateFromFileToDb() {
        experimentLog("Starting loadStateFromFileToDb");
        JsonDeserializer deserializer = null;
        try {
            deserializer = new JsonDeserializer(mConnection, "migration01.json");
            deserializer.loadToDb();
            Log.i(TAG, "Finished DB operations.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        experimentLog("Finished loadStateFromFileToDb");
    }
    private void captureDbStateToFile(List<String> processInstanceIds) throws SQLException {

    }

    public void captureDbStateToFile(String procInstId) throws SQLException {
        ExperimentUtils.timings.addSplit("Starting DB state capture, BPID="+procInstId);

        Statement stmt = null;
        JsonSerializer migrationSerializer = null;

        try {
            migrationSerializer = new JsonSerializer("migration"+procInstId+".json");

            for ( String table : SqlCommandUtil.tables) {
                ExperimentUtils.timings.addSplit("Starting work on DB table: "+table);
                stmt = mConnection.createStatement();
                ResultSet rs = stmt.executeQuery(
                        SqlCommandUtil.getQueryForProcessInstance(table, procInstId));
                migrationSerializer.writeTable(rs, table);
            }
            migrationSerializer.close();
        } catch (SQLException e ) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close(); //TODO should this be  moved into the for loop above?
            }
        }
        ExperimentUtils.timings.addSplit("Finished DB state capture");
    }

    public void emigrateProcess(String processInstanceId) {
            try {
                captureDbStateToFile(processInstanceId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    public void emigrateProcessList(List<String> processInstanceIds) {
        try {
            captureDbStateToFile(processInstanceIds);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
