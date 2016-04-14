FROM ubuntu:latest
MAINTAINER Camilo Gomez <camilo@niclabs.cl>

ENV N 3 MSG_SIZE 20 PAD_LENGTH 10

RUN apt-get update && apt-get install -y software-properties-common \
										 git && \
	add-apt-repository -y ppa:webupd8team/java && \
	echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
	apt-get update && apt-get install -y oracle-java8-installer && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk8-installer && \
	git clone https://github.com/niclabs/directory_dcnet.git
RUN	cd directory_dcnet/ && \
	git pull && \
	./gradlew build

CMD cd directory_dcnet/ && git pull > /dev/null 2>&1 && ./gradlew -q run -PappArgs=[$N,$MSG_SIZE,$PAD_LENGTH]