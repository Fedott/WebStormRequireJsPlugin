requirejs.config({
    baseUrl: basePath + '/plugins/designer/scripts/lib',
 
    paths: {
    	designer: '../app',
    	object: '../app/object',
    	samples: '../app/samples',
    	constraints: '../app/constraints',
    	notifications: '../app/notifications',
    	tests: '../../tests/unit',
        public: '../../../..'
    },
 
	shim: {
		'bootstrap': {
			deps: ['jquery']
		},
//		'public/plugins/bootstrap-select': {
//			deps: ['jquery'],
//			exports: 'jQuery.fn.selectpicker'
//		},
		'bootbox': {
			deps: ['jquery', 'bootstrap']
		},
		'bootstrap-tour': {
			deps: ['jquery', 'bootstrap'],
			export: 'Tour'
		},
		'fabric': {
			deps: ['jquery']
		},
		'fabric-aligning_guidelines': {
			deps: ['fabric'],
			exports: 'fabric'
		},
		'samples/modelBasic': {
		},
 
        // 3D libs
		'modernizr': { exports: 'Modernizr' },
		'3D/ThreexFullscreen': { exports: 'THREEx.FullScreen' },
        '3D/three': { deps: ['3D/ThreexFullscreen'], exports: 'THREE' },
        //'3D/controls/TrackballControls': { deps: ['3D/three'], exports: 'THREE' },
        '3D/controls/PointerLockControls': { deps: ['3D/three'], exports: 'THREE' },
        '3D/controls/OrbitControls': { deps: ['3D/three'], exports: 'THREE' },
        '3D/Detector': { exports: 'Detector' },
        '3D/stats': { exports: 'Stats' },
        '3D/tween': { exports: 'TWEEN' },
        '3D/loaders/MTLLoader': { deps: ['3D/three'], exports: 'THREE' },
        '3D/loaders/OBJMTLLoader': { deps: ['3D/three', '3D/loaders/MTLLoader'], exports: 'THREE' },
        '3D/loaders/DDSLoader': { deps: ['3D/three'], exports: 'THREE' },
        '3D/exporters/SceneExporter': { deps: ['3D/three'], exports: 'THREE' },
        '3D/exporters/MaterialExporter': { deps: ['3D/three'], exports: 'THREE' },
        '3D/exporters/GeometryExporter': { deps: ['3D/three'], exports: 'THREE' },
        '3D/exporters/BufferGeometryExporter': { deps: ['3D/three'], exports: 'THREE' },
        '3D/exporters/OBJExporter': { deps: ['3D/three'], exports: 'THREE' },
        '3D/exporters/ObjectExporter': { deps: ['3D/three', '3D/exporters/GeometryExporter', '3D/exporters/BufferGeometryExporter', '3D/exporters/MaterialExporter'], exports: 'THREE' },
        '3D/csg': { deps: ['3D/three'] },
        '3D/ThreeCSG': { deps: ['3D/three', '3D/csg'], exports: 'THREE' },
        '3D/datgui': { exports: 'dat.gui' }
	}
});
 
// use existing jQuery from eMobic layout
define('jquery', [], function() {
    return jQuery;
});
//use existing bootstrap from eMobic layout
define('bootstrap', [], function() {
    return jQuery;
});
 
// Start the main app logic.
requirejs(['jquery', 'underscore', 'bootstrap', 'bootbox', 'modernizr', 'bootstrap-tour', 'fabric-custom', 'designer/House', 'designer/designer', 'samples/modelBasic'],
function   ($, _,  _bootstrap, bootbox, modernizr, Tour, fabric, House, Designer, modelBasicJSON) {
 
	$( function() {
		//jQuery loaded
		console.log('App init');
 
		// check if canvas is available
		if (!Modernizr.canvas) {
			$('#designer-loader-content').html("<span class='text-danger' style='font-size: 1.3em'><span style='font-size: 2em'>Erreur</span><br /> votre navigateur n'est pas assez récent<br /> pour lancer cette application</span><br />");
			return null;
		}
 
		Designer.initialize();
 
		// Hide loader
		Designer.loader.hide();
 
		if (Designer) {
			requirejs.onError = function (error) {
				Designer.throwError(error);
			};
		}
 
		// Display loader if loading error
		if (Designer &&
			(!$ || !fabric || !House)) {
			Designer.throwError();
		}
		// If QUnit test, run it
		if (typeof QUnit !== 'undefined') {
			console.info('QUnit init');
 
			var testModules = [
				'tests/designerTest',
				'tests/wallTest'
			];
 
			require(testModules, QUnit.start());
		}
	});
});

define(require, function(
	var one = require('designer/designer');
	var notFound = require('constraints/notFound');
));