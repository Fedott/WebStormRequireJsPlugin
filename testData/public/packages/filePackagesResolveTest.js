define(function(require) {
    var depend1 = require('packageSimple');
    var depend2 = require('packageSimple/otherFile');
    var depend3 = require('packageWithMain');
    var depend4 = require('packageWithLocationAndMain');
    var depend5 = require('packageDirNotExists');
    var depend6 = require('packageWithMainNotExists');
    var depend7 = require('packageWithMainNotExists/otherFile');
    var depend8 = require('packageSimple/main');
    var depend9 = require('packageWithLocation');
})
