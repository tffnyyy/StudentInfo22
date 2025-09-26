package service;

import model.Teacher;
import model.Student;
import storage.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserManager {
    private final List<Teacher> teachers;
    private final List<Student> students;
    private final DataStore ds;

    public UserManager(DataStore ds) {
        this.ds = ds;
        this.teachers = ds.loadTeachers();
        this.students = ds.loadStudents();
    }

    // ---------------- TEACHER METHODS ----------------
    public boolean registerTeacher(Teacher t) {
        // Check email duplication
        if (findTeacherByEmail(t.getEmail()) != null) {
            System.out.println("Error: Email is already registered.");
            return false;
        }

        // Check teacherId duplication
        if (findTeacherByTeacherId(t.getTeacherId()) != null) {
            System.out.println("Error: Teacher ID already exists.");
            return false;
        }

        teachers.add(t);
        persistTeachers();
        return true;
    }

    public Teacher loginTeacher(String email, String teacherId, String password) {
        Teacher t = findTeacherByEmail(email);
        if (t != null && t.getTeacherId().equals(teacherId) && t.getPassword().equals(password)) {
            return t;
        }
        return null;
    }

    public Teacher findTeacherByEmail(String email) {
        for (Teacher t : teachers) {
            if (t.getEmail() != null && t.getEmail().equalsIgnoreCase(email)) return t;
        }
        return null;
    }

    public Teacher findTeacherByTeacherId(String teacherId) {
        for (Teacher t : teachers) {
            if (t.getTeacherId() != null && t.getTeacherId().equals(teacherId)) return t;
        }
        return null;
    }

    // ---------------- STUDENT METHODS ----------------
    public boolean registerStudent(Student s) {
        // Check email duplication
        if (findStudentByEmail(s.getEmail()) != null) {
            System.out.println("Error: Email is already registered.");
            return false;
        }
        // Check studentId duplication
        if (findStudentByStudentId(s.getStudentId()) != null) {
            System.out.println("Error: Student ID already exists.");
            return false;
        }

        students.add(s);
        persistStudents();
        return true;
    }

    public Student loginStudent(String email, String studentId, String password) {
        Student s = findStudentByEmail(email);
        if (s != null && s.getStudentId().equals(studentId) && s.getPassword().equals(password)) {
            return s;
        }
        return null;
    }

    public Student findStudentByEmail(String email) {
        for (Student s : students) {
            if (s.getEmail() != null && s.getEmail().equalsIgnoreCase(email)) return s;
        }
        return null;
    }

    public Student findStudentByStudentId(String id) {
        for (Student s : students) {
            if (s.getStudentId() != null && s.getStudentId().equals(id)) return s;
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return students;
    }

    // ---------------- PERSISTENCE ----------------
    public void persistTeachers() {
        ds.saveTeachers(teachers);
    }

    public void persistStudents() {
        ds.saveStudents(students);
    }

    // ---------------- UTILITIES ----------------
    // assign teacher -> student
    public boolean assignTeacherToStudent(String teacherId, String studentId) {
        Student s = findStudentByStudentId(studentId);
        if (s != null) {
            s.setTeacherId(teacherId);
            persistStudents(); // save to JSON
            return true;
        }
        return false;
    }

    // update existing student (for subject, etc.)
    public boolean updateStudent(Student updated) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(updated.getStudentId())) {
                students.set(i, updated);
                persistStudents();
                return true;
            }
        }
        return false;
    }

    // get all students of a teacher
    public List<Student> getStudentsByTeacherId(String teacherId) {
        List<Student> assigned = new ArrayList<>();
        for (Student s : students) {
            if (teacherId.equals(s.getTeacherId())) {
                assigned.add(s);
            }
        }
        return assigned;
    }

    // --------- Helper: check if studentId exists ----------
    public boolean studentIdExists(String studentId) {
        for (Student s : students) {
            if (s.getStudentId() != null && s.getStudentId().equals(studentId)) {
                return true;
            }
        }
        return false;
    }

    // --------- Helper: check if teacherId exists ----------
    public boolean teacherIdExists(String teacherId) {
        for (Teacher t : teachers) {
            if (t.getTeacherId() != null && t.getTeacherId().equals(teacherId)) {
                return true;
            }
        }
        return false;
    }

    // --------- Forgot password (console reset) ----------
    public void forgotPassword(String email, Scanner sc) {
        if (email == null || email.isEmpty()) {
            System.out.println("❌ Please provide an email.");
            return;
        }

        // search students first
        for (Student s : students) {
            if (s.getEmail() != null && s.getEmail().equalsIgnoreCase(email)) {
                System.out.print("Enter new password for student (" + s.getName() + "): ");
                String newPass = sc.nextLine().trim();
                s.setPassword(newPass);
                persistStudents();
                System.out.println("✅ Student password reset successfully.");
                return;
            }
        }

        // search teachers next
        for (Teacher t : teachers) {
            if (t.getEmail() != null && t.getEmail().equalsIgnoreCase(email)) {
                System.out.print("Enter new password for teacher (" + t.getName() + "): ");
                String newPass = sc.nextLine().trim();
                t.setPassword(newPass);
                persistTeachers();
                System.out.println("✅ Teacher password reset successfully.");
                return;
            }
        }

        System.out.println("❌ Email not found.");
    }


    public boolean updateTeacher(Teacher updated) {
        for (int i = 0; i < teachers.size(); i++) {
            Teacher t = teachers.get(i);
            if (t.getTeacherId() != null && t.getTeacherId().equals(updated.getTeacherId())) {
                teachers.set(i, updated);
                persistTeachers();
                return true;
            }
        }
        return false;
    }
}