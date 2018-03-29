package com.ibeiliao.deployment.admin.vo.project;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 模块在每个环境的jvm参数
 * 
 */
public class ModuleJvm {

	/** moduleJvmId */
	private int moduleJvmId;

	/** 模块id */
	private int moduleId;

	/** 环境id */
	private int envId;

	/** 环境名称 */
	private String envName;

	/** jvm 参数 */
	private String jvmArgs;

	public void setModuleJvmId(int moduleJvmId) {
		this.moduleJvmId = moduleJvmId;
	}

	public int getModuleJvmId() {
		return moduleJvmId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setEnvId(int envId) {
		this.envId = envId;
	}

	public int getEnvId() {
		return envId;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public String getEnvName() {
		return envName;
	}

	public void setJvmArgs(String jvmArgs) {
		this.jvmArgs = jvmArgs;
	}

	public String getJvmArgs() {
		return jvmArgs;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
