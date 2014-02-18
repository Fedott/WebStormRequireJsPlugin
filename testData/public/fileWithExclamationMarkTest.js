define(function(require) {
    var depend1 = require('moduleOne!');
    var depend2 = require('moduleTwo!module');
    var depend3 = require('moduleOne!block');
    var depend4 = require('moduleOne!./bloc');
    var depend5 = require('moduleOne!./file');
    var depend6 = require('moduleOne!./');
})
