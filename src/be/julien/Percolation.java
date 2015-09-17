package be.julien;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Given a composite systems comprised of randomly distributed insulating and metallic materials:
 *  what fraction of the materials need to be metallic so that the composite system is an electrical conductor?
 *
 * Given a porous landscape with water on the surface (or oil below), under what conditions will the water be able to drain through to the bottom
 *  (or the oil to gush through to the surface)? Scientists have defined an abstract process known as percolation to model such situations.
 *
 *  Corner cases.
 *  By convention, the row and column indices i and j are integers between 1 and N, where (1, 1) is the upper-left site:
 *      Throw a java.lang.IndexOutOfBoundsException if any argument to open(), isOpen(), or isFull() is outside its prescribed range.
 *  The constructor should throw a java.lang.IllegalArgumentException if N ≤ 0.
 */

public class Percolation {

    /**
     * A cell is open if it points to someone else
     */
    final int[]cells;
    private int occupiedCells;
    private final int n, virtualTop, virtualBottom;

    public Percolation(int n) {
        this.cells = new int[(n * n) + 2];
        this.n = n;
        virtualTop = cells.length;
        virtualBottom = cells.length + 1;
        initArray(n);
        initTopBottom(n);
    }

    public void open(int row, int col) {
        checkArgument(0, n - 1, row - 1, col - 1);
        int index = rowAndColToIndex(row - 1, col - 1);
        if (!isOpen(row, col)) {
            cells[index] = index;
            int[] adjacentIndexes = getAdjacentIndex(index);
            for (int neighboor : adjacentIndexes)
                if (isOpen(indexToRow(neighboor) + 1, indexToCol(neighboor) + 1))
                    union(index, neighboor);
            occupiedCells++;
        }
    }

    public boolean isOpen(int row, int col) {
        return cells[rowAndColToIndex(row - 1, col - 1)] != -1;
    }

    public boolean isFull(int row, int col) {
        int index = rowAndColToIndex(row - 1, col - 1);
        int[] adjacentIndexes = getAdjacentIndex(index);
        for (int i : adjacentIndexes)
            if (!isOpen(indexToRow(i), indexToCol(i)))
                return false;
        return true;
    }

    public boolean percolates() {
        return cells[cells.length - 2] == cells[cells.length - 1];
    }

    private void initTopBottom(int n) {
        cells[cells.length - 2] = virtualTop;
        cells[cells.length - 1] = virtualBottom;
    }

    /**
     * Sets the id for each cell to itself
     * @param n
     */
    private void initArray(int n) {
        for (int i = 0; i < cells.length; i++)
            cells[i] = -1;
    }

    private int findRootOf(int index) {
        while (index != cells[index])
            index = cells[index];
        return index;
    }

    private void union(int index1, int index2) {
        int root1 = findRootOf(index1);
        int root2 = findRootOf(index2);
        setCellValue(root1, root2);
    }

    private void checkArgument(int min, int max, int... vals) {
        for (int i : vals)
            if (i < min || i > max)
                throw new IndexOutOfBoundsException("index is out of bounds, should be between " + min + " and " + n);
    }

    private int[] getAdjacentIndex(int index) {
        int row = indexToRow(index);
        int col = indexToCol(index);
        int size = 2;
        if (isOnEdge(row)) size++;
        if (isOnEdge(col)) size++;

        int[] neighbours = new int[size];
        int arrayIndex = 0;
        // left neighbour
        if (hasLowerNeighbour(col)) neighbours[arrayIndex++] = index - 1;
        if (hasUpperNeighbour(col)) neighbours[arrayIndex++] = index + 1;
        if (hasLowerNeighbour(row)) neighbours[arrayIndex++] = index - n;
        if (hasUpperNeighbour(row)) neighbours[arrayIndex++] = index + n;
        return neighbours;
    }

    private void setCellValue(int index, int value) {       cells[index] = value;    }
    private boolean isOnEdge(int i) {                       return hasLowerNeighbour(i) && hasUpperNeighbour(i);    }
    private int rowAndColToIndex(int row, int col) {        return row * n + col;                                   }
    private int indexToCol(int index) {                     return index % n;                                       }
    private int indexToRow(int index) {                     return index / n;                                       }
    private boolean hasUpperNeighbour(int i) {              return i < n - 1;                                       }
    private boolean hasLowerNeighbour(int i) {              return i > 0;                                           }

    @Override
    public String toString() {
        String s = "\n\n";
        for (int i = 0; i < cells.length; i++) {
            if (i % n == 0)
                s += "\n";
            if (isOpen((i / n) + 1, (i % n) + 1))
                s += "X";
            else
                s += " ";
        }
        for (int i = 0; i < cells.length; i++) {
            if (i % n == 0)
                s += "\n";
            s += " ";
            if (cells[i] > 99)
                s += cells[i];
            else if (cells[i] > 9)
                s += cells[i] + " ";
            else if (cells[i] > -1)
                s += cells[i] + "  ";
            else
                s += cells[i] + " ";

        }
        return "Percolation{" +
                "n=" + n +
                ", cells=" + Arrays.toString(cells) +
                ", occupiedCells=" + occupiedCells +
                '}' + s;
    }

    /**
     * For testing
     * @param fileName
     */
    public void processFile(String fileName) {
        Path path = Paths.get(fileName);
        try {
            Files.lines(path).forEach(s -> processLine(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLine(String s) {
        int[] numbers = getColAndRow(s.split(" "));

        if (numbers[1] != 0)
            open(Integer.valueOf(numbers[0]), Integer.valueOf(numbers[1]));
    }

    private int[] getColAndRow(String[] splitted) {
        int[] coords = new int[2];
        int index = 0;
        for (String s : splitted)
            if (s.matches("[0-9]+"))
                coords[index++] = Integer.valueOf(s);
        return coords;
    }
}