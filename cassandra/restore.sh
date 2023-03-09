#!/bin/bash
backup_prefix=$1
host_ip="127.0.0.1"
user="cassandra"
password="cassandra"
keyspaces=$(ls)
for ks in $keyspaces
do
if [ $ks != 'temp.sh' ]
then
cd $ks
tables=( `ls` )
for table in "${tables[@]}"
do
    echo "Processing $table"
    if [[ -d "${table}/snapshots/10.114.35.68-keyspace-13:04:2020T07:40:59/"  ]]
    then
        #mv $table/snapshots/10.114.35.68-keyspace-13:04:2020T07:40:59/* $table/
        #rm -rf $table/snapshots/
        cp $table/* /tmp/cassandra/data/table/
    fi
done
cd ..
fi
done

tables=( `ls` )
for table in "${tables[@]}"
do
    sstableloader -u cassandra -pw cassandra -d 127.0.0.1 $table/
done
