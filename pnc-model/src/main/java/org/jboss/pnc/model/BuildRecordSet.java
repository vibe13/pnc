package org.jboss.pnc.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class BuildRecordSet, that encapsulates the set of buildRecords that compose a specific version of a Product.
 *
 * @author avibelli
 */
@Entity
public class BuildRecordSet implements GenericEntity<Integer> {

    private static final long serialVersionUID = 1633628406382742445L;

    public static final String DEFAULT_SORTING_FIELD = "id";

    @Id
    @SequenceGenerator(name="build_record_set_id_seq", sequenceName="build_record_set_id_seq", allocationSize=1)    
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="build_record_set_id_seq")
    private Integer id;

    private String buildSetContentId;

    @OneToOne(mappedBy = "buildRecordSet")
    private ProductMilestone productMilestone;

    @OneToOne(mappedBy = "buildRecordSet")
    private ProductRelease productRelease;

    @ManyToMany
    @JoinTable(name = "build_record_set_map", joinColumns = {
            @JoinColumn(name = "build_record_set_id", referencedColumnName = "id") }, inverseJoinColumns = {
            @JoinColumn(name = "build_record_id", referencedColumnName = "id") })
    private List<BuildRecord> buildRecord;

    /**
     * Instantiates a new builds the collection.
     */
    public BuildRecordSet() {

    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the milestone
     */
    public ProductMilestone getProductMilestone() {
        return productMilestone;
    }

    /**
     * @param productMilestone the milestone to set
     */
    public void setProductMilestone(ProductMilestone productMilestone) {
        this.productMilestone = productMilestone;
    }

    public ProductRelease getProductRelease() {
        return productRelease;
    }

    public void setProductRelease(ProductRelease productRelease) {
        this.productRelease = productRelease;
    }

    /**
     * @return the buildRecord
     */
    public List<BuildRecord> getBuildRecord() {
        return buildRecord;
    }

    /**
     * @param record the BuildRecord(s) to set
     */
    public void setBuildRecord(List<BuildRecord> record) {
        this.buildRecord = record;
    }

    public String getBuildSetContentId() {
        return this.buildSetContentId;
    }

    /**
     * @param buildSetContentId The identifier to use when aggregating and retrieving content related to this record set which
     *        is stored via external services.
     */
    public void setBuildSetContentId(String buildSetContentId) {
        this.buildSetContentId = buildSetContentId;
    }

    @Override
    public String toString() {
        String version = "none";
        if ( productRelease != null ) {
            version = productRelease.getVersion();
        } else if ( productMilestone != null ) {
            version = productMilestone.getVersion();
        }
        return "BuildRecordSet [id=" + getId() + ", version=" + version + "]";
    }

    public static class Builder {

        private Integer id;

        private String buildSetContentId;

        private ProductMilestone productMilestone;

        private ProductRelease productRelease;

        private List<BuildRecord> buildRecords;

        private Builder() {
            buildRecords = new ArrayList<>();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public BuildRecordSet build() {
            BuildRecordSet buildRecordSet = new BuildRecordSet();
            buildRecordSet.setId(id);
            buildRecordSet.setBuildSetContentId(buildSetContentId);

            // Set the bi-directional mappings
            if (productMilestone != null) {
                productMilestone.setBuildRecordSet(buildRecordSet);
            }
            buildRecordSet.setProductMilestone(productMilestone);

            if (productRelease != null) {
                productRelease.setBuildRecordSet(buildRecordSet);
            }
            buildRecordSet.setProductRelease(productRelease);

            for (BuildRecord buildRecord : buildRecords) {
                buildRecord.getBuildRecordSets().add(buildRecordSet);
            }
            buildRecordSet.setBuildRecord(buildRecords);

            return buildRecordSet;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder buildSetContentId(String buildSetContentId) {
            this.buildSetContentId = buildSetContentId;
            return this;
        }

        public Builder productRelease(ProductRelease productRelease) {
            this.productRelease = productRelease;
            return this;
        }

        public Builder productMilestone(ProductMilestone productMilestone) {
            this.productMilestone = productMilestone;
            return this;
        }

        public Builder buildRecord(BuildRecord buildRecord) {
            this.buildRecords.add(buildRecord);
            return this;
        }

        public Builder buildRecords(List<BuildRecord> buildRecords) {
            this.buildRecords = buildRecords;
            return this;
        }

    }
}
