# directory_dcnet

Java program that works as a directory to nodes running a DC-NET room with [collision_resolution_protocol](https://github.com/niclabs/collision_resolution_protocol).
 
This program waits for the nodes to connect to it and then informs to the rest of every ip address of the nodes.

## Instructions

This program must be run within the same LAN that the nodes that want to start a DC-NET room session.

The IP of the machine that is running this directory must be known to all the nodes that want to connect to the room.
    
### Run nodes separately

* In order to start a session, the directory node must run the following command:

    ```./gradlew run -PappArgs=[<totalNumberOfNodes>]```

