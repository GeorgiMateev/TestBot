(function () {
    var eventsSettings = {
        events: ['click'],
        batchSize: 10;
        serverUrl: 'testserver.com/api/';
    };

    var internalApi = (function (settings) {
        var eventsBuffer = [];

        function getSelectorForElement (node) {
            var path;
            while (node.length) {
                var name = node.localName;
                if (!name) break;
                name = name.toLowerCase();

                var parent = node.parentNode;

                var sameTagSiblings = parent.childNodes;
                if (sameTagSiblings.length > 1) { 
                    allSiblings = parent.children();
                    var index = allSiblings.indexOf(node) + 1;
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
            var endpoint = settings.serverUrl + 'events';

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

    for (var i = 0; i < settings.events.length; i++) {
        var listenTo = settings.events[i];

        document.addEventListener(listenTo, function () {
            internalApi.recordEvent(listenTo, this, Date.now());
        }, true); 
    };
})();