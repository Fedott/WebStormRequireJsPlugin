requirejs.config({
    map: {
        'some/newModule': {
            'foo': 'foo1.2',
            onlyInNewModule: 'foo1.0.js'
        },
        'some/oldModule': {
            'foo': 'foo1.0',
            onlyInOldModule: 'foo1.3.js'
        },
        '*': {
            'foo': 'foo1.3.js',
            bar: 'bar2.0r1.js'
        }
    }
});