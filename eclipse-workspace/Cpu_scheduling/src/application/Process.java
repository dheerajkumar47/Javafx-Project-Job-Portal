package application;

import javafx.beans.property.*;

public class Process {
    private StringProperty pid;
    private DoubleProperty arrivalTime;
    private DoubleProperty burstTime;
    private IntegerProperty priority;
    private DoubleProperty waitingTime;
    private DoubleProperty departureTime;
    private DoubleProperty remainingTime;
    private DoubleProperty turnaroundTime;

    public Process(String pid, double arrivalTime, double burstTime) {
        this.pid = new SimpleStringProperty(pid);
        this.arrivalTime = new SimpleDoubleProperty(arrivalTime);
        this.burstTime = new SimpleDoubleProperty(burstTime);
        this.priority = new SimpleIntegerProperty(1);
        this.waitingTime = new SimpleDoubleProperty(0);
        this.departureTime = new SimpleDoubleProperty(0);
        this.remainingTime = new SimpleDoubleProperty(burstTime);
        this.turnaroundTime = new SimpleDoubleProperty(0);
    }

    public Process(String pid, double arrivalTime, double burstTime, int priority) {
        this.pid = new SimpleStringProperty(pid);
        this.arrivalTime = new SimpleDoubleProperty(arrivalTime);
        this.burstTime = new SimpleDoubleProperty(burstTime);
        this.priority = new SimpleIntegerProperty(priority);
        this.waitingTime = new SimpleDoubleProperty(0);
        this.departureTime = new SimpleDoubleProperty(0);
        this.remainingTime = new SimpleDoubleProperty(burstTime);
        this.turnaroundTime = new SimpleDoubleProperty(0);
    }

    public String getPid() {
        return pid.get();
    }

    public StringProperty pidProperty() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid.setValue(pid);
    }

    public double getArrivalTime() {
        return arrivalTime.get();
    }

    public DoubleProperty arrivalTimeProperty() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime.setValue(arrivalTime);
    }

    public double getBurstTime() {
        return burstTime.get();
    }

    public DoubleProperty burstTimeProperty() {
        return burstTime;
    }

    public void setBurstTime(double burstTime) {
        this.burstTime.setValue(burstTime);
    }

    public int getPriority() {
        return priority.get();
    }

    public IntegerProperty priorityProperty() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority.setValue(priority);
    }

    public double getWaitingTime() {
        return waitingTime.get();
    }

    public DoubleProperty waitingTimeProperty() {
        return waitingTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime.setValue(waitingTime);
    }

    public double getDepartureTime() {
        return departureTime.get();
    }

    public DoubleProperty departureTimeProperty() {
        return departureTime;
    }

    public void setDepartureTime(double departureTime) {
        this.departureTime.setValue(departureTime);
        // Calculate turnaround time
        double arrival = this.arrivalTime.get();
        double turnaround = departureTime - arrival;
        this.turnaroundTime.setValue(turnaround);
    }


    public double getRemainingTime() {
        return remainingTime.get();
    }

    public DoubleProperty remainingTimeProperty() {
        return remainingTime;
    }

    public void setRemainingTime(double remainingTime) {
        this.remainingTime.set(remainingTime);
    }

    public double getTurnaroundTime() {
        return turnaroundTime.get();
    }

    public DoubleProperty turnaroundTimeProperty() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(double turnaroundTime) {
        this.turnaroundTime.set(turnaroundTime);
    }

    @Override
    public String toString() {
        return "Process{" +
                "pid=" + pid +
                ", arrivalTime=" + arrivalTime +
                ", burstTime=" + burstTime +
                ", priority=" + priority +
                ", waitingTime=" + waitingTime +
                ", departureTime=" + departureTime +
                ", remainingTime=" + remainingTime +
                ", turnaroundTime=" + turnaroundTime +
                '}';
    }
}
