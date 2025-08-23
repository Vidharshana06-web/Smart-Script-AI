package com.example.mission.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class GeminiService {

    private static final String API_KEY = "AIzaSyCOWt9_fsq5cbnX0bKblYp6-9piGsoiaOM"; // Replace with your Gemini API key
    private static final String MODEL_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public String generateFormattedContent(String summary, String format) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String prompt = generatePromptByFormat(format, summary);

        String jsonRequest = "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"parts\": [\n" +
                "        {\n" +
                "          \"text\": \"" + prompt.replace("\"", "\\\"") + "\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(MODEL_URL + "?key=" + API_KEY)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "❌ Error: " + response.code() + " - " + response.message();
            }

            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray candidates = jsonObject.getAsJsonArray("candidates");

            if (candidates.size() > 0) {
                JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");
                return parts.get(0).getAsJsonObject().get("text").getAsString();
            }

            return "⚠️ No content generated.";
        }
    }

    private String generatePromptByFormat(String format, String summary) {
        switch (format.toLowerCase()) {
            case "ieee":
                return "Generate a research paper in IEEE format. Include the following sections in order:\n" +
                        "1. Abstract\n" +
                        "2. Keywords\n" +
                        "3. Introduction\n" +
                        "4. Related Work (if applicable)\n" +
                        "5. Methodology\n" +
                        "6. Experimental Results\n" +
                        "7. Discussion\n" +
                        "8. Conclusion\n" +
                        "9. References\n\n" +
                        "Project Summary:\n" + summary;

            case "springer":
                return "Generate a research paper in Springer format. Include these sections:\n" +
                        "1. Title\n" +
                        "2. Abstract\n" +
                        "3. Keywords\n" +
                        "4. Introduction\n" +
                        "5. Materials and Methods\n" +
                        "6. Results\n" +
                        "7. Discussion and Conclusions\n" +
                        "8. Acknowledgments\n" +
                        "9. References\n\n" +
                        "Project Summary:\n" + summary;

            case "elsevier":
                return "Generate a research paper in Elsevier journal format. Include:\n" +
                        "1. Title\n" +
                        "2. Abstract\n" +
                        "3. Introduction\n" +
                        "4. Materials and Methods\n" +
                        "5. Results\n" +
                        "6. Discussion\n" +
                        "7. Conclusion\n" +
                        "8. Acknowledgments\n" +
                        "9. References\n\n" +
                        "Project Summary:\n" + summary;

            case "ijme":
                return "Generate a manuscript in IJME (Indian Journal of Medical Ethics) format. Include:\n" +
                        "1. Title\n" +
                        "2. Abstract or Summary\n" +
                        "3. Introduction / Background\n" +
                        "4. Research Objectives / Study Aim\n" +
                        "5. Methodology / Ethical Framework\n" +
                        "6. Analysis / Discussion\n" +
                        "7. Findings / Implications\n" +
                        "8. References\n\n" +
                        "Project Summary:\n" + summary;

            default:
                return "Generate a well-structured research paper with Abstract, Introduction, Methodology, Results, and Conclusion based on this summary:\n" + summary;
        }
    }
}
