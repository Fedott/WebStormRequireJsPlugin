require({
    packages: [
        "packageSimple",
        {
           name: "packageWithName"
        },
        {
            name: "packageWithMain",
            main: "packageFile"
        },
        {
            name: "packageWithLocation",
            location: "packageLocation"
        },
        {
            name: "packageWithLocationAndMain",
            main: "packageFile",
            location: "packageLocation2"
        },
        "packageDirNotExists",
        {
            name: "packageWithMainNotExists",
            main: "notExists"
        },
        {
            name: "locationMainPackage",
            main: "location"
            location: "../sub/packages/locationMainPackage"
        },
        {
            name: "locationPackage",
            location: "../sub/packages/locationPackage"
        },
        'packageWithSlash/package/location'
    ]
})
