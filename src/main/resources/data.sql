INSERT INTO users (name, email, role, department)
VALUES ('Dr. Veli Danışman', 'veli@test.com', 'DOCTOR', 'Kardiyoloji')
    ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, role, department)
VALUES ('Dr. Ayşe Yılmaz', 'ayse@test.com', 'DOCTOR', 'Pediatri')
    ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, role, department)
VALUES ('Dr. Mehmet Demir', 'mehmet@test.com', 'DOCTOR', 'Dermatoloji')
    ON CONFLICT (email) DO NOTHING;