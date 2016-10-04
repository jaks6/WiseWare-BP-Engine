package ee.ut.cs.mc.and.activiti521.engine.migration;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public void captureDbStateToFile(String procInstId) throws SQLException {
        experimentLog("Starting DB state capture");
        Statement stmt = null;
        JsonSerializer migrationSerializer = null;

        try {
            migrationSerializer = new JsonSerializer("migration01.json");

            for ( String table : SqlCommandUtil.tables) {
                experimentLog("Starting work on DB table: "+table);
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

        experimentLog("Finished DB state capture");
    }
}
