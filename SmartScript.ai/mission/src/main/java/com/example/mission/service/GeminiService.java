package com.example.mission.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class GeminiService {

    // ✅ Load API key from application.properties
    @Value("${gemini.api.key}")
    private String API_KEY;

    // ✅ Gemini API endpoint (newest v1beta)
    private static final String MODEL_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent";

    // ✅ Main method (with content improvement option)
    public String generateFormattedContent(String summary, String format) throws IOException {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        // ✅ STEP 1: Improve content quality before formatting
        String improvedSummary = improveContent(summary, client);
        if (improvedSummary == null || improvedSummary.isEmpty()) {
            improvedSummary = summary; // fallback if API fails
        }

        // ✅ STEP 2: Generate formatted research paper
        String prompt = generatePromptByFormat(format, improvedSummary);

        System.out.println("✅ Gemini request started");
        System.out.println("➡️ Model URL: " + MODEL_URL);
        System.out.println("🧠 API Key prefix: " + (API_KEY != null ? API_KEY.substring(0, 5) + "*****" : "NULL"));
        System.out.println("📝 Prompt generated for format: " + format);

        String jsonRequest = "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"parts\": [\n" +
                "        { \"text\": \"" + prompt.replace("\"", "\\\"") + "\" }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        RequestBody body = RequestBody.create(
                jsonRequest,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(MODEL_URL + "?key=" + API_KEY)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return "❌ Error: " + response.code() + " - " + response.message() +
                        "\nBody: " + (response.body() != null ? response.body().string() : "No details");
            }

            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

            if (!jsonObject.has("candidates")) {
                return "⚠️ No content generated or invalid response.\nResponse:\n" + responseBody;
            }

            JsonArray candidates = jsonObject.getAsJsonArray("candidates");
            if (candidates.size() > 0) {
                JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");

                if (parts != null && parts.size() > 0 && parts.get(0).getAsJsonObject().has("text")) {
                    return parts.get(0).getAsJsonObject().get("text").getAsString();
                }
            }

            return "⚠️ No text generated.\nResponse:\n" + responseBody;

        } catch (IOException e) {
            return "🚨 IOException occurred: " + e.getMessage();
        }
    }

    // ✅ Function to automatically enhance the project summary before paper generation
    private String improveContent(String summary, OkHttpClient client) throws IOException {
        System.out.println("🔍 Improving content quality before formatting...");

        String improvementPrompt = "Improve the following project summary by making it more academic, structured, and grammatically correct. " +
                "Keep all the technical meaning same but enhance the flow, clarity, and tone. Do not exceed 300 words.\n\nSummary:\n" + summary;

        String jsonRequest = "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"parts\": [\n" +
                "        { \"text\": \"" + improvementPrompt.replace("\"", "\\\"") + "\" }\n" +
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
                System.out.println("⚠️ Improvement request failed: " + response.code());
                return summary;
            }

            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

            if (!jsonObject.has("candidates")) return summary;

            JsonArray candidates = jsonObject.getAsJsonArray("candidates");
            if (candidates.size() > 0) {
                JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");

                if (parts != null && parts.size() > 0 && parts.get(0).getAsJsonObject().has("text")) {
                    String improvedText = parts.get(0).getAsJsonObject().get("text").getAsString();
                    System.out.println("✅ Content improved successfully");
                    return improvedText;
                }
            }

        } catch (IOException e) {
            System.out.println("🚨 Error improving content: " + e.getMessage());
        }
        return summary;
    }

    // ✅ Prompt templates for various formats
    private String generatePromptByFormat(String format, String summary) {
        switch (format.toLowerCase()) {
            case "ieee":
                return "Generate a research paper in IEEE format. Include these sections:\n" +
                        "1. Abstract\n2. Keywords\n3. Introduction\n4. Related Work\n5. Methodology\n6. Results\n7. Conclusion\n8. References\n\nImproved Project Summary:\n" + summary;

            case "springer":
                return "Generate a research paper in Springer format with sections:\n" +
                        "Abstract, Keywords, Introduction, Methods, Results, Discussion, Conclusion, References.\n\nImproved Project Summary:\n" + summary;

            case "elsevier":
                return "Generate a research paper in Elsevier format including:\n" +
                        "Title, Abstract, Introduction, Methods, Results, Discussion, Conclusion, References.\n\nImproved Project Summary:\n" + summary;

            case "ijme":
                return "Generate a manuscript in IJME format including:\n" +
                        "Title, Abstract, Introduction, Methodology, Discussion, Findings, References.\n\nImproved Project Summary:\n" + summary;

            default:
                return "Generate a well-structured academic paper based on this improved summary:\n" + summary;
        }
    }
}
