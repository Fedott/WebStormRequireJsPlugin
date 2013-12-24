define(function(require) {
    var dependWithOneDot = require('.');
    var dependWithOneDotAndSlash = require('./');
    var dependWithOneDotAndSlashTwoChars = require('./bl');
    var dependWithOneDotAndDirectory = require('./childBlocks');
    var dependWithOneDotAndDirectoryAndSlash = require('./childBlocks/');
    var dependWithOneDotAndDirectoryAndSlashTwoChars = require('./childBlocks/ch');
})