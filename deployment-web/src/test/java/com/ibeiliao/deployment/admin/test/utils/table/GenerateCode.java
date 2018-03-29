/**
 * 
 */
package com.ibeiliao.deployment.admin.test.utils.table;

import org.junit.Test;

/**
 * 生成dao代码
 * @author linyi 2016/7/8
 *
 */
public class GenerateCode {

	/**
	 * 主函数，以Maven Test形式运行
	 */
	@SuppressWarnings("unused")
	@Test
	public void generate() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");

		// 本机的数据库帐号、密码
		String username = "platform";
		String password = "sn9GxEPaCkr7";
		String urlHost = "jdbc:mysql://120.26.99.19:3306";

		// 数据库链接
		String url = urlHost + "/ibl_deployment_system?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
		// 类的 package
		String pack = "com.ibeiliao.deployment.admin";
		// 所属模块名称
		String modualPackage = "";
		// 类的author
		String user = "linyi";
		// 生成代码保存地址
		String path = "e:\\temp\\java";
		// 要生成的表
		

		String[] tablesOfServerModual = {
				"t_low_quality_rank"
		};

		//MyEntityUtils.run(username, password, url, pack, user, path, false, tables);
		//MyPOUtils.run(username, password, url, pack, user, path, false, tables);
		
		
		modualPackage = "stat";
		MyPOWithPackageUtils.run(username, password, url, pack, modualPackage, user, path, false, tablesOfServerModual);
		
//		modualPackage = "global";
//		MyPOWithPackageUtils.run(username, password, url, pack, modualPackage, user, path, false, tablesOfGlobalModual);
//		
//		modualPackage = "server";
//		MyPOWithPackageUtils.run(username, password, url, pack, modualPackage, user, path, false, tablesOfServerModual);
//		
//		modualPackage = "project";
//		MyPOWithPackageUtils.run(username, password, url, pack, modualPackage, user, path, false, tablesOfProjectModual);
		
		System.out.println("all Done!");
	}

}
