package simian.testing.dao;

import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import simian.testing.entity.User;

/**
 * DAO (Data Access Object) untuk User Entity
 * Menyediakan operasi CRUD berdasarkan Primary Key (userId)
 */
@Repository
@Transactional
public class UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Create - Menambah user baru ke database
     * @param user User object yang akan disimpan
     * @return userId dari user yang baru dibuat
     */
    public Integer create(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(user);
        return user.getUserId();
    }

    /**
     * Read - Mengambil user berdasarkan userId (Primary Key)
     * @param userId Primary Key dari user
     * @return Optional berisi User jika ditemukan, empty jika tidak
     */
    public Optional<User> read(Integer userId) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.find(User.class, userId);
        return Optional.ofNullable(user);
    }

    /**
     * Update - Mengupdate data user yang sudah ada
     * @param user User object dengan data yang telah diubah
     */
    public void update(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(user);
    }

    /**
     * Delete - Menghapus user berdasarkan userId
     * @param userId Primary Key dari user yang akan dihapus
     */
    public void delete(Integer userId) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.find(User.class, userId);
        if (user != null) {
            session.remove(user);
        }
    }

    /**
     * Mengambil semua user
     * @return List dari semua user
     */
    public List<User> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("FROM User", User.class);
        return query.list();
    }

    /**
     * Mengambil user berdasarkan username
     * @param username Username yang dicari
     * @return Optional berisi User jika ditemukan
     */
    public Optional<User> findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
        query.setParameter("username", username);
        return query.uniqueResultOptional();
    }

    /**
     * Mengambil user berdasarkan email
     * @param email Email yang dicari
     * @return Optional berisi User jika ditemukan
     */
    public Optional<User> findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        return query.uniqueResultOptional();
    }

    /**
     * Mengambil semua user berdasarkan role
     * @param roleId Role ID yang dicari
     * @return List dari user dengan role tersebut
     */
    public List<User> findByRole(Integer roleId) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("FROM User WHERE role.roleId = :roleId", User.class);
        query.setParameter("roleId", roleId);
        return query.list();
    }

    /**
     * Mengambil user aktif berdasarkan roleId
     * @param roleId Role ID yang dicari
     * @return List dari user aktif dengan role tersebut
     */
    public List<User> findActiveByRole(Integer roleId) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery(
            "FROM User WHERE role.roleId = :roleId AND isActive = 1", User.class);
        query.setParameter("roleId", roleId);
        return query.list();
    }

    /**
     * Mengambil semua user yang aktif
     * @return List dari user aktif
     */
    public List<User> findAllActive() {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("FROM User WHERE isActive = 1", User.class);
        return query.list();
    }

    /**
     * Mengecek apakah username sudah ada
     * @param username Username yang dicek
     * @return true jika username sudah ada, false jika belum
     */
    public boolean existsByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
            "SELECT COUNT(*) FROM User WHERE username = :username", Long.class);
        query.setParameter("username", username);
        return query.uniqueResult() > 0;
    }

    /**
     * Mengecek apakah email sudah ada
     * @param email Email yang dicek
     * @return true jika email sudah ada, false jika belum
     */
    public boolean existsByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
            "SELECT COUNT(*) FROM User WHERE email = :email", Long.class);
        query.setParameter("email", email);
        return query.uniqueResult() > 0;
    }
}
