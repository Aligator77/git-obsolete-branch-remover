package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vackosar.gitproblemnotifier.control.BranchInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class Main {
    public static void main(String[] args) throws GitAPIException {
        final Injector injector = Guice.createInjector(new Module());
        injector
                .getInstance(ObsoleteBranchesLister.class)
                .listObsolete().stream()
                .map(BranchInfo::convertToOutputLine)
                .forEach(System.out::println);
        closeGit(injector);
    }

    private static void closeGit(Injector injector) {
        final Git git = injector.getInstance(Git.class);
        git.getRepository().close();
        git.close();
    }
}
