package com.jsoft.dbhelper.dbutility;

public class App {
	public static void main(String[] args) {
		DBHelper db = new DBHelper();
		String sql = "insert into tb_coursetype(id,courseTypeName) values('123','test')";
		db.setSql(sql);
		db.executeUpdate();
	}
}
