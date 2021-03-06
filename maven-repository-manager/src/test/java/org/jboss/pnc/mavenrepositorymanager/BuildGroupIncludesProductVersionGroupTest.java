package org.jboss.pnc.mavenrepositorymanager;

import static org.apache.commons.lang.StringUtils.join;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.commonjava.aprox.client.core.Aprox;
import org.commonjava.aprox.model.core.Group;
import org.commonjava.aprox.model.core.StoreKey;
import org.commonjava.aprox.model.core.StoreType;
import org.jboss.pnc.mavenrepositorymanager.fixture.TestBuildExecution;
import org.jboss.pnc.spi.BuildExecution;
import org.jboss.pnc.spi.BuildExecutionType;
import org.jboss.pnc.spi.repositorymanager.model.RepositorySession;
import org.junit.Test;

public class BuildGroupIncludesProductVersionGroupTest extends AbstractRepositoryManagerDriverTest {

    @Test
    public void verifyGroupComposition_ProductVersion_NoConfSet() throws Exception {
        // create a dummy non-chained build execution and repo session based on it
        BuildExecution execution = new TestBuildExecution("product+myproduct+1.0", null, "build+myproject+12345",
                BuildExecutionType.STANDALONE_BUILD);
        Aprox aprox = driver.getAprox();

        RepositorySession repositoryConfiguration = driver.createBuildRepository(execution);
        String repoId = repositoryConfiguration.getBuildRepositoryId();

        assertThat(repoId, equalTo(execution.getBuildContentId()));

        // check that the build group includes:
        // - the build's hosted repo
        // - the product version repo group
        // - the "shared-releases" repo group
        // - the "shared-imports" repo
        // - the public group
        // ...in that order
        Group buildGroup = aprox.stores().load(StoreType.group, repoId, Group.class);

        System.out.printf("Constituents:\n  %s\n", join(buildGroup.getConstituents(), "\n  "));
        assertGroupConstituents(buildGroup, new StoreKey(StoreType.hosted, execution.getBuildContentId()),
                new StoreKey(StoreType.group, execution.getTopContentId()), new StoreKey(StoreType.group,
                        RepositoryManagerDriver.SHARED_RELEASES_ID), new StoreKey(StoreType.hosted,
                        RepositoryManagerDriver.SHARED_IMPORTS_ID), new StoreKey(StoreType.group,
                        RepositoryManagerDriver.PUBLIC_GROUP_ID));
    }

}
