package service;

import model.Student;
import model.Teacher;

import java.util.List;
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
            System.out.println("\n=== Student Menu ("+student.getName()+") ===");
            System.out.println("1. View grades");
            System.out.println("2. View schedule");
            System.out.println("3. View teacher and subjects");
            System.out.println("4. View profile");
            System.out.println("5. Edit profile");
            System.out.println("6. Logout");
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": viewGrades(); break;
                case "2": viewSchedule(); break;
                case "3": viewTeacherAndSubject(); break;
                case "4": viewProfile(); break;
                case "5": editProfile(sc); break;
                case "6": return;
                default: System.out.println("Invalid"); break;
            }
        }
    }

    private void viewGrades() {
        System.out.println("Grades: " + student.getGrades());
    }

    private void viewSchedule() {
        System.out.println("Schedule: " + student.getSchedule());
    }

    private void viewTeacherAndSubject() {
        String tid = student.getTeacherId();
        if (tid == null || tid.isEmpty()) {
            System.out.println("No teacher assigned.");
            return;
        }
        Teacher t = um.findTeacherByTeacherId(tid);
        if (t == null) {
            System.out.println("Teacher record not found.");
            return;
        }
        System.out.println("Teacher: " + t.getName() + " (ID: " + t.getTeacherId() + ")");
        System.out.println("Teacher's subjects: " + t.getSubjects());
        // Show intersection of student's schedule and teacher's subjects
        List<String> sSched = student.getSchedule();
        boolean any = false;
        for (String sub : sSched) {
            if (t.getSubjects().contains(sub)) {
                System.out.println("- You have subject with this teacher: " + sub);
                any = true;
            }
        }
        if (!any) System.out.println("No matching subjects found between your schedule and teacher's subjects.");
    }

    private void viewProfile() {
        System.out.println("Name: " + student.getName());
        System.out.println("Email: " + student.getEmail());
        System.out.println("StudentId: " + student.getStudentId());
        System.out.println("Year: " + student.getYearLevel());
        System.out.println("Course: " + student.getCourse());
        System.out.println("Type: " + student.getStudentType());
    }

    private void editProfile(Scanner sc) {
        System.out.print("New name (enter to skip): ");
        String tmp = sc.nextLine().trim();
        if (!tmp.isEmpty()) student.setName(tmp);
        System.out.print("New email (enter to skip): ");
        tmp = sc.nextLine().trim();
        if (!tmp.isEmpty()) student.setEmail(tmp);
        System.out.print("New password (enter to skip): ");
        tmp = sc.nextLine().trim();
        if (!tmp.isEmpty()) student.setPassword(tmp);
        um.persistStudents();
        System.out.println("Profile updated.");
    }

}

