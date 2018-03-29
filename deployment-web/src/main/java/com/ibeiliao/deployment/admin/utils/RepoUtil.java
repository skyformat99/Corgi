package com.ibeiliao.deployment.admin.utils;

import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：版本仓库工具类
 * 详细：
 *
 * @author linyi, 2017/3/15.
 */
public class RepoUtil {

    private static final Logger logger = LoggerFactory.getLogger(RepoUtil.class);


    static {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
    }

    /**
     * 返回完整的仓库地址
     *
     * @param repoUrl 仓库基础地址，不包含 trunk/tag等
     * @param tagName 分支名
     * @return
     */
    public static String getRepoUrl(String repoUrl, String tagName) {
        String svnAddr = repoUrl + (tagName.startsWith("/") ? tagName : "/" + tagName);
        return svnAddr;
    }

    /**
     * 返回 pom.xml 在 SVN 的地址: repoUrl + tagName + moduleName + pom.xml
     *
     * @param repoUrl    仓库基础地址，不包含 trunk/tag等
     * @param tagName    分支名
     * @param moduleName 在 SVN 上的模块全名，比如 service/pay-impl
     * @return
     */
    public static String getPomRepoUrl(String repoUrl, String tagName, String moduleName) {
        String svnAddr = getRepoUrl(repoUrl, tagName);
        if (moduleName.startsWith("/")) {
            svnAddr += moduleName;
        } else {
            svnAddr += "/" + moduleName;
        }
        if (moduleName.endsWith("/")) {
            svnAddr += "pom.xml";
        } else {
            svnAddr += "/pom.xml";
        }
        return svnAddr;
    }

    /**
     * 读取 SVN 最后一次提交的版本号
     *
     * @param svnAddr  SVN地址
     * @param account  帐号
     * @param password 密码
     * @return
     * @throws SVNException
     */
    public static String getSvnLastRevision(String svnAddr, String account, String password) throws SVNException {
        SVNURL url = SVNURL.parseURIEncoded(svnAddr);

        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager = SVNClientManager.newInstance(options, account, password);
        SVNWCClient svnwcClient = clientManager.getWCClient();
        SVNInfo info = svnwcClient.doInfo(url, null, null);
        return info.getCommittedRevision().getNumber() + "";
    }

    /**
     * 从 SVN 的 pom.xml 读取 finalName，读取失败抛出异常。
     * 如果有返回，肯定不会为空。
     *
     * @param svnAddr  SVN地址
     * @param account  SVN帐号
     * @param password SVN密码
     * @return
     * @throws Exception
     * @throws IllegalArgumentException 读取pom.xml失败抛出异常
     */
    public static String getFinalNameForSvn(String svnAddr, String account, String password) throws Exception {
        SVNURL url = SVNURL.parseURIEncoded(svnAddr);

        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager = SVNClientManager.newInstance(options, account, password);
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        File tempDir = FileUtils.getTempDirectory();

        String filePath = tempDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + "/";
        File destPath = new File(filePath);
        FileUtils.forceMkdir(destPath);

        updateClient.doExport(url, destPath, null, SVNRevision.HEAD, null, true, SVNDepth.FILES);

        String xml = FileUtils.readFileToString(new File(filePath + "pom.xml"), "utf-8");
        FileUtils.forceDelete(destPath);

        return readPomFinalName(xml);
    }

    /**
     * 从 pom.xml 的内容读取 finalName，读取失败抛出异常。
     * 如果有返回，肯定不会为空。
     *
     * @param xml pom.xml 的内容
     * @return
     * @throws Exception
     */
    public static String readPomFinalName(String xml) throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        // 注：直接parse(xml) 会有编码问题
        Document document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        String finalName = getValue(document, xpath, "//project/build/finalName");
        String packaging = getValue(document, xpath, "//project/packaging");
        if (StringUtils.isEmpty(finalName)) {
            finalName = getValue(document, xpath, "//project/artifactId");
        } else if (finalName.contains("${")) {
            String regx = "\\$\\{([a-zA-z0-9.]+)\\}";
            Pattern pattern = Pattern.compile(regx);
            Matcher matcher = pattern.matcher(finalName);
            if (matcher.matches()) {
                String path = "//" + matcher.group(1).replaceAll("\\.", "/");
                finalName = getValue(document, xpath, path);
            }
        }
        Assert.hasText(finalName, "找不到pom.xml里的finalName");
        return finalName + "." + packaging;
    }

    private static String getValue(Document document, XPath xpath, String exp) throws Exception {
        XPathExpression xpathExpr = xpath.compile(exp);
        Object result = xpathExpr.evaluate(document, XPathConstants.NODESET);
        Assert.notNull(result, "pom.xml找不到: " + exp);
        NodeList nodes = (NodeList) result;
        if (nodes.getLength() <= 0) {
            return "";
        }
        Assert.isTrue(nodes.getLength() == 1, "文件错误, 结果过多: " + exp);
        return nodes.item(0).getTextContent();
    }

    /**
     * 获取git 分支的所有信息
     *
     * @param gitRemoteURL git 地址
     * @param userName     git账户名
     * @param password     git 密码
     * @return key是分支名，value是 版本号 （前8位）
     */
    public static Map<String, String> getGitAllBranchInfo(String gitRemoteURL, String userName, String password) throws GitAPIException {

        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(userName, password);
        Collection<Ref> refs = Git.lsRemoteRepository()
                .setHeads(true)
                .setCredentialsProvider(cp)
                .setRemote(gitRemoteURL)
                .setTags(true)
                .call();

        HashMap<String, String> branch2VersionMap = Maps.newHashMap();
        for (Ref ref : refs) {
            String versionNo = ref.getObjectId().getName().substring(0, 8);
            if (ref.getName().startsWith("refs/heads/master")) {
                branch2VersionMap.put("master", versionNo);
                continue;
            }
            if (ref.getName().startsWith("refs/heads/")) {
                String branchName = ref.getName().replaceFirst("refs/heads/", "/branches/");
                branch2VersionMap.put(branchName, versionNo);
                continue;
            }
            if (ref.getName().startsWith("refs/tags/")) {
                String branchName = ref.getName().replaceFirst("refs/tags/", "/tags/");
                branch2VersionMap.put(branchName, versionNo);
            }
        }

        return branch2VersionMap;
    }


    /**
     * 从git获取finalName
     *
     * @param moduleName
     * @param gitAddr
     * @param account
     * @param password
     * @param branchName 分支名
     * @return
     * @throws Exception
     */
    public static String getFinalNameForGit(String moduleName, String gitAddr, String account, String password, String branchName) throws Exception {
        File tempDir = FileUtils.getTempDirectory();

        String filePath = tempDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + "/";
        File destPath = new File(filePath);
        FileUtils.forceMkdir(destPath);

        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(account, password);

        Git cloneGit;
        // 分支的需要读取分支，master和tag的直接不需要指定branch否则获取不了
        if (isBranch(branchName)) {
            branchName = rebuildBranchName(branchName);
            cloneGit = Git.cloneRepository().setURI(gitAddr)
                    .setCredentialsProvider(cp)
                    .setBranch(branchName)
                    .setDirectory(destPath).call();
        } else {
            cloneGit = Git.cloneRepository().setURI(gitAddr)
                    .setCredentialsProvider(cp)
                    .setDirectory(destPath).call();
        }

        logger.info("是否存在文件：" + new File(filePath + moduleName + "/pom.xml").exists());
        logger.info("destPath: " + destPath);

        String xml = FileUtils.readFileToString(new File(filePath + moduleName + "/pom.xml"), "utf-8");
        FileUtils.forceDelete(destPath);

        return readPomFinalName(xml);
    }

    private static String rebuildBranchName(String branchName) {
        if (branchName.startsWith("/branches/")) {
            branchName = branchName.substring("/branches/".length(), branchName.length());
        }
        if (branchName.startsWith("branches/")) {
            branchName = branchName.substring("branches/".length(), branchName.length());
        }
        return branchName;
    }

    private static boolean isBranch(String branchName) {
        return branchName.startsWith("/branches/") || branchName.startsWith("branches/");
    }

}
