package pbo.f01;

import java.util.List;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import pbo.f01.model.Dorm;
import pbo.f01.model.Student;

public class App {
    private static EntityManagerFactory factory;
    private static EntityManager entityManager;

    public static void main(String[] args) {
        //System.out.println("Starting application...");

        factory = Persistence.createEntityManagerFactory("dormy_pu");
        entityManager = factory.createEntityManager();
        cleanTables();  // Clean tables when starting the application

        Scanner scanner = new Scanner(System.in);

        //System.out.println("Enter commands: ");

        while (true) {
            try {
                if (scanner.hasNextLine()) {
                    String command = scanner.nextLine();
                    //System.out.println("Received command: " + command); // Debug statement
                    if (command.equals("---")) break;

                    String[] parts = command.split("#");
                    switch (parts[0]) {
                        case "dorm-add":
                            addDorm(parts[1], Integer.parseInt(parts[2]), parts[3]);
                            break;
                        case "student-add":
                            addStudent(parts[1], parts[2], Integer.parseInt(parts[3]), parts[4]);
                            break;
                        case "assign":
                            assignStudent(parts[1], parts[2]);
                            break;
                        case "display-all":
                            displayAll();
                            break;
                        default:
                            //System.out.println("Invalid command!");
                    }
                } else {
                    //System.out.println("Waiting for input...");
                }
            } catch (Exception e) {
                //System.out.println("Error processing command: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();

        entityManager.close();
        factory.close();
    }

    private static void addDorm(String name, int capacity, String gender) {
        try {
            entityManager.getTransaction().begin();
            Dorm dorm = new Dorm();
            dorm.setName(name);
            dorm.setCapacity(capacity);
            dorm.setGender(gender);
            entityManager.persist(dorm);
            entityManager.getTransaction().commit();
            //System.out.println("Dorm added successfully!");
            System.out.flush();
        } catch (Exception e) {
            //System.out.println("Error adding dorm: " + e.getMessage());
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void addStudent(String id, String name, int batch, String gender) {
        try {
            entityManager.getTransaction().begin();
            Student student = new Student();
            student.setId(id);
            student.setName(name);
            student.setBatch(batch);
            student.setGender(gender);
            entityManager.persist(student);
            entityManager.getTransaction().commit();
            //System.out.println("Student added successfully!");
            System.out.flush();
        } catch (Exception e) {
            //System.out.println("Error adding student: " + e.getMessage());
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void assignStudent(String studentId, String dormName) {
        try {
            entityManager.getTransaction().begin();
            Dorm dorm = entityManager.createQuery("SELECT d FROM Dorm d WHERE d.name = :dormName", Dorm.class)
                    .setParameter("dormName", dormName)
                    .getSingleResult();
            Student student = entityManager.find(Student.class, studentId);

            // Check gender match
            if (!dorm.getGender().equals(student.getGender())) {
                //System.out.println("Gender mismatch!");
                entityManager.getTransaction().rollback();
                return;
            }

            // Check capacity
            if (dorm.getStudents().size() >= dorm.getCapacity()) {
                //System.out.println("Dorm is full!");
                entityManager.getTransaction().rollback();
                return;
            }

            // Assign student to dorm
            student.setDorm(dorm);
            dorm.getStudents().add(student);
            entityManager.getTransaction().commit();
            //System.out.println("Student assigned to dorm successfully!");
            System.out.flush();
        } catch (Exception e) {
            //System.out.println("Error assigning student: " + e.getMessage());
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void displayAll() {
        try {
            Query query = entityManager.createQuery("SELECT d FROM Dorm d ORDER BY d.name", Dorm.class);
            List<Dorm> dorms = query.getResultList();
            for (Dorm dorm : dorms) {
                System.out.println(dorm);
                List<Student> students = entityManager.createQuery(
                        "SELECT s FROM Student s WHERE s.dorm = :dorm ORDER BY s.name", Student.class)
                        .setParameter("dorm", dorm)
                        .getResultList();
                for (Student student : students) {
                    System.out.println(student);
                }
            }
        } catch (Exception e) {
            //System.out.println("Error displaying data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void cleanTables() {
        try {
            entityManager.getTransaction().begin();
            entityManager.createQuery("DELETE FROM Student").executeUpdate();
            entityManager.createQuery("DELETE FROM Dorm").executeUpdate();
            entityManager.getTransaction().commit();
            //System.out.println("Tables cleaned!");
            System.out.flush();
        } catch (Exception e) {
            //System.out.println("Error cleaning tables: " + e.getMessage());
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
