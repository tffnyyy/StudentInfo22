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
            switch (m) {
                case "1" -> teacherFlow(sc, um);
                case "2" -> studentFlow(sc, um);
                case "3" -> {
                    System.out.println("Goodbye.");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid");
            }
        }
    }

    private static void teacherFlow(Scanner sc, UserManager um) {
        while (true) {
            System.out.println("\n--- TEACHER ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Forgot Password");
            System.out.println("4. Back");
            System.out.print("Choose: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> {
                    System.out.print("Email: ");
                    String email = sc.nextLine().trim();
                    System.out.print("Teacher ID: ");
                    String tid = sc.nextLine().trim();
                    System.out.print("Password: ");
                    String pw = sc.nextLine().trim();
                    Teacher t = um.loginTeacher(email, tid, pw);
                    if (t == null) System.out.println("Login failed.");
                    else new TeacherService(um, t).menu(sc);
                }
                case "2" -> {
                    System.out.println("Type 'back' anytime to cancel registration.");

                    System.out.print("Name: ");
                    String name = sc.nextLine().trim();
                    if (name.equalsIgnoreCase("back")) break;

                    System.out.print("Email: ");
                    String email = sc.nextLine().trim();
                    if (email.equalsIgnoreCase("back")) break;

                    System.out.print("Password: ");
                    String pw = sc.nextLine().trim();
                    if (pw.equalsIgnoreCase("back")) break;

                    System.out.print("Teacher ID: ");
                    String tid = sc.nextLine().trim();
                    if (tid.equalsIgnoreCase("back")) break;

                    String id = UUID.randomUUID().toString();
                    Teacher t = new Teacher(id, name, email, pw, tid);

                    // 🚨 Removed asking for subjects here.
                    // Teacher will add subjects later in the dashboard.

                    boolean ok = um.registerTeacher(t);
                    if (ok) System.out.println("Teacher registered.");
                    else System.out.println("Email already exists.");
                }
                case "3" -> {
                    System.out.print("Enter your registered email: ");
                    String email = sc.nextLine().trim();
                    um.forgotPassword(email, sc); // reset teacher password
                }
                case "4" -> {
                    return;
                } // 🔙 Back
                default -> System.out.println("Invalid.");
            }
        }
    }

    private static void studentFlow(Scanner sc, UserManager um) {
        while (true) {
            System.out.println("\n--- STUDENT ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Forgot Password");
            System.out.println("4. Back");
            System.out.print("Choose: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> {
                    System.out.print("Email: ");
                    String email = sc.nextLine().trim();
                    System.out.print("Student ID: ");
                    String sid = sc.nextLine().trim();
                    System.out.print("Password: ");
                    String pw = sc.nextLine().trim();
                    Student s = um.loginStudent(email, sid, pw);
                    if (s == null) System.out.println("Login failed.");
                    else new StudentService(um, s).menu(sc);
                }
                case "2" -> {
                    System.out.println("Type 'back' anytime to cancel registration.");

                    System.out.print("Name: ");
                    String name = sc.nextLine().trim();
                    if (name.equalsIgnoreCase("back")) break;

                    System.out.print("Email: ");
                    String email = sc.nextLine().trim();
                    if (email.equalsIgnoreCase("back")) break;

                    System.out.print("Password: ");
                    String pw = sc.nextLine().trim();
                    if (pw.equalsIgnoreCase("back")) break;

                    System.out.print("Student ID: ");
                    String sid = sc.nextLine().trim();
                    if (sid.equalsIgnoreCase("back")) break;

                    // <-- NEW: check student ID early and stop if duplicate
                    if (um.studentIdExists(sid)) {
                        System.out.println("❌ Error: Student ID already exists. Registration cancelled.");
                        break; // don't proceed to next questions
                    }

                    String id = UUID.randomUUID().toString();
                    Student s = new Student(id, name, email, pw, sid);

                    // 🔹 Extra info
                    System.out.print("Year level: ");
                    String year = sc.nextLine().trim();
                    if (year.equalsIgnoreCase("back")) break;
                    s.setYearLevel(year);

                    System.out.print("Course: ");
                    String course = sc.nextLine().trim();
                    if (course.equalsIgnoreCase("back")) break;
                    s.setCourse(course);

                    System.out.print("Type (regular/irregular/transferee): ");
                    String type = sc.nextLine().trim();
                    if (type.equalsIgnoreCase("back")) break;
                    s.setStudentType(type);

                    System.out.print("Age: ");
                    String ageInput = sc.nextLine().trim();
                    if (ageInput.equalsIgnoreCase("back")) break;
                    if (!ageInput.isEmpty()) {
                        try {
                            s.setAge(Integer.parseInt(ageInput));
                        } catch (Exception ignored) {
                        }
                    }

                    System.out.print("Birthdate : ");
                    String bday = sc.nextLine().trim();
                    if (bday.equalsIgnoreCase("back")) break;
                    s.setBirthdate(bday);

                    System.out.print("Mother's Name : ");
                    String mName = sc.nextLine().trim();
                    if (mName.equalsIgnoreCase("back")) break;
                    s.setMotherName(mName);

                    System.out.print("Father's Name : ");
                    String fName = sc.nextLine().trim();
                    if (fName.equalsIgnoreCase("back")) break;
                    s.setFatherName(fName);

                    System.out.print("Contact Number ): ");
                    String cnum = sc.nextLine().trim();
                    if (cnum.equalsIgnoreCase("back")) break;
                    s.setContactNumber(cnum);

                    System.out.print("Address : ");
                    String addr = sc.nextLine().trim();
                    if (addr.equalsIgnoreCase("back")) break;
                    s.setAddress(addr);

                    System.out.print("Organization : ");
                    String org = sc.nextLine().trim();
                    if (org.equalsIgnoreCase("back")) break;
                    s.setOrganization(org);

                    System.out.print("Hobbies : ");
                    String hobbies = sc.nextLine().trim();
                    if (hobbies.equalsIgnoreCase("back")) break;
                    s.setHobbies(hobbies);

                    boolean ok = um.registerStudent(s);
                    if (ok) System.out.println("Student registered.");
                    else System.out.println("Email already exists or ID conflict.");
                }
                case "3" -> {
                    System.out.print("Enter your registered email: ");
                    String email = sc.nextLine().trim();
                    um.forgotPassword(email, sc); // reset student password
                }
                case "4" -> {
                    return;
                } // 🔙 Back
                default -> System.out.println("Invalid.");
            }
        }
    }
}