package com.charrey.game;

public enum Direction {
    UP, DOWN, LEFT, RIGHT, NOT_APPLICCABLE;

    public static Direction rotateLeft(Direction direction) {
        return switch (direction) {
            case UP -> LEFT;
            case DOWN -> RIGHT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
            case NOT_APPLICCABLE -> NOT_APPLICCABLE;
        };
    }

    public static Direction rotateRight(Direction direction) {
        return switch (direction) {
            case UP -> RIGHT;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
            case NOT_APPLICCABLE -> NOT_APPLICCABLE;
        };
    }

    public static Direction opposite(Direction direction) {
        return switch (direction) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case NOT_APPLICCABLE -> NOT_APPLICCABLE;
        };
    }
}
