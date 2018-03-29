package com.ibeiliao.deployment.compile.vo;

/**
 * 详情 : 编译的返回结果
 *
 * @author liangguanglong
 */
public class CompileResult {

    /**
     * 是否编译成功
     */
    private boolean compileSuccess = true;

    /**
     * 打包文件名 (包含完整路径)
     */
    private String compiledFileName;

    /**
     * 在存储系统（比如OSS）保存的文件名
     */
    private String saveFileName;

    public boolean isCompileSuccess() {
        return compileSuccess;
    }

    public void setCompileSuccess(boolean compileSuccess) {
        this.compileSuccess = compileSuccess;
    }

    public String getCompiledFileName() {
        return compiledFileName;
    }

    public void setCompiledFileName(String compiledFileName) {
        this.compiledFileName = compiledFileName;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }
}
