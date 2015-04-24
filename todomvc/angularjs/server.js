var path = require('path');
var express = require('express');

var app = express();

var staticPath = path.resolve(__dirname);
app.use(express.static(staticPath));

app.post('/api/data', function (req, res, next) {
    res.send(true);
});

app.listen(8080, function() {
  console.log('listening');
});