package net.souldev07.journalApp.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.souldev07.journalApp.repository.UserRepository;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Disabled("Requires seed data 'ram' in MongoDB")
    public void testFindByUsername() {
        assertNotNull(userRepository.findByUsername("ram"));
    }

    @Test
    public void contextLoads() {
        assertTrue(true);
    }
}
