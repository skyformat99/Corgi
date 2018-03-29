package com.ibeiliao.deployment.admin.po.global;

import java.io.Serializable;


/**
 * 全局配置表
 * 
 * <pre>
 *     自动生成代码: 表名 t_global_setting, 日期: 2017-01-13
 *     setting_id <PK>             int(11)
 *     svn_checkout_dir      varchar(200)
 *     target_server_user    varchar(80)
 *     target_server_dir     varchar(80)
 *     idc                   varchar(80)
 * </pre>
 */
public class GlobalSettingPO implements Serializable {

	private static final long serialVersionUID = -3074457345151316931L;

	/** settingId */
	private int settingId;

	/** svn checkout 目录 */
	private String svnCheckoutDir;

	/** 目标服务器用户 */
	private String targetServerUser;

	/** 目标服务器目录 */
	private String targetServerDir;

	/** idc名字，以英文  隔开 */
	private String idc;

	public void setSettingId(int settingId) {
		this.settingId = settingId;
	}

	public int getSettingId() {
		return settingId;
	}

	public void setSvnCheckoutDir(String svnCheckoutDir) {
		this.svnCheckoutDir = svnCheckoutDir;
	}

	public String getSvnCheckoutDir() {
		return svnCheckoutDir;
	}

	public void setTargetServerUser(String targetServerUser) {
		this.targetServerUser = targetServerUser;
	}

	public String getTargetServerUser() {
		return targetServerUser;
	}

	public void setTargetServerDir(String targetServerDir) {
		this.targetServerDir = targetServerDir;
	}

	public String getTargetServerDir() {
		return targetServerDir;
	}

	public void setIdc(String idc) {
		this.idc = idc;
	}

	public String getIdc() {
		return idc;
	}

}
