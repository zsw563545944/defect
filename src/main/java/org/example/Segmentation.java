package org.example;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;

public class Segmentation {
    private NDArray mask;

    public Segmentation(NDArray mask) {
        this.mask = mask;
    }

    public int getMaskWidth() {
        return (int) mask.getShape().get(1);
    }

    public int getMaskHeight() {
        return (int) mask.getShape().get(0);
    }

    public int getClass(int x, int y) {
        return (int) mask.getFloat(y, x);
    }
}
