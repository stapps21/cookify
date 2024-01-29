package de.hdmstuttgart.recipeapp.exceptions;

public class OutOfBoundViewPagerFragmentsException extends ArrayIndexOutOfBoundsException {
    public OutOfBoundViewPagerFragmentsException(int position) {
        super("Fragment array position out of range: " + position);
    }
}
