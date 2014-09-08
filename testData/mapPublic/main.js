requirejs.config({
    map: {
        'some/newModule': {
            'foo': 'foo1.2'
        },
        'some/oldModule': {
            'foo': 'foo1.0'
        },
        '*': {
            'foo': 'foo.js',
            bar: 'bar2.0r1.js'
        }
    }
});