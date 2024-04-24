SERVICE_NAME=hello
BUILDER_TAG=$(SERVICE_NAME)-build
IMAGE_TAG=$(SERVICE_NAME)-img
IMAGE_VERSION=latest
default: build

clean:
	rm -rf build

build: clean
	# A separate image for build allows the process to avoid dependencies with the build machine.
	sudo docker build -t $(BUILDER_TAG) -f Dockerfile.build .
	# Runs the image generated in the above step to create the actual deployable artifact (i.e. jar file).
	# docker run -t -v `pwd`:/build $(BUILDER_TAG)
	# In the above build step, maven starts of by downloading all dependencies and
	# then proceeds to build the artifact. This can be time consuming. To reuse mvn cache
	# please use the following command instead. This will ensure, a docker volume is
	# responsible for incrementally saving the dependencies and avoids the time taken to download
	# all dependencies every time.
	sudo docker run -v "m2:/root/.m2" -v "`pwd`:/build" -v "/usr/local/bin/docker:/usr/bin/docker" -v "/var/run/docker.sock:/var/run/docker.sock" $(BUILDER_TAG)
	# Builds the docker image for running the service.
	sudo docker build -t $(IMAGE_TAG):$(IMAGE_VERSION) .
	echo "Success"

post-deploy-build:
	echo "Nothing is defined in post-deploy-build step"
