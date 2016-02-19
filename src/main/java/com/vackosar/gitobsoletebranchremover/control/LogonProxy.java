package com.vackosar.gitobsoletebranchremover.control;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Singleton
public class LogonProxy {

    private SshTrasportCallback trasportCallback;

    @Inject
    public LogonProxy(SshTrasportCallback trasportCallback) {
        this.trasportCallback = trasportCallback;
    }

    public interface Callback { void act() throws GitAPIException; }

    public void call(Callback callback) throws GitAPIException {
        try {
            callback.act();
        } catch (TransportException e) {
            System.out.println("Authentification failed.");
            if ("https".equals(trasportCallback.getUri().getScheme())) {
                final String username = String.valueOf(System.console().readLine("Username: "));
                trasportCallback.setUsername(username);
            }
            final String password = String.valueOf(System.console().readPassword("Password: "));
            trasportCallback.setPassword(password);
            callback.act();
        }
    }
}