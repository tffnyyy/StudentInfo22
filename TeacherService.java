package service;

import model.Student;
import model.Teacher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public record TeacherService(UserManager um, Teacher teacher) {

    public void menu(Scanner sc) {
        while (true) {
            System.out.println("\n=== Teacher Menu (" + teacher.getName() + ") ===");
            System.out.println("1. Add student");
            System.out.println("2. Input grades");
            System.out.println("3. Set student year/course/type");
            System.out.println("4. View student info");
            System.out.println("5. Manage class students");
            System.out.println("6. Remove student");
            System.out.println("7. View schedule");
            System.out.println("8. Add subject");
            System.out.println("9. Back / Logout");
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> addStudent(sc);
                case "2" -> inputGrades(sc);
                case "3" -> setStudentMeta(sc);
                case "4" -> viewStudentInfo(sc);
                case "5" -> manageClassStudents(sc);
                case "6" -> removeStudent(sc);
                case "7" -> viewSchedule();
                case "8" -> addSubject(sc);
                case "9" -> { return; }
                default -> System.out.println("Invalid");
            }
        }
    }

    private void addStudent(Scanner sc) {
        System.out.print("Enter Student ID(s) (comma-separated): ");
        String input = sc.nextLine().trim();
        System.out.print("Enter Subject: ");
        String subject = sc.nextLine().trim();

        if (!teacher.getSubjects().containsKey(subject)) {
            System.out.println("❌ You are not assigned to the subject: " + subject + ". Add it first or choose another subject.");
            return;
        }

        String time = teacher.getSubjects().get(subject); // may be empty string

        for (String sid : input.split(",")) {
            sid = sid.trim();
            Student s = um.findStudentByStudentId(sid);
            if (s != null) {
                s.setTeacherId(teacher.getTeacherId());
                s.addToSchedule(subject, time);
                boolean ok = um.updateStudent(s);
                if (ok) {
                    System.out.println("✅ Added " + s.getName() + " with subject: " + subject + (time.isEmpty() ? "" : " (" + time + ")"));
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

                boolean removed = false;
                List<String> keep = new ArrayList<>();
                for (String sched : s.getSchedule()) {
                    String subjPart = sched;
                    int idx = sched.indexOf(" (");
                    if (idx > 0) subjPart = sched.substring(0, idx);
                    if (!subjPart.equalsIgnoreCase(sub)) keep.add(sched);
                    else removed = true;
                }

                if (removed) {
                    s.getSchedule().clear();
                    s.getSchedule().addAll(keep);
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
        if (!teacher.getSubjects().containsKey(sub)) {
            System.out.println("You don't teach that subject.");
            return;
        }
        System.out.print("Grade (numeric): ");
        Double g;
        try {
            g = Double.valueOf(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid number.");
            return;
        }
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
        s.addToSchedule(subject, time);
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
        System.out.println("Schedule: " + s.getSchedule());

        System.out.println("Grades:");
        if (s.getGrades().isEmpty()) {
            System.out.println("  No grades available.");
        } else {
            Set<String> taught = new HashSet<>();
            for (String tSub : teacher.getSubjects().keySet()) taught.add(tSub.toLowerCase());

            for (String subj : s.getGrades().keySet()) {
                if (taught.contains(subj.toLowerCase())) {
                    System.out.println("  " + subj + ": " + s.getGrades().get(subj));
                }
            }
        }
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

        for (int i = 0; i < assigned.size(); i++) {
            Student s = assigned.get(i);
            System.out.println((i + 1) + ". " + s.getName() + " (" + s.getStudentId() + ") - " + s.getCourse());
        }

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

    private void viewSchedule() {
        System.out.println("\n=== Your Teaching Schedule ===");
        if (teacher.getSubjects().isEmpty()) {
            System.out.println("You have no assigned subjects yet.");
            return;
        }

        for (Map.Entry<String, String> entry : teacher.getSubjects().entrySet()) {
            String subj = entry.getKey();
            String time = entry.getValue();
            if (time == null || time.isEmpty()) System.out.println("- " + subj);
            else System.out.println("- " + subj + " [" + time + "]");
        }
    }

    private void addSubject(Scanner sc) {
        System.out.print("Enter new subject name: ");
        String subject = sc.nextLine().trim();

        if (subject.isEmpty()) {
            System.out.println("Subject name cannot be empty.");
            return;
        }
        if (teacher.getSubjects().containsKey(subject)) {
            System.out.println("You already teach this subject.");
            return;
        }

        System.out.print("Enter schedule time (weekdays and time): ");
        String time = sc.nextLine().trim();

        teacher.getSubjects().put(subject, time);
        if (!um.updateTeacher(teacher)) {
            um.persistTeachers(); // fallback
        }
        System.out.println("✅ Subject '" + subject + "' has been added to your schedule (" + time + ").");
    }
}
