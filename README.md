# WebStormRequireJsPlugin #

Require.js completion and reference path plugin for WebStorm, PHPStorm and other Idea family IDE with Javascript plugin


## Getting started ##

To get started, you need to set the path to the public directory. This directory pointed to by the web server.
#### Example

Project stucture:
```dir
.idea/
psd/
    - index.psd
    - view.psd
public/
    - index.html
    - main.js
    - vendor/
        - jquery.js
        - require.js
    - css/
        - main.css
readme.md
```

In this case, the directory pointed to by the web server directory is "public". For proper operation of the plug-in
configuration must specify: "Path to public directory" value "public".

#### Example 2

Project stucture:
```dir
.idea/
backend/
    - src/
        - ...
frontend/
    - psd/
        - index.psd
        - view.psd
    - public/
        - index.html
        - main.js
        - vendor/
            - jquery.js
            - require.js
        - css/
            - main.css
readme.md
```

In this case, the directory pointed to by the web server directory is "frontend/public". For proper operation of the
plug-in configuration must specify: "Path to public directory" value "frontend/public".

#### Example 3

Project stucture:
```dir
.idea/
index.html
main.js
vendor/
    - jquery.js
    - require.js
css/
    - main.css
```

In this case, the directory pointed to by the web server is the root directory of the project. For proper operation of
the plug-in configuration must specify: "Path to public directory" value "" (empty string).