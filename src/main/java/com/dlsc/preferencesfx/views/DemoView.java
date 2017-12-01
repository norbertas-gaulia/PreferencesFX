package com.dlsc.preferencesfx.views;

import com.dlsc.preferencesfx.PreferencesFx;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DemoView extends VBox {
  private PreferencesFx preferencesFx;
  private MenuBar menuBar;
  private Menu menu;
  private MenuItem preferencesMenuItem;
  private RootPane rootPane;

  private Label welcomeLbl;
  private Label brightnessLbl;
  private Label nightModeLbl;
  private Label scalingLbl;
  private Label screenNameLbl;
  private Label resolutionLbl;
  private Label orientationLbl;
  private Label favoritesLbl;
  private Label fontSizeLbl;
  private Label lineSpacingLbl;


  public DemoView(PreferencesFx preferencesFx, RootPane rootPane) {
    this.preferencesFx = preferencesFx;
    this.rootPane = rootPane;

    initializeParts();
    layoutParts();
    setupBindings();
    setupEventHandlers();
  }

  private void initializeParts() {
    menuBar = new MenuBar();
    menu = new Menu("Edit");
    preferencesMenuItem = new MenuItem("Preferences");

    welcomeLbl = new Label();
    brightnessLbl = new Label();
    nightModeLbl = new Label();
    scalingLbl = new Label();
    screenNameLbl = new Label();
    resolutionLbl = new Label();
    orientationLbl = new Label();
    favoritesLbl = new Label();
    fontSizeLbl = new Label();
    lineSpacingLbl = new Label();
  }

  private void layoutParts() {
    // MenuBar
    menu.getItems().add(preferencesMenuItem);
    menuBar.getMenus().add(menu);

    // VBox with values
    VBox valueBox = new VBox(
        welcomeLbl,
        brightnessLbl,
        nightModeLbl,
        scalingLbl,
        screenNameLbl,
        resolutionLbl,
        orientationLbl,
        favoritesLbl,
        fontSizeLbl,
        lineSpacingLbl
    );
    valueBox.setSpacing(20);
    valueBox.setPadding(new Insets(20, 0, 0, 20));

    // VBox with descriptions
    VBox descriptionBox = new VBox(
        new Label("Welcome Text:"),
        new Label("Brightness:"),
        new Label("Night mode:"),
        new Label("Scaling:"),
        new Label("Screen name:"),
        new Label("Resolution:"),
        new Label("Orientation:"),
        new Label("Favorites:"),
        new Label("Font Size:"),
        new Label("Line Spacing:")
    );
    descriptionBox.setSpacing(20);
    descriptionBox.setPadding(new Insets(20, 0, 0, 20));

    // Put everything together
    getChildren().addAll(
        menuBar,
        new HBox(
            descriptionBox,
            valueBox
        )
    );
  }

  private void setupBindings() {
    welcomeLbl.textProperty().bind(rootPane.welcomeText);
    brightnessLbl.textProperty().bind(rootPane.brightness.asString().concat("%"));
    nightModeLbl.textProperty().bind(rootPane.nightMode.asString());
    scalingLbl.textProperty().bind(rootPane.scaling.asString());
    screenNameLbl.textProperty().bind(rootPane.screenName);
    resolutionLbl.textProperty().bind(rootPane.resolutionSelection);
    orientationLbl.textProperty().bind(rootPane.orientationSelection);
    favoritesLbl.textProperty().bind(Bindings.createStringBinding(
        () -> rootPane.favoritesSelection.stream().collect(Collectors.joining(", ")),
        rootPane.favoritesSelection
    ));
    fontSizeLbl.textProperty().bind(rootPane.fontSize.asString());
    lineSpacingLbl.textProperty().bind(rootPane.lineSpacing.asString());
  }

  private void setupEventHandlers() {
    preferencesMenuItem.setOnAction(e -> preferencesFx.show());

  }
}