package service;

import model.Student;
import model.Teacher;

import java.util.Map;
import java.util.Scanner;

public class StudentService {
    private final UserManager um;
    private final Student student;

    public StudentService(UserManager um, Student student) {
        this.um = um;
        this.student = student;
    }

    public void menu(Scanner sc) {
        while (true) {
            System.out.println("\n=== Student Menu (" + student.getName() + ") ===");
            System.out.println("1. View grades");
            System.out.println("2. View schedule");
            System.out.println("3. View profile");
            System.out.println("4. Edit profile");
            System.out.println("5. Logout");
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> viewGrades();
                case "2" -> viewSchedule();
                case "3" -> viewProfile();
                case "4" -> editProfile(sc);
                case "5" -> { return; }
                default -> System.out.println("Invalid");
            }
        }
    }

    private void viewGrades() {
        if (student.getGrades().isEmpty()) {
            System.out.println("No grades found.");
            return;
        }
        System.out.println("=== Grades ===");
        student.getGrades().forEach((sub, g) ->
                System.out.println(sub + ": " + g));
    }

    private void viewSchedule() {
        if (student.getSchedule().isEmpty()) {
            System.out.println("No schedule found.");
            return;
        }

        System.out.println("=== Schedule ===");
        for (Map.Entry<String, String> entry : student.getSchedule().entrySet()) {
            String subjTime = entry.getKey();
            String tId = entry.getValue();
            Teacher t = um.findTeacherByTeacherId(tId);
            String teacherName = (t != null) ? t.getName() : "Unknown Teacher";
            System.out.println(subjTime + " - Teacher: " + teacherName);
        }
    }






    private void viewProfile() {
        System.out.println("=== Profile ===");
        System.out.println("Name: " + student.getName());
        System.out.println("Email: " + student.getEmail());
        System.out.println("StudentId: " + student.getStudentId());
        System.out.println("Year: " + student.getYearLevel());
        System.out.println("Course: " + student.getCourse());
        System.out.println("Type: " + student.getStudentType());
        System.out.println("Age: " + student.getAge());
        System.out.println("Birthdate: " + student.getBirthdate());
        System.out.println("Mother's Name: " + student.getMotherName());
        System.out.println("Father's Name: " + student.getFatherName());
        System.out.println("Contact Number: " + student.getContactNumber());
        System.out.println("Address: " + student.getAddress());
        System.out.println("Organization: " + student.getOrganization());
        System.out.println("Hobbies: " + student.getHobbies());
    }

    private void editProfile(Scanner sc) {
        while (true) {
            System.out.println("\n=== Edit Profile ===");
            System.out.println("1. Name");
            System.out.println("2. Email");
            System.out.println("3. Password");
            System.out.println("4. Age");
            System.out.println("5. Birthdate");
            System.out.println("6. Mother's Name");
            System.out.println("7. Father's Name");
            System.out.println("8. Contact Number");
            System.out.println("9. Address");
            System.out.println("10. Organization");
            System.out.println("11. Hobbies");
            System.out.println("12. Back");
            System.out.print("Choose field to edit: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter new name: ");
                    student.setName(sc.nextLine().trim());
                }
                case "2" -> {
                    System.out.print("Enter new email: ");
                    student.setEmail(sc.nextLine().trim());
                }
                case "3" -> {
                    System.out.print("Enter new password: ");
                    student.setPassword(sc.nextLine().trim());
                }
                case "4" -> {
                    System.out.print("Enter new age: ");
                    try {
                        student.setAge(Integer.parseInt(sc.nextLine().trim()));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                    }
                }
                case "5" -> {
                    System.out.print("Enter new birthdate: ");
                    student.setBirthdate(sc.nextLine().trim());
                }
                case "6" -> {
                    System.out.print("Enter new mother's name: ");
                    student.setMotherName(sc.nextLine().trim());
                }
                case "7" -> {
                    System.out.print("Enter new father's name: ");
                    student.setFatherName(sc.nextLine().trim());
                }
                case "8" -> {
                    System.out.print("Enter new contact number: ");
                    student.setContactNumber(sc.nextLine().trim());
                }
                case "9" -> {
                    System.out.print("Enter new address: ");
                    student.setAddress(sc.nextLine().trim());
                }
                case "10" -> {
                    System.out.print("Enter new organization: ");
                    student.setOrganization(sc.nextLine().trim());
                }
                case "11" -> {
                    System.out.print("Enter new hobbies: ");
                    student.setHobbies(sc.nextLine().trim());
                }
                case "12" -> {
                    um.persistStudents();
                    System.out.println("Profile saved. Returning...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
