define(function(require) {
    var dependNotFound = require('app/as');
    var dependTrue = require('blocks/');
    var dependWithoutSlash = require('blocks');
    var dependTwoChars = require('bl');
    var dependTwoDirectory = require('blocks/childBlocks');
    var dependTwoDirectoryWithSlash = require('blocks/childBlocks/');
    var dependRootFound = require('root');
})