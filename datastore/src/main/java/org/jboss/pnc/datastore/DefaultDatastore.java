package org.jboss.pnc.datastore;

import org.jboss.pnc.datastore.repositories.BuildConfigurationRepository;
import org.jboss.pnc.datastore.repositories.BuildRecordRepository;
import org.jboss.pnc.model.BuildConfiguration;
import org.jboss.pnc.model.BuildRecord;
import org.jboss.pnc.spi.datastore.Datastore;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Stateless
public class DefaultDatastore implements Datastore {

    @Inject
    BuildRecordRepository buildRecordRepository;

    @Inject
    BuildConfigurationRepository buildConfigurationRepository;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void storeCompletedBuild(BuildRecord buildRecord) {
        storeBuildConfiguration(buildRecord);
        buildRecordRepository.save(buildRecord);
    }

    private void storeBuildConfiguration(BuildRecord buildRecord) {
        if(buildRecord.getLatestBuildConfiguration() != null) {
            BuildConfiguration configurationFromDB = buildConfigurationRepository.findOne(buildRecord.getLatestBuildConfiguration().getId());
            buildRecord.setLatestBuildConfiguration(configurationFromDB);
        }
    }
    
}
