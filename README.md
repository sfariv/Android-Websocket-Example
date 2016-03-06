# Android-Websocket-Example

The client is a Android studio project using http://autobahn.ws/android library.
It connects to 2 servers one for streaming and one for status report.

There are 2 Node.js servers. the one for streaming a video file. and the one that opens a websocket that listens for clients status reports.
The streaming server uses Nodejs http module. 
the client status server uses https://github.com/websockets/ws
