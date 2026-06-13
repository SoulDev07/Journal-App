package net.souldev07.journalApp.scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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
    private UserRepositoryImpl userRepository;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AppCache appCache;

    @Scheduled(cron = "0 0 9 * * SUN")
    public void fetchUsersAndSendSentimentAnalysisMail() {
        List<User> users = userRepository.getUsersWithSentimentAnalysisEnabled();
        for (User user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream()
                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
                    .map(x -> x.getSentiment()).collect(Collectors.toList());

            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                if (sentiment != null)
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }

            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }

            if (mostFrequentSentiment != null) {
                SentimentData sentimentData = SentimentData.builder()
                        .email(user.getEmail())
                        .sentiment("Sentiment for last 7 days: " + mostFrequentSentiment)
                        .build();
                try {
                    ListenableFuture<SendResult<String, SentimentData>> sendFuture = kafkaTemplate
                            .send("weekly-sentiments", sentimentData.getEmail(), sentimentData);
                    sendFuture.addCallback(new ListenableFutureCallback<SendResult<String, SentimentData>>() {
                        @Override
                        public void onFailure(Throwable ex) {
                            sendSentimentEmail(sentimentData);
                        }

                        @Override
                        public void onSuccess(SendResult<String, SentimentData> result) {
                        }
                    });
                } catch (Exception e) {
                    sendSentimentEmail(sentimentData);
                }
            }
        }
    }

    @Scheduled(cron = "0 0/10 * ? * *")
    public void clearAppCache() {
        appCache.init();
    }

    private void sendSentimentEmail(SentimentData sentimentData) {
        emailService.sendEmail(sentimentData.getEmail(), "Sentiment for previous week", sentimentData.getSentiment());
    }
}
