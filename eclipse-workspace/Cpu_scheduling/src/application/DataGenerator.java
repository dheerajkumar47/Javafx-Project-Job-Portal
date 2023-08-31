package application;

import javafx.collections.ObservableList;

import java.util.Random;

public class DataGenerator {
    public static void generateData(ObservableList<Process> processes) {
    	 processes.clear();
         Random rand = new Random();
         int n = rand.nextInt(15);
         switch (n) {
             case 0:
//       // Dummy Data - Set 1
                 processes.add(new Process("P1", 0, 5, 4));
                 processes.add(new Process("P2", 3, 5, 3));
                 processes.add(new Process("P3", 2, 5, 2));
                 processes.add(new Process("P4", 15, 5, 1));
                 processes.add(new Process("P5", 25, 5, 1));
                 break;
             case 1:
//         // Dummy Data - Set 2
                 processes.add(new Process("P1", 0, 2, 4));
                 processes.add(new Process("P2", 1, 2, 1));
                 processes.add(new Process("P3", 0, 2, 3));
                 processes.add(new Process("P4", 0, 2, 2));
                 break;
             case 2:
//         // Dummy Data - Set 3
                 processes.add(new Process("P1", 0, 6, 4));
                 processes.add(new Process("P2", 0, 8, 1));
                 processes.add(new Process("P3", 0, 7, 3));
                 processes.add(new Process("P4", 0, 3, 2));
                 processes.add(new Process("P5", 1, 20, 1));
                 processes.add(new Process("P6", 45, 1, 1));
                 break;
             case 3:
//         // Dummy Data - Set 4
                 processes.add(new Process("P1", 0, 6, 4));
                 processes.add(new Process("P2", 5, 6, 1));
                 break;
             case 4:
//         // Dummy Data - Set 5
                 processes.add(new Process("P1", 0, 6, 1));
                 processes.add(new Process("P2", 2, 6, 2));
                 break;
//         // Dummy Data - Set 6
             case 5:
                 processes.add(new Process("P1", 0, 3, 1));
                 processes.add(new Process("P2", 2, 2, 2));
                 break;
             case 6:
//         // Dummy Data - Set 7
                 processes.add(new Process("P1", 0, 8, 1));
                 processes.add(new Process("P2", 1, 4, 2));
                 processes.add(new Process("P3", 2, 9, 2));
                 processes.add(new Process("P4", 3, 5, 2));
                 break;
             case 7:
                 //        // Dummy Data - Set 8 - Priority With gaps
                 processes.add(new Process("P1", 0, 8, 2));
                 processes.add(new Process("P2", 10, 4, 1));
                 break;
//         // Dummy Data - Set 9
             case 8:
                 processes.add(new Process("P1", 0, 6, 3));
                 processes.add(new Process("P2", 1, 2, 2));
                 processes.add(new Process("P3", 2, 3, 1));
                 processes.add(new Process("P4", 4, 1, 1));
                 // GAP
                 processes.add(new Process("P5", 20, 1, 1));
                 break;
             case 9:
//         // Dummy Data - Set 10 - Page 15
                 processes.add(new Process("P1", 0, 8, 3));
                 processes.add(new Process("P2", 1, 4, 2));
                 processes.add(new Process("P3", 2, 9, 1));
                 processes.add(new Process("P4", 3, 5, 1));
                 break;
             case 10:
//         // Dummy Data - Set 11 - Page 12
                 processes.add(new Process("P1", 0, 6, 3));
                 processes.add(new Process("P2", 0, 8, 2));
                 processes.add(new Process("P3", 0, 7, 1));
                 processes.add(new Process("P4", 0, 3, 1));
                 break;
             case 11:
//         // Dummy Data - Set 12 - Page 17
                 processes.add(new Process("P1", 0, 10, 3));
                 processes.add(new Process("P2", 0, 1, 1));
                 processes.add(new Process("P3", 0, 2, 4));
                 processes.add(new Process("P4", 0, 1, 5));
                 processes.add(new Process("P5", 0, 5, 2));
                 break;
             case 12:
//         // Dummy Data - Set 13 - Page 19
                 processes.add(new Process("P1", 0, 24, 1));
                 processes.add(new Process("P2", 0, 3, 1));
                 processes.add(new Process("P3", 0, 3, 1));
                 break;
             case 13:
//         // Dummy Data - Set 14 - Exam
                 processes.add(new Process("P1", 0, 7, 1));
                 processes.add(new Process("P2", 2, 4, 1));
                 processes.add(new Process("P3", 4, 1, 1));
                 processes.add(new Process("P4", 5, 4, 1));
                 break;
             case 14:
//         // Dummy Data - Set 15 - Exam SJF NP & FCFS
                 processes.add(new Process("P1", 0, 2, 1));
                 processes.add(new Process("P2", 0, 4, 1));
                 processes.add(new Process("P3", 2, 1, 1));
                 processes.add(new Process("P4", 2, 3, 1));
                 processes.add(new Process("P5", 3, 2, 1));
                 break;
             case 15:
//         // Dummy Data - Set 16 - Priority P
                 processes.add(new Process("P1", 0, 2, 5));
                 processes.add(new Process("P2", 1, 4, 6));
                 processes.add(new Process("P3", 2, 1, 3));
                 processes.add(new Process("P4", 3, 3, 2));
                 processes.add(new Process("P5", 4, 2, 1));
                 break;
             case 16:
//         // Dummy Data - Set 17 - Priority P
                 processes.add(new Process("P1", 0, 2, 5));
                 processes.add(new Process("P2", 1, 2, 4));
                 processes.add(new Process("P3", 2, 2, 3));
                 processes.add(new Process("P4", 3, 2, 2));
                 processes.add(new Process("P5", 4, 2, 1));
                 processes.add(new Process("P6", 15, 2, 1));
                 break;
         }

    }
}
