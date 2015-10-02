'use strict';

var Table = require('./../../table');

module.exports = Table.extend({

  tabRepeater: 'tabProvider in decisionInstanceTabs',
  tabIndex: 1,
  tabLabel: 'Outputs',

  variableName: function(idx) {
    return element.all(by.repeater('(v, info) in variables')).get(idx).element(by.css('[ng-switch-when="name"]')).getText();
  },

  variableValue: function(idx) {
    return element.all(by.repeater('(v, info) in variables')).get(idx).element(by.css('[ng-switch-when="value"]')).getText();
  }


});
