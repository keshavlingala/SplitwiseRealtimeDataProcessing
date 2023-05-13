CREATE KEYSPACE IF NOT EXISTS splitwise_analysis
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
use splitwise_analysis;
create table IF NOT EXISTS  ping_table
(
    id uuid primary key,
    message text
)
    with caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
     and compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
     and compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
     and speculative_retry = '99p';

create table IF NOT EXISTS  simplified_expenses
(
    expense_id text primary key,
    category text,
    cost double,
    created_at timestamp,
    created_by bigint,
    date text,
    description text,
    group_id bigint,
    payload text,
    payment boolean,
    user_id bigint,
    user_name text
)
    with caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
     and compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
     and compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
     and speculative_retry = '99p';

create table IF NOT EXISTS  user_api_keys
(
    user_id bigint primary key,
    api_key text,
    created_at date,
    name text,
    updated_at date
)
    with caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
     and compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
     and compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
     and speculative_retry = '99p';

create index IF NOT EXISTS  user_api_keys_created_at_index
    on user_api_keys (created_at);
