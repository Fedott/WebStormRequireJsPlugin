define(function(require) {
    var dependWithTwoDot = require('..');
    var dependWithTwoDotAndSlash = require('../');
    var dependWithTwoDotAndSlashTwoChars = require('../bl');
    var dependWithTwoDotAndDirectory = require('../blocks');
    var dependWithTwoDotAndDirectoryAndSlash = require('../blocks/');
    var dependWithTwoDotAndTwoDirectories = require('../blocks/childBlocks');
    var dependWithTwoDotAndTwoDirectoriesAndSlash = require('../blocks/childBlocks/');
    var dependWithTwoDotAndDirectoryAndSlashTwoChars = require('../blocks/blo');

    var dependWithTwoDotParentWebPath = require('../../block');
})