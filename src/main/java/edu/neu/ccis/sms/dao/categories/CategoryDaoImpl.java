package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.categories.Category;
import edu.neu.ccis.sms.util.HibernateUtil;

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

    @Override
    public void saveCategory(Category newCategory) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newCategory);
        closeCurrentSessionwithTransaction();
    }

    @Override
    public void updateCategory(Category modifiedCategory) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedCategory);
        closeCurrentSessionwithTransaction();
    }

    public Category findByCategoryId(Long id) {
        openCurrentSessionwithTransaction();
        Category category = (Category) getCurrentSession().get(Category.class,
                id);
        closeCurrentSessionwithTransaction();
        return category;
    }
    
    @SuppressWarnings("unchecked")
	public Category findByCategoryName(String categoryName) {
    	openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "from Category WHERE name = :categoryName");
        query.setParameter("categoryName", categoryName);
        List<Category> categories = (List<Category>) query.list();
        closeCurrentSessionwithTransaction();
        if (categories == null || categories.isEmpty()) {
            return null;
        } else {
            return categories.get(0);
        }
    }

    @Override
    public void deleteCategory(Category category) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(category);
        closeCurrentSessionwithTransaction();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Category> getAllCategories() {
        openCurrentSessionwithTransaction();
        List<Category> categories = (List<Category>) getCurrentSession()
                .createQuery("from Category").list();
        closeCurrentSessionwithTransaction();
        return categories;
    }

    public void deleteAllCategories() {
        List<Category> categoryList = getAllCategories();
        for (Category category : categoryList) {
            deleteCategory(category);
        }
    }

    @Override
    public Category getCategory(Long id) {
        return findByCategoryId(id);
    }

	@Override
	public Category getCategoryByName(String categoryName) {
		return findByCategoryName(categoryName);
	}
}
