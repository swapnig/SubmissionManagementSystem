package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import edu.neu.ccis.sms.entity.categories.Category;

/**
 * DAO interface for Category Entity bean; Provides methods to access category from persistent store
 * 
 * @author Pramod R. Khare, Swapnil Gupta
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface CategoryDao {
    /**
     * Get all categories available in current installation
     * 
     * @return List<Category> - a list of all retrieved categories
     */
    public List<Category> getAllCategories();

    /**
     * Get category by its category id
     * 
     * @param id
     *            - category id to be retrieved
     * @return Category object if there is a category with given category-id else returns null
     */
    public Category getCategory(Long id);

    /**
     * Get category by its name, as category names are unique for an installation
     * 
     * @param categoryName
     *            - name of category to be retrieved
     * @return Category object if there is a category with given category-name else returns null
     */
    public Category getCategoryByName(String categoryName);

    /**
     * Update category details for an already existing category
     * 
     * @param modifiedCategory
     *            - modified category object
     */
    public void updateCategory(Category modifiedCategory);

    /**
     * Delete a given category
     * 
     * @param category
     *            - category object to be deleted
     */
    public void deleteCategory(Category category);

    /**
     * Save a new category
     * 
     * @param newCategory
     *            - new category to be saved
     */
    public void saveCategory(Category newCategory);
}
