package com.vackosar.gitobsoletebranchremover.control;

import com.google.inject.Singleton;
import com.vackosar.gitobsoletebranchremover.entity.Action;
import com.vackosar.gitobsoletebranchremover.entity.Arguments;
import com.vackosar.gitobsoletebranchremover.entity.BranchInfo;
import com.vackosar.gitobsoletebranchremover.entity.BranchType;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RefSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.function.Consumer;

@Singleton
public class Processor implements Consumer<BranchInfo> {

    Logger log = LoggerFactory.getLogger(getClass());

    @Inject private Git git;
    @Inject private Arguments arguments;
    @Inject private TransportCallback transportCallback;

    @Override
    public void accept(BranchInfo branchInfo) {
        if (arguments.action == Action.remove) {
            remove(branchInfo, false);
        } else if (arguments.action == Action.forceremove) {
            remove(branchInfo, true);
        } else {
            System.out.println(branchInfo.toOutputLine());
        }
    }

    private void remove(BranchInfo branchInfo, boolean force) {
        try {
            if (branchInfo.merged || force) {
                git
                        .branchDelete()
                        .setForce(true)
                        .setBranchNames(branchInfo.getFullBranchName())
                        .call();
                if (arguments.branchType == BranchType.remote) {
                    RefSpec refSpec = new RefSpec()
                            .setSource(null)
                            .setDestination("refs/heads/" + branchInfo.branchName);
                    git
                            .push()
                            .setTransportConfigCallback(transportCallback)
                            .setRefSpecs(refSpec)
                            .setRemote(branchInfo.remoteName.get())
                            .call();
                }
            } else {
                log.error("Unmerged branch '" + branchInfo.getFullBranchName() + "' was not removed.");
            }
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
