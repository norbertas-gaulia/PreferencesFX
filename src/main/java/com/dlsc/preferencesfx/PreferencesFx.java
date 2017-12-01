package com.dlsc.preferencesfx;

import com.dlsc.preferencesfx.util.StorageHandler;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.geometry.Side;
import javafx.scene.control.TreeItem;
import org.controlsfx.control.MasterDetailPane;

public class PreferencesFx extends MasterDetailPane {
  public static final String SELECTED_CATEGORY = "SELECTED_CATEGORY";

  public static final String DIVIDER_POSITION = "DIVIDER_POSITION";
  public static final String BREADCRUMB_DELIMITER = "_";
  public static final double DEFAULT_DIVIDER_POSITION = 0.2;
  public static final int DEFAULT_CATEGORY = 0;

  public static final int DEFAULT_PREFERENCES_WIDTH = 1000;
  public static final int DEFAULT_PREFERENCES_HEIGHT = 700;
  public static final int DEFAULT_PREFERENCES_POS_X = 700;
  public static final int DEFAULT_PREFERENCES_POS_Y = 500;

  public static final String WINDOW_WIDTH = "WINDOW_WIDTH";
  public static final String WINDOW_HEIGHT = "WINDOW_HEIGHT";
  public static final String WINDOW_POS_X = "WINDOW_POS_X";
  public static final String WINDOW_POS_Y = "WINDOW_POS_Y";

  private List<Category> categories;
  private CategoryTree categoryTree;
  private CategoryTreeBox categoryTreeBox;
  private StorageHandler storageHandler;
  private Category displayedCategory;

  private boolean persistWindowState = false;

  PreferencesFx(Class<?> saveClass, Category[] categories) {
    storageHandler = new StorageHandler(saveClass);
    this.categories = Arrays.asList(categories);
    setupParts();
    loadSettingValues();
    setupListeners();
    layoutParts();
  }

  private void loadSettingValues() {
    createBreadcrumbs(categories);
    categoryTree
        .getAllCategoriesFlatAsList()
        .stream()
        .map(Category::getGroups)     // get groups from categories
        .filter(Objects::nonNull)     // remove all null
        .flatMap(Collection::stream)
        .map(Group::getSettings)      // get settings from groups
        .filter(Objects::nonNull)     // remove all null
        .flatMap(Collection::stream)
        .collect(Collectors.toList())
        .forEach(setting -> setting.loadSettingValue(storageHandler));
  }

  private void createBreadcrumbs(List<Category> categories) {
    categories.forEach(category -> {
      if (!Objects.equals(category.getGroups(), null)) {
        category.getGroups().forEach(group -> group.addToBreadcrumb(category.getBreadcrumb()));
      }
      if (!Objects.equals(category.getChildren(), null)) {
        category.createBreadcrumbs(category.getChildren());
      }
    });
  }

  /**
   * Creates the Preferences window.
   *
   * @param saveClass  the class which the preferences are saved as
   *                   Must be unique to the application using the preferences
   * @param categories the items to be displayed in the TreeView
   * @return the preferences window
   */
  public static PreferencesFx of(Class<?> saveClass, Category... categories) {
    return new PreferencesFx(saveClass, categories);
  }

  private void setupParts() {
    categoryTree = new CategoryTree(this, categories);
    categoryTreeBox = new CategoryTreeBox(categoryTree);
  }

  private void layoutParts() {
    setDetailSide(Side.LEFT);
    setDetailNode(categoryTreeBox);
    // Load last selected category in TreeView.
    categoryTree.setSelectedCategoryById(storageHandler.loadSelectedCategory());
    TreeItem treeItem = (TreeItem) categoryTree.getSelectionModel().getSelectedItem();
    showCategory((Category) treeItem.getValue());
  }

  private void setupListeners() {
    // Whenever the divider position is changed, it's position is saved.
    dividerPositionProperty().addListener((observable, oldValue, newValue) ->
        storageHandler.saveDividerPosition(getDividerPosition())
    );
  }

  /**
   * Shows the category as its CategoryPane in the master node.
   *
   * @param category the category to be shown
   */
  public void showCategory(Category category) {
    displayedCategory = category;
    setMasterNode(category.getCategoryPane());
    // Sets the saved divider position.
    setDividerPosition(storageHandler.loadDividerPosition());
  }

  /**
   * Saves the current selected Category.
   */
  public void saveSelectedCategory() {
    TreeItem treeItem = (TreeItem) categoryTree.getSelectionModel().getSelectedItem();
    Category category;
    if (treeItem != null) {
      category = (Category) treeItem.getValue();
    } else {
      category = categories.get(DEFAULT_CATEGORY);
    }
    storageHandler.saveSelectedCategory(category.getId());
  }

  /**
   * Retrieves the category which has last been displayed in the CategoryPane.
   */
  public Category getDisplayedCategory() {
    return displayedCategory;
  }


  /**
   * Shows the PreferencesFX dialog.
   */
  public void show() {
    new PreferencesDialog(this, persistWindowState);
  }

  /**
   * Defines whether the state of the dialog window should be persisted or not.
   *
   * @param persist if true, the size, position and last selected item in the TreeView are
   *                being saved. When the dialog is showed again, it will be restored to
   *                the last saved state. Defaults to false.
   * @return this object for fluent API
   */
  public PreferencesFx persistWindowState(boolean persist) {
    this.persistWindowState = persist;
    return this;
  }

  public StorageHandler getStorageHandler() {
    return storageHandler;
  }

  public CategoryTree getCategoryTree() {
    return categoryTree;
  }
}