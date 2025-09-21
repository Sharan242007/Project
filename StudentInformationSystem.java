import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Scanner;

class Student {
    int rollNo;
    String name;
    int age;
    String course;

    Student(int rollNo, String name, int age, String course) {
        this.rollNo = rollNo;
        this.name = name;
        this.age = age;
        this.course = course;
    }

    Document toDocument() {
        return new Document("rollNo", rollNo)
                .append("name", name)
                .append("age", age)
                .append("course", course);
    }

    static Student fromDocument(Document doc) {
        return new Student(
                doc.getInteger("rollNo"),
                doc.getString("name"),
                doc.getInteger("age"),
                doc.getString("course")
        );
    }

    @Override
    public String toString() {
        return "Roll No: " + rollNo + ", Name: " + name + ", Age: " + age + ", Course: " + course;
    }
}

public class StudentInformationSystem {

    static Scanner sc = new Scanner(System.in);
    static MongoClient mongoClient;
    static MongoDatabase database;
    static MongoCollection<Document> collection;

    
    public static void connectDB() {
        String uri ="mongodb+srv://Student:asdfgf123454@cluster0.vu7nama.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("schoolDB");
        collection = database.getCollection("students");
    }

    
    public static void addStudent() {
        System.out.print("Enter Roll No: ");
        int rollNo = sc.nextInt();
        sc.nextLine();

        if (collection.find(Filters.eq("rollNo", rollNo)).first() != null) {
            System.out.println("Error: A student with this roll number already exists!");
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Course: ");
        String course = sc.nextLine();

        Student student = new Student(rollNo, name, age, course);
        collection.insertOne(student.toDocument());
        System.out.println("Student added successfully!");
    }

    
    public static void viewStudents() {
        MongoCursor<Document> cursor = collection.find().iterator();
        if (!cursor.hasNext()) {
            System.out.println("No students found.");
        } else {
            System.out.println("\n=== List of Students ===");
            while (cursor.hasNext()) {
                Student s = Student.fromDocument(cursor.next());
                System.out.println(s);
            }
        }
    }

    
    public static void searchStudent() {
        System.out.print("Enter Roll No to search: ");
        int rollNo = sc.nextInt();
        Document doc = collection.find(Filters.eq("rollNo", rollNo)).first();

        if (doc != null) {
            Student s = Student.fromDocument(doc);
            System.out.println("Student Found: " + s);
        } else {
            System.out.println("Student with Roll No " + rollNo + " not found.");
        }
    }

    
    public static void updateStudent() {
        System.out.print("Enter Roll No to update: ");
        int rollNo = sc.nextInt();
        sc.nextLine();

        Document doc = collection.find(Filters.eq("rollNo", rollNo)).first();
        if (doc == null) {
            System.out.println("Student with Roll No " + rollNo + " not found.");
            return;
        }

        System.out.print("Enter new Name: ");
        String name = sc.nextLine();
        System.out.print("Enter new Age: ");
        int age = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter new Course: ");
        String course = sc.nextLine();

        collection.updateOne(Filters.eq("rollNo", rollNo),
                new Document("$set", new Document("name", name)
                        .append("age", age)
                        .append("course", course)));

        System.out.println("Student updated successfully!");
    }

    
    public static void deleteStudent() {
        System.out.print("Enter Roll No to delete: ");
        int rollNo = sc.nextInt();

        long deletedCount = collection.deleteOne(Filters.eq("rollNo", rollNo)).getDeletedCount();

        if (deletedCount > 0) {
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("Student with Roll No " + rollNo + " not found.");
        }
    }

    
    public static void menu() {
        int choice;
        do {
            System.out.println("\n===== Student Information System =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student by Roll No");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1: addStudent(); break;
                case 2: viewStudents(); break;
                case 3: searchStudent(); break;
                case 4: updateStudent(); break;
                case 5: deleteStudent(); break;
                case 6: System.out.println("Exiting program... Goodbye!"); break;
                default: System.out.println("Invalid choice! Please try again.");
            }
        } while (choice != 6);
    }

    public static void main(String[] args) {
        connectDB();
        menu();
        mongoClient.close();
        sc.close();
    }
}
