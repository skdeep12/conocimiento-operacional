CREATE KEYSPACE <Keyspave> WITH replication = {'class': 'NetworkTopologyStrategy', 'dc1': '2', 'dc2': '1'}  AND durable_writes = true;

CREATE ROLE <role> WITH PASSWORD = '<password>' AND LOGIN = true AND SUPERUSER = false;
GRANT ALL PERMISSIONS ON KEYSPACE <keyspace> TO <role>;
GRANT SELECT PERMISSION ON KEYSPACE <keyspace> TO <role>;
GRANT MODIFY PERMISSION ON KEYSPACE <keyspace> TO <role>;


