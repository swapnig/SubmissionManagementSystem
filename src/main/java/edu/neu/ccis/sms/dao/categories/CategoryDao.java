package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import edu.neu.ccis.sms.entity.categories.Category;

/**
 * DAO interface for Category Entity bean; Provides methods to access category entity related data
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface CategoryDao {
    /** Get all categories available in current installation */
    public List<Category> getAllCategories();

    /** Get category by its category id */
    public Category getCategory(Long id);

    /** Get category by its name, as category names are unique for an installation */
    public Category getCategoryByName(String categoryName);

    /** Update category details for an already existing category */
    public void updateCategory(Category modifiedCategory);

    /** Delete a given category */
    public void deleteCategory(Category category);

    /** Save a new category */
    public void saveCategory(Category newCategory);
}
