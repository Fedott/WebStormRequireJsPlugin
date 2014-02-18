require({
    baseUrl: '/sub/kits',
    paths: {
        moduleOne: 'kit',
        moduleTwo: '../mainWithRelativePath',
        moduleThree: '/main'
    }
})

define([
    'moduleOne',
    'moduleTwo',
    'moduleThree'
])