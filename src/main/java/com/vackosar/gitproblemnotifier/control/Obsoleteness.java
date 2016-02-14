package com.vackosar.gitproblemnotifier.control;

import com.vackosar.gitproblemnotifier.entity.BranchInfo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;

public class Obsoleteness implements Predicate<BranchInfo> {

    private final Duration difference;

    public Obsoleteness(Integer days) {
        difference = Duration.ofDays(days);
    }

    @Override
    public boolean test(BranchInfo entry) {
        return Duration.between(entry.lastCommit.atStartOfDay(), LocalDateTime.now()).compareTo(difference) > 0;
    }
}