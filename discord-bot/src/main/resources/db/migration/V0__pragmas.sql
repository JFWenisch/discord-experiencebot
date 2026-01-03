-- Flyway migration: SQLite pragmas that must be run outside of a transaction
-- Keep non-transactional statements separate from transactional migrations
PRAGMA foreign_keys = ON;
