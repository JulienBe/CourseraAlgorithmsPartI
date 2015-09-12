package com.company;

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
    private final int[]cells;
    private int occupiedCells;
    private final int n, virtualTop, virtualBottom;

    public Percolation(int n) throws IllegalArgumentException{
        if (n <= 0)
            throw IllegalArgumentException;
        this.cells = new int[(n * n) + 2];
        this.n = n;
        virtualTop = n + 1;
        virtualBottom = n + 2;
        initTopBottom(n);
        initArray(n);
    }

    public void open(int row, int col) {
        checkArgument(0, n - 1, row - 1, col - 1);
        int index = rowAndColToIndex(row, col);
        if (!isOpen(row, col)) {
            int[] adjacentIndexes = getAdjacentIndex(index);
            for (int neighboor : adjacentIndexes)
                union(index, neighboor);
            occupiedCells++;
        }
    }

    public boolean isOpen(int row, int col) {
        return cells[rowAndColToIndex(row - 1, col - 1)] != rowAndColToIndex(row - 1, col - 1);
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
        for (int i = 0; i < n; i++) {
            cells[i] = virtualTop;
            cells[(n * (n - 1)) + i] = virtualBottom;
        }
        cells[cells.length - 2] = virtualTop;
        cells[cells.length - 1] = virtualBottom;
    }

    /**
     * Sets the id for each cell to itself
     * @param n
     */
    private void initArray(int n) {
        for (int i = 0; i < cells.length; i++)
            cells[i] = i;
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

}