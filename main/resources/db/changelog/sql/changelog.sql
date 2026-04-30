DROP TABLE IF EXISTS wallets;
DROP INDEX IF EXISTS idx_wallets_balance;
DROP INDEX IF EXISTS idx_wallets_created_at;

CREATE TABLE wallets
(
    id UUID PRIMARY KEY,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
        CONSTRAINT positive_balance CHECK (balance >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 1 NOT NULL
);

CREATE INDEX idx_wallets_balance ON wallets(balance);
CREATE INDEX idx_wallets_created_at ON wallets(created_at);