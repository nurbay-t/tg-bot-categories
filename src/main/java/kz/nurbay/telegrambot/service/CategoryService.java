package kz.nurbay.telegrambot.service;

import kz.nurbay.telegrambot.model.Category;
import kz.nurbay.telegrambot.model.User;
import kz.nurbay.telegrambot.repository.CategoryRepository;
import kz.nurbay.telegrambot.repository.UserRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Checks if the user has a root category element.
     *
     * @param userId the ID of the user to check for a root element
     * @return true if the user has a root element, false otherwise
     */
    public boolean rootElementExists(Long userId) {
        return categoryRepository.existsByUserIdAndParentIsNull(userId);
    }

    /**
     * Retrieves all categories of the given user.
     *
     * @param userId the ID of the user whose categories are being retrieved
     * @return a list of categories belonging to the user
     */
    public List<Category> getAllCategoriesByUserId(Long userId) {
        return categoryRepository.findAllByUserId(userId);
    }

    /**
     * Builds a string representation of the user's category tree structure.
     *
     * @param userId the ID of the user whose category tree is being built
     * @return a formatted string representing the category tree
     */
    public String getTreeStructure(Long userId) {
        List<Category> categories = getAllCategoriesByUserId(userId);

        Map<Long, List<Category>> categoryMap = new HashMap<>();
        Category root = null;

        for (Category category : categories) {
            Long parentId = category.getParent() != null ? category.getParent().getId() : null;
            if (parentId == null) {
                root = category;
            } else {
                categoryMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(category);
            }
        }

        StringBuilder treeBuilder = new StringBuilder();
        treeBuilder.append("```\n");
        treeBuilder.append(root.getName()).append("\n");
        buildTreeStructure(treeBuilder, root, categoryMap, "", true);
        treeBuilder.append("```");
        return treeBuilder.toString();
    }

    /**
     * Recursively builds the string representation of the category tree.
     *
     * @param builder     the StringBuilder used to construct the tree
     * @param category    the current category being processed
     * @param categoryMap the map of categories organized by parent ID
     * @param prefix      the current indentation prefix for tree levels
     * @param isLast      whether the current category is the last sibling in its branch
     */
    private void buildTreeStructure(StringBuilder builder, Category category, Map<Long, List<Category>> categoryMap, String prefix, boolean isLast) {
        List<Category> children = categoryMap.get(category.getId());

        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                Category child = children.get(i);
                boolean isLastChild = (i == children.size() - 1);
                String childPrefix = prefix + (isLast ? "    " : "│   ");
                builder.append(childPrefix);
                builder.append(isLastChild ? "└── " : "├── ");
                builder.append(child.getName()).append("\n");
                buildTreeStructure(builder, child, categoryMap, childPrefix, isLastChild);
            }
        }
    }

    /**
     * Adds a root element to the user's category tree.
     *
     * @param userId      the ID of the user who is adding the root element
     * @param elementName the name of the root element to add
     */
    public void addRootElement(Long userId, String elementName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));
        Category category = new Category();
        category.setName(elementName);
        category.setUser(user);
        categoryRepository.save(category);
    }

    /**
     * Adds a child element under a specified parent category for the given user.
     *
     * @param userId     the ID of the user who is adding the child element
     * @param parentName the name of the parent category
     * @param childName  the name of the child category to add
     * @return true if the parent exists and the child was added, false otherwise
     */
    public boolean addChildElement(Long userId, String parentName, String childName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));
        Category parent = categoryRepository.findByNameAndUserId(parentName, userId);
        if (parent != null) {
            Category child = new Category();
            child.setName(childName);
            child.setParent(parent);
            child.setUser(user);
            categoryRepository.save(child);
            return true;
        }
        return false;
    }

    /**
     * Removes a category and all its child elements from the user's category tree.
     *
     * @param userId      the ID of the user whose category is being removed
     * @param elementName the name of the category to remove
     * @return true if the category was found and removed, false otherwise
     */
    public boolean removeElementWithChildren(Long userId, String elementName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        Optional<Category> categoryOpt = categoryRepository.findByNameAndUser(elementName, user);

        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();

            List<Category> categories = categoryRepository.findAllByUserId(userId);

            Map<Long, List<Category>> categoryMap = new HashMap<>();
            for (Category cat : categories) {
                Long parentId = cat.getParent() != null ? cat.getParent().getId() : null;
                categoryMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(cat);
            }

            List<Long> idsToDelete = new ArrayList<>();
            collectCategoryIds(category, categoryMap, idsToDelete);

            categoryRepository.deleteAllByIdInBatch(idsToDelete);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Recursively collects the IDs of the category and its child categories.
     *
     * @param category    the category being processed
     * @param categoryMap the map of categories organized by parent ID
     * @param ids         the list of IDs to delete
     */
    private void collectCategoryIds(Category category, Map<Long, List<Category>> categoryMap, List<Long> ids) {
        ids.add(category.getId());
        List<Category> children = categoryMap.get(category.getId());

        if (children != null) {
            for (Category child : children) {
                collectCategoryIds(child, categoryMap, ids);
            }
        }
    }

    /**
     * Creates an Excel file that represents the user's category tree.
     *
     * @param categories the list of categories to include in the Excel file
     * @return a ByteArrayInputStream representing the Excel file
     */
    public ByteArrayInputStream createExcelFileWithCategories(List<Category> categories) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Categories");

            Map<Long, List<Category>> categoryMap = new HashMap<>();
            Category root = null;

            for (Category category : categories) {
                Long parentId = category.getParent() != null ? category.getParent().getId() : null;
                if (parentId == null) {
                    root = category;
                } else {
                    categoryMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(category);
                }
            }

            int[] rowIdx = {0};
            buildExcelTree(sheet, root, categoryMap, rowIdx, 0);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * Recursively builds the Excel tree structure by adding categories as rows.
     *
     * @param sheet       the Excel sheet where the categories are written
     * @param category    the current category being processed
     * @param categoryMap the map of categories organized by parent ID
     * @param rowIdx      the current row index in the Excel sheet
     * @param level       the level of depth for the category in the tree
     */
    private void buildExcelTree(Sheet sheet, Category category, Map<Long, List<Category>> categoryMap, int[] rowIdx, int level) {
        Row row = sheet.createRow(rowIdx[0]++);
        row.createCell(level).setCellValue(category.getName());

        List<Category> children = categoryMap.get(category.getId());

        if (children != null && !children.isEmpty()) {
            for (Category child : children) {
                buildExcelTree(sheet, child, categoryMap, rowIdx, level + 1); // Уровень увеличивается на 1
            }
        }
    }
}