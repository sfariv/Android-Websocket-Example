var fs = require("fs"),
	http = require("http"),
	url = require("url"),
	path = require("path");

http.createServer(function (req, res) {
	if (req.url != "/movie.mp4") {
		res.writeHead(200, { "Content-Type": "text/html" });
		console.log("src: http://" + req.headers.host+'/movie.mp4');
		res.end('<video src="http://'+req.headers.host+'/movie.mp4" controls=""></video>');
	} else {
		var file = path.resolve(__dirname,"public/movie.mp4");
		var range = req.headers.range;
		if(!range) {
			console.log("war: range not set using 0-");
			range = "0-";
		}
		console.log('range: ' + range);
		var positions = range.replace(/bytes=/, "").split("-");
		var start = parseInt(positions[0], 10);

		fs.stat(file, function(err, stats) {
			if(err) {
				console.log("err: " + err.message);
				res.writeHead(404, {});
				res.end(err.message);
				return;
			}
	  		var total = stats.size;
			var end = positions[1] ? parseInt(positions[1], 10) : total - 1;
			var chunksize = (end - start) + 1;

			res.writeHead(206, {
				"Content-Range": "bytes " + start + "-" + end + "/" + total,
				"Accept-Ranges": "bytes",
				"Content-Length": chunksize,
				"Content-Type": "video/mp4",
			});
				//"Cache-Control": "no-cache, no-store, must-revalidate",
				//"Pragma": "no-cache",
				//"Expires": 0
	  		var stream = fs.createReadStream(file, { start: start, end: end }).on("open", function() {
	      		stream.pipe(res);
	    	}).on("error", function(err) {
	      		res.end(err);
	    	});
		});
	}
}).listen(8888);