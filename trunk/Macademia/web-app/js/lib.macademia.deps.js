var NIMBLE_BASE = "/plugins/nimble-0.4-SNAPSHOT/dev/";

$.deps.init('/Macademia', {
    'nimble-login-register': [
        'uploadify',
        NIMBLE_BASE + "/js/jquery/jquery.url.js",
        NIMBLE_BASE + "/js/jquery/jquery.bt.custom.js",
        NIMBLE_BASE + "/js/jquery/jquery.jgrowl.js",
        NIMBLE_BASE + "/js/jquery/nimblecore.js",
        NIMBLE_BASE + "/js/jquery/nimbleui.js",
        NIMBLE_BASE + "/js/jquery/nimbleui.growl.js",
        NIMBLE_BASE + "/js/jquery/jquery.pstrength.min.js",
        NIMBLE_BASE + "/css/famfamfam.css",
        NIMBLE_BASE + "/css/administration.css",
        NIMBLE_BASE + "/css/jquery/jgrowl.css"
    ],
    'uploadify' : [
        '/js/uploadify/swfobject.js',
        '/js/uploadify/jquery.uploadify.v2.1.0.min.js',
        '/js/uploadify/uploadify.css',
        '/js/lib.macademia.upload.js'
    ],
    'none' : [
        NIMBLE_BASE + "/js/jquery/jquery.url.js",
    ]
});