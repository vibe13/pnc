/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.pnc.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class BuildConfigurationSet implements GenericEntity<Integer> {

    private static final long serialVersionUID = 2596901834161647987L;

    public static final String DEFAULT_SORTING_FIELD = "id";

    @Id
    @SequenceGenerator(name="build_configuration_set_id_seq", sequenceName="build_configuration_set_id_seq", allocationSize=1)    
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="build_configuration_set_id_seq")
    private Integer id;

    @NotNull
    private String name;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH })
    private ProductVersion productVersion;

    @ManyToMany
    @JoinTable(
            name="build_configuration_set_map",
            joinColumns={@JoinColumn(name="build_configuration_set_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="build_configuration_id", referencedColumnName="id")})
    private Set<BuildConfiguration> buildConfigurations = new HashSet<>();

    public BuildConfigurationSet() {
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the productVersion
     */
    public ProductVersion getProductVersion() {
        return productVersion;
    }

    /**
     * @param productVersion the productVersion to set
     */
    public void setProductVersion(ProductVersion productVersion) {
        this.productVersion = productVersion;
    }

    /**
     * @return the buildConfigurations set
     */
    public Set<BuildConfiguration> getBuildConfigurations() {
        return buildConfigurations;
    }

    /**
     * @param buildConfigurations the buildConfigurations to set
     */
    public void setBuildConfigurations(Set<BuildConfiguration> buildConfigurations) {
        this.buildConfigurations = buildConfigurations;
    }

    /**
     * @param buildConfiguration the buildConfiguration to add to the set
     */
    public void addBuildConfiguration(BuildConfiguration buildConfiguration) {
        this.buildConfigurations.add(buildConfiguration);
    }

    /**
     * @param buildConfiguration the buildConfiguration to remove from the set
     */
    public void removeBuildConfiguration(BuildConfiguration buildConfiguration) {
        this.buildConfigurations.remove(buildConfiguration);
    }

    public static class Builder {

        private Integer id;

        private String name;

        private ProductVersion productVersion;

        private Set<BuildConfiguration> buildConfigurations = new HashSet<BuildConfiguration>();

        private Builder() {

        }

        public BuildConfigurationSet build() {
            BuildConfigurationSet buildConfigurationSet = new BuildConfigurationSet();
            buildConfigurationSet.setId(id);
            buildConfigurationSet.setName(name);
            buildConfigurationSet.setProductVersion(productVersion);
            buildConfigurationSet.setBuildConfigurations(buildConfigurations);
            for (BuildConfiguration buildConfiguration : buildConfigurations) {
                buildConfiguration.addBuildConfigurationSet(buildConfigurationSet);
            }

            return buildConfigurationSet;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder productVersion(ProductVersion productVersion) {
            this.productVersion = productVersion;
            return this;
        }

        public Builder buildConfigurations(Set<BuildConfiguration> buildConfigurations) {
            this.buildConfigurations = buildConfigurations;
            return this;
        }

        public Builder buildConfiguration(BuildConfiguration buildConfiguration) {
            this.buildConfigurations.add(buildConfiguration);
            return this;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public ProductVersion getProductVersion() {
            return productVersion;
        }

    }

}
