'use strict';

(function() {

  var module = angular.module('pnc.configuration');

  module.controller('ConfigurationListController', [
    '$log',
    '$state',
    'configurationList',
    'PncRestClient',
    function($log, $state, configurationList, PncRestClient) {
      var self = this;
      $log.debug('ConfigurationListController >> this=%O, configurationList=%O',
                 self, configurationList);

      self.configurations = configurationList;
      self.projects = [];

      angular.forEach(self.configurations, function(configuration){

          PncRestClient.Project.get({
              projectId: configuration.projectId
          }).$promise.then(
            function (result) {
              if (result) {
                self.projects.push(result);
                //console.log(JSON.stringify(self.projects));
              }
            }
          );
      });
    }
  ]);

  module.controller('ConfigurationCreateController', [
    '$state',
    '$log',
    'PncRestClient',
    'Notifications',
    'environments',
    'projects',
    function($state, $log, PncRestClient, Notifications, environments,
             projects) {

      this.data = new PncRestClient.Configuration();
      this.environments = environments;
      this.projects = projects;

      var that = this;

      this.submit = function() {
        that.data.$save().then(
          function(result) {
            Notifications.success('Configuration created');
            $state.go('configuration.detail.show', {
              configurationId: result.id
            });
          },
          function(response) {
            $log.error('Create configuration failed: response: %O', response);
            Notifications.error('Action Failed.');
          }
        );
      };
    }
  ]);

  module.controller('ConfigurationDetailController', [
    '$log',
    '$state',
    'Notifications',
    'PncRestClient',
    'configurationDetail',
    'environmentDetail',
    'projectDetail',
    function($log, $state, Notifications, PncRestClient, configurationDetail,
             environmentDetail, projectDetail) {
      $log.debug('ConfigurationDetailController >> arguments=%O', arguments);

      this.configuration = configurationDetail;
      this.environment = environmentDetail;
      this.project = projectDetail;

      var that = this;

      // Executing a build of a configuration
      this.build = function() {
        $log.debug('Initiating build of: %O', this.configuration);


        PncRestClient.Configuration.build({
          configurationId: that.configuration.id }, {}).$promise.then(
            function(result) {
              $log.debug('Initiated Build: %O, result: %O', that.configuration,
                         result);
              Notifications.success('Initiated build of configuration:' +
                                    that.configuration.name);
            },
            function(response) {
              $log.error('Failed to initiated build: %O, response: %O',
                         that.configuration, response);
              Notifications.error('Action Failed.');
            }
          );
      };

      // Update a build configuration after editting
      this.update = function() {
        $log.debug('Updating configuration: %O', this.configuration);

        this.configuration.$update().then(
          function(result) {
            $log.debug('Update Config: %O, result: %O', that.configuration,
                       result);
            Notifications.success('Configuration updated.');
          },
          function(response) {
            $log.error('Update configuration: %O failed, response: %O',
                       that.configuration, response);
            Notifications.error('Action Failed.');
          }
        );
      };

      // Cloning a build configuration
      this.clone = function() {
        this.configuration.$clone().then(function(result) {
          $log.debug('Clone Configuration: %O Successful Result: %O',
               that.configuration, result);

          $state.go('configuration.detail.show', { configurationId: result.id });
          Notifications.success('Configuration cloned.');
        },
        function(response) {
          $log.error('Clone configuration: %O failed, response: %O',
                     that.configuration, response);
          Notifications.error('Action Failed.');
        });
      };

      // Deleting a build configuration
      this.delete = function() {
        this.configuration.$delete().then(
          // Success
          function (result) {
            $log.debug('Delete Config: %O success result: %O',
                       that.configuration, result);
            Notifications.success('Configuration Deleted');
            $state.go('configuration.list', {}, { reload: true, inherit: false,
                      notify: true });
          },
          // Failure
          function (response) {
            $log.error('Delete configuration: %O failed, response: %O',
                       that.configuration, response);
            Notifications.error('Action Failed.');
          }
        );
      };
    }
  ]);

  module.controller('ConfigurationSidebarController', [
    '$log',
    '$stateParams',
    'PncRestClient',
    'buildRecordList',
    function($log, $stateParams, PncRestClient, buildRecordList) {
      $log.debug('ConfigurationSidebarController >> arguments=%O', arguments);

      this.buildRecords = buildRecordList;

      var that = this;

      this.refresh = function() {
        PncRestClient.Record.getAllForConfiguration({
            configurationId: $stateParams.configurationId
        }).$promise.then(
          function(result) {
            that.buildRecords = result;
          }
        );
      };
    }
  ]);

})();
