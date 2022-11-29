package ru.pivovarov;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTests {

    @BeforeAll
    public static void started() {
        System.out.println("Tests started");
    }

    @AfterEach
    public void finished() {
        System.out.println("Test completed");
    }

    @AfterAll
    public static void finishedAll() {
        System.out.println("Tests completed");
    }

    @Test
    void returnPort() throws FileNotFoundException {
        int expected = 1984;

        Scanner file = new Scanner(new File("settings.txt"));
        String[] split = file.nextLine().split(" ");
        int result = Integer.parseInt(split[1]);

        assertEquals(expected, result);
    }
}
