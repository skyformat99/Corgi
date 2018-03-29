package com.ibeiliao.deployment.admin.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * 详情 :  jgit的测试
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/3/29
 */
public class JGitTest {

    private String localPath, remotePath;
    private Repository localRepo;
    private Git git;

    @Before
    public void init() throws IOException {
        localPath = "/Users/kevin/tmp/platform-parent";
        remotePath = "http://gits.ibeiliao.net/platform/platform-parent.git";
        localRepo = new FileRepository(localPath + "/.git");
        git = new Git(localRepo);
    }


    @Test
    public void testClone() throws IOException, GitAPIException {
        long begin = System.currentTimeMillis();
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider("liangguanglong", "kevin711");

        Git cloneGit = Git.cloneRepository().setURI(remotePath)
                .setCredentialsProvider(cp)
                .setBranch("20170329_testBranch")
                .setDirectory(new File(localPath)).call();
        long time = System.currentTimeMillis() - begin;
        System.out.println(time);
    }

    @Test
    public void testGetVersion() throws Exception {

        Repository existingRepo = new FileRepositoryBuilder()
                .setGitDir(new File("/Users/kevin/beiliao/gitlab/platform-parent/.git"))
                .build();
        Git git = new Git(existingRepo);
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider("liangguanglong", "kevin711");
        Collection<Ref> refs = git.lsRemote().setCredentialsProvider(cp).setRemote("origin").call();
        for (Ref ref : refs) {
            System.out.println(ref.getObjectId().getName());
        }
    }

    @Test
    public void testRemoteInfoWithoutClone() throws GitAPIException {
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider("liangguanglong", "kevin711");

        Collection<Ref> refs = Git.lsRemoteRepository()
                .setHeads(true)
                .setCredentialsProvider(cp)
                .setRemote("git@gits.ibeiliao.net:platform/platform-parent.git")
                .setTags(true)
                .call();

        for (Ref ref : refs) {
            System.out.println(ref.getName());
            System.out.println(ref.getObjectId().getName());
        }
    }

    @Test
    public void testGetBranchesWithoutClone() throws GitAPIException, IOException {
        String originURL = "http://gits.ibeiliao.net/platform/platform-parent.git";
        Map<String, String> branchInfo = RepoUtil.getGitAllBranchInfo(originURL, "liangguanglong", "kevin711");
        assert branchInfo.isEmpty();
    }

    @Test
    public void testReadFinalName() throws Exception {
        String finalNameForGit = RepoUtil.getFinalNameForGit("platform-admin", "http://gits.ibeiliao.net/platform/platform-parent.git", "liangguanglong", "kevin711", "branches/20170505_test");
        System.out.println(finalNameForGit);
    }
}
