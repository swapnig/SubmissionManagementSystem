package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.categories.Category;
import edu.neu.ccis.sms.util.HibernateUtil;

/**
 * DAO implementation class for Category Entity bean;
 * 
 * Provides actual implementations for all category entity related access methods
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public class CategoryDaoImpl implements CategoryDao {
    private Session currentSession;
    private Transaction currentTransaction;

    public CategoryDaoImpl() {
    }

    public Session openCurrentSessionwithTransaction() {
        currentSession = getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }

    public void closeCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
    }

    private static SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    /** Save a new category */
    @Override
    public void saveCategory(Category newCategory) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newCategory);
        closeCurrentSessionwithTransaction();
    }

    /** Update category details for an already existing category */
    @Override
    public void updateCategory(Category modifiedCategory) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedCategory);
        closeCurrentSessionwithTransaction();
    }

    /** Find category by its id */
    public Category findByCategoryId(Long id) {
        openCurrentSessionwithTransaction();
        Category category = (Category) getCurrentSession().get(Category.class, id);
        closeCurrentSessionwithTransaction();
        return category;
    }

    /** Get category by its name, as category names are unique for an installation */
    @SuppressWarnings("unchecked")
    public Category findByCategoryName(String categoryName) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery("from Category WHERE name = :categoryName");
        query.setParameter("categoryName", categoryName);
        List<Category> categories = (List<Category>) query.list();
        closeCurrentSessionwithTransaction();
        if (categories == null || categories.isEmpty()) {
            return null;
        } else {
            return categories.get(0);
        }
    }

    /** Delete a given category */
    @Override
    public void deleteCategory(Category category) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(category);
        closeCurrentSessionwithTransaction();
    }

    /** Get all categories available in current installation */
    @Override
    @SuppressWarnings("unchecked")
    public List<Category> getAllCategories() {
        openCurrentSessionwithTransaction();
        List<Category> categories = (List<Category>) getCurrentSession().createQuery("from Category").list();
        closeCurrentSessionwithTransaction();
        return categories;
    }

    /** Delete all available categories */
    public void deleteAllCategories() {
        List<Category> categoryList = getAllCategories();
        for (Category category : categoryList) {
            deleteCategory(category);
        }
    }

    /**
     * Get Category by its Category-Id
     */
    @Override
    public Category getCategory(Long id) {
        return findByCategoryId(id);
    }

    /** Get Category by its Category-Name; as category names are unique per installation */
    @Override
    public Category getCategoryByName(String categoryName) {
        return findByCategoryName(categoryName);
    }
}
