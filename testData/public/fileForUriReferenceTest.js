define(function(require) {
    var depend1 = require('https://cdn.google.com/jquery.js');
    var depend2 = require('//cdn.google.com/jquery.2.js');
    var depend3 = require('uriModuleWithProtocol');
    var depend4 = require('uriModuleWithoutProtocol');

    var depend5 = require('https://google.com/jquery.5');
    var depend6 = require('//google.com/jquery.6');
    var depend7 = require('uriModuleWithProtocolWithoutExt');
    var depend8 = require('uriModuleWithoutProtocolWithoutExt');
});
