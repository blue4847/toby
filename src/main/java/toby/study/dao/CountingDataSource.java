package toby.study.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class CountingDataSource extends SimpleDriverDataSource {

	@Autowired
	DataSource dataSource;

	int counter = 0;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	@Override
	public Connection getConnection() throws SQLException {
		this.counter++;
		return dataSource.getConnection();
	}

	public int getCounter() {
		return this.counter;
	}

}
