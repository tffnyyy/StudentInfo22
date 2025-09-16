package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class Student extends User {
    private final String studentId;
    private String yearLevel;
    private String course;
    private String studentType; // regular/irregular/transferee
    private final Map<String, Double> grades; // subject -> grade
    private final List<String> schedule; // subject list (or use ScheduleEntry if needed later)
    private final List<String> subjects; // ✅ subjects directly assigned by teacher
    private String teacherId; // assigned teacher id (optional)

    public Student(String id, String name, String email, String password, String studentId) {
        super(id, name, email, password);
        this.studentId = studentId;
        this.yearLevel = "";
        this.course = "";
        this.studentType = "regular";
        this.grades = new HashMap<>();
        this.schedule = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.teacherId = "";
    }

    public String getStudentId() { return studentId; }
    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String y) { this.yearLevel = y; }
    public String getCourse() { return course; }
    public void setCourse(String c) { this.course = c; }
    public String getStudentType() { return studentType; }
    public void setStudentType(String t) { this.studentType = t; }

    public Map<String, Double> getGrades() { return grades; }
    public void setGrade(String subject, Double grade) { grades.put(subject, grade); }

    public List<String> getSchedule() { return schedule; }
    public void addToSchedule(String subject) {
        if (!schedule.contains(subject)) schedule.add(subject);
    }

    // ✅ New: subjects management
    public List<String> getSubjects() { return subjects; }
    public void addSubject(String subject) {
        if (!subjects.contains(subject)) subjects.add(subject);
    }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public JSONObject toJson() {
        JSONObject j = new JSONObject();
        j.put("id", id);
        j.put("name", name);
        j.put("email", email);
        j.put("password", password);
        j.put("studentId", studentId);
        j.put("yearLevel", yearLevel);
        j.put("course", course);
        j.put("studentType", studentType);
        j.put("grades", grades);
        j.put("schedule", schedule);
        j.put("subjects", subjects); // ✅ save subjects
        j.put("teacherId", teacherId);
        return j;
    }

    public static Student fromJson(JSONObject j) {
        Student s = new Student(j.getString("id"), j.getString("name"), j.getString("email"),
                j.getString("password"), j.getString("studentId"));
        if (j.has("yearLevel")) s.setYearLevel(j.getString("yearLevel"));
        if (j.has("course")) s.setCourse(j.getString("course"));
        if (j.has("studentType")) s.setStudentType(j.getString("studentType"));
        if (j.has("grades")) {
            JSONObject g = j.getJSONObject("grades");
            for (String key : g.keySet()) s.setGrade(key, g.getDouble(key));
        }
        if (j.has("schedule")) {
            for (Object o : j.getJSONArray("schedule")) s.addToSchedule(o.toString());
        }
        if (j.has("subjects")) { // ✅ load subjects
            for (Object o : j.getJSONArray("subjects")) s.addSubject(o.toString());
        }
        if (j.has("teacherId")) s.setTeacherId(j.getString("teacherId"));
        return s;
    }
}
