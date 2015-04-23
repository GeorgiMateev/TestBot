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

                var parent = node.parentNode;

                name = getChildSelector(parent, node);

                path = name + (path ? '>' + path : '');
                node = parent;
            }

            return path;
        }

        function getChildSelector (parentNode, childNode) {
            if(!childNode || !childNode.localName) return;

            var name = childNode.localName.toLowerCase();

            var siblings = Array.prototype.filter.call(parentNode.childNodes, function (node) {
                return node.nodeType === 1;
            });

            var sameTagSiblings = Array.prototype.filter.call(parentNode.childNodes, function (node) {
                return node.nodeType === 1 && node.localName === name;
            });

            if (sameTagSiblings.length > 1) {
                var index = Array.prototype.indexOf.call(siblings, childNode) + 1;
                if (index > 1) {
                    name = ':nth-child(' + index + ')';
                }
            }

            return name;
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
                if (addedNode.nodeType !== 1 || addedNode.localName === 'script') continue;

                var targetSelector = getSelectorForElement(mutation.target);
                var childSelector = getChildSelector(mutation.target, addedNode);

                var addedSelector = targetSelector ?
                    targetSelector + '>' + childSelector :
                    childSelector;

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
                if (removedNode.nodeType !== 1 || removedNode.localName === 'script') continue;

                var targetSelector = getSelectorForElement(mutation.target);
                var childSelector = getChildSelector(mutation.target, removedNode);

                var removedSelector = targetSelector ?
                    targetSelector + '>' + childSelector :
                    childSelector;

                mutationsBuffer.push({
                    type: 'removed',
                    removedSelector: removedSelector,
                    targetSelector: targetSelector,
                    timeStamp: timeStamp
                });
            };
        }

        function sendData () {
            if (mutationsBuffer.length === 0) return;

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
        var timeStamp = Date.now();

        for(var i = 0; i < mutations.length; i++) {
            var mutation = mutations[i];
            internalApi.mutationTypes[mutation.type](mutation, timeStamp);
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