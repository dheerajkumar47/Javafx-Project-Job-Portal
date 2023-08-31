package application;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    ComboBox<String> scheduleMethod;
    @FXML
    CheckBox preemptive;
    @FXML
    TextField pid;
    @FXML
    TextField arrivalTime;
    @FXML
    TextField burstTime;
    @FXML
    TextField priority;
    @FXML
    TextField timeQuantum;

    @FXML
    TableView<Process> table;
    @FXML
    TableColumn<Process, String> pidCol;
    @FXML
    TableColumn<Process, Double> arrivalTimeCol;
    @FXML
    TableColumn<Process, Double> burstTimeCol;
    @FXML
    TableColumn<Process, Double> waitingTimeCol;
    @FXML
    TableColumn<Process, Double> departureTimeCol;
    @FXML
    TableColumn<Process, Integer> priorityCol;

    @FXML
    TableColumn<Process, Integer> turnaroundTimeCol; // New Turnaround Time column

    @FXML
    HBox ganttChart;

    ObservableList<Process> processes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scheduleMethod.getItems().addAll("FCFS", "SJF", "Priority", "Round Robin");
        scheduleMethod.getSelectionModel().selectFirst();

        preemptive.setVisible(false);
        priority.setVisible(false);
        timeQuantum.setVisible(false);

        // Visible or Hidden Based On Method
        scheduleMethod.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            if (n.equals("FCFS")) {
                preemptive.setVisible(false);
                priority.setVisible(false);
                timeQuantum.setVisible(false);
            } else if (n.equals("SJF")) {
                preemptive.setVisible(true);
                priority.setVisible(false);
                timeQuantum.setVisible(false);
            } else if (n.equals("Priority")) {
                preemptive.setVisible(true);
                priority.setVisible(true);
                priority.setMaxWidth(priority.getMinWidth());
                timeQuantum.setVisible(false);
            } else if (n.equals("Round Robin")) {
                preemptive.setVisible(false);
                priority.setVisible(false);
                priority.setMaxWidth(0);
                timeQuantum.setVisible(true);
            }
        });

        pidCol.setCellValueFactory(new PropertyValueFactory<>("pid"));
        arrivalTimeCol.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        burstTimeCol.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        waitingTimeCol.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        departureTimeCol.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        turnaroundTimeCol.setCellValueFactory(new PropertyValueFactory<>("turnaroundTime")); // Set the cell value factory for the Turnaround Time column

        table.setEditable(true);
        table.setItems(processes);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        pidCol.setCellFactory(TextFieldTableCell.forTableColumn());
        arrivalTimeCol.setCellFactory(TextFieldTableCell.<Process, Double>forTableColumn(new DoubleStringConverter()));
        burstTimeCol.setCellFactory(TextFieldTableCell.<Process, Double>forTableColumn(new DoubleStringConverter()));
        priorityCol.setCellFactory(TextFieldTableCell.<Process, Integer>forTableColumn(new IntegerStringConverter()));

        pid.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) this.onAddProcess();
        });
        arrivalTime.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) this.onAddProcess();
        });
        burstTime.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) this.onAddProcess();
        });
        priority.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) this.onAddProcess();
        });

        timeQuantum.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) this.onCalculateAndDraw();
        });

        table.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.DELETE)) this.onDeleteProcesses();
        });
    }

    public void onCalculateAndDraw() {
        // Calculate turnaround time for each process
        for (Process process : table.getItems()) {
            int turnaroundTime = (int) (process.getDepartureTime() - process.getArrivalTime());
            process.setTurnaroundTime(turnaroundTime);
        }

        // Update the turnaround time column cell values
        turnaroundTimeCol.setCellValueFactory(cellData -> new SimpleIntegerProperty((int) cellData.getValue().getTurnaroundTime()).asObject());

        // Redraw the table to reflect the updated values
        table.refresh();

        double timeQuantumInput = Validator.validate_double(timeQuantum);
        if (timeQuantumInput <= 0 && scheduleMethod.getSelectionModel().getSelectedItem().equals("Round Robin")) {
            timeQuantum.getStyleClass().add("input-wrong");
            return;
        }
        timeQuantum.getStyleClass().removeAll("input-wrong");

        Stage stage = new Stage();
        stage.setTitle("Gantt Chart");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));

        StackPane layout = new StackPane();
        layout.setStyle("-fx-background-color: lightgray;");
        HBox ganttChart = new HBox();
        layout.setAlignment(Pos.CENTER);
        VBox ganttChartContainer = new VBox();
        ganttChartContainer.getChildren().add(ganttChart);
        ganttChartContainer.setAlignment(Pos.CENTER);

        layout.getChildren().add(ganttChartContainer);

        Scene scene = new Scene(layout, 850, 450);
        stage.setScene(scene);

        DoubleProperty leftPadding = new SimpleDoubleProperty(0.1 * scene.getWidth());
        ganttChartContainer.paddingProperty().bind(Bindings.createObjectBinding(() -> new Insets(0, 0, 0, leftPadding.doubleValue()), leftPadding));

        if (scheduleMethod.getSelectionModel().getSelectedItem().equals("FCFS")) {
            Scheduler.sortFCFS(ganttChart, processes, scene);
        } else if (scheduleMethod.getSelectionModel().getSelectedItem().equals("Priority")) {
            Scheduler.sortPriority(ganttChart, processes, preemptive.isSelected(), scene);
        } else if (scheduleMethod.getSelectionModel().getSelectedItem().equals("SJF")) {
            Scheduler.sortSJF(ganttChart, processes, preemptive.isSelected(), scene);
        } else if (scheduleMethod.getSelectionModel().getSelectedItem().equals("Round Robin")) {
            Scheduler.sortRoundRobin(ganttChart, processes, Double.parseDouble(timeQuantum.getText()), scene);
        }

        Label headingLabel = new Label("Gantt Chart");
        headingLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        headingLabel.setTextFill(Color.BLUE);

        VBox INFO = new VBox();
        double waitingTime = 0;
        for (Process p : processes)
            waitingTime += p.getWaitingTime();
        Label awt = new Label("Average Waiting Time = " + new DecimalFormat("###.###").format(waitingTime / processes.size()));
        awt.setFont(Font.font("System", FontWeight.BOLD, 12));
        awt.setAlignment(Pos.CENTER_LEFT);
        awt.setPadding(new Insets(70, 20, 0, 20));
        INFO.getChildren().add(awt);

        double turnAroundTime = 0;
        for (Process p : processes)
            turnAroundTime += (p.getDepartureTime() - p.getArrivalTime());
        Label atta = new Label("Average Turnaround Time = " + new DecimalFormat("###.###").format(turnAroundTime / processes.size()));
        atta.setFont(Font.font("System", FontWeight.BOLD, 12));
        atta.setAlignment(Pos.CENTER);
        atta.setPadding(new Insets(70, 20, 0, 20));

        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(100); // Set the preferred width of the button
        exitButton.setOnAction(event -> {
            stage.close(); // Close the stage when the exit button is clicked
        });


        HBox hbox = new HBox();
        hbox.setAlignment(Pos.BOTTOM_CENTER);
        hbox.setPadding(new Insets(20, 0, 10, 0)); // Set padding to create space from the bottom
        hbox.getChildren().addAll(awt, atta);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(headingLabel, hbox, INFO, exitButton);

        layout.getChildren().add(vbox);
        layout.setAlignment(Pos.CENTER);
        ganttChart.setAlignment(Pos.CENTER);
        ganttChartContainer.setAlignment(Pos.CENTER);

        stage.show();



    }

    public void onAddProcess() {
        String pidInput = Validator.validate_string(pid);
        double arrivalTimeInput = Validator.validate_double(arrivalTime);
        double burstTimeInput = Validator.validate_double(burstTime);
        int priorityInput = Validator.validate_int(priority);
        if (!scheduleMethod.getSelectionModel().getSelectedItem().equals("Priority")) priorityInput = 1;
        if (!pidInput.equals("-1") && arrivalTimeInput != -1 && burstTimeInput != -1 && priorityInput != -1) {
            processes.add(new Process(pidInput, arrivalTimeInput, burstTimeInput, priorityInput));
            pid.clear();
            arrivalTime.clear();
            burstTime.clear();
            priority.clear();
        }
        if (pidInput.equals("-1")) pid.getStyleClass().add("input-wrong");
        else pid.getStyleClass().removeAll("input-wrong");
        if (arrivalTimeInput == -1) arrivalTime.getStyleClass().add("input-wrong");
        else arrivalTime.getStyleClass().removeAll("input-wrong");
        if (burstTimeInput == -1) burstTime.getStyleClass().add("input-wrong");
        else burstTime.getStyleClass().removeAll("input-wrong");
        if (priorityInput == -1) priority.getStyleClass().add("input-wrong");
        else priority.getStyleClass().removeAll("input-wrong");
    }

    public void onDeleteProcesses() {
        table.getItems().removeAll(table.getSelectionModel().getSelectedItems());
    }

    public void resetColumn(TableColumn.CellEditEvent<Process, String> newCell) {

    }

    public void editPidCellEvent(TableColumn.CellEditEvent<Process, String> newCell) {
        Process selectedProcess = table.getSelectionModel().getSelectedItem();
        Process to = (Process) newCell.getTableView().getItems().get(newCell.getTablePosition().getRow());
        String pidInput = Validator.validate_string(newCell.getNewValue());
        if (!pidInput.equals("-1")) {
            selectedProcess.setPid(newCell.getNewValue());
        } else {
            newCell.getTableView().getItems().set(newCell.getTablePosition().getRow(), to);
        }
    }

    public void editPriorityCellEvent(TableColumn.CellEditEvent<Process, Integer> newCell) {
        Process selectedProcess = table.getSelectionModel().getSelectedItem();
        newCell.getTableView().getItems().get(newCell.getTablePosition().getRow());
        int priority = Validator.validate_int(newCell.getNewValue());
        if (priority >= 0) {
            selectedProcess.setPriority(newCell.getNewValue());
        } else {
            throw new NumberFormatException();
        }
    }

    public void editArrivalTimeCellEvent(TableColumn.CellEditEvent<Process, Double> newCell) {
        Process selectedProcess = table.getSelectionModel().getSelectedItem();
        newCell.getTableView().getItems().get(newCell.getTablePosition().getRow());
        double arrivalTime = Validator.validate_double(newCell.getNewValue());
        if (arrivalTime >= 0) {
            selectedProcess.setArrivalTime(newCell.getNewValue());
        } else {
            throw new NumberFormatException();
        }
    }

    public void editBurstTimeCellEvent(TableColumn.CellEditEvent<Process, Double> newCell) {
        Process selectedProcess = table.getSelectionModel().getSelectedItem();
        newCell.getTableView().getItems().get(newCell.getTablePosition().getRow());
        double burstTime = Validator.validate_double(newCell.getNewValue());
        if (burstTime >= 0) {
            selectedProcess.setBurstTime(newCell.getNewValue());
        } else {
            throw new NumberFormatException();
        }
    }
    
    public void generateData() {
        processes.clear();
        DataGenerator.generateData(processes);
    }
}
