package be.julien;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 * Created by julien on 19/09/15.
 */
public class PercolationStats {

    private final double[] tresholds;
    private final int computations;

    /**
     * perform T independent experiments on an N-by-N grid
     */
    public PercolationStats(int gridSize, int computations) {
        this.computations = computations;
        tresholds = new double[computations];
        for (int i = 0; i < computations; i++)
            processGrid(gridSize, i);
    }

    private void processGrid(int gridSize, int i) {
        Percolation percolation = new Percolation(gridSize);
        int tries = 0;
        while (!percolation.percolates()) {
            openCell(gridSize, percolation);
            tries++;
        }
        tresholds[i] = (double) tries / (gridSize * gridSize);
    }

    private void openCell(int gridSize, Percolation percolation) {
        int row = 0;
        int col = 0;
        do {
            col = StdRandom.uniform(gridSize) + 1;
            row = StdRandom.uniform(gridSize) + 1;
        } while (percolation.isOpen(row, col));
        percolation.open(row, col);
    }

    /**
     * sample mean of percolation threshold
     * @return
     */
    public double mean() {
        return StdStats.mean(tresholds);
    }

    /**
     * sample standard deviation of percolation threshold
     * @return
     */
    public double stddev() {
        return StdStats.stddev(tresholds);
    }

    /**
     * low  endpoint of 95% confidence interval
     * @return
     */
    public double confidenceLo() {
        return mean() - ((1.96 * stddev()) / Math.sqrt(computations));
    }

    /**
     * high endpoint of 95% confidence interval
     * @return
     */
    public double confidenceHi() {
        return mean() + ((1.96 * stddev()) / Math.sqrt(computations));
    }

    /**
     * test client (described below)
     * @param args
     */
    public static void
    main(String[] args) {
        int gridSize = Integer.valueOf(args[0]);
        int computations = Integer.valueOf(args[1]);
        PercolationStats p = new PercolationStats(gridSize, computations);
        System.out.println("mean : " + p.mean());
        System.out.println("stddev : " + p.stddev());
        System.out.println("95% confidence interval = " + p.confidenceLo() + ", " + p.confidenceHi());
    }

}
