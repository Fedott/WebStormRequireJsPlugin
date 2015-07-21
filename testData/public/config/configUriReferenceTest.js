requirejs.config({
    paths: {
        uriModuleWithProtocol: 'https://google.com/jquery.js',
        uriModuleWithoutProtocol: '//google.com/jquery.2.js'
        uriModuleWithProtocolWithoutExt: 'https://google.com/jquery.7'
        uriModuleWithoutProtocolWithoutExt: '//google.com/jquery.8'
    }
});
