package ansible;

import com.ibeiliao.deployment.compile.service.JavaCompiler;
import com.ibeiliao.deployment.compile.vo.CompileRequest;
import com.ibeiliao.deployment.log.PushLogService;
import org.junit.Test;

/**
 * 详情 : 编译测试
 *
 * @author liangguanglong
 */

public class TestCompile {

    private PushLogService pushLogService;

    @Test
    public void test() {
        CompileRequest compileRequest = new CompileRequest();
        compileRequest.setModuleName("platform-admin");
        compileRequest.setProjectName("platform");
        compileRequest.setEnv("dev");
        compileRequest.setHistoryId(20);
        compileRequest.setTagName("branch/20160212_test");
        compileRequest.setSvnAddr("https://svn.ibeiliao.net/svn/platform");
        compileRequest.setCompileShell("mvn -P=${env} -Dmaven.test.skip=true -U clean install \n cp -R ${moduleDir}*.war ${targetDir} ");
        compileRequest.setVersion("" + System.currentTimeMillis());

        JavaCompiler packageService = new JavaCompiler(compileRequest, pushLogService);
        packageService.compileModule();
    }


    @Test
    public void testGit() {
        CompileRequest compileRequest = new CompileRequest();
        compileRequest.setModuleName("jenkinsPie");
        compileRequest.setProjectName("jenkins");
        compileRequest.setEnv("dev");
        compileRequest.setHistoryId(20);
        compileRequest.setTagName("");
        compileRequest.setSvnAddr("https://github.com/kevinYin/jenkinsPie.git");
        compileRequest.setCompileShell("mvn -Dmaven.test.skip=true -U clean install \n cp -R ${moduleDir}/target/*.war ${targetDir} ");
        compileRequest.setVersion("" + System.currentTimeMillis());

        JavaCompiler packageService = new JavaCompiler(compileRequest, pushLogService);
        packageService.compileModule();
    }
}
