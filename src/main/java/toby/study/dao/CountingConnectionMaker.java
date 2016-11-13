package toby.study.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker{
	
	int counter = 0;
	
	private ConnectionMaker realConectionMaker;
	
	/**
	 * Constructor
	 * DI real ConnectionMaker
	 * @param realConectionMaker
	 */
	public CountingConnectionMaker( ConnectionMaker realConectionMaker){
		// DI ConnectionMaker
		this.realConectionMaker = realConectionMaker; 
	}
	
	/**
	 * Adaptor method of real ConnectionMaker
	 */
	public Connection makeConnection() throws ClassNotFoundException, SQLException{
		this.counter++; 
		return realConectionMaker.makeConnection();
	}
	
	/**
	 * get connection count
	 * @return
	 */
	public int getCounter(){
		return this.counter;
	}

}
