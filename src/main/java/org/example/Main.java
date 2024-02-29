package org.example;

import java.util.*;
import java.text.*;

// Interface for a record repository
interface RecordRepository {
    void addRecord(Record record);
    List<Record> getAllRecords();
}

// Interface for calculating average waiting time
interface AverageWaitingTimeCalculator {
    int calculateAverageWaitingTime(String serviceQuery, String questionTypeQuery, String responseQuery, Date fromDate, Date toDate);
}

// Class for handling records
class Record {
    String service;
    String questionType;
    String response;
    Date date;
    int time;

    // Constructor
    public Record(String service, String questionType, String response, String date, int time) throws ParseException {
        this.service = service;
        this.questionType = questionType;
        this.response = response;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.date = dateFormat.parse(date);
        this.time = time;
    }

    // Method to check if the record matches the query criteria
    public boolean matchesQuery(String serviceQuery, String questionTypeQuery, String responseQuery, Date fromDate, Date toDate) {
        if (!serviceQuery.equals("*") && !this.service.equals(serviceQuery)) return false;
        if (!questionTypeQuery.equals("*") && !this.questionType.startsWith(questionTypeQuery)) return false;
        if (!responseQuery.equals("*") && !this.response.equals(responseQuery)) return false;
        if (fromDate != null && this.date.before(fromDate)) return false;
        if (toDate != null && this.date.after(toDate)) return false;
        return true;
    }
}

// Class for handling records repository
class InMemoryRecordRepository implements RecordRepository {
    private List<Record> records;

    // Constructor
    public InMemoryRecordRepository() {
        records = new ArrayList<>();
    }

    // Method to add a record
    @Override
    public void addRecord(Record record) {
        records.add(record);
    }

    // Method to get all records
    @Override
    public List<Record> getAllRecords() {
        return records;
    }
}

// Class for handling average waiting time calculation
class AverageWaitingTimeCalculatorImpl implements AverageWaitingTimeCalculator {
    private RecordRepository recordRepository;

    // Constructor
    public AverageWaitingTimeCalculatorImpl(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    // Method to calculate average waiting time
    @Override
    public int calculateAverageWaitingTime(String serviceQuery, String questionTypeQuery, String responseQuery, Date fromDate, Date toDate) {
        List<Record> records = recordRepository.getAllRecords();
        int sum = 0;
        int count = 0;
        for (Record record : records) {
            if (record.matchesQuery(serviceQuery, questionTypeQuery, responseQuery, fromDate, toDate)) {
                sum += record.time;
                count++;
            }
        }
        return count > 0 ? Math.round((float) sum / count) : -1;
    }
}

// Class for handling input/output and orchestrating the application
public class Main {
    private RecordRepository recordRepository;
    private AverageWaitingTimeCalculator averageWaitingTimeCalculator;

    // Constructor
    public Main(RecordRepository recordRepository, AverageWaitingTimeCalculator averageWaitingTimeCalculator) {
        this.recordRepository = recordRepository;
        this.averageWaitingTimeCalculator = averageWaitingTimeCalculator;
    }

    // Method to run the application
    public void run() throws ParseException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of lines:");
        int n = Integer.parseInt(scanner.nextLine().trim());
        for (int i = 0; i < n; i++) {
            System.out.println("Enter line:");
            String[] parts = scanner.nextLine().split(" ");
            if (parts[0].equals("C")) {
                String[] serviceParts = parts[1].split("\\.");
                String service = serviceParts[0];
                String questionType = parts[2];
                String response = parts[3];
                String date = parts[4];
                int time = Integer.parseInt(parts[5]);
                recordRepository.addRecord(new Record(service, questionType, response, date, time));
            } else if (parts[0].equals("D")) {
                String[] serviceParts = parts[1].split("\\.");
                String serviceQuery = serviceParts[0];
                String questionTypeQuery = parts[2];
                String responseQuery = parts[3];
                String[] dateRange = parts[4].split("-");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date fromDate = dateFormat.parse(dateRange[0]);
                Date toDate = dateRange.length > 1 ? dateFormat.parse(dateRange[1]) : null;
                int averageWaitingTime = averageWaitingTimeCalculator.calculateAverageWaitingTime(serviceQuery, questionTypeQuery, responseQuery, fromDate, toDate);
                if (!(averageWaitingTime < 0)) {
                    System.out.println(averageWaitingTime);
                } else {
                    System.out.println("-");
                }
            }
        }
        scanner.close();
    }

    public static void main(String[] args) throws ParseException {
        RecordRepository recordRepository = new InMemoryRecordRepository();
        AverageWaitingTimeCalculator averageWaitingTimeCalculator = new AverageWaitingTimeCalculatorImpl(recordRepository);
        Main main = new Main(recordRepository, averageWaitingTimeCalculator);
        main.run();
    }
}