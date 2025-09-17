package service;

import model.Student;
import model.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public record TeacherService(UserManager um, Teacher teacher) {

    public void menu(Scanner sc) {
        while (true) {
            System.out.println("\n=== Teacher Menu (" + teacher.getName() + ") ===");
            System.out.println("1. Add student");
            System.out.println("2. Input grades");
            System.out.println("3. Set student year/course/type");
            System.out.println("4. View student info");
            System.out.println("5. Manage class students (view all assigned)");
            System.out.println("6. Remove student");
            System.out.println("7. Back / Logout"); // 🔙 back
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> addStudent(sc);
                case "2" -> inputGrades(sc);
                case "3" -> setStudentMeta(sc);
                case "4" -> viewStudentInfo(sc);
                case "5" -> manageClassStudents(sc);
                case "6" -> removeStudent(sc);
                case "7" -> {
                    return;
                } // 🔙 back
                default -> System.out.println("Invalid");
            }
        }
    }


    private void addStudent(Scanner sc) {
        System.out.print("Enter Student ID(s): ");
        String input = sc.nextLine().trim();
        System.out.print("Enter Subject: ");
        String subject = sc.nextLine().trim();
        System.out.print("Enter Time: ");

        for (String sid : input.split(",")) {
            sid = sid.trim();
            Student s = um.findStudentByStudentId(sid);
            if (s != null) {
                s.setTeacherId(teacher.getTeacherId());
                s.addToSchedule(subject, sc.nextLine().trim()); // ✅ ask for time and add


                boolean ok = um.updateStudent(s);
                if (ok) {
                    System.out.println("✅ Added " + s.getName() + " with subject: " + subject);
                } else {
                    System.out.println("❌ Failed to update student " + sid);
                }
            } else {
                System.out.println("⚠️ No student found with ID: " + sid);
            }
        }
    }

    private void removeStudent(Scanner sc) {
        System.out.print("Enter StudentId to remove: ");
        String sid = sc.nextLine().trim();

        Student s = um.findStudentByStudentId(sid);

        if (s == null) {
            System.out.println("No student found with StudentId: " + sid);
            return;
        }

        // Check if this teacher owns the student
        if (!teacher.getTeacherId().equals(s.getTeacherId())) {
            System.out.println("You are not assigned to this student.");
            return;
        }

        System.out.println("Do you want to remove specific subject or unassign completely?");
        System.out.println("1. Remove subject");
        System.out.println("2. Unassign completely");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("Enter subject to remove: ");
                String sub = sc.nextLine().trim();
                if (s.getSchedule().contains(sub)) {
                    s.getSchedule().remove(sub);
                    s.getGrades().remove(sub);
                    System.out.println("Subject removed from student.");
                } else {
                    System.out.println("Subject not found in student's schedule.");
                }
            }
            case "2" -> {
                s.setTeacherId(null);
                s.getSchedule().clear();
                s.getGrades().clear();
                System.out.println("Student fully unassigned from you.");
            }
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }

        boolean ok = um.updateStudent(s);
        if (ok) {
            um.persistStudents();
            System.out.println("Student update saved.");
        } else {
            System.out.println("Failed to update student.");
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
        s.setGrade(sub, String.valueOf(g));
        um.persistStudents();
        System.out.println("Grade saved.");
    }

    private void inputSchedule(Scanner sc) {
        Student s = pickAssignedStudent(sc);
        if (s == null) return;

        System.out.print("Enter Subject: ");
        String subject = sc.nextLine().trim();

        System.out.print("Enter Time: ");
        String time = sc.nextLine().trim();

        s.addToSchedule(subject, time); // ✅ correct usage
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
        System.out.println("=== Student Info (Academic) ===");
        System.out.println("Name: " + s.getName());
        System.out.println("StudentId: " + s.getStudentId());
        System.out.println("Email: " + s.getEmail());
        System.out.println("Year: " + s.getYearLevel());
        System.out.println("Course: " + s.getCourse());
        System.out.println("Type: " + s.getStudentType());
        System.out.println("Schedule: " + s.getSchedule());
        System.out.println("Grades: " + s.getGrades());
    }


    private void manageClassStudents(Scanner sc) {
        List<Student> all = um.getAllStudents();
        System.out.println("\n=== Students Assigned to You ===");
        List<Student> assigned = new ArrayList<>();

        for (Student s : all) {
            if (teacher.getTeacherId().equals(s.getTeacherId())) {
                assigned.add(s);
            }
        }

        if (assigned.isEmpty()) {
            System.out.println("No students assigned yet.");
            return;
        }

        // Show list with index
        for (int i = 0; i < assigned.size(); i++) {
            Student s = assigned.get(i);
            System.out.println((i + 1) + ". " + s.getName() + " (" + s.getStudentId() + ") - " + s.getCourse());
        }

        System.out.print("Choose: ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice > 0 && choice <= assigned.size()) {
                Student s = assigned.get(choice - 1);
                System.out.println("\n=== Student Info ===");
                System.out.println("Name: " + s.getName());
                System.out.println("StudentId: " + s.getStudentId());
                System.out.println("Year: " + s.getYearLevel());
                System.out.println("Course: " + s.getCourse());
                System.out.println("Type: " + s.getStudentType());
                System.out.println("Schedule: " + s.getSchedule());
                System.out.println("Grades: " + s.getGrades());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
}