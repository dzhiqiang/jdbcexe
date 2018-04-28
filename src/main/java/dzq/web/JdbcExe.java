package dzq.web;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class JdbcExe {

	private String jdbcType;
	private String sqls;


	public String getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	public String getSqls() {
		return sqls;
	}

	public void setSqls(String sqls) {
		this.sqls = sqls;
	}

	public List<String> toSqlList() {
		List<String> sqlList = new ArrayList<String>();
		if (StringUtils.hasText(sqls)) {
			sqlList = SqlUtil.sqlToList(sqls);
		}
		return sqlList;
	}
}
