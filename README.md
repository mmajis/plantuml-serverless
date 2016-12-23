## Rendering PlantUML diagrams with AWS Lambda

Serverless Framework project to render [PlantUML](http://plantuml.com) diagrams with an AWS API Gateway + Lambda
function backend in such a way that the URLs could be used directly in HTML IMG tags, for example.

This is work in progress, only a simple proof of concept for now.

Demo: https://393sqs3x53.execute-api.eu-west-1.amazonaws.com/plantuml/png/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0

(Markdown won't render a non-image content type as an image so embedding an image here does not work :( )

Issues:
- Serverless does not support specifying binary response MIME types for API Gateway. So this has to be done manually after serverless deployment.
- API Gateway Lambda Proxy integration needs the request to contain a specific MIME type in the Accept header in order
to return binary data. Otherwise it returns the data base64 encoded. Currently this sort of works by setting `*/*` as a binary MIME type and 
returning a response with `Content-Type: */*` but this is obviously not good to do. `*/*` works because browsers make requests where `*/*` is in the Accept header.
    - Need API Gateway to support raw binary responses without requiring the client to request correct MIME type.
    - Or switch to use non proxy Lambda integration
