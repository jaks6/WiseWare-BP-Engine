package ee.ut.cs.mc.and.activiti521.engine.migration;

import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static ee.ut.cs.mc.and.activiti521.ExperimentUtils.experimentLog;

/**
 * Created by Jakob on 24.08.2016.
 */

public class JsonDeserializer {

    private static final String TAG = JsonDeserializer.class.getName();
    private File file;
    private FileInputStream out;
    JsonReader reader;

    Connection dbConnection;
    private Statement stmt;

    public JsonDeserializer(Connection con, String filename) throws FileNotFoundException, UnsupportedEncodingException {
        dbConnection = con;
        String dir = Environment.getExternalStorageDirectory()+File.separator+"myDirectory";
        file = new File(dir, filename);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
        reader = new JsonReader(isr);
    }

    public void readTables() throws IOException {
        reader.beginObject();
        reader.nextName();
        reader.beginArray();
        while (reader.hasNext()) {
            readTable();
        }
        reader.endArray();
        reader.endObject();

    }

    private void readTable() throws IOException {
        experimentLog("Reading table");
        reader.beginObject();
        String tableName = reader.nextName();
        experimentLog(tableName);
        reader.beginArray();
        //Columns
        List<String> columnNames = readColumnNames();

        //rows
        reader.beginObject();
        reader.nextName();
        reader.beginArray();

        while(reader.hasNext()){
            List<String> vals = readRowVals();
            insertRowToDb(tableName, vals);
        }
        reader.endArray();
        reader.endObject();


        reader.endArray();
        reader.endObject(); // end table


    }

    private void insertRowToDb(String tableName, List<String> vals)  {
        try {
            String sql = "INSERT INTO "+tableName+" VALUES (?"+StringUtils.repeat( ", ?", vals.size()-1) +")";
            PreparedStatement statement = dbConnection.prepareStatement(sql);
            for (int i = 0; i < vals.size(); i++) {
                statement.setString(i+1,  vals.get(i));
            }

            Log.d(TAG, "Sql Statement= " + sql);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> readRowVals() throws IOException {
        List<String> rowVals = new ArrayList<String>();
        reader.beginObject();
        reader.nextName();
        reader.beginArray();
        JsonToken check;
        while(reader.hasNext()){
             check = reader.peek();
            if (check == JsonToken.NULL) {
                rowVals.add(null);
                reader.nextNull();
            } else {
                rowVals.add(reader.nextString());
            }
        }
        reader.endArray();
        reader.endObject();
        Log.w("readRowVals", StringUtils.join(rowVals, "," ));
        return rowVals;
    }

    private List<String> readColumnNames() throws IOException {
        List<String> colNames = new ArrayList<String>();

        reader.beginObject();
        reader.nextName();
        reader.beginArray();
        while (reader.hasNext()){
            reader.beginObject();
            colNames.add(reader.nextName());
            reader.nextInt(); // type of col
            reader.endObject();
        }
        reader.endArray();
        reader.endObject();

        Log.w("readColumnNames", StringUtils.join(colNames, ","));
        return colNames;
    }

    public void loadToDb() throws IOException {
        stmt = null;
        try {
            if (dbConnection.getAutoCommit()){
                Log.w(TAG, "Disabling DB connection autoCommit!");
                dbConnection.setAutoCommit(false);
            }

//            stmt = dbConnection.createStatement();
            readTables();
//            int [] updateCounts = stmt.executeBatch();

            dbConnection.commit();
            dbConnection.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
