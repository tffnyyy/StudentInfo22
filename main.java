package sis.cli;

import model.Teacher;
import model.Student;
import service.UserManager;
import service.TeacherService;
import service.StudentService;
import storage.DataStore;

import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        DataStore ds = new DataStore();
        UserManager um = new UserManager(ds);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Teacher");
            System.out.println("2. Student");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            String m = sc.nextLine().trim();
            if (!m.equals("1")) {
                if (m.equals("2")) studentFlow(sc, um);
                else if (m.equals("3")) {
                    System.out.println("Goodbye.");
                    break;
                } else System.out.println("Invalid");
            } else {
                teacherFlow(sc, um);
            }
        }
        sc.close();
    }

    private static void teacherFlow(Scanner sc, UserManager um) {
        while (true) {
            System.out.println("\n--- TEACHER ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Back");
            System.out.print("Choose: ");
            String c = sc.nextLine().trim();
            if (!c.equals("1")) {
                if (c.equals("2")) {
                    System.out.print("Name: "); String name = sc.nextLine().trim();
                    System.out.print("Email: "); String email = sc.nextLine().trim();
                    System.out.print("Password: "); String pw = sc.nextLine().trim();
                    System.out.print("Teacher ID: "); String tid = sc.nextLine().trim();
                    String id = UUID.randomUUID().toString();
                    Teacher t = new Teacher(id, name, email, pw, tid);
                    System.out.print("Add subject(s) for this teacher (comma-separated, optional): ");
                    String subs = sc.nextLine().trim();
                    if (!subs.isEmpty()) {
                        for (String s : subs.split(",")) t.addSubject(s.trim());
                    }
                    boolean ok = um.registerTeacher(t);
                    if (ok) System.out.println("Teacher registered.");
                    else System.out.println("Email already exists.");
                } else if (c.equals("3")) return;
                else System.out.println("Invalid.");
            } else {
                System.out.print("Email: "); String email = sc.nextLine().trim();
                System.out.print("Teacher ID: "); String tid = sc.nextLine().trim();
                System.out.print("Password: "); String pw = sc.nextLine().trim();
                Teacher t = um.loginTeacher(email, tid, pw);
                if (t == null) System.out.println("Login failed.");
                else new TeacherService(um, t).menu(sc);
            }
        }
    }

    private static void studentFlow(Scanner sc, UserManager um) {
        while (true) {
            System.out.println("\n--- STUDENT ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Back");
            System.out.print("Choose: ");
            String c = sc.nextLine().trim();
            if (!c.equals("1")) {
                if (c.equals("2")) {
                    System.out.print("Name: "); String name = sc.nextLine().trim();
                    System.out.print("Email: "); String email = sc.nextLine().trim();
                    System.out.print("Password: "); String pw = sc.nextLine().trim();
                    System.out.print("Student ID: "); String sid = sc.nextLine().trim();
                    String id = UUID.randomUUID().toString();
                    Student s = new Student(id, name, email, pw, sid);
                    System.out.print("Year level (optional): ");
                    s.setYearLevel(sc.nextLine().trim());
                    System.out.print("Course (optional): ");
                    s.setCourse(sc.nextLine().trim());
                    System.out.print("Type (regular/irregular/transferee): ");
                    s.setStudentType(sc.nextLine().trim());
                    boolean ok = um.registerStudent(s);
                    if (ok) System.out.println("Student registered.");
                    else System.out.println("Email already exists.");
                } else if (c.equals("3")) return;
                else System.out.println("Invalid.");
            } else {
                System.out.print("Email: "); String email = sc.nextLine().trim();
                System.out.print("Student ID: "); String sid = sc.nextLine().trim();
                System.out.print("Password: "); String pw = sc.nextLine().trim();
                Student s = um.loginStudent(email, sid, pw);
                if (s == null) System.out.println("Login failed.");
                else new StudentService(um, s).menu(sc);

            }

        }
    }
}
