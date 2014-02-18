define(function(require) {
    var depend1 = require('moduleOne!notFound');
    var depend2 = require('moduleTwo!moduleOne');
    var depend3 = require('moduleOne!block');
    var depend4 = require('moduleOne!../fileWithExclamationMarkTest');
    var depend5 = require('moduleOne!./fileWithDotPath');
})
