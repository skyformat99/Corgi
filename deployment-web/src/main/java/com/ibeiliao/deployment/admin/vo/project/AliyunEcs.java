package com.ibeiliao.deployment.admin.vo.project;

import java.io.Serializable;

/**
 * 功能：阿里云 ECS
 * 详细：
 *
 * @author linyi, 2017/3/29.
 */
public class AliyunEcs implements Serializable {

    /**
     * 内网IP，对应 DescribeInstancesResponse.Instance.innerIpAddress
     */
    private String innerIpAddress = "";

    /**
     * 公网IP，对应 DescribeInstancesResponse.Instance.publicIpAddress
     */
    private String publicIpAddress = "";

    /**
     * 实例名称
     */
    private String instanceName = "";

    /**
     * 区域，对应 DescribeInstancesResponse.Instance.regionId
     */
    private String regionId;

    /**
     * 规格，若干属性的组合: cpu + memory
     */
    private String spec;

    /**
     * 实例ID
     */
    private String instanceId;

    /**
     * 网络类型
     */
    private String networkType;

    /**
     * cpu数量
     */
    private int cpu;

    /**
     * 内存，MB
     */
    private int memory;

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInnerIpAddress() {
        return innerIpAddress;
    }

    public void setInnerIpAddress(String innerIpAddress) {
        this.innerIpAddress = innerIpAddress;
    }

    public String getPublicIpAddress() {
        return publicIpAddress;
    }

    public void setPublicIpAddress(String publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }
}
