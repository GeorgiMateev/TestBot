(function () {
  var basicUrl = 'http://localhost:8189';

  var startTesting = function() {
    var loadingIndicator = $('.loading-indicator');
    var testingButton = $('button.start-testing');
    var testingPromise = $.ajax(basicUrl + '/automate');
    testingButton.hide();
    loadingIndicator.show();

    testingPromise
    .then(function(run) {
      displayRun(run);
    })
    .always(function() {
      loadingIndicator.hide();
      testingButton.show();
    });
  }

  var displayRun = function(run) {
    var issues = []
    run.issues.forEach(function(issue) {
      issues.push([issue.expectedParentHtml, issue.actualParentHtml])
    })

    drowResults(issues);
  };

  var fulfillChart = function() {
    var runs = $('.previous-runs');
    var numberOfRuns = runs.length;
    var warningsByRun = [];
    runs.each(function(indx) {
      var list = runs[indx].getElementsByTagName('ul')[0];
      warningsByRun.push(list.children.length);
    });

    createChart(numberOfRuns, warningsByRun)
  };

  var fulfillRuns = function () {
    var runsPromise = $.ajax(basicUrl + '/runs')

    runsPromise
    .then(function (runs) {
      runs.forEach(displayRun);
    })
  };

  $(document).ready(function() {
    var testingButton = $('button.start-testing');
    testingButton.click(startTesting);

    fulfillRuns();
  });
}());