#! /bin/bash

rm -rf review-client/gen-java
rm -rf review-server/gen-java

DIR=`pwd`
FILE="${DIR}/data.thrift"

(cd review-client && thrift -gen java ${FILE})
(cd review-server&& thrift -gen java ${FILE})

(cd python-scripts && python gen_hooks.py --file ../data.thrift --mode gen-java > ../review-client/src/main/java/com/omartech/review/client/DataClients.java)



if [[ $# -ne 0 ]]; then
    (cd thrift-router && ./gen.sh)
fi
