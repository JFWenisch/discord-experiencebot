-- Flyway migration: initial schema for SQLite
-- Enable foreign key enforcement for SQLite

CREATE TABLE IF NOT EXISTS sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    guild TEXT NOT NULL,
    member TEXT NOT NULL,
    starttime TEXT NOT NULL,
    endtime TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS session_exp (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    session INTEGER NOT NULL,
    member TEXT NOT NULL,
    exp INTEGER NOT NULL,
    FOREIGN KEY (session) REFERENCES sessions(id) ON DELETE CASCADE
);

-- optional indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_sessions_guild ON sessions(guild);
CREATE INDEX IF NOT EXISTS idx_session_exp_member ON session_exp(member);
CREATE INDEX IF NOT EXISTS idx_session_exp_session ON session_exp(session);