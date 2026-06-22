package net.souldev07.journalApp.consumer;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import net.souldev07.journalApp.model.JournalEntryEvent;

@Service
@Slf4j
public class JournalEntryConsumer {

    @Value("${sentiment.service.url:http://SENTIMENT-SERVICE}")
    private String sentimentServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(topics = "journal-entries", groupId = "sentiment-analysis-group")
    public void consume(JournalEntryEvent event) {
        log.info("Received journal entry event for analysis: {}", event.getEntryId());
        try {
            Map<String, String> request = new HashMap<>();
            request.put("entry_id", event.getEntryId());
            request.put("user_id", event.getUserId());
            request.put("content", event.getContent());

            // Call Python Sentiment Service
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) restTemplate.postForObject(
                    sentimentServiceUrl + "/analyze", request, Map.class);

            if (response != null) {
                String sentiment = (String) response.get("sentiment");
                if (sentiment != null) {
                    Map<String, String> body = new HashMap<>();
                    body.put("sentiment", sentiment);

                    restTemplate.put(
                            "http://JOURNAL-SERVICE/internal/" + event.getEntryId() + "/sentiment",
                            body);
                    log.info("Successfully updated sentiment to {} for entry {}", sentiment, event.getEntryId());
                }
            }
        } catch (Exception e) {
            log.error("Failed to analyze or write back sentiment: ", e);
        }
    }
}
