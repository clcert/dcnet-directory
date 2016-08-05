#!/bin/bash

function usage() {
	echo "Usage: $0 [-s <room_size>] [-l <message_max_length>] [-p <padding_length>] [-m <non_probabilistic_mode>]";
	exit 2
}

while getopts ":s:l:p:m:" o;
do
	case "${o}" in
		s)
			room_size=${OPTARG}
			;;
		l)
			message_max_length=${OPTARG}
			;;
		p)
			padding_length=${OPTARG}
			;;
		m)
			non_probabilistic_mode=${OPTARG}
			;;
		*)
			usage
			;;
	esac
done

if [ -z "${room_size}" ] || [ -z "${message_max_length}" ] || [ -z "${padding_length}" ] || [ -z "${non_probabilistic_mode}" ]
then
	usage
fi 

! ls "$(pwd)/build/libs/directory_dcnet-all-1.0-SNAPSHOT.jar" > /dev/null 2<&1 && ./gradlew fatJar > /dev/null 2<&1
java -jar "$(pwd)/build/libs/directory_dcnet-all-1.0-SNAPSHOT.jar" ${room_size} ${message_max_length} ${padding_length} ${non_probabilistic_mode}