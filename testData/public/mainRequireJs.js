requirejs.config({
    baseUrl: '/blocks',
    paths: {
        moduleRelativeBaseUrlPath: 'childBlocks/childBlock',
        moduleAbsolutePath: '/blocks/block',
        moduleRelativeOneDotPath: './block',
        moduleRelativeTwoDotPAth: '../rootWebPathFile'
    }
})