var express = require('express');
var app = express();
var formidable = require('formidable');
var path = require('path');
var fs = require('fs');

app.set('port', (process.env.PORT || 5000));

app.use(express.static(__dirname + '/public'));


app.get('/', function(request, response) {
  response.json("Hello Uploader :D");
});

app.post('/upload', function(req, res){

  // create an incoming form object
  var form = new formidable.IncomingForm();

  var ListFiles = [];

  // specify that we want to allow the user to upload multiple files in a single request
  form.multiples = true;

  // store all uploads in the /uploads directory
  form.uploadDir = path.join(__dirname, '/uploads');

  // every time a file has been uploaded successfully,
  // rename it to it's orignal name
  form.on('file', function(field, file) {
    fs.rename(file.path, path.join(form.uploadDir, file.name));
    var _file = "/uploads/" + file.name;
    ListFiles.push(_file);
  });

  // log any errors that occur
  form.on('error', function(err) {
    res.json({
      "status" : "error",
      "data" : err.message
    });
  });

  // once all the files have been uploaded, send a response to the client hell
  form.on('end', function() {
    res.json({
      "status" : "success",
      "data" : ListFiles
    });
  });

  // parse the incoming request containing the form data
  form.parse(req);

});

app.listen(app.get('port'), function() {
  console.log('Node app is running on port', app.get('port'));
});


