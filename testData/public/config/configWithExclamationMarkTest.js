require({
    baseUrl: '/blocks',
    paths: {
        moduleOne: 'childBlocks/childBlock',
        moduleTwo: '/blocks/block',
        moduleThree: '../rootWebPathFile'
    }
})