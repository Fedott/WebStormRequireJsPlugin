define(function(require) {
    var depend = require('app/as');
    var dependTrue = require('blocks/');

    var dependWithOneDot = require('.');
    var dependWithOneDotAndSlash = require('./');
    var dependWithOneDotAndSlashTwoChars = require('./bl');
    var dependWithOneDotAndDirectory = require('./childBlocks');
    var dependWithOneDotAndDirectoryAndSlash = require('./childBlocks/');
    var dependWithOneDotAndDirectoryAndSlashTwoChars = require('./childBlocks/ch');

    var dependWithTwoDot = require('..');
    var dependWithTwoDotAndSlash = require('../');
    var dependWithTwoDotAndSlashTwoChars = require('../bl');
    var dependWithTwoDotAndDirectory = require('../blocks');
    var dependWithTwoDotAndDirectoryAndSlash = require('../blocks/');
    var dependWithTwoDotAndTwoDirectories = require('../blocks/childBlocks');
    var dependWithTwoDotAndTwoDirectoriesAndSlash = require('../blocks/childBlocks/');
    var dependWithTwoDotAndDirectoryAndSlashTwoChars = require('../blocks/blo');
})