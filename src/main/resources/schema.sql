-- Create Role Table
CREATE TABLE IF NOT EXISTS role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_description VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Access Table
CREATE TABLE IF NOT EXISTS access (
    access_id INT AUTO_INCREMENT PRIMARY KEY,
    access_name VARCHAR(100) NOT NULL UNIQUE,
    access_description VARCHAR(255),
    module_name VARCHAR(50),
    action_type VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create User Table
CREATE TABLE IF NOT EXISTS user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    is_active TINYINT(1) DEFAULT 1,
    role_id INT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Role_Access Junction Table (Many-to-Many relationship)
CREATE TABLE IF NOT EXISTS role_access (
    role_access_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    access_id INT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_access_role FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_role_access_access FOREIGN KEY (access_id) REFERENCES access(access_id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_role_access (role_id, access_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better query performance
CREATE INDEX idx_user_role_id ON user(role_id);
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_email ON user(email);
CREATE INDEX idx_role_access_role_id ON role_access(role_id);
CREATE INDEX idx_role_access_access_id ON role_access(access_id);

-- Insert Default Roles
INSERT INTO role (role_name, role_description) VALUES 
('Project Manager', 'Divisi Project Manager - Mengelola project order'),
('Consultant', 'Divisi Konsultan - Membuat dan update sub task'),
('Development', 'Divisi Development - Mengerjakan dan update sub task');

-- Insert Default Access Permissions
INSERT INTO access (access_name, access_description, module_name, action_type) VALUES 
('CREATE_PROJECT_ORDER', 'Create project order', 'PROJECT_ORDER', 'CREATE'),
('CLOSE_PROJECT_ORDER', 'Close project order', 'PROJECT_ORDER', 'CLOSE'),
('DELETE_PROJECT_ORDER', 'Delete project order', 'PROJECT_ORDER', 'DELETE'),
('READ_PROJECT_ORDER', 'Read project order', 'PROJECT_ORDER', 'READ'),
('CREATE_SUBTASK', 'Create sub task project', 'SUBTASK', 'CREATE'),
('UPDATE_SUBTASK', 'Update sub task project', 'SUBTASK', 'UPDATE'),
('DELETE_SUBTASK', 'Delete sub task project', 'SUBTASK', 'DELETE'),
('VIEW_PROJECT_ORDER', 'View project order', 'PROJECT_ORDER', 'VIEW'),
('VIEW_SUBTASK', 'View sub task project', 'SUBTASK', 'VIEW');

-- Assign Access to Roles
-- Project Manager: Create, Close, Delete project order
INSERT INTO role_access (role_id, access_id) 
SELECT r.role_id, a.access_id FROM role r, access a 
WHERE r.role_name = 'Project Manager' AND a.access_name IN ('CREATE_PROJECT_ORDER', 'CLOSE_PROJECT_ORDER', 'DELETE_PROJECT_ORDER');

-- Consultant: Read project order, Create/Update/Delete sub task
INSERT INTO role_access (role_id, access_id) 
SELECT r.role_id, a.access_id FROM role r, access a 
WHERE r.role_name = 'Consultant' AND a.access_name IN ('READ_PROJECT_ORDER', 'CREATE_SUBTASK', 'UPDATE_SUBTASK', 'DELETE_SUBTASK');

-- Development: View project order, View sub task, Update sub task
INSERT INTO role_access (role_id, access_id) 
SELECT r.role_id, a.access_id FROM role r, access a 
WHERE r.role_name = 'Development' AND a.access_name IN ('VIEW_PROJECT_ORDER', 'VIEW_SUBTASK', 'UPDATE_SUBTASK');
