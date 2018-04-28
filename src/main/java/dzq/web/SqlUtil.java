package dzq.web;

import java.util.ArrayList;
import java.util.List;


public class SqlUtil {

	public static String[] split(String sqls) {
		return split(sqls, "\n");
	}

	public static String[] split(String sqls, String spliter) {
		return sqls.split(spliter);
	}

	public static List<String> sqlToList(String sqls) {
		String[] sqlArray = split(sqls);
		List<String> sqlList = new ArrayList<String>();
		for (String sql : sqlArray) {
			sqlList.add(alloate(sql));
		}
		return sqlList;
	}

	private static String alloate(String sql) {
		if (sql != null && sql.indexOf(";") > -1) {
			return sql.substring(0, sql.indexOf(";"));
		}
		return sql;
	}

}
