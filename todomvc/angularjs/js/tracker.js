(function () {
    if (navigator.userAgent === 'TestBot') return;

    var eventsSettings = {
        events: ['click'],
        batchSize: 3,
        serverUrl: 'http://localhost:8080'
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

            var idSelector = childNode.getAttribute('id');
            var classes = childNode.getAttribute('class');
            var classSelector = classes ? classes.replace(/\s/g, '.') : '';

            if (idSelector) {
                name += '#' + idSelector;
            }
            else if (classSelector) {
                name += '.' + classSelector;
            }

            return name;
        }

        function recordEvent (eventName, target, timeStamp) {
            var selector = getSelectorForElement(target);

            eventsBuffer.push({
                targetSelector: selector,
                type: eventName,
                timeStamp: timeStamp
            });
        }

        function recordChildListMutationNodes (target, nodes, type, timeStamp) {
            for (var i = 0; i < nodes.length; i++) {
                var node = nodes[i];
                //process only elements for now
                if (node.nodeType !== 1 ||
                    node.localName === 'script' ||
                    node.localName === 'style') continue;

                var targetSelector = getSelectorForElement(target);
                var childSelector = getChildSelector(target, node);

                var mutationSelector = targetSelector ?
                    targetSelector + '>' + childSelector :
                    childSelector;

                // the area around the mutation, helps for visualization of the problematic part of the page
                var surroundingHtml = target.outerHTML;

                mutationsBuffer.push({
                    type: type,
                    childSelector: mutationSelector,
                    targetSelector: targetSelector,
                    surroundingHtml: surroundingHtml,
                    timeStamp: timeStamp
                });
            };
        }

        function recordChildListMutation (mutation, timeStamp) {
            recordChildListMutationNodes(mutation.target, mutation.addedNodes, 'added', timeStamp);
            recordChildListMutationNodes(mutation.target, mutation.removedNodes, 'removed', timeStamp);
        }

        function sendData () {
            if (mutationsBuffer.length === 0) return;

            var endpoint = settings.serverUrl + '/data';

            var xmlhttp = new XMLHttpRequest();
            xmlhttp.open("POST", endpoint, true);
            xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            xmlhttp.send(JSON.stringify({
                events: eventsBuffer,
                mutations: mutationsBuffer,
                timeStamp: Date.now()
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

    // for (var i = 0; i < eventsSettings.events.length; i++) {
    //     var listenTo = eventsSettings.events[i];

    //     document.addEventListener(listenTo, function (e) {
    //         internalApi.recordEvent(listenTo, e.target, Date.now());
    //     }, true); 
    // };
    
    document.addEventListener('click', function (e) {
        internalApi.recordEvent('click', e.target, Date.now());
    }, true);

    document.addEventListener('keypress', function (e) {
        var currentFocus = e.target;
        if (currentFocus.localName === 'input' &&
            currentFocus.type === 'text' &&
            currentFocus.value &&
            e.keyCode === 13) { //enter
            internalApi.recordEvent('enter', e.target, Date.now());
        }        
    }, true);

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