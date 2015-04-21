(function () {
    var eventsSettings = {
        events: ['click'],
        batchSize: 3,
        serverUrl: 'http://localhost:8080/api'
    };

    var internalApi = (function (settings) {
        var eventsBuffer = [];
        var mutationsBuffer = [];

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

        function recordEvent (eventName, target, timeStamp) {
            var selector = getSelectorForElement(target);

            eventsBuffer.push({
                targetSelector: selector,
                eventName: eventName,
                timeStamp: timeStamp
            });
        }

        function recordChildListMutation (mutation, timeStamp) {
            for (var i = 0; i < mutation.addedNodes.length; i++) {
                var addedNode = mutation.addedNodes[i];
                //process only elements for now
                if (addedNode.nodeType !== 1) continue;

                var targetSelector = getSelectorForElement(mutation.target);
                var addedSelector = targetSelector + '>' + addedNode.localName;

                mutationsBuffer.push({
                    type: 'added',
                    addedSelector: addedSelector,
                    targetSelector: targetSelector,
                    timeStamp: timeStamp
                });
            };

            for (var i = 0; i < mutation.removedNodes.length; i++) {
                var removedNode = mutation.removedNodes[i];
                //process only elements for now
                if (removedNode.nodeType !== 1) continue;

                var targetSelector = getSelectorForElement(mutation.target);
                var removedSelector = targetSelector + '>' + removedNode.localName;

                mutationsBuffer.push({
                    type: 'removed',
                    removedSelector: removedSelector,
                    targetSelector: targetSelector,
                    timeStamp: timeStamp
                });
            };
        }

        function sendData () {
            var endpoint = settings.serverUrl + '/data';

            var xmlhttp = new XMLHttpRequest();
            xmlhttp.open("POST", endpoint, true);
            xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            xmlhttp.send(JSON.stringify({
                events: eventsBuffer,
                mutations: mutationsBuffer
            }));
            xmlhttp.onreadystatechange = function() {
                if(xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                    eventsBuffer.length = 0;
                    mutationsBuffer.length = 0;
                }
            }
        }

        return {
            recordEvent: recordEvent,
            mutationTypes: {
                childList: recordChildListMutation
            },
            sendData: sendData
        };
    })(eventsSettings);

    for (var i = 0; i < eventsSettings.events.length; i++) {
        var listenTo = eventsSettings.events[i];

        document.addEventListener(listenTo, function (e) {
            internalApi.recordEvent(listenTo, e.target, Date.now());
        }, true); 
    };

    var observer = new MutationObserver(function (mutations) {
        for(var i = 0; i < mutations.length; i++) {
            var mutation = mutations[i];
            internalApi.mutationTypes[mutation.type](mutation, Date.now());
        }

        internalApi.sendData();
    });

    observer.observe(document, {
        subtree: true,
        childList: true,
        attributes: false,
        attributeOldValue: false
    });
})();