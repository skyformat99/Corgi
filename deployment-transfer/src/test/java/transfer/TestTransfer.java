package transfer;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.common.enums.ModuleType;
import com.ibeiliao.deployment.transfer.enums.DeployType;
import com.ibeiliao.deployment.transfer.service.JavaTransferService;
import com.ibeiliao.deployment.transfer.vo.TransferRequest;
import org.junit.Test;

/**
 * 详情 :  测试
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/2/7
 */
public class TestTransfer {

    @Test
    public void testTransform() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setHistoryId(20);
        transferRequest.setSaveFileName("dev/statement-impl.tar.gz");
        transferRequest.setModuleName("statement-impl");
        transferRequest.setEnv("dev");
        transferRequest.setModuleType(ModuleType.SERVICE.getValue());
        transferRequest.setTargetServerIps(Lists.newArrayList("123.56.158.175"));
        transferRequest.setProjectName("pay");
        transferRequest.setRestartShell("com.alibaba.dubbo.container.Main");
        transferRequest.setJvmArgs("-Xms128m -Xmx192m -server -d64");
        // 发布  重启  stop
        JavaTransferService javaTransferService = new JavaTransferService(transferRequest, DeployType.STOP);

        javaTransferService.pushPackageToServer();
        //JavaTransferService.buildModuleRestartShell(transferRequest, new TransferResult());
    }

    @Test
    public void testTemplate() {

    }

}
