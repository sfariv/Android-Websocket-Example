# Android-Websocket-Example

The client is a Android studio project using http://autobahn.ws/android library.
It connects to 2 servers one for streaming and 1 for status report.

There are 2 Node.js servers. 1 for streaming a video file. and 1 that opens a websocket that listens for clients status reports.
The streaming server uses Nodejs http module. 
the client status server uses https://github.com/websockets/ws
