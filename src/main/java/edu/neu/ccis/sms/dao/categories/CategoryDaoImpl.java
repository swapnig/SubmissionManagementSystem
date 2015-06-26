package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.categories.Category;
import edu.neu.ccis.sms.util.HibernateUtil;

/**
 * DAO implementation class for Category Entity bean;
 * 
 * Provides actual implementations for all category entity related access methods
 * 
 * @author Pramod R. Khare, Swapnil Gupta
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public class CategoryDaoImpl implements CategoryDao {
    /** Hibernate session instance */
    private Session currentSession;

    /** Hibernate session transaction instance */
    private Transaction currentTransaction;

    /** Default Constructor */
    public CategoryDaoImpl() {
    }

    /**
     * private utility book-keeping method to open hibernate session with a new transaction
     * 
     * @return - Hibernate session instance
     */
    private Session openCurrentSessionwithTransaction() {
        currentSession = HibernateUtil.getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }

    /**
     * private utility book-keeping method to close current transaction along with hibernate session, committing any new
     * changes to entities; nulling out old references;
     */
    private void closeCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
        currentTransaction = null;
        currentSession = null;
    }

    /**
     * Getter method for current active hibernate session, if there isn't any active session then returns null
     * 
     * @return - current active hibernate session instance else null
     */
    public Session getCurrentSession() {
        return currentSession;
    }

    /**
     * Getter method for current active hibernate transaction, if there isn't any active transaction then returns null
     * 
     * @return - current active hibernate transaction instance else null
     */
    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    /**
     * Save a new category
     * 
     * @param newCategory
     *            - new category to be saved
     */
    @Override
    public void saveCategory(final Category newCategory) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newCategory);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Update category details for an already existing category
     * 
     * @param modifiedCategory
     *            - modified category object
     */
    @Override
    public void updateCategory(final Category modifiedCategory) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedCategory);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Find Category by its category-id if it exists, else return null
     * 
     * @param id
     *            - category id
     * @return - retrieved category instance if it exists else null
     */
    public Category findByCategoryId(final Long id) {
        openCurrentSessionwithTransaction();
        Category category = (Category) getCurrentSession().get(Category.class, id);
        closeCurrentSessionwithTransaction();
        return category;
    }

    /**
     * Find Category by its category-name if it exists else returns null;
     * 
     * @note category names are unique for every installation
     * 
     * @param categoryName
     *            - name of category to find
     * @return - Category instance with given category-name if it exists else returns null
     */
    @SuppressWarnings("unchecked")
    public Category findByCategoryName(final String categoryName) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery("from Category WHERE name = :categoryName");
        query.setParameter("categoryName", categoryName);
        List<Category> categories = query.list();
        closeCurrentSessionwithTransaction();
        if (categories == null || categories.isEmpty()) {
            return null;
        } else {
            return categories.get(0);
        }
    }

    /**
     * Delete a given category
     * 
     * @param category
     *            - category object to be deleted
     */
    @Override
    public void deleteCategory(final Category category) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(category);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Get all categories available in current installation
     * 
     * @return List<Category> - a list of all retrieved categories
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Category> getAllCategories() {
        openCurrentSessionwithTransaction();
        List<Category> categories = getCurrentSession().createQuery("from Category").list();
        closeCurrentSessionwithTransaction();
        return categories;
    }

    /**
     * Get category by its category id
     * 
     * @param id
     *            - category id to be retrieved
     * @return Category object if there is a category with given category-id else returns null
     */
    @Override
    public Category getCategory(final Long id) {
        return findByCategoryId(id);
    }

    /**
     * Get category by its name, as category names are unique for an installation
     * 
     * @param categoryName
     *            - name of category to be retrieved
     * @return Category object if there is a category with given category-name else returns null
     */
    @Override
    public Category getCategoryByName(final String categoryName) {
        return findByCategoryName(categoryName);
    }
}
