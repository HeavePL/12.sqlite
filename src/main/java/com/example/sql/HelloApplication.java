package com.example.sql;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HelloApplication extends Application {

    static String url = "jdbc:mysql://localhost:3306/bazaamid";
    static String username = "root";
    static String password = "";
    public ObservableList SHOW_TABLES(){
        ObservableList tables = FXCollections.observableArrayList();;
        String query = "SHOW TABLES";


        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
             statement.executeQuery(query);
             ResultSet rs = statement.getResultSet();
             while (rs.next()){
                 tables.addAll(rs.getString(1));
             }
             rs.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tables;
    }
    public ObservableList DESCRIBE_TABLE(String table){
        ObservableList columns= FXCollections.observableArrayList();;
        String query = "DESCRIBE "+table;


        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            statement.executeQuery(query);
            ResultSet rs = statement.getResultSet();
            while (rs.next()){
                columns.addAll(rs.getString(1));
            }
            rs.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return columns;
    }
    public ObservableList DESCRIBE_TABLE_FOR_TYPE(String table){
        ObservableList types= FXCollections.observableArrayList();;
        String query = "DESCRIBE "+table;


        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            statement.executeQuery(query);
            ResultSet rs = statement.getResultSet();
            while (rs.next()){
                types.addAll(rs.getString(2));
            }
            rs.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return types;
    }
    public void WHERE(String column, String table, String where, TextArea textArea) {
        String query = "SELECT * FROM "+table+" WHERE "+column+" = '"+where+"'";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            statement.executeQuery(query);
            ResultSet rs = statement.getResultSet();

            ResultSetMetaData dane = rs.getMetaData();
            int columnCount = dane.getColumnCount();
            textArea.clear();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    textArea.appendText(dane.getColumnName(i) + ": " + rs.getString(i) + "\n");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public void CREATE_TABLE(String query,TextArea outputTA){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            outputTA.setText(" Table created");
        } catch (SQLException ex) {
            outputTA.setText("Error with sql");
            ex.printStackTrace();
        }
    }
    public void INSERT_INTO(String query, TextArea outputTA){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            outputTA.setText(" Table updated");
        } catch (SQLException ex) {
            outputTA.setText("Error with sql");
            ex.printStackTrace();
        }
    }

    public VBox addColumn(int col_amount, List<HBox> columnBoxes){
        VBox columnsVBox = new VBox(10);
        columnsVBox.setPadding(new Insets(10));
        for (int i = 0; i < col_amount; i++) {
            HBox columnBox = new HBox(10);
            Label columnNameLabel = new Label("COLUMN NAME:");
            TextField columnNameTextField = new TextField();
            Label columnTypeLabel = new Label("TYPE:");
            ComboBox<String> columnTypeComboBox = new ComboBox<>();
            columnTypeComboBox.getItems().addAll("INT", "VARCHAR(255)");
            CheckBox primaryKeyCheckBox = new CheckBox("PRIMARY KEY");
            CheckBox autoIncrementCheckBox = new CheckBox("AUTO INCREMENT");
            columnBox.getChildren().addAll(columnNameLabel, columnNameTextField, columnTypeLabel, columnTypeComboBox,
                    primaryKeyCheckBox, autoIncrementCheckBox);
            columnBoxes.add(columnBox);
            columnsVBox.getChildren().addAll(columnBox);
        }
    return columnsVBox;
    }

    public Group createMenu(Stage stage){
        Group root = new Group();
        Group buttonReturnGroup = new Group();
        Group buttonsGroup = new Group();

        Text dbnameText = new Text(stage.getWidth()/2-40,20,"DB name: bazaamid");
            root.getChildren().addAll(dbnameText);

        Button selectTable = new Button("Find in Table");
            selectTable.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent actionEvent) {
                    buttonReturnGroup.getChildren().clear();
                    ComboBox<String> tableChoiceBox = new ComboBox<>();
                        tableChoiceBox.setTooltip(new Tooltip("Select Table"));
                        tableChoiceBox.setLayoutY(100);
                        tableChoiceBox.setLayoutX(stage.getWidth()/6*2);
                        tableChoiceBox.prefWidth(100);
                        tableChoiceBox.prefHeight(70);
                        tableChoiceBox.setItems(SHOW_TABLES());

                    Label tableNameLabel = new Label("Select Table: ");
                        tableNameLabel.setLayoutY(tableChoiceBox.getLayoutY());
                        tableNameLabel.setLayoutX(tableChoiceBox.getLayoutX()-75);

                    ComboBox<String> columnschoiceBox = new ComboBox<>();
                        columnschoiceBox.setTooltip(new Tooltip("Select Column: "));
                        columnschoiceBox.setLayoutY(150);
                        columnschoiceBox.setLayoutX(stage.getWidth()/6*2);
                        columnschoiceBox.prefWidth(100);
                        columnschoiceBox.prefHeight(70);
                    tableChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                if(!tableChoiceBox.getValue().toString().isEmpty()){

                                    columnschoiceBox.setItems(DESCRIBE_TABLE(tableChoiceBox.getValue().toString()));
                                }
                            }
                        });
                    Label columnsLabel = new Label("Select Column: ");
                        columnsLabel.setLayoutY(columnschoiceBox.getLayoutY());
                        columnsLabel.setLayoutX(columnschoiceBox.getLayoutX()-85);

                    TextField whereTF = new TextField();
                        whereTF.setLayoutY(200);
                        whereTF.setLayoutX(stage.getWidth()/6*2);
                        whereTF.prefWidth(100);
                        whereTF.prefHeight(70);

                    Label whereLabel = new Label("WHERE column = ");
                        whereLabel.setLayoutY(whereTF.getLayoutY());
                        whereLabel.setLayoutX(whereTF.getLayoutX()-100);

                    Button searchBtn = new Button("Search");
                        searchBtn.setLayoutY(230);
                        searchBtn.setLayoutX(stage.getWidth()/6*2);


                    TextArea searchReturnText = new TextArea();
                        searchReturnText.setEditable(false);
                        searchReturnText.setPrefWidth(stage.getWidth());
                        searchReturnText.setLayoutY(260);
                        searchReturnText.setLayoutX(stage.getWidth()/6*0);

                    searchBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            if(tableChoiceBox.getValue().toString().isEmpty() ) {
                                searchReturnText.setText("Select Table");
                            } else
                            if(columnschoiceBox.getValue().toString().isEmpty()) {
                                searchReturnText.setText("Select Column");
                            } else
                            if(whereTF.toString().isEmpty()){
                                searchReturnText.setText("Input a condition");
                            }else{
                                WHERE(columnschoiceBox.getValue().toString(),tableChoiceBox.getValue().toString(),whereTF.getText().toString(),searchReturnText);
                            }

                            }
                    });


                    buttonReturnGroup.getChildren().addAll(tableChoiceBox, tableNameLabel, columnschoiceBox, columnsLabel, whereLabel, whereTF, searchBtn, searchReturnText);

                }
            });

            selectTable.setLayoutX(stage.getWidth()/6*1);
            selectTable.setLayoutY(50);
            selectTable.setMinWidth(100);
            selectTable.setMaxWidth(100);
            selectTable.setMaxHeight(40);

        Button createTable = new Button("Create Table");
            createTable.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    buttonReturnGroup.getChildren().clear();

                    TextField tableNameTextField = new TextField("Table Name");
                        tableNameTextField.setLayoutY(100);
                        tableNameTextField.setLayoutX(stage.getWidth()/6*2);
                        tableNameTextField.setMaxWidth(100);
                        tableNameTextField.setMinWidth(100);
                    Label tableNameLabel = new Label("Table Name: ");
                        tableNameLabel.setLayoutY(tableNameTextField.getLayoutY());
                        tableNameLabel.setLayoutX(tableNameTextField.getLayoutX()-75);
                    TextField columnAmmountTextField = new TextField();
                        columnAmmountTextField.setLayoutY(150);
                        columnAmmountTextField.setLayoutX(stage.getWidth()/6*2);
                        columnAmmountTextField.setMaxWidth(100);
                        columnAmmountTextField.setMinWidth(100);
                    Label columnAmountLabel = new Label("Columns Ammount: ");
                        columnAmountLabel.setLayoutY(columnAmmountTextField.getLayoutY());
                        columnAmountLabel.setLayoutX(columnAmmountTextField.getLayoutX()-120);

                    Button setColumnsBtn = new Button("Set Columns");
                        setColumnsBtn.setLayoutY(columnAmmountTextField.getLayoutY()+30);
                        setColumnsBtn.setLayoutX(columnAmountLabel.getLayoutX());
                    VBox columnsVBox = new VBox(10);
                        columnsVBox.setLayoutY(setColumnsBtn.getLayoutY()+80);
                    TextArea errorsTA = new TextArea();
                        errorsTA.setEditable(false);
                        errorsTA.setPrefWidth(stage.getWidth());
                        errorsTA.setPrefHeight(40);
                        errorsTA.setLayoutY(columnsVBox.getLayoutY()-40);

                    List<HBox> columnBoxes = new ArrayList<>();
                    buttonReturnGroup.getChildren().addAll(columnsVBox);
                    setColumnsBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            try {
                                columnsVBox.getChildren().clear();
                                columnsVBox.getChildren().addAll(addColumn(Integer.parseInt(columnAmmountTextField.getText()),columnBoxes));

                                if(buttonReturnGroup.getLayoutBounds().getMaxY()>=stage.getHeight()-100)
                                {
                                    stage.setHeight(stage.getHeight()+50);
                                }
                            } catch (NumberFormatException e){
                                e.printStackTrace();
                                errorsTA.setText("Wrong format of columns amount");
                            } catch (Exception e ){

                                e.printStackTrace();
                            }



                            }
                        });


                    Button submitBtn = new Button("Create");
                        submitBtn.setLayoutY(setColumnsBtn.getLayoutY());
                        submitBtn.setLayoutX(setColumnsBtn.getLayoutX()+100);
                        submitBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                StringBuilder queryBuilder = new StringBuilder("CREATE TABLE ");
                                String tableName = tableNameTextField.getText().trim();
                                tableName = tableName.replaceAll(" ","_");
                                if (tableName.isEmpty()) {
                                    errorsTA.setText("Wrong name for table");
                                    return;
                                }
                                queryBuilder.append(tableName).append(" (");
                                for (int i = 0; i < columnBoxes.size(); i++) {
                                    HBox columnBox = columnBoxes.get(i);
                                    TextField columnNameTextField = (TextField) columnBox.getChildren().get(1);
                                    ComboBox<String> columnTypeComboBox = (ComboBox<String>) columnBox.getChildren().get(3);
                                    CheckBox primaryKeyCheckBox = (CheckBox) columnBox.getChildren().get(4);
                                    CheckBox autoIncrementCheckBox = (CheckBox) columnBox.getChildren().get(5);

                                    String columnName = columnNameTextField.getText().trim();
                                    columnName = columnName.replaceAll(" ","_");
                                    if (columnName.isEmpty()) {
                                        errorsTA.setText("Wrong name for column");
                                        return;
                                    }
                                    String columnType = columnTypeComboBox.getValue();
                                    if (columnType == null || columnType.isEmpty()) {
                                        errorsTA.setText("Select type of column");
                                        return;
                                    }
                                    queryBuilder.append(columnName).append(" ").append(columnType).append(" ");
                                    if (primaryKeyCheckBox.isSelected()) {
                                        queryBuilder.append("PRIMARY KEY ");
                                    }

                                    if (autoIncrementCheckBox.isSelected()) {
                                        queryBuilder.append("AUTO_INCREMENT ");
                                    }
                                    queryBuilder.append(",");
                                }
                                queryBuilder.deleteCharAt(queryBuilder.length() - 1);
                                queryBuilder.append(");");

                                CREATE_TABLE(queryBuilder.toString(),errorsTA);

                            }
                        });
                    buttonReturnGroup.getChildren().addAll(errorsTA,columnAmmountTextField, submitBtn,setColumnsBtn,tableNameLabel,tableNameTextField,columnAmountLabel);
                }
            });
            createTable.setLayoutX(stage.getWidth()/6*2);
            createTable.setLayoutY(50);
            createTable.setMinWidth(100);
            createTable.setMaxWidth(100);
            createTable.setMaxHeight(40);

        Button selectRecord = new Button("Add Record");
            selectRecord.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    buttonReturnGroup.getChildren().clear();
                    ComboBox<String> tableChoiceBox = new ComboBox<>();
                    tableChoiceBox.setTooltip(new Tooltip("Select Table"));
                    tableChoiceBox.setLayoutY(100);
                    tableChoiceBox.setLayoutX(stage.getWidth()/6*2);
                    tableChoiceBox.prefWidth(100);
                    tableChoiceBox.prefHeight(70);
                    tableChoiceBox.setItems(SHOW_TABLES());

                    Label tableNameLabel = new Label("Select Table: ");
                    tableNameLabel.setLayoutY(tableChoiceBox.getLayoutY());
                    tableNameLabel.setLayoutX(tableChoiceBox.getLayoutX()-75);

                    ComboBox<String> columnschoiceBox = new ComboBox<>();
                        columnschoiceBox.setTooltip(new Tooltip("Select Column: "));
                        columnschoiceBox.setLayoutY(150);
                        columnschoiceBox.setLayoutX(stage.getWidth()/6*2);
                        columnschoiceBox.prefWidth(100);
                        columnschoiceBox.prefHeight(70);



                    Label columnsLabel = new Label("Select Column: ");
                        columnsLabel.setLayoutY(columnschoiceBox.getLayoutY());
                        columnsLabel.setLayoutX(columnschoiceBox.getLayoutX()-85);



                    TextField valueTF = new TextField();
                    valueTF.setLayoutY(200);
                    valueTF.setLayoutX(stage.getWidth()/6*2);
                    valueTF.prefWidth(100);
                    valueTF.prefHeight(70);

                    Label valueLabel = new Label("Value ");
                    valueLabel.setLayoutY(valueTF.getLayoutY());
                    valueLabel.setLayoutX(valueTF.getLayoutX()-100);

                    Button addBtn = new Button("Add");
                    addBtn.setLayoutY(260);
                    addBtn.setLayoutX(stage.getWidth()/6*2);

                    TextField whereTF = new TextField();
                    whereTF.setLayoutY(230);
                    whereTF.setLayoutX(stage.getWidth()/6*2+100);
                    whereTF.prefWidth(100);
                    whereTF.prefHeight(70);

                    Label whereLabel = new Label("WHERE column = ");
                    whereLabel.setLayoutY(whereTF.getLayoutY());
                    whereLabel.setLayoutX(whereTF.getLayoutX()-200);

                    ComboBox<String> columnschoiceBoxWhere = new ComboBox<>();
                        columnschoiceBoxWhere.setLayoutY(whereTF.getLayoutY());
                        columnschoiceBoxWhere.setLayoutX(whereTF.getLayoutX()-100);

                    TextArea searchReturnText = new TextArea();
                    searchReturnText.setEditable(false);
                    searchReturnText.setPrefWidth(stage.getWidth());
                    searchReturnText.setLayoutY(300);
                    searchReturnText.setLayoutX(stage.getWidth()/6*0);

                    tableChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            if(!tableChoiceBox.getValue().toString().isEmpty()){

                                columnschoiceBox.setItems(DESCRIBE_TABLE(tableChoiceBox.getValue().toString()));
                                columnschoiceBoxWhere.setItems(columnschoiceBox.getItems());

                            }
                        }
                    });
                    addBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            String query;
                            if(tableChoiceBox.getValue().toString().isEmpty() ) {
                                searchReturnText.setText("Select Table");
                            } else
                            if(columnschoiceBox.getValue().toString().isEmpty()) {
                                searchReturnText.setText("Select Column");
                            } else
                            if(valueTF.toString().isEmpty()){
                                searchReturnText.setText("Input a value");
                            }else{
                                if(whereTF.getText().toString().isEmpty()){
                                    query = "INSERT INTO "+tableChoiceBox.getValue().toString()+" ( "+columnschoiceBox.getValue().toString()+") VALUES ('"+valueTF.getText().toString()+"')";
                                    INSERT_INTO(query,searchReturnText);

                                }else if (columnschoiceBoxWhere.getValue().toString().isEmpty()){
                                    searchReturnText.setText("Select columnt to WHERE clause");
                                }else {
                                    query = "UPDATE " +tableChoiceBox.getValue().toString() +" SET "+columnschoiceBox.getValue().toString() +" = '"+valueTF.getText().toString()+ "' WHERE " +columnschoiceBoxWhere.getValue().toString()+"='"+ whereTF.getText().toString()+"'";
                                    INSERT_INTO(query,searchReturnText);
                                }
                            }

                        }
                    });


                    buttonReturnGroup.getChildren().addAll(columnschoiceBoxWhere,whereTF,whereLabel,tableChoiceBox, tableNameLabel, columnschoiceBox, columnsLabel, valueLabel, valueTF, addBtn, searchReturnText);

                }

            });
            selectRecord.setLayoutX(stage.getWidth()/6*3);
            selectRecord.setLayoutY(50);
            selectRecord.setMinWidth(100);
            selectRecord.setMaxWidth(100);
            selectRecord.setMaxHeight(40);

        Button addRecord = new Button("Exit");
            addRecord.setLayoutX(stage.getWidth()/6*4);
            addRecord.setLayoutY(50);
            addRecord.setMinWidth(100);
            addRecord.setMaxWidth(100);
            addRecord.setMaxHeight(40);
            addRecord.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.exit(1);
                }
            });


        buttonsGroup.getChildren().addAll(addRecord,selectRecord,selectTable,createTable);
        root.getChildren().addAll(buttonReturnGroup,buttonsGroup);
        return root;
    }

    public Group addTable(Scene scene, Group order){

        TextField tableName = new TextField("Table Name");
            tableName.setLayoutY(100);
            tableName.setLayoutX(scene.getWidth()/6*3);
            tableName.setMaxWidth(100);
            tableName.setMinWidth(100);
        Text tableNameText = new Text("Table Name:");
            tableNameText.setY(100);
            tableNameText.setX(tableName.getWidth()-150);

        order.getChildren().clear();
        order.getChildren().addAll(tableNameText,tableName);
        return order;
    }


    @Override
    public void start(Stage stage) throws IOException {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        Group menu = createMenu(stage);
        root.getChildren().addAll(menu);









    }

    public static void main(String[] args) {
        launch();
    }
}