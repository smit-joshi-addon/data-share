-- Drop the existing primary key constraint
ALTER TABLE analytics DROP CONSTRAINT analytics_pkey;


-- Add a composite primary key (uuid and called_at)
ALTER TABLE analytics 
    ADD CONSTRAINT analytics_pkey PRIMARY KEY (uuid, called_at);

    
    -- Ensure there is a unique index that includes the time column (called_at)
CREATE UNIQUE INDEX idx_analytics_unique ON analytics (uuid, called_at);

SELECT create_hypertable('analytics', 'called_at',chunk_time_interval => interval '6 hours');

SELECT add_retention_policy('analytics', INTERVAL '24 hours');