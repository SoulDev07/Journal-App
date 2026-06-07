package net.souldev07.journalApp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.souldev07.journalApp.entity.JournalEntry;
import net.souldev07.journalApp.entity.User;
import net.souldev07.journalApp.repository.JournalEntryRepository;

@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    public void saveEntry(JournalEntry journalEntry) {
        journalEntry.setDate(LocalDateTime.now());
        journalEntryRepository.save(journalEntry);
    }

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        journalEntry.setDate(LocalDateTime.now());
        journalEntryRepository.save(journalEntry);
        user.getJournalEntries().add(journalEntry);
        userService.saveUser(user);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String username) {
        User user = userService.findByUsername(username);
        boolean removed = false;
        removed = user.getJournalEntries().removeIf(entry -> entry.getId().equals(id));

        if (removed) {
            userService.saveUser(user);
            journalEntryRepository.deleteById(id);
        }

        return removed;
    }
}
