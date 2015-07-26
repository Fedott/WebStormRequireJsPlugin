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
    var depend10 = require('locationMainPackage');
    var depend11 = require('locationPackage');
    var depend12 = require('packageWithSlash/package/location');
    var depend13 = require('packageWithSlash/package/location/otherFile');
    var depend14 = require('packageWithSlash/package/location/notFound');
})
