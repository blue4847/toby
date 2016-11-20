package toby.study.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection을 제공하는 Component<br>
 * SimpleConnectionMaker의 Adaptor class<br>
 * Connection을 제공할 때마다 연결 횟수를 카운트 함
 * @author blue4
 */
public class CountingConnectionMaker implements ConnectionMaker{
	
	/** connection counter */
	int counter = 0;
	
	/** DI property */
	/** ConnectionMaker */
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
