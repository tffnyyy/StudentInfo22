package model;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class Teacher extends User {
    private final String teacherId;
    private Map<String, String> subjects; // subject -> time

    public Teacher(String id, String name, String email, String password, String teacherId) {
        super(id, name, email, password);
        this.teacherId = teacherId;
        this.subjects = new HashMap<>();
    }

    public String getTeacherId() { return teacherId; }

    // Expose the map (caller should not replace the map reference ideally)
    public Map<String, String> getSubjects() { return subjects; }
    public void setSubjects(Map<String, String> subjects) { this.subjects = subjects; }

    // Convenience
    public void addSubject(String subject) {
        if (subject == null || subject.isEmpty()) return;
        subjects.putIfAbsent(subject, "");
    }

    public void addSubject(String subject, String time) {
        if (subject == null || subject.isEmpty()) return;
        subjects.put(subject, time == null ? "" : time);
    }

    public JSONObject toJson() {
        JSONObject j = new JSONObject();
        j.put("id", id);
        j.put("name", name);
        j.put("email", email);
        j.put("password", password);
        j.put("teacherId", teacherId);
        // store subjects as an object for easy parsing later
        j.put("subjects", new JSONObject(subjects));
        return j;
    }

    public static Teacher fromJson(JSONObject j) {
        Teacher t = new Teacher(
                j.getString("id"),
                j.getString("name"),
                j.getString("email"),
                j.getString("password"),
                j.getString("teacherId")
        );

        if (j.has("subjects")) {
            Object subsObj = j.get("subjects");
            // Support older format (JSONArray of strings) or new JSONObject map
            if (subsObj instanceof JSONArray) {
                JSONArray arr = j.getJSONArray("subjects");
                for (Object o : arr) {
                    // old format: just subject names without times
                    t.addSubject(o.toString());
                }
            } else if (subsObj instanceof JSONObject) {
                JSONObject subjObj = j.getJSONObject("subjects");
                for (String key : subjObj.keySet()) {
                    t.addSubject(key, subjObj.optString(key, ""));
                }
            }
        }
        return t;
    }
}
