package ee.ut.cs.mc.and.activiti521.engine.migration;

import android.os.Environment;
import android.util.JsonWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by Jakob on 24.08.2016.
 */

public class JsonSerializer {
    private File file;
    private FileOutputStream out;
    JsonWriter writer;

    public JsonSerializer(String filename) throws IOException {

        String dir = Environment.getExternalStorageDirectory()+File.separator+"myDirectory";
        file = new File(dir, filename);
        out = new FileOutputStream(file);

        if (!file.exists()) {
            file.createNewFile();
        }
        writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")));

        writer.beginObject().name("Migration");
        writer.beginArray();
    }

    /** Write a database table to file*/
    public void writeTable(ResultSet rs, String tableName) throws SQLException, IOException {
        ResultSetMetaData rsMeta = rs.getMetaData();
        int columnCount = rsMeta.getColumnCount();

        writer.beginObject().name(tableName);//start table
        writer.beginArray();
        writeColumns(rsMeta, columnCount);
        writeRows(rs, columnCount);
        writer.endArray();
        writer.endObject();
    }

    private void writeRows(ResultSet rs, int columnCount) throws IOException, SQLException {
        writer.beginObject().name("rows").beginArray();
        int rowNumber = 0;
        while (rs.next()) {
            rowNumber++;
            writeRow(rs, columnCount, rowNumber);
        }

        writer.endArray();
        writer.endObject();
    }

    private void writeRow(ResultSet rs, int columnCount, int rowNumber) throws IOException, SQLException {
        writer.beginObject().name(String.valueOf(rowNumber)); //row, name=row number, value= array of colvalues
        writer.beginArray(); //row vals
        for (int i = 1; i <= columnCount; i++) {
            writer.value(rs.getString(i));
        }
        writer.endArray();
        writer.endObject(); //end single row
    }


    private void writeColumns(ResultSetMetaData rsMeta, int columnCount) throws IOException, SQLException {
        writer.beginObject().name("columns").beginArray();
        for (int i = 1; i <= columnCount; i++) {
            writeColumn(rsMeta, i);
        }
        writer.endArray();
        writer.endObject();
    }

    private void writeColumn(ResultSetMetaData rsMeta, int i) throws IOException, SQLException {
        writer.beginObject().name(rsMeta.getColumnName(i));
        writer.value(rsMeta.getColumnType(i));
        writer.endObject();
    }

    public void close() throws IOException {
        writer.endArray();
        writer.endObject();
        writer.close();
    }

    public void prettyPrint(){
        //TODO
    }

}
