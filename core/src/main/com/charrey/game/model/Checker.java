package com.charrey.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.charrey.game.model.Direction.*;

/**
 * Class used to verify the correctness of a Grid.
 */
public class Checker {

    private final List<Consumer<GridCheckerError>> listeners = new ArrayList<>();

    /**
     * Checks a Grid for faults and notifies any listeners of each fault found.
     *
     * @param grid grid to check
     * @return true iff no faults were found
     */
    public boolean check(Grid grid) {
        int topSum = grid.getExport(UP).size() + grid.getPad(UP).size();
        int bottomSum = grid.getExport(DOWN).size() + grid.getPad(DOWN).size();
        boolean withoutErrors = true;
        if (topSum != bottomSum) {
            ExportError error = new ExportError("The sum of exports and pads at the top should equal that on the bottom.\nTop:\t" + topSum + "\nBottom:\t" + bottomSum);
            listeners.forEach(l -> l.accept(error));
            withoutErrors = false;
        }
        int leftSum = grid.getExport(LEFT).size() + grid.getPad(LEFT).size();
        int rightSum = grid.getExport(RIGHT).size() + grid.getPad(RIGHT).size();
        if (leftSum != rightSum) {
            ExportError error = new ExportError("The sum of exports and pads at the left should equal that on the right.\nLeft:\t" + leftSum + "\nRight:\t" + rightSum);
            listeners.forEach(l -> l.accept(error));
            withoutErrors = false;
        }
        if ((topSum > 0 || bottomSum > 0) && leftSum == 0 && rightSum == 0) {
            ExportError error = new ExportError("A grid that exports vertically must at least have a height of 1 (exporting or padding left and right).");
            listeners.forEach(l -> l.accept(error));
            withoutErrors = false;
        }
        if ((leftSum > 0 || rightSum > 0) && topSum == 0 && bottomSum == 0) {
            ExportError error = new ExportError("A grid that exports horizontally must at least have a width of 1 (exporting or padding top and bottom).");
            listeners.forEach(l -> l.accept(error));
            withoutErrors = false;
        }
        return withoutErrors;
    }

    /**
     * Adds a listener that is notified of found grid faults.
     *
     * @param listener listener for faults.
     */
    public void addListener(Consumer<GridCheckerError> listener) {
        listeners.add(listener);
    }


    /**
     * Interface for errors that a grid may have that are found using the Checker class
     */
    public interface GridCheckerError {
        /**
         * Returns the descriptor of this error
         *
         * @return description
         */
        String getMessage();
    }

    /**
     * Error involving edges wrongly marked as exported or padded, or an inconsistancy in the markings.
     */
    public static class ExportError implements GridCheckerError {

        private final String message;

        /**
         * Creates a new ExportError
         *
         * @param message description
         */
        public ExportError(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
