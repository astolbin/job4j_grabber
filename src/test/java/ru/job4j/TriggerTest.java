package ru.job4j;

import org.junit.Test;

import static org.junit.Assert.*;

public class TriggerTest {

    @Test
    public void test() {
        assertEquals(1, new Trigger().someLogic());
    }

}