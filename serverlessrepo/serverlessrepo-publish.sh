aws --region us-east-1 serverlessrepo create-application-version --application-id arn:aws:serverlessrepo:us-east-1:293246570391:applications/plantuml-render --semantic-version 0.0.8 --source-code-url https://github.com/mmajis/plantuml-serverless/releases/tag/v0.0.9 --template-body file://.aws-sam/build/template.yaml
#maybe this could be the future:
#sam publish --region us-east-1 --template .aws-sam/build/template.yaml --semantic-version 0.0.9

# The issue here is that serverless application repository doesn't appear to support lambdas with PackageType: Image (containers)
