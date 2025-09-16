package service;

import model.Student;
import model.Teacher;

import java.util.List;
import java.util.Scanner;

public record TeacherService(UserManager um, Teacher teacher) {

    public void menu(Scanner sc) {
        while (true) {
            System.out.println("\n=== Teacher Menu (" + teacher.getName() + ") ===");
            System.out.println("1. Add student");
            System.out.println("2. Input grades");
            System.out.println("3. Input schedule");
            System.out.println("4. Set student year/course/type");
            System.out.println("5. View student info");
            System.out.println("6. Manage class students (view all assigned)");
            System.out.println("7. Back / Logout");
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1":
                    addStudent(sc);
                    break;
                case "2":
                    inputGrades(sc);
                    break;
                case "3":
                    inputSchedule(sc);
                    break;
                case "4":
                    setStudentMeta(sc);
                    break;
                case "5":
                    viewStudentInfo(sc);
                    break;
                case "6":
                    manageClassStudents();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("Invalid");
                    break;
            }
        }
    }

    private void addStudent(Scanner sc) {
        System.out.print("New student internal id (random or typed): ");
        String id = sc.nextLine().trim();
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String pw = sc.nextLine().trim();
        System.out.print("StudentId (school id): ");
        String sid = sc.nextLine().trim();

        Student s = new Student(id, name, email, pw, sid);
        s.setTeacherId(teacher.getTeacherId());
        boolean ok = um.registerStudent(s);
        if (ok) {
            um.assignTeacherToStudent(teacher.getTeacherId(), sid);
            System.out.println("Student added and assigned to you.");
        } else {
            System.out.println("A student with that email already exists.");
        }
    }

    private Student pickAssignedStudent(Scanner sc) {
        System.out.print("Enter student school id: ");
        String sid = sc.nextLine().trim();
        Student s = um.findStudentByStudentId(sid);
        if (s == null || !teacher.getTeacherId().equals(s.getTeacherId())) {
            System.out.println("Student not found or not assigned to you.");
            return null;
        }
        return s;
    }

    private void inputGrades(Scanner sc) {
        Student s = pickAssignedStudent(sc);
        if (s == null) return;
        System.out.print("Subject: ");
        String sub = sc.nextLine().trim();
        System.out.print("Grade (numeric): ");
        Double g = Double.valueOf(sc.nextLine().trim());
        s.setGrade(sub, g);
        um.persistStudents();
        System.out.println("Grade saved.");
    }

    private void inputSchedule(Scanner sc) {
        Student s = pickAssignedStudent(sc);
        if (s == null) return;
        System.out.print("Add subject to student's schedule: ");
        String sub = sc.nextLine().trim();
        s.addToSchedule(sub);
        um.persistStudents();
        System.out.println("Schedule updated.");
    }

    private void setStudentMeta(Scanner sc) {
        Student s = pickAssignedStudent(sc);
        if (s == null) return;
        System.out.print("Year level: ");
        s.setYearLevel(sc.nextLine().trim());
        System.out.print("Course: ");
        s.setCourse(sc.nextLine().trim());
        System.out.print("Student type (regular/irregular/transferee): ");
        s.setStudentType(sc.nextLine().trim());
        um.persistStudents();
        System.out.println("Student meta updated.");
    }

    private void viewStudentInfo(Scanner sc) {
        Student s = pickAssignedStudent(sc);
        if (s == null) return;
        System.out.println("=== Student Info ===");
        System.out.println("Name: " + s.getName());
        System.out.println("StudentId: " + s.getStudentId());
        System.out.println("Email: " + s.getEmail());
        System.out.println("Year: " + s.getYearLevel());
        System.out.println("Course: " + s.getCourse());
        System.out.println("Type: " + s.getStudentType());
        System.out.println("Schedule: " + s.getSchedule());
        System.out.println("Grades: " + s.getGrades());
    }

    private void manageClassStudents() {
        List<Student> all = um.getAllStudents();
        System.out.println("Students assigned to you:");
        for (Student s : all) {
            if (teacher.getTeacherId().equals(s.getTeacherId())) {
                System.out.println("- " + s.getName() + " (" + s.getStudentId() + ") Course:" + s.getCourse());
            }
        }
    }
}
