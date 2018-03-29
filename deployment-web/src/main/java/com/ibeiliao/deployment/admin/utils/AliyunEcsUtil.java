package com.ibeiliao.deployment.admin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.cms.model.v20170301.QueryMetricListRequest;
import com.aliyuncs.cms.model.v20170301.QueryMetricListResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.vo.project.AliyunEcs;
import com.ibeiliao.deployment.base.ApiCode;
import com.ibeiliao.deployment.cfg.EncryptionPropertyPlaceholderConfigurer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 功能：阿里云 ECS 工具类
 * 详细：
 *
 * @author linyi, 2017/3/29.
 */
public class AliyunEcsUtil {

    /**
     * VPC 网络
     */
    private static final String NETWORK_VPC = "vpc";

    private static final String ISO_TIME = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final String DEFAULT_TIME = "yyyy-MM-dd HH:mm:ss";

    private static final String[] REGIONS = {"cn-hangzhou", "cn-shenzhen"};

    /**
     * 发布新进程至少需要多少 free memory, newFree = free - Xmx，
     * 单位：mb
     */
    public static final int NEW_PROC_MIN_FREE_MEMORY = 500;

    /**
     * 发布新进程要求至少有多少 idle cpu，服务器繁忙的时候可能发布失败
     */
    private static final int NEW_PROC_MIN_IDLE_CPU = 40;

    /**
     * http://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/parallel.html#default_heap_size
     * 实际上是物理内存的 1/4，这里简单化
     * 单位MB
     */
    public static final int DEFAULT_XMX = 1024;

    /**
     * http://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/sizing.html#sizing_generations
     * 默认的 xms，单位MB
     */
    public static final int DEFAULT_XMS = 8;

    private static final Logger logger = LoggerFactory.getLogger(AliyunEcsUtil.class);

    private static boolean dev = false;

    static {
        dev = "true".equalsIgnoreCase(EncryptionPropertyPlaceholderConfigurer.getConfig("dev"));
    }

    private static List<AliyunEcs> list = null;

    /**
     * instanceId -> AliyunEcs
     */
    private static Map<String, AliyunEcs> instanceEcsMap = null;

    private static Map<String, String> ipToInstanceMap = null;

    private static long lastLoadTime = 0L;

    private static final long CACHE_EXPIRE = 5 * 60 * 1000L;

    public static List<AliyunEcs> load() {
        long now = System.currentTimeMillis();
        if (list == null || (now - lastLoadTime) > CACHE_EXPIRE) {
            load0(now);
            lastLoadTime = now;
        }
        return list;
    }

    /**
     * 返回最近10分钟的时间范围，使用 ISO8601 UTC 格式。
     * https://help.aliyun.com/document_detail/25696.html?spm=5176.doc25612.2.3.z9WfJp
     * @return  [startTime, endTime]
     */
    public static String[] getUtcRecentTimeRange() {
        return getRecentTimeRange(TimeZone.getTimeZone("UTC"), ISO_TIME);
    }

    /**
     * 返回最近10分钟的时间范围，使用默认的 TimeZone
     * @return
     */
    public static String[] getRecentTimeRange() {
        return getRecentTimeRange(TimeZone.getDefault(), DEFAULT_TIME);
    }

    public static String[] getRecentTimeRange(TimeZone tz, String format) {
        FastDateFormat dateFormat = FastDateFormat.getInstance(format, tz);
        Date endTime = new Date();
        Date startTime = DateUtils.addMinutes(endTime, -10);
        return new String[] {dateFormat.format(startTime), dateFormat.format(endTime)};
    }

    /**
     * 内容：读取阿里云最近10分钟的监控项，然后计算平均值
     * 说明：
     * 监控项: https://help.aliyun.com/document_detail/28619.html?spm=5176.doc51936.2.2.aU6s06
     * java sdk: https://help.aliyun.com/document_detail/28621.html?spm=5176.doc43505.6.643.fGlxir
     *
     * @param instanceId  阿里云实例ID
     */
    public static EcsMonitor getRecentMonitor(String instanceId) {
        String[] range = getRecentTimeRange();
        String startTime = range[0];
        String endTime = range[1];
        double cpuIdle = getAverageValue(query(instanceId, "cpu_idle", startTime, endTime), false);
        double load5m = getAverageValue(query(instanceId, "load_5m", startTime, endTime), false);
        double totalMem = getAverageValue(query(instanceId, "memory_totalspace", startTime, endTime), true);
        //double freeMem = getAverageValue(query(instanceId, "memory_freespace", startTime, endTime));
        double actualUsedMem = getAverageValue(query(instanceId, "memory_actualusedspace", startTime, endTime), true);

        double freeMem = totalMem - actualUsedMem;
        logger.info("cpuIdle: " + cpuIdle + ", load: " + load5m + ", freeMem: " + freeMem + ", totalMem: " + totalMem);
        return new EcsMonitor(cpuIdle, freeMem, load5m);
    }

    /**
     * 判断目标服务器是否够资源发布服务。
     * 详细规则见 {@link #isEnoughRes(String, String)} 的说明
     * @param ip         IP
     * @param jvmArgs    JVM参数表
     * @return
     */
    public static RestResult<Object> isEnoughResByIp(String ip, String jvmArgs) {
        if (MapUtils.isEmpty(ipToInstanceMap)) {
            load();
        }
        String instanceId = ipToInstanceMap.get(ip);
        Assert.notNull(instanceId, "找不到IP: " + ip);
        return isEnoughRes(instanceId, jvmArgs);
    }

    /***
     * 判断目标服务器是否够资源发布服务<br/>
     *
     * 读取阿里云【最近10分钟】的监控项，然后计算【平均值】
     * 1. 平均负载 < CPU数量，如果大于CPU数量，不能发布
     * 2. 剩余内存 - Xmx >= 500MB 才可以发布，如果不指定 Xmx，暂时按1GB算，在配置项目的时候强制填写 Xmx 参数
     * 3. 空闲CPU必须 >= 40% 才可以发布
     * @param instanceId  阿里云实例ID
     * @param jvmArgs     JVM参数表
     * @return
     */
    public static RestResult<Object> isEnoughRes(String instanceId, String jvmArgs) {
        EcsMonitor monitor = getRecentMonitor(instanceId);
        if (monitor.isAllUnknown()) {
            return success("没有监控");
        }
        AliyunEcs ecs = instanceEcsMap.get(instanceId);
        if (!monitor.isLoadUnknown() && ecs.getCpu() <= monitor.getLoad()) {
            logger.info("服务器负载过高, instanceId: {}, ip: {}, cpu: {}, load: {}", instanceId,
                    ecs.getInnerIpAddress(), ecs.getCpu(), monitor.getLoad());

            return fail("服务器负载过高, CPU数量: " + ecs.getCpu() + ", 5分钟平均负载: " + formatDouble(monitor.getLoad()));
        }
        if (!monitor.isCpuIdleUnknow() && monitor.getCpuIdle() < NEW_PROC_MIN_IDLE_CPU) {
            logger.info("服务器空闲CPU过低, instanceId: {}, ip: {}, cpuIdle: {}, min idle require: {}", instanceId,
                    ecs.getInnerIpAddress(), monitor.getCpuIdle(), NEW_PROC_MIN_IDLE_CPU);
            return fail("服务器空闲CPU过低: " + formatDouble(monitor.getCpuIdle()) + "%");
        }

        if (!monitor.isFreeMemUnknown()) {
            double freeMB = monitor.getFreeMemory() / 1024 / 1024;
            int heapSize = getHeapSizeFromJvmArg(jvmArgs);
            double newFree = freeMB - heapSize;
            if (newFree < NEW_PROC_MIN_FREE_MEMORY) {
                logger.info("服务器内存过低, instanceId: {}, ip: {}, freeMem(MB): {}, mem require(MB): {}", instanceId,
                        ecs.getInnerIpAddress(), freeMB, NEW_PROC_MIN_FREE_MEMORY + heapSize);
                return fail("服务器空闲内存过低, -Xmx: " + heapSize + "MB(不指定按" + DEFAULT_XMX + "MB算), 空闲内存: " + formatDouble(freeMB) + ", 要求内存: " + (NEW_PROC_MIN_FREE_MEMORY + heapSize));

            }
        }
        return success("通过");
    }

    /**
     * 根据 ip获取服务器的负载信息
     * @param ip
     * @return
     */
    public static String getServerLoadInfo(String ip) {
        if (MapUtils.isEmpty(ipToInstanceMap)) {
            load();
        }
        String instanceId = ipToInstanceMap.get(ip);
        Assert.notNull(instanceId, "找不到IP: " + ip);
        EcsMonitor monitor = getRecentMonitor(instanceId);
        if (monitor.isAllUnknown()) {
            return "";
        }
        AliyunEcs ecs = instanceEcsMap.get(instanceId);
        long freeMB = Math.round(monitor.getFreeMemory() / 1024 / 1024);
        return "<br/>CPU数量: " + ecs.getCpu() + "<br/>近5分钟平均负载: " + formatDouble(monitor.getLoad()) + "<br/>空闲内存：" + freeMB + "MB";
    }

    /**
     * 返回 JVM 参数里的 Xmx 配置的内存，单位：mb
     * @param jvmArg
     * @return
     */
    public static int getHeapSizeFromJvmArg(String jvmArg) {
        int xmx = getValueFromJvmArg(jvmArg, "-Xmx", DEFAULT_XMX);
        int xms = getValueFromJvmArg(jvmArg, "-Xms", DEFAULT_XMS);
        return Math.max(xmx, xms);
    }

    private static RestResult<Object> success(String message) {
        return new RestResult<>(ApiCode.SUCCESS, message);
    }

    private static RestResult<Object> fail(String message) {
        return new RestResult<>(ApiCode.FAILURE, message);
    }

    private static String formatDouble(double d) {
        return new BigDecimal(d).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static synchronized void load0(long now) {
        if (list == null || (now - lastLoadTime) > CACHE_EXPIRE) {
            final int CAPACITY = 256;
            list = new ArrayList<>(CAPACITY);
            instanceEcsMap = new HashMap<>(CAPACITY);
            ipToInstanceMap = new HashMap<>(CAPACITY);
            for (String region : REGIONS) {
                List<AliyunEcs> result = load(region);
                list.addAll(result);
            }
        }
    }

    private static List<AliyunEcs> load(String region) {
        String accessKeyId = getAccessKeyId();
        String accessKeySecret = getAccessKeySecret();

        List<AliyunEcs> result = new LinkedList<>();
        DescribeInstancesRequest describe = new DescribeInstancesRequest();
        IClientProfile profile = DefaultProfile.getProfile(region, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        int page = 1;
        final int PAGE_SIZE = 100;
        describe.setPageSize(PAGE_SIZE);
        while (true) {
            describe.setPageNumber(page);
            try {
                DescribeInstancesResponse response
                        = client.getAcsResponse(describe);
                if (CollectionUtils.isNotEmpty(response.getInstances())) {
                    for (DescribeInstancesResponse.Instance instance : response.getInstances()) {
//                        logger.info("instance: " + JSON.toJSONString(instance));

                        AliyunEcs ecs = new AliyunEcs();
                        if (isVpcServer(instance)) {
                            ecs.setInnerIpAddress(instance.getVpcAttributes().getPrivateIpAddress().get(0));
                        } else if (CollectionUtils.isNotEmpty(instance.getInnerIpAddress())) {
                            ecs.setInnerIpAddress(instance.getInnerIpAddress().get(0));
                        }
                        if (CollectionUtils.isNotEmpty(instance.getPublicIpAddress())) {
                            ecs.setPublicIpAddress(StringUtils.join(instance.getPublicIpAddress()));
                        }
                        ecs.setInstanceName(instance.getInstanceName());
                        ecs.setRegionId(instance.getRegionId());
                        ecs.setInstanceId(instance.getInstanceId());
                        ecs.setNetworkType(instance.getInstanceNetworkType());
                        String spec = "[cpu: " + instance.getCpu() + ", memory: " + instance.getMemory() + "]";
                        ecs.setSpec(spec);
                        ecs.setCpu(instance.getCpu());
                        ecs.setMemory(instance.getMemory());
                        // 控制界面上显示什么服务器
                        if (isValidInstance(instance.getInstanceName())) {
                            result.add(ecs);
                        }

                        instanceEcsMap.put(ecs.getInstanceId(), ecs);
                        ipToInstanceMap.put(ecs.getInnerIpAddress(), ecs.getInstanceId());

                    }
                    if (response.getInstances().size() < PAGE_SIZE) {
                        break;
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                logger.error("读取阿里云服务器出错", e);
                break;
            }

            page++;
        }
        return result;
    }

    /**
     * 测试环境只能看到 test、dev 的服务器
     * @param instanceName
     * @return
     */
    private static boolean isValidInstance(String instanceName) {
        if (!dev || instanceName == null) {
            return true;
        }
        instanceName = instanceName.toLowerCase();
        return (instanceName.contains("test") || instanceName.contains("dev"));
    }

    private static boolean isVpcServer(DescribeInstancesResponse.Instance instance) {
        return NETWORK_VPC.equalsIgnoreCase(instance.getInstanceNetworkType());
    }

    private static String getAccessKeyId() {
        return EncryptionPropertyPlaceholderConfigurer.getConfig("aliyun.ecs.accessKeyId.encryption");
    }

    private static String getAccessKeySecret() {
        return EncryptionPropertyPlaceholderConfigurer.getConfig("aliyun.ecs.accessKeySecret.encryption");
    }


    private static int getValueFromJvmArg(String jvmArg, String option, int defaultValue) {
        if (StringUtils.isEmpty(jvmArg)) {
            return defaultValue;
        }
        if (jvmArg.contains(option)) {
            int pos1 = jvmArg.indexOf(option) + option.length();
            int pos2 = jvmArg.indexOf(" ", pos1);
            if (pos2 < 0) {
                pos2 = jvmArg.length();
            }
            String value = jvmArg.substring(pos1, pos2).toLowerCase();
            int memory = 0;
            if (value.endsWith("m") || value.endsWith("mb")) {
                memory = NumberUtils.toInt(cutRightString(value));
            } else if (value.endsWith("g") || value.endsWith("gb")) {
                memory = NumberUtils.toInt(cutRightString(value)) * 1024;
            } else if (value.endsWith("k") || value.endsWith("kb")) {
                memory = NumberUtils.toInt(cutRightString(value)) / 1024;
            }
            return memory;
        }
        return defaultValue;
    }

    private static String cutRightString(String str) {
        String s = str;
        while (s.length() > 0) {
            String tmp = s.substring(s.length() - 1);
            if (!NumberUtils.isDigits(tmp)) {
                s = s.substring(0, s.length() - 1);
            } else {
                break;
            }
        }
        return s;
    }

    private static String query(String instanceId, String metric, String startTime, String endTime) {
        if (MapUtils.isEmpty(instanceEcsMap)) {
            load();
        }
        if (!instanceEcsMap.containsKey(instanceId)) {
            return null;
        }
        String region = instanceEcsMap.get(instanceId).getRegionId();
        QueryMetricListRequest request = new QueryMetricListRequest();
        request.setProject("acs_ecs_dashboard");
        request.setMetric(metric);
        request.setPeriod("60");
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        JSONObject dim = new JSONObject();
        dim.put("instanceId", instanceId);
        request.setDimensions(dim.toJSONString());
        request.setAcceptFormat(FormatType.JSON);
        IClientProfile profile = DefaultProfile.getProfile(region,
                getAccessKeyId(), getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        try {
            QueryMetricListResponse response = client.getAcsResponse(request);
            //logger.info(metric + "监控数据: " + response.getDatapoints());
            return response.getDatapoints();
        } catch (Exception e) {
            logger.error("查询出错", e);
        }
        return null;
    }

    /**
     * 取监控数据的值
     * @param data  阿里云接口返回的数据，json格式
     * @param last  是否只取最新的一条，如果是 false，取所有数据的平均值
     * @return 返回 EcsMonitor.UNKNOWN 表示没有数据，否则返回 >=0 的值
     */
    private static double getAverageValue(String data, boolean last) {
        if (StringUtils.isEmpty(data)) {
            return EcsMonitor.UNKNOWN;
        }
        List<HashMap<String, Object>> dataList = JSON.parseObject(data, new TypeReference<List<HashMap<String, Object>>>() {
        });
        if (dataList == null || dataList.size() == 0) {
            return EcsMonitor.UNKNOWN;
        }

        if (last) {
            HashMap<String, Object> row = dataList.get(dataList.size() - 1);
            Number average = (Number) row.get("Average");
            return average.doubleValue();
        }
        double total = 0;
        for (HashMap<String, Object> row : dataList) {
            Number average = (Number) row.get("Average");
            total += average.doubleValue();
        }
        return total / dataList.size();
    }

    public static class EcsMonitor {

        public static final int UNKNOWN = -1;

        private double cpuIdle;

        /**
         * 空闲内存，byte
         */
        private double freeMemory;

        /**
         * 5分钟平均负载
         */
        private double load;

        public EcsMonitor() {}

        public EcsMonitor(double cpuIdle, double freeMemory, double load) {
            this.cpuIdle = cpuIdle;
            this.freeMemory = freeMemory;
            this.load = load;
        }

        public double getCpuIdle() {
            return cpuIdle;
        }

        public void setCpuIdle(double cpuIdle) {
            this.cpuIdle = cpuIdle;
        }

        public double getFreeMemory() {
            return freeMemory;
        }

        public void setFreeMemory(double freeMemory) {
            this.freeMemory = freeMemory;
        }

        public double getLoad() {
            return load;
        }

        public void setLoad(double load) {
            this.load = load;
        }

        public boolean isCpuIdleUnknow() {
            return cpuIdle <= UNKNOWN;
        }

        public boolean isLoadUnknown() {
            return load <= UNKNOWN;
        }

        public boolean isFreeMemUnknown() {
            return freeMemory < UNKNOWN;
        }

        public boolean isAllUnknown() {
            return isLoadUnknown() && isCpuIdleUnknow() && isFreeMemUnknown();
        }
    }
}
