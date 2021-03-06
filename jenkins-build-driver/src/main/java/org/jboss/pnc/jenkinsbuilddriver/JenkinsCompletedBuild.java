package org.jboss.pnc.jenkinsbuilddriver;

import org.jboss.pnc.spi.builddriver.BuildDriverResult;
import org.jboss.pnc.spi.builddriver.BuildDriverStatus;
import org.jboss.pnc.spi.builddriver.CompletedBuild;
import org.jboss.pnc.spi.builddriver.exception.BuildDriverException;
import org.jboss.pnc.spi.environment.RunningEnvironment;

/**
 * Created by <a href="mailto:matejonnet@gmail.com">Matej Lazar</a> on 2014-12-23.
 */
class JenkinsCompletedBuild implements CompletedBuild {

    private JenkinsServerFactory jenkinsServerFactory;
    private BuildJob buildJob;
    private RunningEnvironment runningEnvironment;
    private BuildDriverStatus buildDriverStatus;

    JenkinsCompletedBuild(JenkinsServerFactory jenkinsServerFactory, BuildJob buildJob, RunningEnvironment runningEnvironment, BuildDriverStatus buildDriverStatus) {
        this.jenkinsServerFactory = jenkinsServerFactory;
        this.buildJob = buildJob;
        this.runningEnvironment = runningEnvironment;
        this.buildDriverStatus = buildDriverStatus;
    }

    @Override
    public BuildDriverStatus getCompleteStatus() {
        return buildDriverStatus;
    }

    @Override
    public BuildDriverResult getBuildResult() throws BuildDriverException {
        return new JenkinsBuildResult(jenkinsServerFactory, buildJob, runningEnvironment);
    }

    @Override
    public RunningEnvironment getRunningEnvironment() {
        return runningEnvironment;
    }
}
