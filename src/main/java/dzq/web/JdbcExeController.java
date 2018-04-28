package dzq.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/jdbcexe")
public class JdbcExeController {


	@RequestMapping(value = "exe.do", method = RequestMethod.GET)
	public String create(Model model) {
		// set the appRoot
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		String s = request.getContextPath();
		model.addAttribute("appRoot", s);
		model.addAttribute("proList", getProList());
		return "exe";
	}

	@RequestMapping(value = "exe.do", method = RequestMethod.POST)
	@ResponseBody
	public String exe(JdbcExe exe) {

		String result = doExe(exe);
//		logger.info(result);
		return result;
	}

	private String doExe(JdbcExe exe) {
		StringBuffer log = new StringBuffer();
		log.append(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date())).append("---");
		Connection connection = null;
		try {
			connection = getConnection(exe.getJdbcType());

			log.append("getConnection success\r\n");
			List<String> sqlList = exe.toSqlList();

			int totalCount = 0;
			for (String sql : sqlList) {

				totalCount += exeOneSql(sql, connection, log);

			}
			log.append("totalCount = ").append(totalCount);

		} catch (SQLException e) {
			log.append("getConnection error:").append(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.append("getConnection error:").append(e.getMessage());
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
//					logger.info("connection close");
					log.append(",connection close success");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return log.toString();
	}

	private int exeOneSql(String sql, Connection connection, StringBuffer log) {

		PreparedStatement preparedStatement = null;
		try{
			try {
				preparedStatement = connection.prepareStatement(sql);
			} catch (SQLException e) {
				log.append(sql).append(",prepareStatement error:").append(e.getMessage()).append("\r\n");
				e.printStackTrace();
				return 0;
			}
			int count;
			try {
				count = preparedStatement.executeUpdate();
			} catch (SQLException e) {
				log.append(sql).append(",executeUpdate error:").append(e.getMessage()).append("\r\n");
				e.printStackTrace();
				return 0;
			}
			log.append(sql).append(",executeUpdate success,count = ").append(count).append("\r\n");
			return count;
		}finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private Connection getConnection(String jdbcType) throws Exception {
		return ConnectionFactory.getConnection(jdbcType);
	}

	public String[] getProList() {
		String path = this.getClass().getResource("/").getPath();
		File file = new File(path);

		if (file.exists() && file.isDirectory()) {
			String[] propertiesList = file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".properties");
				}
			});
			String[] proList = new String[propertiesList.length];
			for (int i = 0; i < propertiesList.length; i++) {
				int subIndex = propertiesList[i].indexOf(".");
				proList[i] = propertiesList[i].substring(0, subIndex);
			}
			return proList;
		}
		return null;
	}
}
