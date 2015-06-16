package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import edu.neu.ccis.sms.entity.categories.Category;

/**
 * DAO interface for Category Entity bean
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface CategoryDao {
    // Category
    public List<Category> getAllCategories();

    public Category getCategory(Long id);

    public Category getCategoryByName(String categoryName);

    public void updateCategory(Category modifiedCategory);

    public void deleteCategory(Category category);

    public void saveCategory(Category newCategory);

}
