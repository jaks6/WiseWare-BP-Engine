package ee.ut.cs.mc.and.activiti521.migration;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

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
    }

    public void captureDbStateToFile() throws SQLException {
        Statement stmt = null;
        String query = null;
        JsonSerializer migrationSerializer = null;

        try {
            migrationSerializer = new JsonSerializer("migration01.json");

            for ( String table : SqlCommandUtil.tables) {
                String procInstId = "4";
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
                stmt.close();
            }
        }
    }
}
