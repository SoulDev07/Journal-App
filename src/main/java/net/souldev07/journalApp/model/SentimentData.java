package net.souldev07.journalApp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SentimentData {
    private String email;
    private String sentiment;
}
