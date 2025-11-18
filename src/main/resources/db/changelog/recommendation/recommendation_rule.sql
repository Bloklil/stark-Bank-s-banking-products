-- liquibase formatted sql

-- changeset AntonS:1

CREATE TABLE IF NOT EXISTS recommendation_rule (
    id UUID PRIMARY KEY,
    product_name TEXT NOT NULL,
    product_id UUID NOT NULL,
    product_text TEXT NOT NULL,
    rule_json JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);