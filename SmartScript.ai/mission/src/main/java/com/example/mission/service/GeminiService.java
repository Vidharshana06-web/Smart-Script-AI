package com.example.mission.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeminiService {

    private static final String API_KEY = "AIzaSyB2qDLL3JIrAPA_JTy1BA3kgeZtRmxlaFU"; // Replace with your Gemini API key
    private static final String MODEL_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public String generateFormattedContent(String summary, String format) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String prompt = generatePromptByFormat(format, summary);

        String jsonRequest = "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"parts\": [\n" +
                "        {\n" +
                "          \"text\": \"" + prompt + "\"\n" +
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
                return "Generate an IEEE-formatted research paper with Abstract, Introduction, Methodology, Results, and Conclusion for this summary:\n\n" + summary;
            case "springer":
                return "Generate a Springer journal research article with typical sections like Abstract, Background, Proposed Method, Results, and References based on:\n\n" + summary;
            case "elsevier":
                return "Create an Elsevier journal-style research manuscript. Include Abstract, Introduction, Materials and Methods, Results, Discussion, and Conclusion. Base it on:\n\n" + summary;
            case "ijee":
                return "Write an IJEE-format academic research paper with sections such as Introduction, Related Works, Implementation, and Experimental Results using:\n\n" + summary;
            case "ijme":
                return "Generate an IJME-format manuscript including Abstract, Research Objectives, Methodology, Analysis, and Findings. Use this summary:\n\n" + summary;
            default:
                return "Generate a research paper based on this summary:\n\n" + summary;
        }
    }
}
