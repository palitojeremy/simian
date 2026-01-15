package simian.testing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simian.testing.dao.UserDAO;
import simian.testing.dao.RoleDAO;
import simian.testing.entity.User;
import simian.testing.entity.Role;

/**
 * Service untuk User Management
 * Menyediakan business logic untuk operasi CRUD user dengan role
 * Menangani validasi dan business rules
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    /**
     * Create - Membuat user baru dengan role yang sudah ada
     * @param username Username (harus unik)
     * @param email Email (harus unik)
     * @param password Password user
     * @param firstName Nama depan user
     * @param lastName Nama belakang user
     * @param roleId ID dari role yang akan diberikan
     * @return User yang telah dibuat
     * @throws IllegalArgumentException jika username/email sudah ada atau role tidak ditemukan
     */
    public User createUser(String username, String email, String password, 
                          String firstName, String lastName, Integer roleId) {
        
        // Validasi username belum ada
        if (userDAO.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' sudah digunakan");
        }
        
        // Validasi email belum ada
        if (userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' sudah digunakan");
        }
        
        // Validasi role ada
        Optional<Role> role = roleDAO.read(roleId);
        if (!role.isPresent()) {
            throw new IllegalArgumentException("Role dengan ID " + roleId + " tidak ditemukan");
        }
        
        // Buat user baru
        User user = new User(username, email, password, firstName, lastName);
        user.setRole(role.get());
        
        Integer userId = userDAO.create(user);
        user.setUserId(userId);
        
        return user;
    }

    /**
     * Read - Mengambil user berdasarkan userId
     * @param userId ID user yang dicari
     * @return User jika ditemukan
     * @throws IllegalArgumentException jika user tidak ditemukan
     */
    public User getUserById(Integer userId) {
        Optional<User> user = userDAO.read(userId);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User dengan ID " + userId + " tidak ditemukan");
        }
        return user.get();
    }

    /**
     * Update - Mengupdate data user
     * @param userId ID user yang akan diupdate
     * @param email Email baru (opsional)
     * @param firstName Nama depan baru (opsional)
     * @param lastName Nama belakang baru (opsional)
     * @param roleId Role ID baru (opsional)
     * @param isActive Status aktif/tidak aktif (opsional)
     * @return User yang telah diupdate
     * @throws IllegalArgumentException jika user atau role tidak ditemukan
     */
    public User updateUser(Integer userId, String email, String firstName, 
                          String lastName, Integer roleId, Integer isActive) {
        
        User user = getUserById(userId);
        
        // Update email jika berbeda dan belum ada user lain dengan email tersebut
        if (email != null && !email.equals(user.getEmail())) {
            if (userDAO.existsByEmail(email)) {
                throw new IllegalArgumentException("Email '" + email + "' sudah digunakan");
            }
            user.setEmail(email);
        }
        
        // Update nama jika disediakan
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        
        // Update role jika disediakan
        if (roleId != null && !roleId.equals(user.getRole().getRoleId())) {
            Optional<Role> role = roleDAO.read(roleId);
            if (!role.isPresent()) {
                throw new IllegalArgumentException("Role dengan ID " + roleId + " tidak ditemukan");
            }
            user.setRole(role.get());
        }
        
        // Update status aktif jika disediakan
        if (isActive != null) {
            user.setIsActive(isActive);
        }
        
        userDAO.update(user);
        return user;
    }

    /**
     * Delete - Menghapus user berdasarkan userId
     * @param userId ID user yang akan dihapus
     * @throws IllegalArgumentException jika user tidak ditemukan
     */
    public void deleteUser(Integer userId) {
        getUserById(userId); // Validasi user ada
        userDAO.delete(userId);
    }

    /**
     * Mengambil semua user
     * @return List dari semua user
     */
    public List<User> getAllUsers() {
        return userDAO.getAll();
    }

    /**
     * Mengambil user berdasarkan username
     * @param username Username yang dicari
     * @return User jika ditemukan
     * @throws IllegalArgumentException jika user tidak ditemukan
     */
    public User getUserByUsername(String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User dengan username '" + username + "' tidak ditemukan");
        }
        return user.get();
    }

    /**
     * Mengambil user berdasarkan email
     * @param email Email yang dicari
     * @return User jika ditemukan
     * @throws IllegalArgumentException jika user tidak ditemukan
     */
    public User getUserByEmail(String email) {
        Optional<User> user = userDAO.findByEmail(email);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User dengan email '" + email + "' tidak ditemukan");
        }
        return user.get();
    }

    /**
     * Mengambil semua user dengan role tertentu
     * @param roleId ID role yang dicari
     * @return List dari user dengan role tersebut
     */
    public List<User> getUsersByRole(Integer roleId) {
        roleDAO.read(roleId).orElseThrow(() -> 
            new IllegalArgumentException("Role dengan ID " + roleId + " tidak ditemukan"));
        return userDAO.findByRole(roleId);
    }

    /**
     * Mengambil semua user aktif dengan role tertentu
     * @param roleId ID role yang dicari
     * @return List dari user aktif dengan role tersebut
     */
    public List<User> getActiveUsersByRole(Integer roleId) {
        roleDAO.read(roleId).orElseThrow(() -> 
            new IllegalArgumentException("Role dengan ID " + roleId + " tidak ditemukan"));
        return userDAO.findActiveByRole(roleId);
    }

    /**
     * Mengambil semua user yang aktif
     * @return List dari user aktif
     */
    public List<User> getAllActiveUsers() {
        return userDAO.findAllActive();
    }

    /**
     * Mengaktifkan user
     * @param userId ID user yang akan diaktifkan
     * @return User yang telah diaktifkan
     */
    public User activateUser(Integer userId) {
        User user = getUserById(userId);
        user.setIsActive(1);
        userDAO.update(user);
        return user;
    }

    /**
     * Menonaktifkan user
     * @param userId ID user yang akan dinonaktifkan
     * @return User yang telah dinonaktifkan
     */
    public User deactivateUser(Integer userId) {
        User user = getUserById(userId);
        user.setIsActive(0);
        userDAO.update(user);
        return user;
    }

    /**
     * Mengubah password user
     * @param userId ID user
     * @param newPassword Password baru
     * @return User dengan password yang telah diubah
     */
    public User changePassword(Integer userId, String newPassword) {
        User user = getUserById(userId);
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong");
        }
        user.setPassword(newPassword);
        userDAO.update(user);
        return user;
    }

    /**
     * Mengubah role user
     * @param userId ID user
     * @param roleId ID role baru
     * @return User dengan role yang telah diubah
     */
    public User changeUserRole(Integer userId, Integer roleId) {
        User user = getUserById(userId);
        Role role = roleDAO.read(roleId).orElseThrow(() -> 
            new IllegalArgumentException("Role dengan ID " + roleId + " tidak ditemukan"));
        user.setRole(role);
        userDAO.update(user);
        return user;
    }

    /**
     * Mengecek apakah username sudah ada
     * @param username Username yang dicek
     * @return true jika username sudah ada
     */
    public boolean checkUsernameExists(String username) {
        return userDAO.existsByUsername(username);
    }

    /**
     * Mengecek apakah email sudah ada
     * @param email Email yang dicek
     * @return true jika email sudah ada
     */
    public boolean checkEmailExists(String email) {
        return userDAO.existsByEmail(email);
    }
}
