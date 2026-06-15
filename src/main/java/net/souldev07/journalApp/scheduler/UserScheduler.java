package net.souldev07.journalApp.scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.souldev07.journalApp.cache.AppCache;
import net.souldev07.journalApp.entity.JournalEntry;
import net.souldev07.journalApp.entity.User;
import net.souldev07.journalApp.enums.Sentiment;
import net.souldev07.journalApp.model.SentimentData;
import net.souldev07.journalApp.repository.UserRepositoryImpl;
import net.souldev07.journalApp.service.EmailService;

@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private AppCache appCache;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    @Scheduled(cron = "0 0 9 * * SUN")
    public void fetchUsersAndSendSaMail() {
        List<User> users = userRepository.getUserForSA();
        for (User user : users) {
            List<Sentiment> sentiments = user.getJournalEntries().stream()
                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
                    .map(JournalEntry::getSentiment)
                    .collect(Collectors.toList());

            Map<Sentiment, Integer> counts = new HashMap<>();
            for (Sentiment s : sentiments) {
                if (s != null) counts.put(s, counts.getOrDefault(s, 0) + 1);
            }

            Sentiment top = null;
            int max = 0;
            for (Map.Entry<Sentiment, Integer> e : counts.entrySet()) {
                if (e.getValue() > max) {
                    max = e.getValue();
                    top = e.getKey();
                }
            }

            if (top != null) {
                SentimentData data = SentimentData.builder()
                        .email(user.getEmail())
                        .sentiment("Sentiment for last 7 days: " + top)
                        .build();
                try {
                    kafkaTemplate.send("weekly-sentiments", data.getEmail(), data);
                } catch (Exception e) {
                    emailService.sendEmail(data.getEmail(), "Sentiment", data.getSentiment());
                }
            }
        }
    }

    @Scheduled(cron = "0 0/10 * ? * *")
    public void clearAppCache() {
        appCache.init();
    }
}
