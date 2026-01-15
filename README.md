# Simian Work Order Management - User Management Module
Palito
Dokumentasi lengkap untuk User Management Module dengan Role-Based Access Control (RBAC).
## Database Schema

### File: `src/main/resources/schema.sql`

#### Tabel: `role`
Menyimpan informasi divisi/role dalam sistem

```sql
CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_description VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Divisi yang tersedia:**
- Project Manager
- Consultant
- Development

#### Tabel: `access`
Menyimpan permission/hak akses menu

```sql
CREATE TABLE access (
    access_id INT AUTO_INCREMENT PRIMARY KEY,
    access_name VARCHAR(100) NOT NULL UNIQUE,
    access_description VARCHAR(255),
    module_name VARCHAR(50),
    action_type VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Permission yang tersedia:**
- CREATE_PROJECT_ORDER
- CLOSE_PROJECT_ORDER
- DELETE_PROJECT_ORDER
- READ_PROJECT_ORDER
- CREATE_SUBTASK
- UPDATE_SUBTASK
- DELETE_SUBTASK
- VIEW_PROJECT_ORDER
- VIEW_SUBTASK

#### Tabel: `user`
Menyimpan informasi user

```sql
CREATE TABLE user (
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
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(role_id)
);
```

#### Tabel: `role_access` (Junction Table)
Menyimpan relasi many-to-many antara role dan access

```sql
CREATE TABLE role_access (
    role_access_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    access_id INT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_access_role FOREIGN KEY (role_id) REFERENCES role(role_id),
    CONSTRAINT fk_role_access_access FOREIGN KEY (access_id) REFERENCES access(access_id),
    UNIQUE KEY uk_role_access (role_id, access_id)
);
```

---

## Hibernate Entities

### 1. Role Entity
**File:** `src/main/java/simian/testing/entity/Role.java`

```java
@Entity
@Table(name = "role")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;
    
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
    
    @Column(name = "role_description")
    private String roleDescription;
    
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<User> users;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_access", ...)
    private Set<Access> accesses;
}
```

**Relationships:**
- **One-to-Many dengan User:** Satu role memiliki banyak user
- **Many-to-Many dengan Access:** Satu role memiliki banyak access permissions

### 2. User Entity
**File:** `src/main/java/simian/testing/entity/User.java`

```java
@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "is_active")
    private Integer isActive;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
```

**Relationships:**
- **Many-to-One dengan Role:** Banyak user memiliki satu role

**Helper Methods:**
- `getFullName()` - Menggabungkan firstName dan lastName
- `isActive()` - Mengecek status aktif user

### 3. Access Entity
**File:** `src/main/java/simian/testing/entity/Access.java`

```java
@Entity
@Table(name = "access")
public class Access implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accessId;
    
    @Column(name = "access_name", nullable = false, unique = true)
    private String accessName;
    
    @Column(name = "access_description")
    private String accessDescription;
    
    @Column(name = "module_name")
    private String moduleName;
    
    @Column(name = "action_type")
    private String actionType;
    
    @ManyToMany(mappedBy = "accesses", fetch = FetchType.LAZY)
    private Set<Role> roles;
}
```

**Relationships:**
- **Many-to-Many dengan Role:** Banyak role dapat memiliki akses ini

---

## DAO Classes

### 1. UserDAO
**File:** `src/main/java/simian/testing/dao/UserDAO.java`

**CRUD Operations:**
- `create(User user)` - Menambah user baru
- `read(Integer userId)` - Mengambil user berdasarkan ID
- `update(User user)` - Mengupdate user yang sudah ada
- `delete(Integer userId)` - Menghapus user

**Query Methods:**
- `getAll()` - Mengambil semua user
- `findByUsername(String username)` - Mencari user berdasarkan username
- `findByEmail(String email)` - Mencari user berdasarkan email
- `findByRole(Integer roleId)` - Mengambil user dengan role tertentu
- `findActiveByRole(Integer roleId)` - Mengambil user aktif dengan role tertentu
- `findAllActive()` - Mengambil semua user yang aktif
- `existsByUsername(String username)` - Mengecek ketersediaan username
- `existsByEmail(String email)` - Mengecek ketersediaan email

### 2. RoleDAO
**File:** `src/main/java/simian/testing/dao/RoleDAO.java`

**CRUD Operations:**
- `create(Role role)` - Menambah role baru
- `read(Integer roleId)` - Mengambil role berdasarkan ID
- `update(Role role)` - Mengupdate role yang sudah ada
- `delete(Integer roleId)` - Menghapus role

**Query Methods:**
- `getAll()` - Mengambil semua role
- `findByName(String roleName)` - Mencari role berdasarkan nama
- `existsByName(String roleName)` - Mengecek ketersediaan role name
- `getUserCountByRole(Integer roleId)` - Menghitung jumlah user dalam role

### 3. AccessDAO
**File:** `src/main/java/simian/testing/dao/AccessDAO.java`

**CRUD Operations:**
- `create(Access access)` - Menambah access baru
- `read(Integer accessId)` - Mengambil access berdasarkan ID
- `update(Access access)` - Mengupdate access yang sudah ada
- `delete(Integer accessId)` - Menghapus access

**Query Methods:**
- `getAll()` - Mengambil semua access
- `findByName(String accessName)` - Mencari access berdasarkan nama
- `findByModule(String moduleName)` - Mengambil access dalam module tertentu
- `findByActionType(String actionType)` - Mengambil access dengan action type tertentu
- `existsByName(String accessName)` - Mengecek ketersediaan access name
- `findAccessesByRole(Integer roleId)` - Mengambil access untuk role tertentu

---

## Service Classes

### 1. UserService
**File:** `src/main/java/simian/testing/service/UserService.java`

**CRUD Operations:**
- `createUser(String username, String email, String password, String firstName, String lastName, Integer roleId)` - Membuat user baru dengan role
- `getUserById(Integer userId)` - Mengambil user berdasarkan ID
- `updateUser(Integer userId, String email, String firstName, String lastName, Integer roleId, Integer isActive)` - Mengupdate user
- `deleteUser(Integer userId)` - Menghapus user

**Additional Methods:**
- `getAllUsers()` - Mengambil semua user
- `getUserByUsername(String username)` - Mencari user berdasarkan username
- `getUserByEmail(String email)` - Mencari user berdasarkan email
- `getUsersByRole(Integer roleId)` - Mengambil user dengan role tertentu
- `getActiveUsersByRole(Integer roleId)` - Mengambil user aktif dengan role tertentu
- `getAllActiveUsers()` - Mengambil semua user yang aktif
- `activateUser(Integer userId)` - Mengaktifkan user
- `deactivateUser(Integer userId)` - Menonaktifkan user
- `changePassword(Integer userId, String newPassword)` - Mengubah password user
- `changeUserRole(Integer userId, Integer roleId)` - Mengubah role user
- `checkUsernameExists(String username)` - Mengecek ketersediaan username
- `checkEmailExists(String email)` - Mengecek ketersediaan email

### 2. RoleService
**File:** `src/main/java/simian/testing/service/RoleService.java`

**CRUD Operations:**
- `createRole(String roleName, String roleDescription)` - Membuat role baru
- `getRoleById(Integer roleId)` - Mengambil role berdasarkan ID
- `updateRole(Integer roleId, String roleName, String roleDescription)` - Mengupdate role
- `deleteRole(Integer roleId)` - Menghapus role (dengan validasi tidak ada user)

**Access Management Methods:**
- `addAccessToRole(Integer roleId, Integer accessId)` - Menambahkan satu access ke role
- `removeAccessFromRole(Integer roleId, Integer accessId)` - Menghapus access dari role
- `addMultipleAccessToRole(Integer roleId, List<Integer> accessIds)` - Menambahkan multiple access ke role

**Query Methods:**
- `getAllRoles()` - Mengambil semua role
- `getRoleByName(String roleName)` - Mencari role berdasarkan nama
- `getRoleAccesses(Integer roleId)` - Mengambil semua access untuk role
- `roleHasAccess(Integer roleId, Integer accessId)` - Mengecek apakah role memiliki access
- `roleHasAccessByName(Integer roleId, String accessName)` - Mengecek access berdasarkan nama
- `getUserCountByRole(Integer roleId)` - Menghitung user dalam role
- `checkRoleNameExists(String roleName)` - Mengecek ketersediaan role name

### 3. AccessService
**File:** `src/main/java/simian/testing/service/AccessService.java`

**CRUD Operations:**
- `createAccess(String accessName, String accessDescription, String moduleName, String actionType)` - Membuat access baru
- `getAccessById(Integer accessId)` - Mengambil access berdasarkan ID
- `updateAccess(Integer accessId, String accessName, String accessDescription, String moduleName, String actionType)` - Mengupdate access
- `deleteAccess(Integer accessId)` - Menghapus access

**Query Methods:**
- `getAllAccess()` - Mengambil semua access
- `getAccessByName(String accessName)` - Mencari access berdasarkan nama
- `getAccessesByModule(String moduleName)` - Mengambil access dalam module tertentu
- `getAccessesByActionType(String actionType)` - Mengambil access dengan action type tertentu
- `getAccessesByRole(Integer roleId)` - Mengambil access untuk role tertentu
- `checkAccessNameExists(String accessName)` - Mengecek ketersediaan access name

---

## Contoh Penggunaan

### Membuat User Baru
```java
@Autowired
private UserService userService;

// Membuat user baru dengan role Project Manager
User newUser = userService.createUser(
    "john.doe",           // username
    "john@simian.com",    // email
    "password123",        // password
    "John",               // firstName
    "Doe",                // lastName
    1                     // roleId (1 = Project Manager)
);
```

### Mengubah Role User
```java
// Mengubah role user dari Project Manager ke Development
User updatedUser = userService.changeUserRole(1, 3); // 3 = Development
```

### Menambahkan Access ke Role
```java
@Autowired
private RoleService roleService;

// Menambahkan permission CREATE_PROJECT_ORDER ke role Project Manager
Role role = roleService.addAccessToRole(1, 1);

// Menambahkan multiple permission sekaligus
List<Integer> accessIds = Arrays.asList(1, 2, 3);
roleService.addMultipleAccessToRole(1, accessIds);
```

### Mengecek Access User
```java
// Mengambil user
User user = userService.getUserById(1);

// Mengambil role user
Role userRole = user.getRole();

// Mengecek apakah role memiliki access tertentu
boolean hasAccess = roleService.roleHasAccessByName(
    userRole.getRoleId(), 
    "CREATE_PROJECT_ORDER"
);

if (hasAccess) {
    // User dapat membuat project order
}
```

### Mengambil User Berdasarkan Divisi
```java
// Mengambil semua user aktif dalam divisi Development
List<User> developmentTeam = userService.getActiveUsersByRole(3);

// Iterasi untuk mengambil akses setiap user
for (User user : developmentTeam) {
    List<Access> userAccess = roleService.getRoleAccesses(user.getRole().getRoleId());
    // ...
}
```

---

## Divisi dan Akses

### Project Manager (role_id = 1)
**Deskripsi:** Mengelola project order dalam sistem

**Access yang diberikan:**
1. `CREATE_PROJECT_ORDER` - Membuat project order baru
2. `CLOSE_PROJECT_ORDER` - Menutup/menyelesaikan project order
3. `DELETE_PROJECT_ORDER` - Menghapus project order

### Consultant (role_id = 2)
**Deskripsi:** Membuat dan mengelola sub task untuk project

**Access yang diberikan:**
1. `READ_PROJECT_ORDER` - Membaca detail project order
2. `CREATE_SUBTASK` - Membuat sub task baru
3. `UPDATE_SUBTASK` - Mengupdate sub task yang sudah ada
4. `DELETE_SUBTASK` - Menghapus sub task

### Development (role_id = 3)
**Deskripsi:** Mengerjakan dan melaporkan progres sub task

**Access yang diberikan:**
1. `VIEW_PROJECT_ORDER` - Melihat project order
2. `VIEW_SUBTASK` - Melihat detail sub task
3. `UPDATE_SUBTASK` - Mengupdate progress sub task

---

## Konfigurasi Database

Pastikan aplikasi Spring Boot dikonfigurasi dengan benar di `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/simian_work_order
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true
```

---

## Dependencies (pom.xml)

```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

---
Dibuat untuk: **PT Simian Solutions**  
Modul: **User Management dengan Role-Based Access Control**  
Tanggal: **2026-01-15**
Creator: Palito
email: palitopanggabean@gmail.com