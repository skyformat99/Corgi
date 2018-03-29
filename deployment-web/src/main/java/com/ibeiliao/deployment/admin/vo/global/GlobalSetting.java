package com.ibeiliao.deployment.admin.vo.global;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 全局配置表
 * 
 */
public class GlobalSetting implements Serializable {

	private static final long serialVersionUID = -3074457344027957391L;

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

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
