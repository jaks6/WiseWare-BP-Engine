package ee.ut.cs.mc.and.activiti521.engine.migration;

import android.util.Log;
import android.util.TimeUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.mockito.internal.util.collections.ListUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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

    public void loadStateFromFileToDb(String fileName) {
        experimentLog("Starting loadStateFromFileToDb");
        JsonDeserializer deserializer = null;
        try {
            deserializer = new JsonDeserializer(mConnection, fileName);
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
    private void captureDbStateToFile(String[] processInstanceIds) throws SQLException {
        ExperimentUtils.timings.addSplit("Starting DB state capture, BPID="+
                StringUtils.join( processInstanceIds, "; "));

        Statement stmt = null;
        JsonSerializer migrationSerializer = null;

        try {
            String timestamp = DateTime.now().toString();
            timestamp = StringUtils.substring(timestamp, 5, 19);
            migrationSerializer = new JsonSerializer("migration_"+timestamp+".json");

            for ( String table : SqlCommandUtil.tables) {
                ExperimentUtils.timings.addSplit("Starting work on DB table: "+table);
                stmt = mConnection.createStatement();
                ResultSet rs = stmt.executeQuery(
                        SqlCommandUtil.getQueryForProcessInstanceList(table, processInstanceIds));
                migrationSerializer.writeTable(rs, table);
                if (stmt != null) stmt.close();
            }
            migrationSerializer.close();
        } catch (SQLException e ) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExperimentUtils.timings.addSplit("Finished DB state capture");

    }

    public void captureDbStateToFile(String procInstId) throws SQLException {
        captureDbStateToFile( new String[]{procInstId} );
    }

    public void emigrateProcess(String processInstanceId) {
            try {
                captureDbStateToFile(processInstanceId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    public void emigrateProcessList(String[] processInstanceIds) {
        try {
            captureDbStateToFile(processInstanceIds);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
