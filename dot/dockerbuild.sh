#!/bin/bash -xe

# Build a graphviz dot executable that works with AWS Lambda.
#
# The build is done in an Amazon Linux container which matches
# to the Lambda runtime environment.

mkdir build
curl https://www2.graphviz.org/Packages/stable/portable_source/graphviz-2.42.3.tar.gz -o build/graphviz-2.42.3.tar.gz
tar zxvf build/graphviz-2.42.3.tar.gz -C build
docker run -v $(pwd)/build/graphviz-2.42.3:/dot amazonlinux:2.0.20191217.0 \
bash -c "yum groupinstall -y 'Development Tools' ;\
yum install -y expat-devel ;\
cd /dot ;\
./configure --enable-static=yes --with-expat=yes ;\
make"
cp build/graphviz-2.42.3/cmd/dot/dot_static ../src/main/resources
