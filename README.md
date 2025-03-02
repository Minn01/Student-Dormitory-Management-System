DDL Language for this project SQL code

CREATE TABLE ROOM_TYPE (
    type_id NUMBER PRIMARY KEY,
    type_name VARCHAR2(50) NOT NULL,
    monthly_rate NUMBER NOT NULL
);

CREATE TABLE ROOMS (
    room_id NUMBER PRIMARY KEY,
    room_no VARCHAR2(10) UNIQUE NOT NULL,
    status VARCHAR2(20) DEFAULT 'Available' CHECK (status IN ('Available', 'Occupied', 'Unavailable')),
    type_id NUMBER NOT NULL,
    CONSTRAINT fk_room_type FOREIGN KEY (type_id) REFERENCES Room_Type(type_id)
);

CREATE TABLE STUDENT (
    student_id NUMBER PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) UNIQUE,
    phone VARCHAR2(20),
    emergency_contact VARCHAR2(100) NOT NULL
);

-- Room Allocation Table (Tracks which student is in which room)
CREATE TABLE Room_Allocation (
    allocation_id NUMBER PRIMARY KEY,
    student_id NUMBER REFERENCES Student(student_id) ON DELETE CASCADE,
    room_id NUMBER REFERENCES Rooms(room_id),
    check_in_date DATE DEFAULT SYSDATE,
    check_out_date DATE NULL
);

CREATE TABLE Payment (
    payment_id NUMBER PRIMARY KEY,
    amount NUMBER NOT NULL,
    payment_date DATE DEFAULT SYSDATE,
    receipt_number VARCHAR2(50) UNIQUE,
    student_id NUMBER NOT NULL,
    payment_type VARCHAR2(100),
    CONSTRAINT fk_payment_student FOREIGN KEY (student_id) REFERENCES Student(student_id),
);
