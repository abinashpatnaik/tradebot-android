const { URL } = require('url');
const reqUrl = new URL("http://10.0.2.2:3002/api/portfolio");
const uri = new URL("http://56.228.3.36:3001");
reqUrl.hostname = uri.hostname;
console.log(reqUrl.toString());
