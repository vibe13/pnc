'use strict';

(function() {
  var app = angular.module('pnc', [
    'ui.router',
    'pnc.Dashboard',
    'pnc.remote',
    'pnc.product',
    'pnc.project',
    'pnc.configuration',
    'pnc.record',
    'pnc.configuration-set',
    'pnc.websockets',
    'pnc.environment'
    ]);

  app.config(['$stateProvider', '$urlRouterProvider', '$locationProvider',
    function($stateProvider, $urlRouterProvider, $locationProvider) {

      $locationProvider.html5Mode(false).hashPrefix('!');

      $stateProvider.state('error', {
        url: '/error',
        views: {
          'content@': {
            templateUrl: 'error.html'
          }
        }
      });

        // Redirect any unmatched URLs to the error state.
        $urlRouterProvider.otherwise('/error');
      }
      ]);

  app.run(['$rootScope', '$log', 'AuthService', 'ENV',
    function($rootScope, $log, AuthService, ENV) {

      if (ENV.name === 'prod') {
        AuthService.login('keycloak.json');
      }

      // Handle errors with state changes.
      $rootScope.$on('$stateChangeError',
        function(event, toState, toParams, fromState, fromParams, error) {
          $log.debug('Caught $stateChangeError: event=%O, toState=%O, ' +
            'toParams=%O, fromState=%O, fromParams=%O, error=%O',
            event, toState, toParams, fromState, fromParams, error);
        }
      );
    }
  ]);

})();
