package ee.ut.cs.mc.and.activiti521.engine.migration;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jakob on 25.08.2016.
 */

public class SqlCommandUtil {

    public static String[] tables = {
            "ACT_RU_VARIABLE",
            "ACT_RU_EXECUTION",
            "ACT_RU_JOB"};

    /** Keys are table names, values are select statements with WHERE clause for process id */
    static final Map<String, String> tableCmdsMap;
    static
    {
        tableCmdsMap = new HashMap<String, String>();
        tableCmdsMap.put("ACT_RU_VARIABLE", "SELECT * FROM ACT_RU_VARIABLE  WHERE PROC_INST_ID_ IN ");
        tableCmdsMap.put("ACT_RU_EXECUTION", "SELECT * FROM ACT_RU_EXECUTION  WHERE PROC_INST_ID_ IN ");
        tableCmdsMap.put("ACT_RU_JOB", "SELECT * FROM ACT_RU_JOB  WHERE PROCESS_INSTANCE_ID_ IN ");
    }


    static String getQueryForProcessInstance(String tableName, String instanceId){
        return tableCmdsMap.get(tableName) + instanceId;
    }

    static String getQueryForProcessInstanceList(String tableName, String[] instanceIds){
        return tableCmdsMap.get(tableName) + "( " + StringUtils.join(instanceIds, ", ") + " )";
    }
}
