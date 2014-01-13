require({
    'baseUrl': 'blocks',
    'paths': {
        'aliasChildBlock': 'childBlocks/childBlock',
        'aliasRelativePath': '../blocks'
    }
})

define([
    'alias',
    'aliasRelativePath/child',

    'aliasChildBlock',
    'aliasRelativePath/block'
])