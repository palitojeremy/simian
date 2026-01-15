package simian.testing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simian.testing.dao.AccessDAO;
import simian.testing.entity.Access;

/**
 * Service untuk Access Management
 * Menyediakan business logic untuk operasi CRUD access permissions
 * Menangani validasi dan business rules untuk access
 */
@Service
@Transactional
public class AccessService {

    @Autowired
    private AccessDAO accessDAO;

    /**
     * Create - Membuat access baru
     * @param accessName Nama access (harus unik)
     * @param accessDescription Deskripsi access
     * @param moduleName Nama module yang di-protect oleh access ini
     * @param actionType Tipe aksi (CREATE, READ, UPDATE, DELETE, VIEW, CLOSE, dll)
     * @return Access yang telah dibuat
     * @throws IllegalArgumentException jika access name sudah ada
     */
    public Access createAccess(String accessName, String accessDescription, 
                              String moduleName, String actionType) {
        
        // Validasi access name belum ada
        if (accessDAO.existsByName(accessName)) {
            throw new IllegalArgumentException("Access '" + accessName + "' sudah ada");
        }
        
        // Buat access baru
        Access access = new Access(accessName, accessDescription, moduleName, actionType);
        Integer accessId = accessDAO.create(access);
        access.setAccessId(accessId);
        
        return access;
    }

    /**
     * Read - Mengambil access berdasarkan accessId
     * @param accessId ID access yang dicari
     * @return Access jika ditemukan
     * @throws IllegalArgumentException jika access tidak ditemukan
     */
    public Access getAccessById(Integer accessId) {
        Optional<Access> access = accessDAO.read(accessId);
        if (!access.isPresent()) {
            throw new IllegalArgumentException("Access dengan ID " + accessId + " tidak ditemukan");
        }
        return access.get();
    }

    /**
     * Update - Mengupdate data access
     * @param accessId ID access yang akan diupdate
     * @param accessName Nama access baru (opsional)
     * @param accessDescription Deskripsi access baru (opsional)
     * @param moduleName Nama module baru (opsional)
     * @param actionType Tipe aksi baru (opsional)
     * @return Access yang telah diupdate
     * @throws IllegalArgumentException jika access tidak ditemukan atau access name sudah ada
     */
    public Access updateAccess(Integer accessId, String accessName, String accessDescription,
                              String moduleName, String actionType) {
        
        Access access = getAccessById(accessId);
        
        // Update access name jika berbeda dan belum ada access lain dengan nama tersebut
        if (accessName != null && !accessName.equals(access.getAccessName())) {
            if (accessDAO.existsByName(accessName)) {
                throw new IllegalArgumentException("Access '" + accessName + "' sudah ada");
            }
            access.setAccessName(accessName);
        }
        
        // Update access description jika disediakan
        if (accessDescription != null) {
            access.setAccessDescription(accessDescription);
        }
        
        // Update module name jika disediakan
        if (moduleName != null) {
            access.setModuleName(moduleName);
        }
        
        // Update action type jika disediakan
        if (actionType != null) {
            access.setActionType(actionType);
        }
        
        accessDAO.update(access);
        return access;
    }

    /**
     * Delete - Menghapus access berdasarkan accessId
     * @param accessId ID access yang akan dihapus
     * @throws IllegalArgumentException jika access tidak ditemukan
     */
    public void deleteAccess(Integer accessId) {
        getAccessById(accessId); // Validasi access ada
        accessDAO.delete(accessId);
    }

    /**
     * Mengambil semua access
     * @return List dari semua access
     */
    public List<Access> getAllAccess() {
        return accessDAO.getAll();
    }

    /**
     * Mengambil access berdasarkan nama
     * @param accessName Nama access yang dicari
     * @return Access jika ditemukan
     * @throws IllegalArgumentException jika access tidak ditemukan
     */
    public Access getAccessByName(String accessName) {
        Optional<Access> access = accessDAO.findByName(accessName);
        if (!access.isPresent()) {
            throw new IllegalArgumentException("Access '" + accessName + "' tidak ditemukan");
        }
        return access.get();
    }

    /**
     * Mengambil semua access dalam modul tertentu
     * @param moduleName Nama module yang dicari
     * @return List dari access dalam module tersebut
     */
    public List<Access> getAccessesByModule(String moduleName) {
        return accessDAO.findByModule(moduleName);
    }

    /**
     * Mengambil semua access dengan action type tertentu
     * @param actionType Tipe aksi yang dicari
     * @return List dari access dengan action type tersebut
     */
    public List<Access> getAccessesByActionType(String actionType) {
        return accessDAO.findByActionType(actionType);
    }

    /**
     * Mengecek apakah access name sudah ada
     * @param accessName Nama access yang dicek
     * @return true jika access name sudah ada
     */
    public boolean checkAccessNameExists(String accessName) {
        return accessDAO.existsByName(accessName);
    }

    /**
     * Mengambil semua access untuk role tertentu
     * @param roleId ID role yang dicari
     * @return List dari access yang dimiliki role tersebut
     */
    public List<Access> getAccessesByRole(Integer roleId) {
        return accessDAO.findAccessesByRole(roleId);
    }
}
