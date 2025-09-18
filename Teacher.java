package model;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class Teacher extends User {
    private final String teacherId;
    private final List<String> subjects; // simple list of subject names or codes

    public Teacher(String id, String name, String email, String password, String teacherId) {
        super(id, name, email, password);
        this.teacherId = teacherId;
        this.subjects = new ArrayList<>();
    }

    public String getTeacherId() { return teacherId; }
    public List<String> getSubjects() { return subjects; }
    public void addSubject(String s) { if (!subjects.contains(s)) subjects.add(s); }

    public JSONObject toJson() {
        JSONObject j = new JSONObject();
        j.put("id", id);
        j.put("name", name);
        j.put("email", email);
        j.put("password", password);
        j.put("teacherId", teacherId);
        j.put("subjects", subjects);
        return j;
    }

    public static Teacher fromJson(JSONObject j) {
        Teacher t = new Teacher(j.getString("id"), j.getString("name"), j.getString("email"),
                j.getString("password"), j.getString("teacherId"));
        if (j.has("subjects")) {
            for (Object o : j.getJSONArray("subjects")) t.addSubject(o.toString());
        }
        return t;
    }
}