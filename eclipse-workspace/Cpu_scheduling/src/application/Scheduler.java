package application;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.*;

public class Scheduler {
    private static void resetProcesses(ObservableList<Process> processes) {
        for (Process x : processes) {
            x.setRemainingTime(x.getBurstTime());
            x.setDepartureTime(-1);
            x.setWaitingTime(-1);
        }
    }

    public static void sortFCFS(HBox ganttChart, ObservableList<Process> P, Scene scene) {
        ObservableList<Process> processes = FXCollections.observableArrayList(P);
        resetProcesses(processes);
        processes.sort((o1, o2) -> {

            return (o1.getArrivalTime() > o2.getArrivalTime()) ? 1 : 0;
        });
        ObservableList<Process> arrivedProcesses = FXCollections.observableArrayList();
        for (Process p : processes)
			p.getBurstTime();

        int finished = 0;
        double timer = 0;
        GanttChart gantt = new GanttChart();
        for (Process process : processes)
            if (timer >= process.getArrivalTime())
                arrivedProcesses.add(process);
        int i = 0;
        while (arrivedProcesses.size() > 0 || finished < processes.size()) {
            while (arrivedProcesses.size() > 0) {
                processes.get(i).setDepartureTime(timer + processes.get(i).getBurstTime());
                processes.get(i).setWaitingTime(timer - processes.get(i).getArrivalTime());

                gantt.addCell(new GanttChartCell(timer, -1, arrivedProcesses.get(0).getPid()));
                arrivedProcesses.remove(0);

                timer += processes.get(i).getBurstTime();
                i++;
                finished++;
            }
            for (int j = i; j < processes.size(); j++) {
                if (timer >= processes.get(j).getArrivalTime()) {
                    arrivedProcesses.add(processes.get(j));
                }
            }
            if (arrivedProcesses.isEmpty() && finished < processes.size()) {
                gantt.addCell(new GanttChartCell(timer, 0, "IDLE"));
                timer = processes.get(i).getArrivalTime();
            }

        }
        gantt.addCell(new GanttChartCell(timer, -1, processes.get(i-1).getPid()));

        gantt.draw(ganttChart, scene);

    }

    private static void sortPriorityNonPreemptive(HBox ganttChart, ObservableList<Process> processes, Scene scene, double totalTime) {
        ObservableList<Process> arrivedProcesses = FXCollections.observableArrayList();
        int finished = 0;
        double timer = 0;
        GanttChart gantt = new GanttChart();
        for (Process process : processes)
            if (timer >= process.getArrivalTime())
                arrivedProcesses.add(process);
        int i = 0;
        Process nextProcess = arrivedProcesses.get(0);
        while (arrivedProcesses.size() > 0 || finished < processes.size()) {
            nextProcess = arrivedProcesses.get(0);
            for (int j = 0; j < arrivedProcesses.size(); j++)
                if (arrivedProcesses.get(j).getPriority() < nextProcess.getPriority())
                    nextProcess = arrivedProcesses.get(j);

            nextProcess.setDepartureTime(timer + nextProcess.getBurstTime());
            nextProcess.setWaitingTime(timer - nextProcess.getArrivalTime());
            nextProcess.setRemainingTime(0);

            gantt.addCell(new GanttChartCell(timer, -1, nextProcess.getPid()));
            arrivedProcesses.remove(nextProcess);

            timer += nextProcess.getBurstTime();
            i++;
            finished++;
            for (int j = 0; j < processes.size(); j++) {
                if (timer >= processes.get(j).getArrivalTime() && processes.get(j).getRemainingTime() > 0 && !arrivedProcesses.contains(processes.get(j))) {
                    arrivedProcesses.add(processes.get(j));
                }
            }
            if (arrivedProcesses.isEmpty() && finished < processes.size()) {
                gantt.addCell(new GanttChartCell(timer, 0, "IDLE"));
                timer = processes.get(i).getArrivalTime();
            }
            for (int j = 0; j < processes.size(); j++) {
                if (timer >= processes.get(j).getArrivalTime() && processes.get(j).getRemainingTime() > 0 && !arrivedProcesses.contains(processes.get(j))) {
                    arrivedProcesses.add(processes.get(j));
                }
            }

        }
        gantt.addCell(new GanttChartCell(timer, -1, nextProcess.getPid()));

        gantt.draw(ganttChart, scene);
    }

    private static void sortPriorityPreemptive(HBox ganttChart, ObservableList<Process> processes, Scene scene, double totalTime, ObservableList<Process> P) {
        Process nextProcess = processes.get(0);
        int nextProcessIndex = 0;
        double timer = 0;
        boolean flag = false;
        int finished = 0;
        GanttChart gantt = new GanttChart();
        ObservableList<Process> arrivedProcesses = FXCollections.observableArrayList();
        for (int i = 0; i < processes.size(); i++)
            if (processes.get(i).getArrivalTime() <= timer)
                arrivedProcesses.add(processes.get(i));

        while (arrivedProcesses.size() > 0 || finished < processes.size()) {
            nextProcess = arrivedProcesses.get(0);
            nextProcessIndex = processes.indexOf(nextProcess);
            for (int j = 0; j < arrivedProcesses.size(); j++) {
                if (arrivedProcesses.get(j).getPriority() < nextProcess.getPriority()) {
                    nextProcess = arrivedProcesses.get(j);
                    nextProcessIndex = processes.indexOf(nextProcess);
                }
            }


            if (nextProcess.getRemainingTime() == nextProcess.getBurstTime())
            gantt.addCell(new GanttChartCell(timer, -1, nextProcess.getPid()));
            if (arrivedProcesses.size() == processes.size() || flag) {
                timer += nextProcess.getRemainingTime();
                nextProcess.setRemainingTime(0);
                arrivedProcesses.remove(nextProcess);
                flag = true;
            } else {
                Process highestNextRemaining = processes.get(0);
                boolean selectedMax = false;
                for (int i = 0; i < processes.size(); i++) {
                    if (processes.get(i).getPriority() < highestNextRemaining.getPriority() && processes.get(i).getRemainingTime() > 0 && processes.get(i) != nextProcess) {
                        if (((selectedMax && highestNextRemaining.getArrivalTime() > processes.get(i).getArrivalTime()) || !selectedMax) && !arrivedProcesses.contains(processes.get(i))) {
                            highestNextRemaining = processes.get(i);
                            selectedMax = true;
                        }

                    }
                }
                if (highestNextRemaining.getRemainingTime() == 0) {
                    selectedMax = false;
                }

                if (highestNextRemaining.getArrivalTime() >= timer + nextProcess.getBurstTime() || highestNextRemaining == nextProcess || !selectedMax) { // Complete untill it finishes
                    timer += nextProcess.getRemainingTime();
                    nextProcess.setRemainingTime(0);
                } else { // Stop and compare
                    double diff = highestNextRemaining.getArrivalTime() - timer;
                    timer = highestNextRemaining.getArrivalTime();
                    nextProcess.setRemainingTime(nextProcess.getRemainingTime() - diff);
                }

            }
            if (nextProcess.getRemainingTime() == 0) {
                finished++;
                processes.get(nextProcessIndex).setRemainingTime(0);
                processes.get(nextProcessIndex).setDepartureTime(timer);
                processes.get(nextProcessIndex).setWaitingTime(processes.get(nextProcessIndex).getDepartureTime() - processes.get(nextProcessIndex).getArrivalTime() - processes.get(nextProcessIndex).getBurstTime());
                arrivedProcesses.remove(nextProcess);
            }

            for (int j = arrivedProcesses.size(); j < processes.size() && !flag; j++)
                if (processes.get(j).getArrivalTime() <= timer && processes.get(j).getRemainingTime() > 0 && !arrivedProcesses.contains(processes.get(j)))
                    arrivedProcesses.add(processes.get(j));

            if (finished < processes.size() && arrivedProcesses.size() == 0) {
                Process nextArrivedProcess = processes.get(0);
                boolean selectedNext = false;
                for (int i = 0; i < processes.size(); i++) {
                    if (processes.get(i).getRemainingTime() > 0) {
                        if (!selectedNext) {
                            nextArrivedProcess = processes.get(i);
                            selectedNext = true;
                        } else {
                            if (nextArrivedProcess.getArrivalTime() > processes.get(i).getArrivalTime()) {
                                nextArrivedProcess = processes.get(i);
                            }
                        }
                    }
                }
                if (nextArrivedProcess.getRemainingTime() == 0) {
                    selectedNext = false;
                }
                if (selectedNext) {
                    gantt.addCell(new GanttChartCell(timer, 0, "IDLE"));
                    timer = nextArrivedProcess.getArrivalTime();
                }
            }
            for (int j = arrivedProcesses.size(); j < processes.size() && !flag; j++)
                if (processes.get(j).getArrivalTime() <= timer && processes.get(j).getRemainingTime() > 0 && !arrivedProcesses.contains(processes.get(j)))
                    arrivedProcesses.add(processes.get(j));
        }
        nextProcess.setDepartureTime(timer);
        gantt.addCell(new GanttChartCell(timer, -1, nextProcess.getPid()));

        gantt.draw(ganttChart, scene);

        for (int i = 0; i < processes.size(); i++) {
            P.get(P.indexOf(processes.get(i))).setDepartureTime(processes.get(i).getDepartureTime());
            P.get(P.indexOf(processes.get(i))).setWaitingTime(processes.get(i).getWaitingTime());
        }
    }

    public static void sortPriority(HBox ganttChart, ObservableList<Process> P, boolean isPreemptive, Scene scene) {
        ObservableList<Process> processes = FXCollections.observableArrayList(P);
        resetProcesses(processes);
        double totalTime = 0;
        for (Process p : processes)
            totalTime += p.getBurstTime();
        processes.sort((o1, o2) -> {
            if (o1.getArrivalTime() == o2.getArrivalTime())
                return (o1.getPriority() > o2.getPriority()) ? 1 : 0;
            return (o1.getArrivalTime() > o2.getArrivalTime()) ? 1 : 0;
        });

        if (!isPreemptive) {
            sortPriorityNonPreemptive(ganttChart, processes, scene, totalTime);

        } else {
            sortPriorityPreemptive(ganttChart, processes, scene, totalTime,P);
        }
    }

    private static void sortSJFNonPreemptive(HBox ganttChart, ObservableList<Process> processes, Scene scene, double totalTime) {
        ObservableList<Process> arrivedProcesses = FXCollections.observableArrayList();
        int finished = 0;
        GanttChart gantt = new GanttChart();
        double timer = 0;
        for (Process process : processes)
            if (timer >= process.getArrivalTime())
                arrivedProcesses.add(process);
        int i = 0;
        Process nextProcess = arrivedProcesses.get(0);
        while (arrivedProcesses.size() > 0 || finished < processes.size()) {
            nextProcess = arrivedProcesses.get(0);
            for (int j = 0; j < arrivedProcesses.size(); j++)
                if (arrivedProcesses.get(j).getBurstTime() < nextProcess.getBurstTime())
                    nextProcess = arrivedProcesses.get(j);

            nextProcess.setDepartureTime(timer + nextProcess.getBurstTime());
            nextProcess.setWaitingTime(timer - nextProcess.getArrivalTime());
            nextProcess.setRemainingTime(0);

            gantt.addCell(new GanttChartCell(timer, -1, nextProcess.getPid()));
            arrivedProcesses.remove(nextProcess);

            timer += nextProcess.getBurstTime();
            i++;
            finished++;
            for (int j = 0; j < processes.size(); j++) {
                if (timer >= processes.get(j).getArrivalTime() && processes.get(j).getRemainingTime() > 0 && !arrivedProcesses.contains(processes.get(j))) {
                    arrivedProcesses.add(processes.get(j));
                }
            }
            if (arrivedProcesses.isEmpty() && finished < processes.size()) {
                gantt.addCell(new GanttChartCell(timer, 0, "IDLE"));
                timer = processes.get(i).getArrivalTime();
            }
            for (int j = 0; j < processes.size(); j++) {
                if (timer >= processes.get(j).getArrivalTime() && processes.get(j).getRemainingTime() > 0 && !arrivedProcesses.contains(processes.get(j))) {
                    arrivedProcesses.add(processes.get(j));
                }
            }

        }
        gantt.addCell(new GanttChartCell(timer, -1, nextProcess.getPid()));
        gantt.draw(ganttChart, scene);
    }

    private static void sortSJFPreemptive(HBox ganttChart, ObservableList<Process> processes, Scene scene, double totalTime) {
        Process nextProcess = processes.get(0);
        int nextProcessIndex = 0;
        double timer = 0;
        boolean flag = false;
        int finished = 0;
        GanttChart gantt = new GanttChart();
        ObservableList<Process> arrivedProcesses = FXCollections.observableArrayList();
        for (int i = 0; i < processes.size(); i++)
            if (processes.get(i).getArrivalTime() <= timer)
                arrivedProcesses.add(processes.get(i));

        while (arrivedProcesses.size() > 0 || finished < processes.size()) {
            nextProcess = arrivedProcesses.get(0);
            nextProcessIndex = processes.indexOf(nextProcess);
            for (int j = 0; j < arrivedProcesses.size(); j++) {
                if (arrivedProcesses.get(j).getRemainingTime() < nextProcess.getRemainingTime()) {
                    nextProcess = arrivedProcesses.get(j);
                    nextProcessIndex = processes.indexOf(nextProcess);
                }
            }


            if (nextProcess.getRemainingTime() == nextProcess.getBurstTime())
            gantt.addCell(new GanttChartCell(timer, -1, nextProcess.getPid()));
            if (arrivedProcesses.size() == processes.size() || flag) {
                timer += nextProcess.getRemainingTime();
                nextProcess.setRemainingTime(0);
                arrivedProcesses.remove(nextProcess);
                flag = true;
            } else {
                Process highestNextRemaining = processes.get(0);
                boolean selectedMax = false;
                for (int i = 0; i < processes.size(); i++) {
                    if (processes.get(i).getRemainingTime() < highestNextRemaining.getRemainingTime() && processes.get(i).getRemainingTime() > 0 && processes.get(i) != nextProcess) {
                        if (((selectedMax && highestNextRemaining.getArrivalTime() > processes.get(i).getArrivalTime()) || !selectedMax) && !arrivedProcesses.contains(processes.get(i))) {
                            highestNextRemaining = processes.get(i);
                            selectedMax = true;
                        }

                    }
                }
                if (highestNextRemaining.getRemainingTime() == 0) {
                    selectedMax = false;
                }

                if (highestNextRemaining.getArrivalTime() >= timer + nextProcess.getBurstTime() || highestNextRemaining == nextProcess || !selectedMax) { // Complete untill it finishes
                    timer += nextProcess.getRemainingTime();
                    nextProcess.setRemainingTime(0);
                } else { // Stop and compare
                    double diff = highestNextRemaining.getArrivalTime() - timer;
                    timer = highestNextRemaining.getArrivalTime();
                    nextProcess.setRemainingTime(nextProcess.getRemainingTime() - diff);
                }

            }
            if (nextProcess.getRemainingTime() == 0) {
                finished++;
                processes.get(nextProcessIndex).setRemainingTime(0);
                processes.get(nextProcessIndex).setDepartureTime(timer);
                processes.get(nextProcessIndex).setWaitingTime(processes.get(nextProcessIndex).getDepartureTime() - processes.get(nextProcessIndex).getArrivalTime() - processes.get(nextProcessIndex).getBurstTime());
                arrivedProcesses.remove(nextProcess);
            }

            for (int j = arrivedProcesses.size(); j < processes.size() && !flag; j++)
                if (processes.get(j).getArrivalTime() <= timer && processes.get(j).getRemainingTime() > 0 && !arrivedProcesses.contains(processes.get(j)))
                    arrivedProcesses.add(processes.get(j));

            if (finished < processes.size() && arrivedProcesses.size() == 0) {
                Process nextArrivedProcess = processes.get(0);
                boolean selectedNext = false;
                for (int i = 0; i < processes.size(); i++) {
                    if (processes.get(i).getRemainingTime() > 0) {
                        if (!selectedNext) {
                            nextArrivedProcess = processes.get(i);
                            selectedNext = true;
                        } else {
                            if (nextArrivedProcess.getArrivalTime() > processes.get(i).getArrivalTime()) {
                                nextArrivedProcess = processes.get(i);
                            }
                        }
                    }
                }
                if (nextArrivedProcess.getRemainingTime() == 0) {
                    selectedNext = false;
                }
                if (selectedNext) {
                    gantt.addCell(new GanttChartCell(timer, 0, "IDLE"));
                    timer = nextArrivedProcess.getArrivalTime();
                }
            }

            for (int j = arrivedProcesses.size(); j < processes.size() && !flag; j++)
                if (processes.get(j).getArrivalTime() <= timer && processes.get(j).getRemainingTime() > 0 && !arrivedProcesses.contains(processes.get(j)))
                    arrivedProcesses.add(processes.get(j));

        }
        nextProcess.setDepartureTime(timer);
        gantt.addCell(new GanttChartCell(timer, -1, nextProcess.getPid()));

        gantt.draw(ganttChart, scene);
    }

    public static void sortSJF(HBox ganttChart, ObservableList<Process> P, boolean isPreemptive, Scene scene) {
        ObservableList<Process> processes = FXCollections.observableArrayList(P);
        resetProcesses(processes);
        double totalTime = 0;
        for (Process p : processes)
            totalTime += p.getBurstTime();
        processes.sort((o1, o2) -> {
            if (o1.getArrivalTime() == o2.getArrivalTime())
                return (o1.getBurstTime() > o2.getBurstTime()) ? 1 : 0;
            return (o1.getArrivalTime() > o2.getArrivalTime()) ? 1 : 0;
        });

        if (!isPreemptive) {
            sortSJFNonPreemptive(ganttChart, processes, scene, totalTime);

        } else {
            sortSJFPreemptive(ganttChart, processes, scene, totalTime);
        }
    }

    @SuppressWarnings("unused")
	public static void sortRoundRobin(HBox ganttChart, ObservableList<Process> P, double timeQuantum, Scene scene) {
        ObservableList<Process> processes = FXCollections.observableArrayList(P);
        resetProcesses(processes);
        processes.sort((o1, o2) -> {
            return (o1.getArrivalTime() > o2.getArrivalTime()) ? 1 : 0;
        });
        for (Process p : processes) {
		}

        Process nextProcess = processes.get(0);
        double timer = 0;
        boolean flag = false;
        int finished = 0;
        boolean firstLoop = true;
        GanttChart gantt = new GanttChart();
        ObservableList<Process> arrivedProcesses = FXCollections.observableArrayList();
        for (int i = 0; i < processes.size(); i++)
            if (processes.get(i).getArrivalTime() <= timer)
                arrivedProcesses.add(processes.get(i));

        while (arrivedProcesses.size() > 0 || finished < processes.size()) {
            for (int j = 0; arrivedProcesses.size() > 0 && j < arrivedProcesses.size(); j++) {
                if (nextProcess == arrivedProcesses.get(j) && j != arrivedProcesses.size() - 1 && !firstLoop) {
                    arrivedProcesses.remove(nextProcess);
                    arrivedProcesses.add(nextProcess);
                    nextProcess = arrivedProcesses.get(j);
                } else {
                    nextProcess = arrivedProcesses.get(j);
                }
                firstLoop = false;
                if (nextProcess.getRemainingTime() == nextProcess.getBurstTime()) {

                }

                if (timeQuantum >= nextProcess.getRemainingTime()) {
                    timer += nextProcess.getRemainingTime();
                    gantt.addCell(new GanttChartCell(timer-nextProcess.getRemainingTime(), -1, nextProcess.getPid()));
                    nextProcess.setRemainingTime(0);
                    arrivedProcesses.remove(nextProcess);
                    finished++;
                    j--;
                    int index = processes.indexOf(nextProcess);
                    processes.get(index).setDepartureTime(timer);
                    processes.get(index).setWaitingTime(processes.get(index).getDepartureTime() - processes.get(index).getArrivalTime() - processes.get(index).getBurstTime());
                    P.get(index).setDepartureTime(processes.get(index).getDepartureTime());
                    P.get(index).setWaitingTime(processes.get(index).getWaitingTime());

                } else {
                    timer += timeQuantum;
                    nextProcess.setRemainingTime(nextProcess.getRemainingTime() - timeQuantum);
                    gantt.addCell(new GanttChartCell(timer-timeQuantum, -1, nextProcess.getPid()));


                }

            }
            if (arrivedProcesses.size() == processes.size())
                flag = true;

            for (int j = arrivedProcesses.size(); j < processes.size() && !flag; j++)
                if (processes.get(j).getArrivalTime() <= timer && processes.get(j).getRemainingTime() > 0 && !arrivedProcesses.contains(processes.get(j)))
                    arrivedProcesses.add(processes.get(j));

            if (finished < processes.size() && arrivedProcesses.size() == 0) {
                Process nextArrivedProcess = processes.get(0);
                boolean selectedNext = false;
                for (int i = 0; i < processes.size(); i++) {
                    if (processes.get(i).getRemainingTime() > 0) {
                        if (!selectedNext) {
                            nextArrivedProcess = processes.get(i);
                            selectedNext = true;
                        } else {
                            if (nextArrivedProcess.getArrivalTime() > processes.get(i).getArrivalTime()) {
                                nextArrivedProcess = processes.get(i);
                            }
                        }
                    }
                }
                if (nextArrivedProcess.getRemainingTime() == 0) {
                    selectedNext = false;
                }
                if (selectedNext) {
                    gantt.addCell(new GanttChartCell(timer, 0, "IDLE"));
                    timer = nextArrivedProcess.getArrivalTime();
                }
            }
           

            ObservableList<Process> tempProcesses = FXCollections.observableArrayList();
            for (Process temp : arrivedProcesses)
                tempProcesses.add(temp);


            for (int j = arrivedProcesses.size(); j < processes.size() && !flag; j++)
                if (processes.get(j).getArrivalTime() <= timer && processes.get(j).getRemainingTime() > 0 && !arrivedProcesses.contains(processes.get(j)))
                    arrivedProcesses.add(processes.get(j));

            for (Process temp : tempProcesses) {
                arrivedProcesses.add(temp);
                arrivedProcesses.remove(0);
            }
            tempProcesses.clear();

        }
        nextProcess.setDepartureTime(timer);
        gantt.addCell(new GanttChartCell(timer, -1, nextProcess.getPid()));
        gantt.drawWithDuplicates(ganttChart, scene);
        for (int i = 0; i < processes.size(); i++) {
            P.get(P.indexOf(processes.get(i))).setDepartureTime(processes.get(i).getDepartureTime());
            P.get(P.indexOf(processes.get(i))).setWaitingTime(processes.get(i).getWaitingTime());
        }
    }
}