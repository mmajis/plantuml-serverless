## Rendering PlantUML diagrams with AWS Lambda

Serverless Framework project to render [PlantUML](http://plantuml.com) diagrams with an AWS API Gateway + Lambda
function backend in such a way that the URLs could be used directly in HTML IMG tags, for example.

This is work in progress, only a simple proof of concept for now.

## Available in AWS Serverless Application Repository

See the pre-packaged AWS SAM application [here in the AWS Serverless Application Repository](https://serverlessrepo.aws.amazon.com/applications/arn:aws:serverlessrepo:us-east-1:293246570391:applications~plantuml-render) and deploy it easily to your AWS account!

### Demo

#### PNG Diagram

![Rendered PNG diagram should be here](https://393sqs3x53.execute-api.eu-west-1.amazonaws.com/plantuml/png/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0)

#### SVG Diagram 

![Rendered SVG diagram should be here](https://393sqs3x53.execute-api.eu-west-1.amazonaws.com/plantuml/svg/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0)

#### TXT Diagram

https://393sqs3x53.execute-api.eu-west-1.amazonaws.com/plantuml/txt/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0

### Build

- `mvn clean package`

### Deploy

You can deploy with Serverless framework or AWS SAM. Also note that if you just want to run a released version, it's available from AWS Serverless Application Repository (see link above).
#### Serverless framework:
- `sls deploy`

(Will have to remove or edit custom domains for this to work)

#### AWS SAM

- Edit `sam-deploy.sh` to replace the deployment bucket with your own.
- Run `sam-deploy.sh`

### Issues
- Serverless does not support specifying binary response MIME types for API Gateway. So this has to be done manually after serverless deployment. This does not affect the SAM deployment.
    - Set `*/*` as binary type.
    - UPDATE/TODO: Use the binary content plugin for serverless to avoid manual step: https://www.npmjs.com/package/serverless-apigw-binary
