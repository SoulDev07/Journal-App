package net.souldev07.journalApp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.souldev07.journalApp.entity.JournalEntry;
import net.souldev07.journalApp.repository.JournalEntryRepository;

@Service
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            journalEntry.setUserName(username);
            journalEntry.setDate(LocalDateTime.now());
            journalEntryRepository.save(journalEntry);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving the entry.", e);
        }
    }

    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String username) {
        try {
            Optional<JournalEntry> entryOpt = journalEntryRepository.findById(id);
            if (entryOpt.isPresent() && entryOpt.get().getUserName().equals(username)) {
                journalEntryRepository.deleteById(id);
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting entry: ", e);
            throw new RuntimeException("An error occurred while deleting the entry.", e);
        }
        return false;
    }
}
