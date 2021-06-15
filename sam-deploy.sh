#!/bin/bash
# Note! This needs an ECR repo created like this:
# aws ecr create-repository --repository-name plantuml-sam \
# --image-tag-mutability IMMUTABLE --image-scanning-configuration scanOnPush=true

set -eo pipefail
readonly basedir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
usage() {
cat >&2 <<EOUSAGE
$(basename "${BASH_SOURCE[0]}") -b <s3bucket> [ -s <stackName> ] [ -p <awsProfile> ] [ -p <awsRegion> ]
Options
  -b | --bucket       (Required) The S3 bucket to upload artifacts to.
  -s | --stack-name   The name of the CloudFormation stack to create.
                      (default: plantuml-sam)
  -p | --profile      Specify an AWS profile
  -r | --region       Specify an AWS region
  -a | --account      Specify container image repository account id
  -i | --image-repo)  Specify container image repository name
                      (default: plantuml-sam)
  -h | --help         Print this usage message and exit
EOUSAGE
}
awsProfile=()
rawRegion=""
awsRegion=()
s3bucket=""
stackName="plantuml-sam"
repoName="plantuml-sam"
accountId=""
while [[ $# -gt 0 ]]; do
  opt="$1"
  shift
  case "$opt" in
    -h|--help)        usage; exit;;
    -p|--profile)     awsProfile=(--profile "$1"); shift;;
    -r|--region)      rawRegion="$1"; awsRegion=(--region "$1"); shift;;
    -b|--bucket)      s3bucket="$1"; shift;;
    -s|--stack-name)  stackName="$1"; shift;;
    -a|--account)     accountId="$1"; shift;;
    -i|--image-repo)  repoName="$1"; shift;;
    *)                echo "Unknown option $opt"; usage; exit 1;;
  esac
done

if [[ "$rawRegion" == "" ]]; then
  rawRegion="$(aws ${awsProfile[*]} ec2 describe-availability-zones --output text --query 'AvailabilityZones[0].[RegionName]')"
  awsRegion=(--region "$rawRegion")
fi

if [[ "$accountId" == "" ]]; then
  accountId="$(aws ${awsProfile[*]} sts get-caller-identity --query Account --output text)"
fi

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
--password-stdin "${accountId}.dkr.ecr.${rawRegion}.amazonaws.com"

# Replace image-repository with your own
sam deploy "${awsProfile[@]}" "${awsRegion[@]}" \
--stack-name "${stackName}" \
--capabilities CAPABILITY_IAM \
--image-repository "${accountId}.dkr.ecr.${rawRegion}.amazonaws.com/${repoName}" \
--s3-bucket "${s3bucket}"

#These would be for serverless application repository, but it doesn't support container based lambdas at the moment.
#aws ${awsProfile[@]} s3 cp LICENSE s3://"${s3bucket}"/LICENSE
#aws ${awsProfile[@]} s3 cp serverlessrepo/README.md s3://"${s3bucket}"/README.md
