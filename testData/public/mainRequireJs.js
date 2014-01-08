requirejs.config({
    baseUrl: '/blocks',
    paths: {
        moduleRelativeBaseUrlPath: 'childBlocks/childBlock',
        moduleAbsolutePath: '/blocks/block',
        moduleRelativeOneDotPath: './blocks/block',
        moduleRelativeTwoDotPAth: '../rootWebPathFile'
    }
})