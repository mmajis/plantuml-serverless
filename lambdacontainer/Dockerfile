FROM public.ecr.aws/lambda/java:11 AS dotbuild

RUN mkdir build
RUN yum install -y curl tar gzip
RUN curl https://www2.graphviz.org/Packages/stable/portable_source/graphviz-2.42.3.tar.gz -o build/graphviz-2.42.3.tar.gz
RUN tar zxvf build/graphviz-2.42.3.tar.gz -C build
RUN yum groupinstall -y 'Development Tools'
RUN yum install -y expat-devel
RUN cd build/graphviz-2.42.3 && pwd && ./configure --enable-static=yes --with-expat=yes && make

FROM public.ecr.aws/lambda/java:11

RUN yum install -y fontconfig && yum clean all

# Copy function code and runtime dependencies from Maven layout
COPY --from=dotbuild /var/task/build/graphviz-2.42.3/cmd/dot/dot_static /opt/dot_static
COPY target/classes ${LAMBDA_TASK_ROOT}
COPY target/dependency/* ${LAMBDA_TASK_ROOT}/lib/

# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
#CMD [ "com.example.LambdaHandler::handleRequest" ]
