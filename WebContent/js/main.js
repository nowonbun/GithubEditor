"use strict";
(function (obj) {
    $(obj.onLoad);
})((function () {
    var __ = {
        property: {},
        fn: {},
        ev: function () {
        }
    };
    $(__.ev);
    $(function () {
    });
    return {
        onLoad: function () {
            var table = $('#analysisList').DataTable({
                ajax: {
                    url: 'analysis.ajax',
                    type: "POST",
                    dataSrc: ''
                },
                columns: [{
                        data: 'idx'
                    }, {
                        data: 'url'
                    }, {
                        data: 'referrer'
                    }, {
                        data: 'browser'
                    }, {
                        data: 'agent'
                    }, {
                        data: 'createddate'
                    }],
                lengthMenu: [5],
                lengthChange: false,
                //bInfo : false
            });
        }
    };
})());
