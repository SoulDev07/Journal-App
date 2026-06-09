package net.souldev07.journalApp.service;

import org.springframework.stereotype.Service;

@Service
public class SentimentAnalysisService {
    
    public String getSentimentScore(String text) {
        // Placeholder for sentiment analysis logic
        // In a real implementation, this would call an external API or use a machine learning model
        return "0"; // Return a dummy score for now
    }
}
