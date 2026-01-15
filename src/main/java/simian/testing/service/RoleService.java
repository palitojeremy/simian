package simian.testing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simian.testing.dao.RoleDAO;
import simian.testing.dao.AccessDAO;
import simian.testing.entity.Role;
import simian.testing.entity.Access;

/**
 * Service untuk Role Management
 * Menyediakan business logic untuk operasi CRUD role dengan access permissions
 * Menangani validasi dan business rules untuk role dan access
 */
@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private AccessDAO accessDAO;

    /**
     * Create - Membuat role baru
     * @param roleName Nama role (harus unik)
     * @param roleDescription Deskripsi role
     * @return Role yang telah dibuat
     * @throws IllegalArgumentException jika role name sudah ada
     */
    public Role createRole(String roleName, String roleDescription) {
        
        // Validasi role name belum ada
        if (roleDAO.existsByName(roleName)) {
            throw new IllegalArgumentException("Role '" + roleName + "' sudah ada");
        }
        
        // Buat role baru
        Role role = new Role(roleName, roleDescription);
        Integer roleId = roleDAO.create(role);
        role.setRoleId(roleId);
        
        return role;
    }

    /**
     * Read - Mengambil role berdasarkan roleId
     * @param roleId ID role yang dicari
     * @return Role jika ditemukan
     * @throws IllegalArgumentException jika role tidak ditemukan
     */
    public Role getRoleById(Integer roleId) {
        Optional<Role> role = roleDAO.read(roleId);
        if (!role.isPresent()) {
            throw new IllegalArgumentException("Role dengan ID " + roleId + " tidak ditemukan");
        }
        return role.get();
    }

    /**
     * Update - Mengupdate data role
     * @param roleId ID role yang akan diupdate
     * @param roleName Nama role baru (opsional)
     * @param roleDescription Deskripsi role baru (opsional)
     * @return Role yang telah diupdate
     * @throws IllegalArgumentException jika role tidak ditemukan atau role name sudah ada
     */
    public Role updateRole(Integer roleId, String roleName, String roleDescription) {
        
        Role role = getRoleById(roleId);
        
        // Update role name jika berbeda dan belum ada role lain dengan nama tersebut
        if (roleName != null && !roleName.equals(role.getRoleName())) {
            if (roleDAO.existsByName(roleName)) {
                throw new IllegalArgumentException("Role '" + roleName + "' sudah ada");
            }
            role.setRoleName(roleName);
        }
        
        // Update role description jika disediakan
        if (roleDescription != null) {
            role.setRoleDescription(roleDescription);
        }
        
        roleDAO.update(role);
        return role;
    }

    /**
     * Delete - Menghapus role berdasarkan roleId
     * Sebelum menghapus, pastikan tidak ada user yang menggunakan role ini
     * @param roleId ID role yang akan dihapus
     * @throws IllegalArgumentException jika role tidak ditemukan atau masih ada user yang menggunakan role
     */
    public void deleteRole(Integer roleId) {
        
        Role role = getRoleById(roleId); // Validasi role ada
        
        // Cek apakah ada user yang menggunakan role ini
        Long userCount = roleDAO.getUserCountByRole(roleId);
        if (userCount > 0) {
            throw new IllegalArgumentException(
                "Role '" + role.getRoleName() + "' tidak dapat dihapus karena masih ada " + 
                userCount + " user yang menggunakannya");
        }
        
        roleDAO.delete(roleId);
    }

    /**
     * Mengambil semua role
     * @return List dari semua role
     */
    public List<Role> getAllRoles() {
        return roleDAO.getAll();
    }

    /**
     * Mengambil role berdasarkan nama
     * @param roleName Nama role yang dicari
     * @return Role jika ditemukan
     * @throws IllegalArgumentException jika role tidak ditemukan
     */
    public Role getRoleByName(String roleName) {
        Optional<Role> role = roleDAO.findByName(roleName);
        if (!role.isPresent()) {
            throw new IllegalArgumentException("Role '" + roleName + "' tidak ditemukan");
        }
        return role.get();
    }

    /**
     * Menambahkan access ke role
     * @param roleId ID role yang akan ditambahi access
     * @param accessId ID access yang akan ditambahkan
     * @return Role dengan access yang telah ditambahkan
     */
    public Role addAccessToRole(Integer roleId, Integer accessId) {
        
        Role role = getRoleById(roleId);
        Optional<Access> access = accessDAO.read(accessId);
        
        if (!access.isPresent()) {
            throw new IllegalArgumentException("Access dengan ID " + accessId + " tidak ditemukan");
        }
        
        // Cek apakah access sudah ditambahkan ke role ini
        if (role.getAccesses().contains(access.get())) {
            throw new IllegalArgumentException(
                "Access '" + access.get().getAccessName() + "' sudah ada di role ini");
        }
        
        role.getAccesses().add(access.get());
        roleDAO.update(role);
        
        return role;
    }

    /**
     * Menghapus access dari role
     * @param roleId ID role yang akan dikurangi access
     * @param accessId ID access yang akan dihapus
     * @return Role dengan access yang telah dihapus
     */
    public Role removeAccessFromRole(Integer roleId, Integer accessId) {
        
        Role role = getRoleById(roleId);
        Optional<Access> access = accessDAO.read(accessId);
        
        if (!access.isPresent()) {
            throw new IllegalArgumentException("Access dengan ID " + accessId + " tidak ditemukan");
        }
        
        // Cek apakah access ada di role ini
        if (!role.getAccesses().contains(access.get())) {
            throw new IllegalArgumentException(
                "Access '" + access.get().getAccessName() + "' tidak ada di role ini");
        }
        
        role.getAccesses().remove(access.get());
        roleDAO.update(role);
        
        return role;
    }

    /**
     * Mengambil semua access untuk role tertentu
     * @param roleId ID role yang dicari
     * @return List dari access yang dimiliki role tersebut
     */
    public List<Access> getRoleAccesses(Integer roleId) {
        
        getRoleById(roleId); // Validasi role ada
        
        return accessDAO.findAccessesByRole(roleId);
    }

    /**
     * Mengecek apakah role memiliki access tertentu
     * @param roleId ID role
     * @param accessId ID access
     * @return true jika role memiliki access tersebut
     */
    public boolean roleHasAccess(Integer roleId, Integer accessId) {
        
        Role role = getRoleById(roleId);
        Optional<Access> access = accessDAO.read(accessId);
        
        if (!access.isPresent()) {
            return false;
        }
        
        return role.getAccesses().contains(access.get());
    }

    /**
     * Mengecek apakah role memiliki access berdasarkan nama access
     * @param roleId ID role
     * @param accessName Nama access
     * @return true jika role memiliki access tersebut
     */
    public boolean roleHasAccessByName(Integer roleId, String accessName) {
        
        Role role = getRoleById(roleId);
        
        return role.getAccesses().stream()
                .anyMatch(access -> access.getAccessName().equals(accessName));
    }

    /**
     * Menghitung jumlah user yang memiliki role tertentu
     * @param roleId ID role
     * @return Jumlah user dengan role tersebut
     */
    public Long getUserCountByRole(Integer roleId) {
        
        getRoleById(roleId); // Validasi role ada
        
        return roleDAO.getUserCountByRole(roleId);
    }

    /**
     * Mengecek apakah role name sudah ada
     * @param roleName Nama role yang dicek
     * @return true jika role name sudah ada
     */
    public boolean checkRoleNameExists(String roleName) {
        return roleDAO.existsByName(roleName);
    }

    /**
     * Memberikan multiple access ke role sekaligus
     * @param roleId ID role yang akan ditambahi access
     * @param accessIds List dari access IDs yang akan ditambahkan
     * @return Role dengan semua access yang telah ditambahkan
     */
    public Role addMultipleAccessToRole(Integer roleId, List<Integer> accessIds) {
        
        Role role = getRoleById(roleId);
        
        for (Integer accessId : accessIds) {
            Optional<Access> access = accessDAO.read(accessId);
            
            if (!access.isPresent()) {
                throw new IllegalArgumentException("Access dengan ID " + accessId + " tidak ditemukan");
            }
            
            // Hanya tambahkan jika belum ada
            if (!role.getAccesses().contains(access.get())) {
                role.getAccesses().add(access.get());
            }
        }
        
        roleDAO.update(role);
        return role;
    }
}
