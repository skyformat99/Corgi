package com.ibeiliao.deployment.admin.po.project;

/**
 * 模块在每个环境的jvm参数
 * 
 * <pre>
 *     自动生成代码: 表名 t_module_jvm, 日期: 2017-02-24
 *     module_jvm_id <PK>     int(20)
 *     module_id        int(20)
 *     env_id           int(20)
 *     env_name         varchar(50)
 *     jvm_args         varchar(100)
 * </pre>
 */
public class ModuleJvmPO {


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

}
