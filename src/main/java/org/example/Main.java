package org.example;

import java.util.*;
import java.text.*;

public class Main {
    List<Lines> lines;

    public Main() {
        lines = new ArrayList<>();
    }

    public void addRecord(Lines line) {
        lines.add(line);
    }

    public int calculateAverageWaitingTime(String serviceQuery, String questionTypeQuery, String responseQuery, Date fromDate, Date toDate) {
        int sum = 0;
        int count = 0;
        for (Lines line : lines) {
            if (line.matchesQuery(serviceQuery, questionTypeQuery, responseQuery, fromDate, toDate)) {
                sum += line.time;
                count++;
            }
        }
        return count > 0 ? Math.round((float) sum / count) : -1;
    }

    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of lines:");
        int s = Integer.parseInt(scanner.nextLine().trim());
        Main main = new Main();
        for (int i = 0; i < s; i++) {
            System.out.println("Enter line:");
            String[] parts = scanner.nextLine().split(" ");
            if (parts[0].equals("C")) {
                String[] serviceParts = parts[1].split("\\.");
                String service = serviceParts[0];
                String questionType = parts[2];
                String response = parts[3];
                String date = parts[4];
                int time = Integer.parseInt(parts[5]);
                main.addRecord(new Lines(service, questionType, response, date, time));
            } else if (parts[0].equals("D")) {
                String[] serviceParts = parts[1].split("\\.");
                String serviceQuery = serviceParts[0];
                String questionTypeQuery = parts[2];
                String responseQuery = parts[3];
                String[] dateRange = parts[4].split("-");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date fromDate = dateFormat.parse(dateRange[0]);
                Date toDate = dateRange.length > 1 ? dateFormat.parse(dateRange[1]) : null;
                int averageWaitingTime = main.calculateAverageWaitingTime(serviceQuery, questionTypeQuery, responseQuery, fromDate, toDate);
                if (!(averageWaitingTime < 0)) {
                    System.out.println(averageWaitingTime);
                } else {
                    System.out.println("-");
                }
            }
        }
        scanner.close();
    }
}

class Lines {
    String service, questionType, response;
    Date date;
    int time;

    public Lines(String service, String questionType, String response, String date, int time) throws ParseException {
        this.service = service;
        this.questionType = questionType;
        this.response = response;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.date = dateFormat.parse(date);
        this.time = time;
    }

    public boolean matchesQuery(String serviceQuery, String questionTypeQuery, String responseQuery, Date fromDate, Date toDate) {
        if (!serviceQuery.equals("*") && !service.equals(serviceQuery)) return false;
        if (!questionTypeQuery.equals("*") && !questionType.startsWith(questionTypeQuery)) return false;
        if (!responseQuery.equals("*") && !response.equals(responseQuery)) return false;
        if (fromDate != null && date.before(fromDate)) return false;
        if (toDate != null && date.after(toDate)) return false;
        return true;
    }
}