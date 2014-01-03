define(function(require) {
    var referenceNotFound = require('app/as');
    var reference1 = require('blocks/block');
    var reference2 = require('block');
    var reference3 = require('childBlocks/childBlock');
    var reference4 = require('childBlocks');
    var reference5 = require('/block');
    var reference6 = require('/blocks/block');
    var reference7 = require('/childBlocks/childBlock');
    var reference8 = require('./blocks/block');
    var reference9 = require('../public/blocks/block');
})