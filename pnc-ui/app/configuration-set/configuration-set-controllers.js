'use strict';

(function() {

  var module = angular.module('pnc.configuration-set');

  module.controller('ConfigurationSetListController', [
    '$log',
    '$state',
    'configurationSetList',
    function($log, $state, configurationSetList) {
      $log.debug('ConfigurationSetListController >> this=%O, configurationSetList=%O',
                 this, configurationSetList);

      this.buildconfigurationsets = configurationSetList;
    }
  ]);

  module.controller('ConfigurationSetCreateController', [
    '$log',
    '$state',
    'products',
    'PncRestClient',
    'Notifications',
    function($log, $state, products, PncRestClient, Notifications) {
      $log.debug('ConfigurationSetCreateController >> this=%O, products=%O',
                 this, products);

      this.data = new PncRestClient.ConfigurationSet();

      var self = this;
      self.products = products;
      self.productVersions = [];

      self.getProductVersions = function(productId) {
        $log.debug('**Getting productVersions of Product: %0**', productId);

        if (productId) {
          PncRestClient.Version.query({
            productId: productId
          }).$promise.then(
            function(result) {
              self.productVersions = result;
              if (result) {
                self.data.productVersionId = result[0].id;
              }
            }
          );
        }
        else {
          self.productVersions = [];
        }
      };

      this.submit = function() {
        self.data.$save().then(
          function(result) {
            $log.debug('Configuration Set created: %s', result);
            Notifications.success('Configuration Set created');
            if (self.data.productVersionId) {
              var params = { productId: parseInt(self.selectedProductId), versionId: self.data.productVersionId };
              $state.go('product.version', params, { reload: true, inherit: false,
                      notify: true });
            }
            else {
              $state.go('configuration-set.list');
            }
          },
          function(response) {
            $log.error('Create Configuration Set failed: response: %O', response);
            Notifications.error('Action Failed.');
          }
        );
      };
    }
  ]);

  module.controller('ConfigurationSetAddConfigurationController', [
    '$log',
    '$state',
    'configurationSetDetail',
    'projects',
    'PncRestClient',
    'Notifications',
    function($log, $state, configurationSetDetail, projects, PncRestClient, Notifications) {
      $log.debug('ConfigurationSetAddConfigurationController >> this=%O, products=%O',
                 this, projects);

      var self = this;
      self.configurationSetDetail = configurationSetDetail;
      self.projects = projects;
      self.configurations = [];
      self.selectedConfiguration = {};

      self.getBuildConfigurations = function(projectId) {
        $log.debug('**Getting build configurations of Project: %0**', projectId);

        if (projectId) {
          PncRestClient.Configuration.getAllForProject({
            projectId: projectId
          }).$promise.then(
            function(result) {
              self.configurations = result;
              if (result) {
                self.data.configurationId = result[0].id;
              }
            }
          );
        }
        else {
          self.configurations = [];
        }
      };

      self.submit = function() {
       // Retrieve all the last builds (based on ID, not date) of all the build configurations
       angular.forEach(self.configurations, function(configuration){

          if (configuration.id === parseInt(self.data.configurationId)) {
            self.selectedConfiguration = configuration;
            return;
          }
       });

       if (self.selectedConfiguration) {
         PncRestClient.ConfigurationSet.addConfiguration({
            configurationSetId: self.configurationSetDetail.id
         }, self.selectedConfiguration).$promise.then(
           function(result) {
             $log.debug('Configuration added to Configuration Set: %s', result);
             Notifications.success('Build Configuration added to Build Configuration Set');
             var params = { configurationSetId: self.configurationSetDetail.id };
             $state.go('configuration-set.detail', params, { reload: true, inherit: false,
                      notify: true });
           },
           function(response) {
             $log.error('Build Configuration adding failed: response: %O', response);
             Notifications.error('Action Failed.');
           }
         );
       }
      };
    }
  ]);

  module.controller('ConfigurationSetDetailController', [
    '$log',
    '$state',
    'Notifications',
    'PncRestClient',
    'configurationSetDetail',
    'configurations',
    'previousState',
    function($log, $state, Notifications, PncRestClient, configurationSetDetail,
             configurations, previousState) {
      var self = this;

      $log.debug('ConfigurationSetDetailController >> this=%O', self);
      self.set = configurationSetDetail;
      self.configurations = configurations;
      self.lastBuildRecords = [];

      // Retrieve all the last builds (based on ID, not date) of all the build configurations
      angular.forEach(configurations, function(configuration){

          PncRestClient.Record.getLatestForConfiguration({
              configurationId: configuration.id
          }).$promise.then(
            function (result) {
              if (result[0]) {
                self.lastBuildRecords.push(result[0]);
              }
            }
          );
      });

      self.build = function() {
        $log.debug('**Initiating build of SET: %s**', self.set.name);

        PncRestClient.ConfigurationSet.build({
          configurationSetId: self.set.id }, {}).$promise.then(
            function(result) {
              $log.debug('Initiated Build: %O, result: %O', self.set,
                         result);
              Notifications.success('Initiated build of configurationSet:' +
                                    self.set.name);
            },
            function(response) {
              $log.error('Failed to initiated build: %O, response: %O',
                         self.set, response);
              Notifications.error('Action Failed.');
            }
        );
      };

      // Update a build configuration set after editting
      this.update = function() {
        $log.debug('Updating configuration-set: %O', this.set);

        this.set.$update().then(
          function(result) {
            $log.debug('Update Config: %O, result: %O', self.set,
                       result);
            Notifications.success('Configuration updated.');
          },
          function(response) {
            $log.error('Update set: %O failed, response: %O',
                       self.set, response);
            Notifications.error('Action Failed.');
          }
        );
      };

      self.getProductVersions = function(productId) {
        $log.debug('**Getting productVersions of Product: %0**', productId);

        if (productId) {
          PncRestClient.Version.query({
            productId: productId
          }).$promise.then(
            function(result) {
              self.productVersions = result;
              if (result) {
                self.data.productVersionId = result[0].id;
              }
            }
          );
        }
        else {
          self.productVersions = [];
        }
      };

      self.remove = function(configurationId) {
        $log.debug('**Removing configurationId: %0 from Build Configuration Set: %0**', configurationId, self.set);

        PncRestClient.ConfigurationSet.removeConfiguration({
            configurationSetId: self.set.id,
            configurationId: configurationId
        }).$promise.then(
          // Success
          function (result) {
            $log.debug('Removal of Configuration from Configuration Set: %O success result: %O',
             self.set, result);
            Notifications.success('Configuration removed from Configuration Set');
            var params = { configurationSetId: self.set.id };
            $state.go('configuration-set.detail', params, { reload: true, inherit: false, notify: true });
          },
          // Failure
          function (response) {
            $log.error('Removal of Configuration from Configuration Set: %O failed, response: %O',
             self.set, response);
            Notifications.error('Action Failed.');
          }
        );
      };

      // Deleting a build configuration set
      self.delete = function() {
        self.set.$delete().then(
          // Success
          function (result) {
            $log.debug('Delete Configuration Set: %O success result: %O',
                       self.set, result);
            Notifications.success('Build Configuration Set Deleted');
            // Attempt to fo to previous state
            $state.go(previousState.Name, previousState.Params);
          },
          // Failure
          function (response) {
            $log.error('Delete configuration set: %O failed, response: %O',
                       self.set, response);
            Notifications.error('Action Failed.');
          }
        );
      };

    }
    ]);

})();
