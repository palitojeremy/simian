package simian.testing.dao;

import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import simian.testing.entity.Role;

/**
 * DAO (Data Access Object) untuk Role Entity
 * Menyediakan operasi CRUD berdasarkan Primary Key (roleId)
 */
@Repository
@Transactional
public class RoleDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Create - Menambah role baru ke database
     * @param role Role object yang akan disimpan
     * @return roleId dari role yang baru dibuat
     */
    public Integer create(Role role) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(role);
        return role.getRoleId();
    }

    /**
     * Read - Mengambil role berdasarkan roleId (Primary Key)
     * @param roleId Primary Key dari role
     * @return Optional berisi Role jika ditemukan, empty jika tidak
     */
    public Optional<Role> read(Integer roleId) {
        Session session = sessionFactory.getCurrentSession();
        Role role = session.find(Role.class, roleId);
        return Optional.ofNullable(role);
    }

    /**
     * Update - Mengupdate data role yang sudah ada
     * @param role Role object dengan data yang telah diubah
     */
    public void update(Role role) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(role);
    }

    /**
     * Delete - Menghapus role berdasarkan roleId
     * @param roleId Primary Key dari role yang akan dihapus
     */
    public void delete(Integer roleId) {
        Session session = sessionFactory.getCurrentSession();
        Role role = session.find(Role.class, roleId);
        if (role != null) {
            session.remove(role);
        }
    }

    /**
     * Mengambil semua role
     * @return List dari semua role
     */
    public List<Role> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Query<Role> query = session.createQuery("FROM Role", Role.class);
        return query.list();
    }

    /**
     * Mengambil role berdasarkan nama
     * @param roleName Nama role yang dicari
     * @return Optional berisi Role jika ditemukan
     */
    public Optional<Role> findByName(String roleName) {
        Session session = sessionFactory.getCurrentSession();
        Query<Role> query = session.createQuery("FROM Role WHERE roleName = :roleName", Role.class);
        query.setParameter("roleName", roleName);
        return query.uniqueResultOptional();
    }

    /**
     * Mengecek apakah role name sudah ada
     * @param roleName Nama role yang dicek
     * @return true jika role name sudah ada, false jika belum
     */
    public boolean existsByName(String roleName) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
            "SELECT COUNT(*) FROM Role WHERE roleName = :roleName", Long.class);
        query.setParameter("roleName", roleName);
        return query.uniqueResult() > 0;
    }

    /**
     * Mendapatkan jumlah user dalam sebuah role
     * @param roleId Role ID yang dicek
     * @return Jumlah user dalam role tersebut
     */
    public Long getUserCountByRole(Integer roleId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
            "SELECT COUNT(*) FROM User WHERE role.roleId = :roleId", Long.class);
        query.setParameter("roleId", roleId);
        return query.uniqueResult();
    }
}
