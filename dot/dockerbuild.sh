#!/bin/bash -xe

# Build a graphviz dot executable that works with AWS Lambda.
#
# The build is done in an Amazon Linux container which matches
# to the Lambda runtime environment.

mkdir build
curl http://pkgs.fedoraproject.org/repo/pkgs/graphviz/graphviz-2.38.0.tar.gz/5b6a829b2ac94efcd5fa3c223ed6d3ae/graphviz-2.38.0.tar.gz -o build/graphviz-2.38.0.tar.gz
tar zxvf graphviz-2.38.0.tar.gz -C build
docker run -v $(pwd)/build/graphviz-2.38.0:/dot amazonlinux:2017.03 \
bash -c "yum groupinstall -y 'Development Tools' ;\
yum install -y expat-devel ;\
cd /dot ;\
./configure --enable-static=yes --with-expat=yes ;\
make"
cp build/graphviz-2.38.0/cmd/dot/dot_static ../src/main/resources
