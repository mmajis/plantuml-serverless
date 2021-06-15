#!/bin/bash
# Note! This needs an ECR repo created like this:
# aws ecr create-repository --repository-name plantuml-sam \
# --image-tag-mutability IMMUTABLE --image-scanning-configuration scanOnPush=true
#
# After repo creation, edit the script to replace the repo URL in the commands below

set -eo pipefail
readonly basedir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
usage() {
cat >&2 <<EOUSAGE
$(basename "${BASH_SOURCE[0]}") -b <s3bucket> [ -s <stackName> ] [ -p <awsProfile> ] [ -p <awsRegion> ]
Options
  -b | --bucket       (Required) The S3 bucket to upload artifacts to.
  -s | --stack-name   The name of the CloudFormation stack to create.
                      (default: sam-plantuml)
  -p | --profile      Specify an AWS profile
  -r | --region       Specify an AWS region
  -h | --help         Print this usage message and exit
EOUSAGE
}
awsProfile=()
awsRegion=()
s3bucket=""
stackName="sam-plantuml"
while [[ $# -gt 0 ]]; do
  opt="$1"
  shift
  case "$opt" in
    -h|--help)        usage; exit;;
    -p|--profile)     awsProfile=(--profile "$1"); shift;;
    -r|--region)      awsRegion=(--region "$1"); shift;;
    -b|--bucket)      s3bucket="$1"; shift;;
    -s|--stack-name)  stackName="$1"; shift;;
    *)                echo "Unknown option $opt"; usage; exit 1;;
  esac
done

if [[ "$s3bucket" == "" ]]; then
  echo "please provide an S3 bucket name with -b" >&2
  exit 1
fi

if [[ "$stackName" == "" ]]; then
  echo "please provide a name for the CFN stack with -s" >&2
  exit 1
fi

pushd "${basedir}" 2>/dev/null
codeUri="$(ls target/plantuml-serverless-*.jar | head -n 1)"
if [[ ! -f "$codeUri" ]]; then
  echo "missing plantuml-serverless-*.jar. please run 'mvn clean package'." >&2
  exit 1
fi

sam build --cached

# Edit the AWS account id and region in the repo URL here
aws ecr get-login-password | docker login --username AWS \
--password-stdin 293246570391.dkr.ecr.eu-west-1.amazonaws.com

# Replace image-repository with your own
sam deploy "${awsProfile[@]}" "${awsRegion[@]}" \
--stack-name "${stackName}" \
--capabilities CAPABILITY_IAM \
--image-repository 293246570391.dkr.ecr.eu-west-1.amazonaws.com/plantuml-sam \
--s3-bucket "${s3bucket}"

#These would be for serverless application repository, but it doesn't support container based lambdas at the moment.
#aws s3 cp LICENSE s3://"${s3bucket}"/LICENSE
#aws s3 cp serverlessrepo/README.md s3://"${s3bucket}"/README.md
