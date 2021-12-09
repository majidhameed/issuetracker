CREATE TABLE developer (
    id int IDENTITY PRIMARY KEY,
    name varchar(255)
);

CREATE TABLE issue (
    issue_type varchar(31) NOT NULL,
    id int IDENTITY PRIMARY KEY,
    created_on date DEFAULT CURRENT_DATE,
    description varchar(255),
    title varchar(255),
    estimated_point_value int,
    status varchar(255) NOT NULL,
    priority int,
    developer_id int
);