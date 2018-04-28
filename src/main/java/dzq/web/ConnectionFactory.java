package dzq.web;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ConnectionFactory {

	public static final String[] PROPERTIES = new String[]{
		"druid.url", "druid.username", "druid.password", "druid.maxActive","druid.driverClassName"
	};

	private static Map<String, DruidDataSource> poolCache = new HashMap<>();

	private static ReadWriteLock lock = new ReentrantReadWriteLock();

	public static Connection getConnection(String jdbcType) throws SQLException {
		DruidDataSource dataSource = getDataSourceInstance(jdbcType);
		return dataSource.getConnection();
	}

	private static DruidDataSource getDataSourceInstance(String jdbcType) {
		DruidDataSource dataSource = poolCache.get(jdbcType);
		if (dataSource == null) {
			dataSource = maybeInit(jdbcType);
		}
		return dataSource;
	}

	private static DruidDataSource maybeInit(String jdbcType) {
		lock.readLock().lock();
		DruidDataSource dataSource = poolCache.get(jdbcType);
		try {
			if (dataSource == null) {
				lock.readLock().unlock();
				lock.writeLock().lock();
				dataSource = poolCache.get(jdbcType);
				try {
					if (dataSource == null) {
						dataSource = createDruidDataSoure(jdbcType);
						poolCache.put(jdbcType, dataSource);
					}
				}finally {
					lock.writeLock().unlock();
					lock.readLock().lock();
				}

			}
		}finally {
			lock.readLock().unlock();
		}

		return dataSource;
	}

	private static DruidDataSource createDruidDataSoure(String jdbcType) {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.configFromPropety(loadProperties(jdbcType));
		return dataSource;
	}

	private static Properties loadProperties(String propfile) {
		Properties prop = new Properties();

		ResourceBundle rb = ResourceBundle.getBundle(propfile);
		for (String property : PROPERTIES) {
			if (rb.containsKey(property)) {
				prop.setProperty(property, rb.getString(property));
			}
		}

		return prop;
	}

}
