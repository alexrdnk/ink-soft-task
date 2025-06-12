package com.example.localnews_backend.service;

import com.example.localnews_backend.model.Article;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class LlmClassifier {
    private final OpenAiService openAi;
    private final ObjectMapper mapper = new ObjectMapper();

    public LlmClassifier(@Value("${openai.api.key}") String apiKey) {
        this.openAi = new OpenAiService(apiKey);
    }

    public Classification classify(Article article) throws Exception {
        // 1) System prompt to enforce JSON-only output
        String systemPrompt = """
            You are a JSON-only classifier. Input is a news article title and snippet.
            You must respond with exactly one JSON object, no extra text, in this form:
            
            {
              "scope": "LOCAL" or "GLOBAL",
              "cityState": "<City Name>, <State Code>" or null
            }
            
            - If GLOBAL, cityState must be null.
            - If LOCAL, cityState must match one city from the US cities list.
            """;

        // 2) User prompt with the actual article
        String userPrompt = String.format(
                "Title: %s\n\nSnippet: %s",
                article.getTitle(),
                article.getBody().length() > 200
                        ? article.getBody().substring(0, 200) + "…"
                        : article.getBody()
        );

        var request = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(Arrays.asList(
                        new ChatMessage("system", systemPrompt),
                        new ChatMessage("user", userPrompt)
                ))
                .temperature(0.0)  // deterministic
                .build();

        var response = openAi.createChatCompletion(request);
        ChatCompletionChoice choice = response.getChoices().get(0);
        String content = choice.getMessage().getContent();

        // 3) Parse the JSON
        JsonNode node = mapper.readTree(content);
        String scope = node.get("scope").asText();
        JsonNode csNode = node.get("cityState");
        String cityState = csNode.isNull() ? null : csNode.asText();

        return new Classification(scope, cityState);
    }

    /** Simple DTO for the two fields we care about. */
    public record Classification(String scope, String cityState) {}
}
