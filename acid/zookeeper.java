zookeeper：
1.普通创建的节点没有ephemeralOwner编号，而临时节点有该编号

kafka:
1.创建主题：
./kafka-topics.sh --zookeeper localhost:2181 --create --topic qinmeng --partitions 2 --replication-factor 1
                  使用的zookeeper服务器地址   创建     主题            分区数量       副本数量
./kafka-topics.sh --zookeeper localhost:2181 --list
./kafka-topics.sh --zookeeper localhost:2181 --describe --topic qinmeng		
		  Topic:qinmeng   PartitionCount:2(分区数量)        ReplicationFactor:1（每个分区的副本数量）     Configs:
        Topic: qinmeng  Partition: 0    Leader: 0       Replicas: 0     Isr: 0  info:主题qinmeng的第一个分区的
        Topic: qinmeng  Partition: 1    Leader: 0       Replicas: 0     Isr: 0
./kafka-console-producer.sh --broker-list localhost:9092 --topic qinmeng
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic qinmeng
通过--help查看发现--broker-list和--bootstrap-server的区别是：生产者