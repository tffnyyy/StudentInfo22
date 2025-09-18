package storage;

import model.Teacher;
import model.Student;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataStore {
    private static final Logger LOGGER = Logger.getLogger(DataStore.class.getName());

    private final File teacherFile = new File("teachers.json");
    private final File studentFile = new File("students.json");

    public DataStore() {
        try {
            // createNewFile() returns boolean, check result
            if (!teacherFile.exists() && teacherFile.createNewFile()) {
                LOGGER.info("Created new teacher file.");
            }
            if (!studentFile.exists() && studentFile.createNewFile()) {
                LOGGER.info("Created new student file.");
            }

            // initialize if empty
            if (teacherFile.length() == 0) {
                writeJsonArray(teacherFile, new JSONArray());
            }
            if (studentFile.length() == 0) {
                writeJsonArray(studentFile, new JSONArray());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error initializing datastore files", e);
        }
    }

    private JSONArray readJsonArray(File f) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            if (sb.isEmpty()) return new JSONArray(); // cleaner
            return new JSONArray(sb.toString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reading JSON from " + f.getName(), e);
            return new JSONArray();
        }
    }

    private void writeJsonArray(File f, JSONArray arr) {
        try (FileWriter fw = new FileWriter(f, false)) {
            fw.write(arr.toString(2));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing JSON to " + f.getName(), e);
        }
    }

    public List<Teacher> loadTeachers() {
        JSONArray arr = readJsonArray(teacherFile);
        List<Teacher> out = new ArrayList<>();
        for (Object o : arr) out.add(Teacher.fromJson((JSONObject) o));
        return out;
    }

    public List<Student> loadStudents() {
        JSONArray arr = readJsonArray(studentFile);
        List<Student> out = new ArrayList<>();
        for (Object o : arr) out.add(Student.fromJson((JSONObject) o));
        return out;
    }

    public void saveTeachers(List<Teacher> teachers) {
        JSONArray arr = new JSONArray();
        for (Teacher t : teachers) arr.put(t.toJson());
        writeJsonArray(teacherFile, arr);
    }

    public void saveStudents(List<Student> students) {
        JSONArray arr = new JSONArray();
        for (Student s : students) arr.put(s.toJson());
        writeJsonArray(studentFile, arr);
    }
}