## Rendering PlantUML diagrams with AWS Lambda

Serverless Framework 1.4.0 project to render [PlantUML](http://plantuml.com) diagrams with an AWS API Gateway + Lambda
function backend in such a way that the URLs could be used directly in HTML IMG tags, for example.

This is work in progress, only a simple proof of concept for now.

Issues:
- Serverless does not support specifying binary response MIME types for API Gateway. So this has to be done manually after serverless deployment.
- API Gateway Lambda Proxy integration needs the request to contain a specific MIME type in the Accept header in order
to return binary data. Otherwise it returns the data base64 encoded. Currently this sort of works by setting `*/*` as a binary MIME type and 
returning a response with `Content-Type: */*` but this is obviously not good to do. `*/*` works because browsers make requests where `*/*` is in the Accept header.

