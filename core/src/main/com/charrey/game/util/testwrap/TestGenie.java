package com.charrey.game.util.testwrap;

/**
 * Class that may be used to check whether the system is currently under test.
 */
public class TestGenie {

    private static boolean amIUnderTest;

    private TestGenie() {
    }

    /**
     * Returns whether the system is currently under test.
     *
     * @return whether the system is currently under test.
     */
    public static boolean isAmIUnderTest() {
        return amIUnderTest;
    }

    /**
     * Called by test methods. Due to game engine limitations, some graphics operations cannot be called in a headless environment.
     *
     * @param amIUnderTest whether the system is under test
     */
    public static void setAmIUnderTest(boolean amIUnderTest) {
        TestGenie.amIUnderTest = amIUnderTest;
    }
}
