(function () {
    var eventsSettings = {
        events: ['click'],
        batchSize: 10,
        serverUrl: 'http://testserver.com/api'
    };

    var internalApi = (function (settings) {
        var eventsBuffer = [];

        function getSelectorForElement (node) {
            var path;
            while (node) {
                var name = node.localName;
                if (!name || name === 'body') break;
                name = name.toLowerCase();

                var parent = node.parentNode;

                var siblings = Array.prototype.filter.call(parent.childNodes, function (node) {
                    return node.nodeType === 1;
                });

                if (siblings.length > 1) {
                    var index = Array.prototype.indexOf.call(siblings, node) + 1;
                    if (index > 1) {
                        name += ':nth-child(' + index + ')';
                    }
                }

                path = name + (path ? '>' + path : '');
                node = parent;
            }

            return path;
        }

        function sendEvents () {
            var endpoint = settings.serverUrl + '/events';

            var xmlhttp = new XMLHttpRequest();
            xmlhttp.open("POST", endpoint, true);
            xmlhttp.submittedData = eventsBuffer;
            xmlhttp.send();
        }

        function recordEvent (eventName, target, timeStamp) {
            var selector = getSelectorForElement(target);

            if (eventsBuffer.length > settings.batchSize) {
                sendEvents();
                eventsBuffer.length = 0;
            }
            else {
                eventsBuffer.push({
                    selector: selector,
                    eventName: eventName,
                    timeStamp: timeStamp
                });
            }
        }

        return {
            recordEvent: recordEvent
        };    
    })(eventsSettings);    

    for (var i = 0; i < eventsSettings.events.length; i++) {
        var listenTo = eventsSettings.events[i];

        document.addEventListener(listenTo, function (e) {
            internalApi.recordEvent(listenTo, e.target, Date.now());
        }, true); 
    };
})();