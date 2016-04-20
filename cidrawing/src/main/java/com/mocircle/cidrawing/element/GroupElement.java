package com.mocircle.cidrawing.element;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import java.util.List;

public class GroupElement extends ElementGroup {

    public GroupElement() {
        super();
    }

    public GroupElement(List<DrawElement> elements) {
        super(elements);
    }

    @Override
    public Object clone() {
        GroupElement element = new GroupElement();
        cloneTo(element);
        return element;
    }

    @Override
    public void drawElement(Canvas canvas) {
        for (DrawElement element : elements) {
            canvas.save();
            Matrix originalDisplay = new Matrix(element.getDisplayMatrix());
            canvas.concat(originalDisplay);
            element.drawElement(canvas);
            canvas.restore();
        }
    }

    @Override
    public void applyMatrixForData(Matrix matrix) {
        super.applyMatrixForData(matrix);

        for (DrawElement element : elements) {

            // Before applying data matrix, it should restore the extra display matrices
            // which including element's display matrix and parent's display matrix.

            Matrix parentInvertDisplay = new Matrix(getInvertedDisplayMatrix());
            Matrix originalDisplay = new Matrix(element.getDisplayMatrix());
            originalDisplay.postConcat(parentInvertDisplay);
            Matrix originalInvertDisplay = new Matrix();
            originalDisplay.invert(originalInvertDisplay);

            element.applyDisplayMatrixToData();
            element.applyMatrixForData(matrix);
            element.applyMatrixForData(originalInvertDisplay);
            element.getDisplayMatrix().postConcat(originalDisplay);
            element.updateBoundingBox();
        }
        recalculateBoundingBox();
    }

    @Override
    public RectF getOuterBoundingBox() {
        RectF box = new RectF();
        for (DrawElement element : elements) {
            DrawElement temp = (DrawElement) element.clone();
            temp.getDisplayMatrix().postConcat(getDisplayMatrix());
            box.union(temp.getOuterBoundingBox());
        }
        return box;
    }
}