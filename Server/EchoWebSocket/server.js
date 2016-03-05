var WebSocketServer = require('ws').Server,
	wss = new WebSocketServer({ port: 3000 });

wss.on('connection', function connection(ws) {
  	console.log("ws: " + getClientIPAddress(ws));
  	ws.send('Hello');
  	ws.on('message', function incoming(message) {
    	console.log('received: %s', message);
    	ws.send(message);
  	});
});

console.log("websocket listening on port 3000");

function getClientIPAddress(socket) {
	return socket.upgradeReq.headers['x-forwarded-for'] || socket.upgradeReq.connection.remoteAddress
}