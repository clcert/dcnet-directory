# directory_dcnet [![Build Status](https://travis-ci.org/niclabs/directory_dcnet.svg?branch=master)](https://travis-ci.org/niclabs/directory_dcnet)

Java program that works as a directory to nodes running a DC-NET room with [collision_resolution_protocol](https://github.com/niclabs/collision_resolution_protocol).
 
This program waits for the nodes to connect to it and then informs to the rest of every ip address of the nodes.

## System Requirements

* [Java 8](http://www.oracle.com/technetwork/java/index.html)

## Instructions

This program must be run within the same LAN that the nodes that want to start a DC-NET room session.

The IP of the machine that is running this directory must be known to all the nodes that want to connect to the room.
    
### Run Directory node

* In order to start a session, the directory node must run the following command:

    ```./gradlew run -PappArgs=[<totalNumberOfNodes>, <maximumMessageLength>, <messagePaddingLength>, <nonProbabilisticMode>]```

### Using Docker

* Also you can use [docker](https://www.docker.com/) in order to run a directory node, using the following commands: (first build and create the image, and then running this image)

    ```docker build -t directoryNode .```
    
    ```docker run --env N=<totalNumberOfNodes> --env MSG_SIZE=<maximumMessageLength> --env PAD_LENGTH=<messagePaddingLength> --env NON_PROB=<nonProbabilisticMode> directoryNode```