package com.jsoft.dbhelper.dbutility;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.jstl.sql.*;

/*
 * http://blog.csdn.net/qiumuxia0921/article/details/50575498
*/
public class DBHelper {

	private String sql; // 要传入的sql语句

	public void setSql(String sql) {
		this.sql = sql;
	}

	private List sqlValues; // sql语句的参数

	public void setSqlValues(List sqlValues) {
		this.sqlValues = sqlValues;
	}

	private List<List> sqlValue; // sql语句的参数

	public void setSqlValue(List<List> sqlValues) {
		this.sqlValue = sqlValues;
	}

	private Connection con; // 连接对象

	public void setCon(Connection con) {
		this.con = con;
	}

	public DBHelper() {
		this.con = getConnection(); // 给Connection的对象赋初值
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 */
	private Connection getConnection() {

		String driver_class = null;
		String driver_url = null;
		String database_user = null;
		String database_password = null;
		try {
			InputStream fis = this.getClass().getResourceAsStream("/db.properties"); // 加载数据库配置文件到内存中
			Properties p = new Properties();
			p.load(fis);

			driver_class = p.getProperty("driver_class"); // 获取数据库配置文件
			driver_url = p.getProperty("driver_url");
			database_user = p.getProperty("database_user");
			database_password = p.getProperty("database_password");

			Class.forName(driver_class);
			con = DriverManager.getConnection(driver_url, database_user, database_password);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * 关闭数据库
	 * 
	 * @param con
	 * @param pst
	 * @param rst
	 */
	private void closeAll(Connection con, PreparedStatement pst, ResultSet rst) {
		if (rst != null) {
			try {
				rst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 关闭数据库
	 * 
	 * @param con
	 * @param pst
	 * @param rst
	 */
	private void closeAll(Connection con, Statement pst, ResultSet rst) {
		if (rst != null) {
			try {
				rst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 查找
	 * 
	 * @param sql
	 * @param sqlValues
	 * @return
	 */
	public Result executeQuery() {
		Result result = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		try {

			pst = con.prepareStatement(sql);
			if (sqlValues != null && sqlValues.size() > 0) { // 当sql语句中存在占位符时
				setSqlValues(pst, sqlValues);
			}
			rst = pst.executeQuery();
			result = ResultSupport.toResult(rst); // 一定要在关闭数据库之前完成转换

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.closeAll(con, pst, rst);
		}

		return result;
	}

	/**
	 * 增删改
	 * 
	 * @return
	 */
	public int executeUpdate() {
		int result = -1;
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql);
			if (sqlValues != null && sqlValues.size() > 0) { // 当sql语句中存在占位符时
				setSqlValues(pst, sqlValues);
			}
			result = pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.closeAll(con, pst, null);
		}
		return result;
	}

	/**
	 * 使用PreparedStatement加批量的方法
	 * 
	 * @return
	 */
	public int[] executeUpdateMore() {
		int[] result = null;
		try {
			PreparedStatement prest = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			for (List sqlValueString : sqlValue) {
				for (int i = 0; i < sqlValueString.size(); i++) {
					try {
						prest.setObject(i + 1, sqlValueString.get(i));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				prest.addBatch();
			}
			prest.executeBatch();
			/* con.commit(); */
			this.closeAll(con, prest, null);
		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
		return result;

	}

	/**
	 * 使用PreparedStatement加批量的方法,strvalue: "INSERT
	 * INTOadlogs(ip,website,yyyymmdd,hour,object_id)
	 * VALUES('192.168.1.3','localhost','20081009',8,'23123')"
	 * 
	 * @return
	 * @throws SQLException
	 */
	public int[] executeUpdateMoreNotAuto() throws SQLException {
		int[] result = null;
		con.setAutoCommit(false);
		Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		String[] SqlString = null;
		for (String strvalue : SqlString) {
			stmt.execute(strvalue);
		}
		con.commit();
		return result;
	}

	/**
	 * 使用PreparedStatement加批量的方法,strvalue: "INSERT
	 * INTOadlogs(ip,website,yyyymmdd,hour,object_id)
	 * VALUES('192.168.1.3','localhost','20081009',8,'23123')"
	 * 
	 * @return
	 * @throws SQLException
	 */
	public int[] executeMoreNotAuto() throws SQLException {
		// 保存当前自动提交模式
		Boolean booleanautoCommit = false;
		String[] SqlString = null;
		int[] result = null;
		try {
			booleanautoCommit = con.getAutoCommit();
			// 关闭自动提交
			con.setAutoCommit(false);
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// 使用Statement同时收集多条sql语句
			/*
			 * stmt.addBatch(insert_sql1); stmt.addBatch(insert_sql2);
			 * stmt.addBatch(update_sql3);
			 */
			for (String strvalue : SqlString) {
				stmt.addBatch(strvalue);
			}

			// 同时提交所有的sql语句
			stmt.executeBatch();
			// 提交修改
			con.commit();
			con.setAutoCommit(booleanautoCommit);
			this.closeAll(con, stmt, null);
		} catch (Exception e) {
			e.printStackTrace();
			con.rollback(); // 设定setAutoCommit(false)没有在catch中进行Connection的rollBack操作，操作的表就会被锁住，造成数据库死锁
		}
		return result;
	}

	/**
	 * 给sql语句中的占位符赋值
	 * 
	 * @param pst
	 * @param sqlValues
	 */
	private void setSqlValues(PreparedStatement pst, List sqlValues) {
		for (int i = 0; i < sqlValues.size(); i++) {
			try {
				pst.setObject(i + 1, sqlValues.get(i));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}