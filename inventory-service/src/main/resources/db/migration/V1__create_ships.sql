CREATE TABLE IF NOT EXISTS ships (
    id              VARCHAR(50)  PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    total_slots     INT          NOT NULL,
    available_slots INT          NOT NULL
);

INSERT INTO ships (id, name, total_slots, available_slots)
VALUES
    ('SH001', 'Vessel 001', 500, 500),
    ('SH002', 'Vessel 002', 300, 300),
    ('SH003', 'Vessel 003', 200, 200)
ON CONFLICT (id) DO NOTHING;