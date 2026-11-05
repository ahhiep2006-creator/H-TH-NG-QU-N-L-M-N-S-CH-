CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS readers (
    id VARCHAR(20) NOT NULL,
    reader_type VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_readers_users FOREIGN KEY (id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS student_readers (
    id VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_student_readers_readers FOREIGN KEY (id) REFERENCES readers (id)
);

CREATE TABLE IF NOT EXISTS priority_student_readers (
    id VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_priority_student_readers_readers FOREIGN KEY (id) REFERENCES readers (id)
);

CREATE TABLE IF NOT EXISTS lecturer_readers (
    id VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_lecturer_readers_readers FOREIGN KEY (id) REFERENCES readers (id)
);

CREATE TABLE IF NOT EXISTS books (
    id VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(100),
    quantity INTEGER NOT NULL,
    `value` DECIMAL(15, 2) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS borrow_tickets (
    id VARCHAR(20) NOT NULL,
    reader_id VARCHAR(20) NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_tickets_readers FOREIGN KEY (reader_id) REFERENCES readers (id)
);

CREATE TABLE IF NOT EXISTS borrow_ticket_details (
    ticket_id VARCHAR(20) NOT NULL,
    book_id VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (ticket_id, book_id),
    CONSTRAINT fk_details_tickets FOREIGN KEY (ticket_id) REFERENCES borrow_tickets (id),
    CONSTRAINT fk_details_books FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE TABLE IF NOT EXISTS return_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ticket_id VARCHAR(20) NOT NULL,
    return_date DATE NOT NULL,
    fine_amount DECIMAL(15, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_return_records_ticket UNIQUE (ticket_id),
    CONSTRAINT fk_returns_tickets FOREIGN KEY (ticket_id) REFERENCES borrow_tickets (id)
);
