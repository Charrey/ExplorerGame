package com.charrey.game.util.testwrap;

public class TestGenie {

    private TestGenie() {}

    private static boolean amIUnderTest;

    public static void setAmIUnderTest(boolean amIUnderTest) {
        TestGenie.amIUnderTest = amIUnderTest;
    }

    public static boolean isAmIUnderTest() {
        return amIUnderTest;
    }
}
