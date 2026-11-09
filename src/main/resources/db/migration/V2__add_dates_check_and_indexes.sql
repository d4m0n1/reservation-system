ALTER TABLE reservations
    ADD CONSTRAINT reservations_dates_check CHECK (end_date > start_date);

CREATE INDEX idx_reservations_room_id ON reservations (room_id);
CREATE INDEX idx_reservations_user_id ON reservations (user_id);
