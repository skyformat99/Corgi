package com.ibeiliao.deployment.admin.controller.deploy;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.service.global.ProjectEnvService;
import com.ibeiliao.deployment.admin.service.project.ProjectModuleService;
import com.ibeiliao.deployment.admin.service.project.ProjectService;
import com.ibeiliao.deployment.admin.service.server.ServerGroupService;
import com.ibeiliao.deployment.admin.service.server.ServerService;
import com.ibeiliao.deployment.admin.utils.RepoUtil;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.admin.vo.deploy.DeploymentOrder;
import com.ibeiliao.deployment.admin.vo.project.ModuleJvm;
import com.ibeiliao.deployment.admin.vo.project.Project;
import com.ibeiliao.deployment.admin.vo.project.ProjectModule;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.common.RepositoryConstants;
import com.ibeiliao.deployment.common.enums.ModuleRepoType;
import com.ibeiliao.deployment.common.enums.ModuleType;
import com.ibeiliao.deployment.base.ApiCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * 服务器部署记录
 *
 * @author linyi 2017/1/20
 */
@Menu(name = "创建上线单", parent = "项目发布", sequence = 1000000)
@Controller
@RequestMapping("/admin/deploy/")
public class CreateDeploymentController {

    private static final Logger logger = LoggerFactory.getLogger(CreateDeploymentController.class);

    @Autowired
    private DeployHistoryService deployHistoryService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectModuleService projectModuleService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private ServerGroupService serverGroupService;

    /**
     * 页面最多显示200个项目
     */
    private static final int MAX_DISPLAY_PROJECTS = 200;

    static {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
    }

    /**
     * 创建上线单主页，xhtml 仅用于展示页面，ajax 调用 .do 接口获取参数
     *
     * @return
     */
    @MenuResource("创建上线单主页")
    @RequestMapping("create.xhtml")
    public String index(HttpServletRequest request) {
        // 读取用户可以访问的项目
        long accountId = AdminContext.getAccountId();
        List<Project> projects = projectService.queryAdminProjects(accountId, null, 0L,null, 1, MAX_DISPLAY_PROJECTS);
        List<ProjectModule> modules = new LinkedList<>();
        List<ProjectModule> projectModules = null;
        // 读取每个项目的module
        for (Project project : projects) {
            List<ProjectModule> tmpModules = projectModuleService.getByProjectId(project.getProjectId());
            modules.addAll(tmpModules);
        }
        request.setAttribute("projects", projects);
        request.setAttribute("allModules", JSON.toJSON(modules));
        int moduleId = 0;
        int projectId = 0;
        if (request.getParameter("moduleId") != null) {
            moduleId = NumberUtils.toInt(request.getParameter("moduleId"), 0);
            if (moduleId > 0) {
                ProjectModule module = projectModuleService.getByModuleId(moduleId);
                if (module != null) {
                    projectId = module.getProjectId();
                }
            }
            logger.info("输入参数 moduleId: " + moduleId + ", projectId: " + projectId);
        }
        if (projectId <= 0 && projects.size() > 0) {
            projectId = projects.get(0).getProjectId();
            projectModules = projectModuleService.getByProjectId(projectId);
        } else if (projectId > 0) {
            projectModules = projectModuleService.getByProjectId(projectId);
        } else {
            projectModules = Collections.emptyList();
        }
        String repoUrl = "";
        if (moduleId <= 0 && projectModules.size() > 0) {
            moduleId = projectModules.get(0).getModuleId();
            repoUrl = projectModules.get(0).getRepoUrl();
        } else {
            for (ProjectModule pm : projectModules) {
                if (pm.getModuleId() == moduleId) {
                    repoUrl = pm.getRepoUrl();
                    break;
                }
            }
        }
        // 指定发布的module
        request.setAttribute("moduleId", moduleId);
        request.setAttribute("projectId", projectId);
        request.setAttribute("projectModules", projectModules);
        request.setAttribute("repoUrl", repoUrl);
        request.setAttribute("envList", projectEnvService.findAllEnv());
        return ("/deploy/create_deployment");
    }

    /**
     * 查询发布记录列表，按创建时间倒序
     *
     * @return result.object=List，List不为null
     */
    @RequestMapping("create.do")
    @ResponseBody
    @MenuResource("创建上线单")
    public RestResult<Object> create(@ModelAttribute("order") @Valid DeploymentOrder order, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new RestResult<>(ApiCode.FAILURE, bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (order.getServerId().length == 0) {
            return new RestResult<>(ApiCode.FAILURE, "请选择服务器");
        }

        RestResult<Object> resResult = enoughServerRes(order);
        if (!resResult.isSuccess()) {
            return resResult;
        }

        order.setAccountId(AdminContext.getAccountId());
        order.setRealName(AdminContext.getName());
        ensureRevision(order);

        deployHistoryService.createDeploymentOrder(order);
        return new RestResult<>(null);
    }

    @RequestMapping("listRepository")
    @MenuResource("查询分支列表")
    @ResponseBody
    public RestResult<List<Map<String, Object>>> listRepository(int moduleId) {
        ProjectModule module = projectModuleService.getByModuleId(moduleId);
        List<Map<String, Object>> tags = new ArrayList<>();
        if (module != null) {
            if (module.getRepoType() == ModuleRepoType.SVN.getValue()) {

                String tagsPath = (module.getRepoUrl() + RepositoryConstants.TAGS).replaceAll("/" + RepositoryConstants.TAGS, RepositoryConstants.TAGS);
                tags.addAll(fetchSvnTags(tagsPath, module));
                String branchesPath = (module.getRepoUrl() + RepositoryConstants.BRANCHES).replaceAll("/" + RepositoryConstants.BRANCHES, RepositoryConstants.BRANCHES);
                tags.addAll(fetchSvnTags(branchesPath, module));

            } else if (module.getRepoType() == ModuleRepoType.GIT.getValue()) {

                tags.addAll(fetchGitBranches(module.getRepoUrl(), module));

            } else {
                logger.warn("不支持类型 {}, moduleId: {}", module.getRepoType(), moduleId);
            }
        } else {
            logger.error("module=null, moduleId: " + moduleId);
        }
        return new RestResult<>(tags);
    }

    /**
     * 查询模块在某个环境的服务器列表
     *
     * @param moduleId 模块ID
     * @param envId    环境ID，必定大于0
     * @return
     */
    @RequestMapping("queryModuleServer")
    @ResponseBody
    @MenuResource("查询模块服务器列表")
    public RestResult<Map<String, Object>> queryModuleServer(int moduleId, int envId) {
        Assert.isTrue(moduleId > 0);
        Assert.isTrue(envId > 0);

        List<ServerGroup> groups = serverGroupService.getByModuleAndEnv(moduleId, envId);

        List<Integer> groupIds = new ArrayList<>(groups.size());
        for (ServerGroup group : groups) {
            groupIds.add(group.getGroupId());
        }
        List<Server> servers = serverService.getByGroupIds(groupIds);
        Map<String, Object> result = new HashMap<>(4);
        result.put("servers", servers);
        result.put("groups", groups);
        return new RestResult<>(result);
    }

    /**
     * 判断服务器资源是否足够
     * @param order
     * @return ApiCode.SUCCESS=足够
     * @author linyi 2017/5/11
     */
    private RestResult<Object> enoughServerRes(DeploymentOrder order) {
        List<DeployHistory> histories = deployHistoryService.queryDeployHistory(order.getEnvId(), order.getModuleId(), 1, 10);
        // 静态项目不做判断
        ProjectModule module = projectModuleService.getByModuleId(order.getModuleId());
        if (module.getModuleType() == ModuleType.STATIC.getValue()) {
            return new RestResult<>(ApiCode.SUCCESS, "");
        }
        // TODO: 2017/7/18 先屏蔽第一次发布检测
        // 第一次发布才会判断
       /* if (CollectionUtils.isEmpty(histories)) {
            logger.info("第一次发布");
            String jvmArgs = getJvmArgs(order.getModuleId(), order.getEnvId());
            for (int serverId : order.getServerId()) {
                Server server = serverService.getById(serverId);
                if (server == null || StringUtils.isBlank(server.getIp())) {
                    return new RestResult<>(ApiCode.FAILURE, "serverId或IP不存在, serverId: " + serverId);
                }
                RestResult<Object> result = AliyunEcsUtil.isEnoughResByIp(server.getIp(), jvmArgs);
                if (!result.isSuccess()) {
                    return result;
                }
            }
        }*/
        return new RestResult<>(ApiCode.SUCCESS, "");
    }

    private String getJvmArgs(int moduleId, int envId) {
        ProjectModule module = projectModuleService.getByModuleId(moduleId);
        Assert.notNull(module, "模块不存在");
        List<ModuleJvm> list = module.getModuleJvms();
        if (CollectionUtils.isNotEmpty(list)) {
            for (ModuleJvm jvm : list) {
                if (jvm.getEnvId() == envId) {
                    return jvm.getJvmArgs();
                }
            }
        }
        return null;
    }

    private List<Map<String, Object>> fetchSvnTags(String svnAddr, ProjectModule module) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {

            SVNURL url = SVNURL.parseURIEncoded(svnAddr);
            final String prePath = (svnAddr.contains("/") ? svnAddr.substring(svnAddr.lastIndexOf("/")) : "");
            DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
            SVNClientManager clientManager = SVNClientManager.newInstance(options, module.getSvnAccount(), module.getSvnPassword());
            SVNLogClient client = clientManager.getLogClient();
            client.doList(url, SVNRevision.HEAD, SVNRevision.HEAD, false, false, new ISVNDirEntryHandler() {
                @Override
                public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
                    if (StringUtils.isNotEmpty(dirEntry.getRelativePath())) {
                        Map<String, Object> row = Maps.newHashMapWithExpectedSize(2);
                        row.put("url", prePath + "/" + dirEntry.getRelativePath());
                        row.put("version", dirEntry.getRevision());
                        list.add(row);
                    }
                }
            });

            if (list.size() > 0) {
                // 倒序排列
                sortBranchInfo(list);
            }
        } catch (SVNException e) {
            logger.error("读取SVN地址失败: " + svnAddr, e);
        }
        return list;
    }

    private List<Map<String, Object>> fetchGitBranches(String gitURL, ProjectModule module) {
        try {
            List<Map<String, Object>> branchInfoList = new ArrayList<>();
            Map<String, String> branch2VersionMap = RepoUtil.getGitAllBranchInfo(gitURL, module.getSvnAccount(), module.getSvnPassword());
            for (Map.Entry<String, String> entry : branch2VersionMap.entrySet()) {
                HashMap<String, Object> infoType2ValueMap = Maps.newHashMap();
                infoType2ValueMap.put("url", entry.getKey());
                infoType2ValueMap.put("version", entry.getValue());
                branchInfoList.add(infoType2ValueMap);
            }
            if (CollectionUtils.isNotEmpty(branchInfoList)) {
                sortBranchInfo(branchInfoList);
            }
            return branchInfoList;
        } catch (GitAPIException e) {
            logger.error("获取git 分支信息失败", e);
        }
        return Collections.emptyList();
    }

    private void sortBranchInfo(List<Map<String, Object>> branchInfoList) {
        branchInfoList.sort(new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                String url1 = (String) o1.get("url");
                String url2 = (String) o2.get("url");
                int n = url1.compareTo(url2);
                if (n > 0) {
                    return -1;
                } else if (n < 0) {
                    return 1;
                }
                return 0;
            }
        });
    }

    /**
     * 读取svn最大版本号，失败抛出异常
     *
     * @param order
     */
    private void ensureRevision(final DeploymentOrder order) {
        if (StringUtils.isBlank(order.getVersionNo()) && StringUtils.isNotBlank(order.getTagName())) {
            ProjectModule module = projectModuleService.getByModuleId(order.getModuleId());
            Assert.notNull(module, "模块不存在");
            if (module.getRepoType() == ModuleRepoType.SVN.getValue()) {
                ensureSvnRevision(order, module);
            } else if (module.getRepoType() == ModuleRepoType.GIT.getValue()) {
                ensureGitRevision(order, module);
            }
        }
    }

    private void ensureGitRevision(DeploymentOrder order, ProjectModule module) {
        String tagName = order.getTagName().trim();
        try {
            Map<String, String> branch2VersionMap = RepoUtil.getGitAllBranchInfo(module.getRepoUrl(), module.getSvnAccount(), module.getSvnPassword());
            order.setVersionNo(branch2VersionMap.get(tagName));

        } catch (Exception e) {
            logger.error("读取git版本号出错", e);
        }
        if (StringUtils.isEmpty(order.getVersionNo())) {
            throw new IllegalArgumentException("读取git版本号出错: " + order.getTagName());
        }
    }

    private void ensureSvnRevision(DeploymentOrder order, ProjectModule module) {
        String tagName = order.getTagName().trim();
        String svnAddr = module.getRepoUrl() + (tagName.startsWith("/") ? tagName : "/" + tagName);
        try {
            SVNURL url = SVNURL.parseURIEncoded(svnAddr);

            DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
            SVNClientManager clientManager = SVNClientManager.newInstance(options, module.getSvnAccount(), module.getSvnPassword());
            SVNWCClient svnwcClient = clientManager.getWCClient();
            SVNInfo info = svnwcClient.doInfo(url, null, null);
            order.setVersionNo(info.getCommittedRevision().getNumber() + "");

        } catch (Exception e) {
            logger.error("读取版本号出错", e);
        }
        if (StringUtils.isEmpty(order.getVersionNo())) {
            throw new IllegalArgumentException("读取SVN版本号出错: " + order.getTagName());
        }
    }
}
