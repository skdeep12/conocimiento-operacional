bin/brooklin-rest-client.sh -o CREATE -u http://localhost:32311/ -n first-mirroring-stream -s "kafka://localhost:9092/^(first|second)-topic$" -c kafkaMirroringConnector -t kafkaTransportProvider -m '{"owner":"test-user","system.reuseExistingDestination":"false"}' 2>/dev/null


export KAFKA_OPTS="-Djava.security.auth.login.config=/Users/na-kazcid/Downloads/brooklin-1.0.2/config/src-jaas.config"

export KAFKA_OPTS="-Djava.security.auth.login.config=/Users/na-kazcid/Downloads/brooklin-1.0.2/config/dest-jaas.config"


curl "https://www.apache.org/dist/kafka/1.0.0/kafka_1.0-1.0.0.tgz" -o ~/Downloads/kafka.tgz

./kafka-topics.sh --zookeeper localhost:2181 --create --topic <topic> --replication-factor 1 --partitions 1 

~/kafka/bin/kafka-console-producer.sh  --topic first-topic --broker-list localhost:9092


./kafka-topics.sh --zookeeper 192.168.1.102:2181 --create --topic <topic> --replication-factor 1 --partitions 1 
./kafka-topics.sh --zookeeper 192.168.1.102:2181 --create --topic <topic> --replication-factor 1 --partitions 1 
./kafka-topics.sh --zookeeper 192.168.1.102:2181 --list


./kafka-topics.sh --zookeeper 192.168.1.102:2181 --describe --topic <topic>
./kafka-console-producer.sh --broker-list <broke_ip>:<port> --topic <topic> --producer.config ../config/producer.properties

docker run -d --rm -p 9000:9000 \
    -e KAFKA_PROPERTIES=$(cat ~/repos/learning/kafdrop/chart/kafka.properties | base64) \
    -e KAFKA_BROKERCONNECT=192.168.1.102:9092 \
    -e JVM_OPTS="-Xms32M -Xmx64M" \
    -e SERVER_SERVLET_CONTEXTPATH="/kafdrop" \
    obsidiandynamics/kafdrop:3.16.0


bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --property print.key=true --property key.separator="-" --topic <topic> --from-beginning


./kafka-console-consumer.sh --bootstrap-server 192.168.1.103:9092 --topic number.service --consumer.config ../config/consumer.properties --from-beginning


./kafka-consumer-groups.sh --bootstrap-server 10.120.2.60:9092 --command-config ~/config.properties --describe --group <group>



sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="<user>" password="<password>";


./kafka-acls.sh --bootstrap-server 10.120.2.60:9093 --command-config ~/config.properties --list --topic <topic>


./kafka-acls.sh --authorizer kafka.security.auth.SimpleAclAuthorizer --authorizer-properties zookeeper.connect=10.120.2.70:2181 --list --topic <topic>