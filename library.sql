-- 创建数据库
CREATE DATABASE IF NOT EXISTS library 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE library;

-- 创建管理员用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(50),
    phone VARCHAR(11),
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建图书表
CREATE TABLE IF NOT EXISTS books (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(50) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    publisher VARCHAR(50),
    publish_date DATE,
    price DOUBLE,
    category VARCHAR(30),
    stock INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建读者表
CREATE TABLE IF NOT EXISTS readers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    id_card VARCHAR(18) UNIQUE,
    phone VARCHAR(11) UNIQUE,
    email VARCHAR(50),
    address VARCHAR(100),
    reader_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建借阅记录表
CREATE TABLE IF NOT EXISTS borrow_records (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    reader_id BIGINT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (reader_id) REFERENCES readers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入初始管理员用户 (密码: admin123)
INSERT INTO users (username, password, email, phone, role, status) VALUES
('admin', 'admin123', 'admin@example.com', '13800138000', 'ADMIN', 'ACTIVE'),
('冯榆婷', '041101', 'aelm102101@139.com', '18712345679', 'ADMIN', 'ACTIVE'),
('邓双林', '748264', 'aelm102100@139.com', '18712345678', 'ADMIN', 'ACTIVE');

-- 插入图书初始数据
INSERT INTO books (title, author, isbn, publisher, publish_date, price, category, stock, status) VALUES
('Java核心技术', 'Cay S. Horstmann', '9787111647218', '机械工业出版社', '2019-10-01', 139.00, '计算机', 10, 'AVAILABLE'),
('Python编程：从入门到实践', 'Eric Matthes', '9787115428028', '人民邮电出版社', '2016-07-01', 89.00, '计算机', 8, 'AVAILABLE'),
('深入理解计算机系统', 'Randal E. Bryant', '9787111544937', '机械工业出版社', '2016-01-01', 139.00, '计算机', 5, 'AVAILABLE'),
('活着', '余华', '9787506365437', '作家出版社', '2012-08-01', 20.00, '文学', 15, 'AVAILABLE'),
('百年孤独', '加西亚·马尔克斯', '9787544270878', '南海出版公司', '2011-06-01', 39.50, '文学', 12, 'AVAILABLE'),
('三体', '刘慈欣', '9787536692930', '重庆出版社', '2008-01-01', 32.00, '科幻', 20, 'AVAILABLE'),
('数学之美', '吴军', '9787115429124', '人民邮电出版社', '2014-11-01', 49.00, '科普', 7, 'AVAILABLE'),
('时间简史', '史蒂芬·霍金', '9787535732309', '湖南科学技术出版社', '2002-02-01', 32.00, '科普', 9, 'AVAILABLE');

-- 插入读者初始数据
INSERT INTO readers (name, id_card, phone, email, address, reader_type, status) VALUES
('张三', '110101199001011234', '13800138001', 'zhangsan@example.com', '北京市朝阳区', 'STUDENT', 'ACTIVE'),
('李四', '110101199001011235', '13800138002', 'lisi@example.com', '北京市海淀区', 'TEACHER', 'ACTIVE'),
('王五', '110101199001011236', '13800138003', 'wangwu@example.com', '北京市西城区', 'STUDENT', 'ACTIVE'),
('赵六', '110101199001011237', '13800138004', 'zhaoliu@example.com', '北京市东城区', 'STAFF', 'ACTIVE'),
('孙七', '110101199001011238', '13800138005', 'sunqi@example.com', '北京市丰台区', 'STUDENT', 'ACTIVE');

-- 插入借阅记录初始数据
INSERT INTO borrow_records (book_id, reader_id, borrow_date, due_date, return_date, status) VALUES
(1, 1, '2025-12-01', '2025-12-15', NULL, 'BORROWING'),
(2, 2, '2025-12-05', '2025-12-19', NULL, 'BORROWING'),
(3, 3, '2025-11-20', '2025-12-04', '2025-12-03', 'RETURNED'),
(4, 4, '2025-11-15', '2025-11-29', '2025-11-28', 'RETURNED'),
(5, 5, '2025-12-10', '2025-12-24', NULL, 'BORROWING');


