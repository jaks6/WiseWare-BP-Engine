package org.sqldroid;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import android.database.Cursor;

public class SQLDroidResultSet implements ResultSet {
  
    private final Cursor c;
    private int androidLastColumnRead;

    public SQLDroidResultSet(Cursor c) {
        this.c = c;
    }

  /**
   * convert JDBC columns index count (from one) to sqlite (from zero)
   * @param colID
   */
  private int ci(int colID) {
    return colID - 1;
  }
  
  /**
   * convert sqlite columns index count (from zero) to JDBC (from one)
   * @param colID
   */
  private int cu(int colID) {
    return colID + 1;
  }

  @Override
  public boolean absolute(int row) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return false;
  }

  @Override
  public void afterLast() throws SQLException {
    try {
      c.moveToLast();
      c.moveToNext();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public void beforeFirst() throws SQLException {
    try {
      c.moveToFirst();
      c.moveToPrevious();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public void cancelRowUpdates() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
  }

  @Override
  public void clearWarnings() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void close() throws SQLException {
    try {
      if (c != null) {
        c.close();
      }
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public void deleteRow() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
  }

  @Override
  public int findColumn(String columnName) throws SQLException {
    try {
    	System.out.println(getClass().getCanonicalName() + ": Column " + columnName + " has index " + c.getColumnIndex(columnName) + " in column list: " + printColumnNames());
    	return c.getColumnIndex(columnName); //+1?
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public boolean first() throws SQLException {
    try {
      return c.moveToFirst();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public Array getArray(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }


  @Override
  public Array getArray(String colName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public InputStream getAsciiStream(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public InputStream getAsciiStream(String columnName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public BigDecimal getBigDecimal(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public BigDecimal getBigDecimal(String columnName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public BigDecimal getBigDecimal(int colID, int scale)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public BigDecimal getBigDecimal(String columnName, int scale)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public InputStream getBinaryStream(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public InputStream getBinaryStream(String columnName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Blob getBlob(int jdbcIndex) throws SQLException {
    try {
      androidLastColumnRead = ci(jdbcIndex);
      byte [] b = c.getBlob(ci(jdbcIndex));
      return new SQLDroidBlob(b);
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public Blob getBlob(String columnName) throws SQLException {
    try {
      int androidIndex = c.getColumnIndex(columnName);
  	System.out.println(getClass().getCanonicalName() + ".getBlob(): Column " + columnName + " has index " + androidIndex + " in column list: " + printColumnNames());
      androidLastColumnRead = androidIndex;
      return new SQLDroidBlob(c.getBlob(androidIndex));
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public boolean getBoolean(int jdbcIndex) throws SQLException {
    try {
      androidLastColumnRead = ci(jdbcIndex);
      return c.getInt(ci(jdbcIndex)) != 0;
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public boolean getBoolean(String columnName) throws SQLException {
    try {
        int androidIndex = c.getColumnIndex(columnName);
    	System.out.println(getClass().getCanonicalName() + ".getBoolean(): Column " + columnName + " has index " + androidIndex + " in column list: " + printColumnNames());
        System.out.println("getBoolean() for " + columnName + ": " + (c.getInt(androidIndex) != 0));

        androidLastColumnRead = androidIndex;
      return c.getInt(androidIndex) != 0;
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public byte getByte(int jdbcIndex) throws SQLException {
    try {
        androidLastColumnRead = ci(jdbcIndex);
      return (byte)c.getShort(ci(jdbcIndex));
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public byte getByte(String columnName) throws SQLException {
    try {
        int androidIndex = c.getColumnIndex(columnName);
//    	System.out.println(getClass().getCanonicalName() + ".getByte(): Column " + columnName + " has index " + index + " in column list: " + printColumnNames());
        System.out.println("getByte() for " + columnName + ": " + c.getShort(androidIndex));

        androidLastColumnRead = androidIndex;
      return (byte)c.getShort(androidIndex);
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public byte[] getBytes(int jdbcIndex) throws SQLException {
    try {
        androidLastColumnRead = ci(jdbcIndex);
      return c.getBlob(ci(jdbcIndex));
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public byte[] getBytes(String columnName) throws SQLException {
    try {
        int androidIndex = c.getColumnIndex(columnName);
//    	System.out.println(getClass().getCanonicalName() + ".getBytes(): Column " + columnName + " has index " + index + " in column list: " + printColumnNames());
        System.out.println("getBytes() for " + columnName + ": " + c.getBlob(androidIndex));

        androidLastColumnRead = androidIndex;
      return c.getBlob(androidIndex);
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public Reader getCharacterStream(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Reader getCharacterStream(String columnName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Clob getClob(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Clob getClob(String colName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public int getConcurrency() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return 0;
  }

  @Override
  public String getCursorName() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Date getDate(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Date getDate(String columnName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Date getDate(int colID, Calendar cal) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Date getDate(String columnName, Calendar cal) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public double getDouble(int jdbcIndex) throws SQLException {
    try {
        androidLastColumnRead = ci(jdbcIndex);
      return c.getDouble(ci(jdbcIndex));
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public double getDouble(String columnName) throws SQLException {
    try {
        int androidIndex = c.getColumnIndex(columnName);
//    	System.out.println(getClass().getCanonicalName() + ".getDouble(): Column " + columnName + " has index " + index + " in column list: " + printColumnNames());
        System.out.println("getDouble() for " + columnName + ": " + c.getDouble(androidIndex));

        androidLastColumnRead = androidIndex;
      return c.getDouble(androidIndex);
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public int getFetchDirection() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return 0;
  }

  @Override
  public int getFetchSize() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return 0;
  }

  @Override
  public float getFloat(int jdbcIndex) throws SQLException {
    try {
        androidLastColumnRead = ci(jdbcIndex);
      return c.getFloat(ci(jdbcIndex));
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public float getFloat(String columnName) throws SQLException {
    try {
        int androidIndex = c.getColumnIndex(columnName);
//    	System.out.println(getClass().getCanonicalName() + ".getFloat(): Column " + columnName + " has index " + index + " in column list: " + printColumnNames());
        System.out.println("getFloat() for " + columnName + ": " + c.getFloat(androidIndex));

        androidLastColumnRead = androidIndex;
      return c.getFloat(androidIndex);
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public int getInt(int jdbcIndex) throws SQLException {
    try {
        androidLastColumnRead = ci(jdbcIndex);
      return c.getInt(ci(jdbcIndex));
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public int getInt(String columnName) throws SQLException {
    try {
        int androidIndex = c.getColumnIndex(columnName);
//    	System.out.println(getClass().getCanonicalName() + ".getBlob(): Column " + columnName + " has index " + index + " in column list: " + printColumnNames());
        System.out.println("getInt() for " + columnName + ": " + c.getInt(androidIndex));

        androidLastColumnRead = androidIndex;
      return c.getInt(androidIndex);
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public long getLong(int jdbcIndex) throws SQLException {
    try {
        androidLastColumnRead = ci(jdbcIndex);
      return c.getLong(ci(jdbcIndex));
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public long getLong(String columnName) throws SQLException {
    try {
        int androidIndex = c.getColumnIndex(columnName);
//    	System.out.println(getClass().getCanonicalName() + ".getLong(): Column " + columnName + " has index " + index + " in column list: " + printColumnNames());
        System.out.println("getLong() for " + columnName + ": " + c.getLong(androidIndex));
        androidLastColumnRead = androidIndex;
      return c.getLong(androidIndex);
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return new SQLDroidResultSetMetaData(c);
  }

    @Override
    public Object getObject(int jdbcIndex) throws SQLException {
        androidLastColumnRead = ci(jdbcIndex);
        int newIndex = ci(jdbcIndex);
        switch(SQLDroidResultSetMetaData.getType(c, newIndex)) {
            case 4: // Cursor.FIELD_TYPE_BLOB:
                //CONVERT TO BYTE[] OBJECT
                return new SQLDroidBlob(c.getBlob(newIndex));
            case 2: // Cursor.FIELD_TYPE_FLOAT:
                return new Float(c.getFloat(newIndex));
            case 1: // Cursor.FIELD_TYPE_INTEGER:
                return new Integer(c.getInt(newIndex));
            case 3: // Cursor.FIELD_TYPE_STRING:
                return c.getString(newIndex);
            case 0: // Cursor.FIELD_TYPE_NULL:
                return null;
            default:
                return c.getString(newIndex);
        }
    }

  @Override
  public Object getObject(String columnName) throws SQLException {
    int index = c.getColumnIndex(columnName);
//  	System.out.println(getClass().getCanonicalName() + ".getObject(): Column " + columnName + " has index " + index + " in column list: " + printColumnNames());
    System.out.println("getObject() for " + columnName + ": " + getObject(index));

    return getObject(index);
  }

  @Override
  public Object getObject(int arg0, Map<String, Class<?>> arg1)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Object getObject(String arg0, Map<String, Class<?>> arg1)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }


  public <T> T getObject(int columnIndex, Class<T> clazz) throws SQLException {
    // This method is entitled to throw if the conversion is not supported, so, 
    // since we don't support any conversions we'll throw.
    // The only problem with this is that we're required to support certain conversion as specified in the docs.
    throw new SQLException("Conversion not supported.  No conversions are supported.  This method will always throw."); 
  }

  public <T> T getObject(String columnLabel, Class<T> clazz) throws SQLException {
    // This method is entitled to throw if the conversion is not supported, so, 
    // since we don't support any conversions we'll throw.
    // The only problem with this is that we're required to support certain conversion as specified in the docs.
    throw new SQLException("Conversion not supported.  No conversions are supported.  This method will always throw."); 
  }

  @Override
  public Ref getRef(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Ref getRef(String colName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public int getRow() throws SQLException {
    try {
      // convert to jdbc standard (counting from one)
      return c.getPosition() + 1;
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public short getShort(int jdbcIndex) throws SQLException {
    try {
        androidLastColumnRead = ci(jdbcIndex);
      return c.getShort(ci(jdbcIndex));
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public short getShort(String columnName) throws SQLException {
    try {
        int androidIndex = c.getColumnIndex(columnName);
    	System.out.println(getClass().getCanonicalName() + ".getShort(): Column " + columnName + " has index " + androidIndex + " in column list: " + printColumnNames());

        androidLastColumnRead = androidIndex;
      return c.getShort(androidIndex);
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public Statement getStatement() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public String getString(int jdbcIndex) throws SQLException {
    try {
        androidLastColumnRead = ci(jdbcIndex);
      return c.getString(ci(jdbcIndex));
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public String getString(String columnName) throws SQLException {
    try {
      int androidIndex = c.getColumnIndex(columnName);
//      index++;
//    	System.out.println(getClass().getCanonicalName() + ".getString(): Column " + columnName + " has index " + index + " in column list: " + printColumnNames());

      androidLastColumnRead = androidIndex;
      
//      if (columnName.equalsIgnoreCase("NAME_")) {
//    	  System.out.println(getClass().getCanonicalName() + ": Error appears now.");
//      }
      
      System.out.println("getString() for " + columnName + ": " + c.getString(androidIndex));
      return c.getString(androidIndex);
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }
  
  public String printColumnNames() {
	  StringBuilder sb = new StringBuilder();
	  
	  for (String s : c.getColumnNames()) {
		  sb.append(s + " ");
	  }
	  
	  return sb.toString();
  }

  @Override
  public Time getTime(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Time getTime(String columnName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Time getTime(int colID, Calendar cal) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Time getTime(String columnName, Calendar cal) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Timestamp getTimestamp(int index) throws SQLException {
    try {
      ResultSetMetaData md = getMetaData();
      Timestamp timestamp = null;
      switch ( md.getColumnType(index)) {
        case Types.INTEGER:
        case Types.BIGINT:
          timestamp = new Timestamp(getLong(index));
          System.out.println(getClass().getCanonicalName() + ": INTEGER -> getTimestamp() for " + c.getColumnName(index-1) + ": " + getString(index) + ": " + timestamp);

          break;
        case Types.DATE:
          timestamp = new Timestamp(getDate(index).getTime());
          System.out.println(getClass().getCanonicalName() + ": DATE -> getTimestamp() for " + c.getColumnName(index-1) + ": " + getString(index) + ": " + timestamp);
          break;
        default:
          // format 2011-07-11 11:36:30.000
          try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            System.out.println(getClass().getCanonicalName() + ": Date from result set in format: " + dateFormat.toPattern());
            System.out.println("* getTimestamp() for " + c.getColumnName(index-1) + ": " + getString(index));
            if (getString(index) != null) {
            	java.util.Date parsedDate = dateFormat.parse(getString(index));
                timestamp = new Timestamp(parsedDate.getTime());
            } else {
                System.err.println(getClass().getCanonicalName() + ": Timestamp is null.");
            }
          } catch ( Exception any ) {
            any.printStackTrace();
          }
          break;

      }
      System.out.println("getTimestamp(): " + timestamp);
      return timestamp;
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public Timestamp getTimestamp(String columnName) throws SQLException {
    int index = findColumn(columnName)+1; // +1
    
//    if (columnName.equalsIgnoreCase("LOCK_EXP_TIME_"))
//    	index--;
    
    System.out.println("getTimestamp() for " + columnName + " with index " + index);

    return getTimestamp(index);
  }

  @Override
  public Timestamp getTimestamp(int colID, Calendar cal)
  throws SQLException {
    System.err.println(" ********************* not implemented correctly - Calendar is ignored. @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return getTimestamp(colID);
  }

  @Override
  public Timestamp getTimestamp(String columnName, Calendar cal)
  throws SQLException {
    System.err.println(" ********************* not implemented correctly - Calendar is ignored. @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return getTimestamp(columnName);
  }

  @Override
  public int getType() throws SQLException {
    return ResultSet.TYPE_SCROLL_SENSITIVE;
  }

  @Override
  public URL getURL(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public URL getURL(String columnName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public InputStream getUnicodeStream(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public InputStream getUnicodeStream(String columnName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public void insertRow() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public boolean isAfterLast() throws SQLException {
    if ( isClosed() ) {
      return false;
    }
    try {
      return c.isAfterLast();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public boolean isBeforeFirst() throws SQLException {
    if ( isClosed() ) {
      return false;
    }
    try {
      return c.isBeforeFirst();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public boolean isFirst() throws SQLException {
    if ( isClosed() ) {
      return false;
    }
    try {
      return c.isFirst();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public boolean isLast() throws SQLException {
    if ( isClosed() ) {
      return false;
    }
    try {
      return c.isLast();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public boolean last() throws SQLException {
    try {
      return c.moveToLast();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public void moveToCurrentRow() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void moveToInsertRow() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public boolean next() throws SQLException {
    try {
      return c.moveToNext();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public boolean previous() throws SQLException {
    try {
      return c.moveToPrevious();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public void refreshRow() throws SQLException {
    try {
      c.requery();
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public boolean relative(int rows) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return false;
  }

  @Override
  public boolean rowDeleted() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return false;
  }

  @Override
  public boolean rowInserted() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return false;
  }

  @Override
  public boolean rowUpdated() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return false;
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateArray(int colID, Array x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateArray(String columnName, Array x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateAsciiStream(int colID, InputStream x, int length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateAsciiStream(String columnName, InputStream x, int length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBigDecimal(int colID, BigDecimal x)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBigDecimal(String columnName, BigDecimal x)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBinaryStream(int colID, InputStream x, int length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBinaryStream(String columnName, InputStream x, int length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBlob(int colID, Blob x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBlob(String columnName, Blob x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBoolean(int colID, boolean x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBoolean(String columnName, boolean x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateByte(int colID, byte x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateByte(String columnName, byte x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBytes(int colID, byte[] x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBytes(String columnName, byte[] x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateCharacterStream(int colID, Reader x, int length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateCharacterStream(String columnName, Reader reader,
      int length) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateClob(int colID, Clob x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateClob(String columnName, Clob x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateDate(int colID, Date x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateDate(String columnName, Date x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateDouble(int colID, double x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateDouble(String columnName, double x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateFloat(int colID, float x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateFloat(String columnName, float x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateInt(int colID, int x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateInt(String columnName, int x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateLong(int colID, long x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateLong(String columnName, long x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateNull(int colID) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateNull(String columnName) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateObject(int colID, Object x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateObject(String columnName, Object x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateObject(int colID, Object x, int scale)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateObject(String columnName, Object x, int scale)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateRef(int colID, Ref x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateRef(String columnName, Ref x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateRow() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateShort(int colID, short x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateShort(String columnName, short x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateString(int colID, String x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateString(String columnName, String x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateTime(int colID, Time x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateTime(String columnName, Time x) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateTimestamp(int colID, Timestamp x)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateTimestamp(String columnName, Timestamp x)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
  }

  @Override
  public boolean wasNull() throws SQLException {
    try {
        return c.isNull(androidLastColumnRead);

//      return c.isNull(ci(lastColumnRead));
    } catch (android.database.SQLException e) {
      throw SQLDroidConnection.chainException(e);
    }
  }

  @Override
  public boolean isWrapperFor(Class<?> arg0) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return false;
  }

  @Override
  public <T> T unwrap(Class<T> arg0) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public int getHoldability() throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return 0;
  }

  @Override
  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public NClob getNClob(int columnIndex) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public NClob getNClob(String columnLabel) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public String getNString(int columnIndex) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public String getNString(String columnLabel) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public RowId getRowId(int columnIndex) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public RowId getRowId(String columnLabel) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

  @Override
  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());
    return null;
  }

    @Override
    public boolean isClosed() throws SQLException {
        return c.isClosed();
    }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, long length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, long length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, long length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x,
      long length) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream, long length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream,
      long length) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, long length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader,
      long length) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateClob(String columnLabel, Reader reader)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateClob(int columnIndex, Reader reader, long length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateClob(String columnLabel, Reader reader, long length)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x)
  throws SQLException {
    System.err.println(" ********************* not implemented @ " + DebugPrinter.getFileName() + " line " + DebugPrinter.getLineNumber());

  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader)
  throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x, long length)
  throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader,
      long length) throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNClob(String columnLabel, NClob nClob)
  throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNClob(String columnLabel, Reader reader)
  throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNClob(int columnIndex, Reader reader, long length)
  throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNClob(String columnLabel, Reader reader, long length)
  throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNString(int columnIndex, String nString)
  throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateNString(String columnLabel, String nString)
  throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateRowId(int columnIndex, RowId value) throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateRowId(String columnLabel, RowId value)
  throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject)
  throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateSQLXML(String columnLabel, SQLXML xmlObject)
  throws SQLException {
    // TODO Auto-generated method stub

  }

}
