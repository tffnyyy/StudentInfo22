package service;

import model.Teacher;
import model.Student;
import storage.DataStore;

import java.util.*;

public class UserManager {
    private final List<Teacher> teachers;
    private final List<Student> students;
    private final DataStore ds;

    public UserManager(DataStore ds) {
        this.ds = ds;
        this.teachers = ds.loadTeachers();
        this.students = ds.loadStudents();
    }

    // TEACHER methods
    public boolean registerTeacher(Teacher t) {
        if (findTeacherByEmail(t.getEmail()) != null) return false;
        teachers.add(t);
        persistTeachers();
        return true;
    }

    public Teacher loginTeacher(String email, String teacherId, String password) {
        Teacher t = findTeacherByEmail(email);
        if (t != null && t.getTeacherId().equals(teacherId) && t.getPassword().equals(password)) return t;
        return null;
    }

    public Teacher findTeacherByEmail(String email) {
        for (Teacher t : teachers) if (t.getEmail().equalsIgnoreCase(email)) return t;
        return null;
    }

    public Teacher findTeacherByTeacherId(String teacherId) {
        for (Teacher t : teachers) if (t.getTeacherId().equals(teacherId)) return t;
        return null;
    }

    // STUDENT method
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
        if (s != null && s.getStudentId().equals(studentId) && s.getPassword().equals(password)) return s;
        return null;
    }

    public Student findStudentByEmail(String email) {
        for (Student s : students) if (s.getEmail().equalsIgnoreCase(email)) return s;
        return null;
    }

    public Student findStudentByStudentId(String id) {
        for (Student s : students) if (s.getStudentId().equals(id)) return s;
        return null;
    }

    public List<Student> getAllStudents() { return students; }

    public void persistTeachers() { ds.saveTeachers(teachers); }
    public void persistStudents() { ds.saveStudents(students); }

    // utility: connect teacher <-> student by teacherId
    public void assignTeacherToStudent(String teacherId, String studentId) {
        Student s = findStudentByStudentId(studentId);
        if (s != null) {
            s.setTeacherId(teacherId);
            persistStudents();


        }
    }
}
