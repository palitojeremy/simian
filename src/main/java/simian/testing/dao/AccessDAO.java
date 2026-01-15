package simian.testing.dao;

import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import simian.testing.entity.Access;

/**
 * DAO (Data Access Object) untuk Access Entity
 * Menyediakan operasi CRUD berdasarkan Primary Key (accessId)
 */
@Repository
@Transactional
public class AccessDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Create - Menambah access baru ke database
     * @param access Access object yang akan disimpan
     * @return accessId dari access yang baru dibuat
     */
    public Integer create(Access access) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(access);
        return access.getAccessId();
    }

    /**
     * Read - Mengambil access berdasarkan accessId (Primary Key)
     * @param accessId Primary Key dari access
     * @return Optional berisi Access jika ditemukan, empty jika tidak
     */
    public Optional<Access> read(Integer accessId) {
        Session session = sessionFactory.getCurrentSession();
        Access access = session.find(Access.class, accessId);
        return Optional.ofNullable(access);
    }

    /**
     * Update - Mengupdate data access yang sudah ada
     * @param access Access object dengan data yang telah diubah
     */
    public void update(Access access) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(access);
    }

    /**
     * Delete - Menghapus access berdasarkan accessId
     * @param accessId Primary Key dari access yang akan dihapus
     */
    public void delete(Integer accessId) {
        Session session = sessionFactory.getCurrentSession();
        Access access = session.find(Access.class, accessId);
        if (access != null) {
            session.remove(access);
        }
    }

    /**
     * Mengambil semua access
     * @return List dari semua access
     */
    public List<Access> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Query<Access> query = session.createQuery("FROM Access", Access.class);
        return query.list();
    }

    /**
     * Mengambil access berdasarkan nama
     * @param accessName Nama access yang dicari
     * @return Optional berisi Access jika ditemukan
     */
    public Optional<Access> findByName(String accessName) {
        Session session = sessionFactory.getCurrentSession();
        Query<Access> query = session.createQuery("FROM Access WHERE accessName = :accessName", Access.class);
        query.setParameter("accessName", accessName);
        return query.uniqueResultOptional();
    }

    /**
     * Mengambil semua access berdasarkan module
     * @param moduleName Module name yang dicari
     * @return List dari access dalam module tersebut
     */
    public List<Access> findByModule(String moduleName) {
        Session session = sessionFactory.getCurrentSession();
        Query<Access> query = session.createQuery(
            "FROM Access WHERE moduleName = :moduleName", Access.class);
        query.setParameter("moduleName", moduleName);
        return query.list();
    }

    /**
     * Mengambil semua access berdasarkan action type
     * @param actionType Action type yang dicari
     * @return List dari access dengan action type tersebut
     */
    public List<Access> findByActionType(String actionType) {
        Session session = sessionFactory.getCurrentSession();
        Query<Access> query = session.createQuery(
            "FROM Access WHERE actionType = :actionType", Access.class);
        query.setParameter("actionType", actionType);
        return query.list();
    }

    /**
     * Mengecek apakah access name sudah ada
     * @param accessName Nama access yang dicek
     * @return true jika access name sudah ada, false jika belum
     */
    public boolean existsByName(String accessName) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
            "SELECT COUNT(*) FROM Access WHERE accessName = :accessName", Long.class);
        query.setParameter("accessName", accessName);
        return query.uniqueResult() > 0;
    }

    /**
     * Mengambil semua access untuk sebuah role
     * @param roleId Role ID yang dicari
     * @return List dari access yang dimiliki role tersebut
     */
    public List<Access> findAccessesByRole(Integer roleId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Access> query = session.createQuery(
            "SELECT a FROM Access a JOIN a.roles r WHERE r.roleId = :roleId", Access.class);
        query.setParameter("roleId", roleId);
        return query.list();
    }
}
