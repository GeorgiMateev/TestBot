(function () {

  var startTesting = function() {
    var loadingIndicator = $('.loading-indicator');
    var testingButton = $('button.start-testing');
    var testingPromise = $.ajax('http://localhost:8182/automate');
    testingButton.hide();
    loadingIndicator.show();

    testingPromise
    .then(function() {
      $('.tests-result').attr('data-available', 'true');
    })
    .always(function() {
      loadingIndicator.hide();
      testingButton.show();
    });
  }

  $(document).ready(function() {
    var testingButton = $('button.start-testing');
    testingButton.click(startTesting);
  });
}());