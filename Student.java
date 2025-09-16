package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class Student {
    private final String id;
    private String name;
    private String email;
    private String password;
    private String studentId;

    private String yearLevel;
    private String course;
    private String studentType;
    private int age;
    private String birthdate;
    private String motherName;
    private String fatherName;
    private String contactNumber;
    private String address;
    private String organization;
    private String hobbies;

    // 🔹 Academic data
    private final Map<String, String> grades = new HashMap<>();
    private final List<String> schedule = new ArrayList<>(); // subject + time
    private String teacherId;

    public Student(String id, String name, String email, String password, String studentId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.studentId = studentId;
    }

    // ===== Getters & Setters =====
    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getStudentType() { return studentType; }
    public void setStudentType(String studentType) { this.studentType = studentType; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getHobbies() { return hobbies; }
    public void setHobbies(String hobbies) { this.hobbies = hobbies; }

    // ===== Academic Methods =====
    public Map<String, String> getGrades() { return grades; }

    public void setGrade(String subject, String grade) {
        grades.put(subject, grade);
    }

    public List<String> getSchedule() { return schedule; }

    public void addToSchedule(String subject, String time) {
        this.schedule.add(subject + " (" + time + ")");
    }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    // ===== JSON Persistence =====
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
        j.put("age", age);
        j.put("birthdate", birthdate);
        j.put("motherName", motherName);
        j.put("fatherName", fatherName);
        j.put("contactNumber", contactNumber);
        j.put("address", address);
        j.put("organization", organization);
        j.put("hobbies", hobbies);
        j.put("grades", grades);
        j.put("schedule", schedule);
        j.put("teacherId", teacherId);
        return j;
    }

    public static Student fromJson(JSONObject j) {
        Student s = new Student(
                j.getString("id"),
                j.getString("name"),
                j.getString("email"),
                j.getString("password"),
                j.getString("studentId")
        );

        s.setYearLevel(j.optString("yearLevel", null));
        s.setCourse(j.optString("course", null));
        s.setStudentType(j.optString("studentType", null));
        s.setAge(j.optInt("age", 0));
        s.setBirthdate(j.optString("birthdate", null));
        s.setMotherName(j.optString("motherName", null));
        s.setFatherName(j.optString("fatherName", null));
        s.setContactNumber(j.optString("contactNumber", null));
        s.setAddress(j.optString("address", null));
        s.setOrganization(j.optString("organization", null));
        s.setHobbies(j.optString("hobbies", null));
        s.setTeacherId(j.optString("teacherId", null));

        if (j.has("grades")) {
            JSONObject gr = j.getJSONObject("grades");
            for (String key : gr.keySet()) {
                s.setGrade(key, gr.getString(key));
            }
        }

        if (j.has("schedule")) {
            JSONArray arr = j.getJSONArray("schedule");
            for (Object o : arr) s.schedule.add(o.toString());
        }

        return s;
    }
}
