define(function (require) {
    var foo = require('foo');
    var bar = require('bar');

    var foo = require('f');
    var bar = require('b');
    var onlyInNewModule = require('on');
})