package com.example.mission.model;

public class GeminiRequest {
    private String summary;
    private String universityFormat;
    private String tone = "formal";
    private String detailLevel = "detailed";

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getUniversityFormat() { return universityFormat; }
    public void setUniversityFormat(String universityFormat) { this.universityFormat = universityFormat; }

    public String getTone() { return tone; }
    public void setTone(String tone) { this.tone = tone; }

    public String getDetailLevel() { return detailLevel; }
    public void setDetailLevel(String detailLevel) { this.detailLevel = detailLevel; }
}
