/**
 * This class is used as a final List for every added unique Element.
 * Currently not all prohibited functions are overwritten (e. g.) addAll, removeAll.
 *
 * Only add an element to an ArrayList when it does not already exist.
 * Ofc. could have used a (Hash-) Set but a List can be used with an index.
 *
 * @version 1.0
 * @date 10.06.2017
 * @see ArrayList
 */
package symboltable;

import java.util.ArrayList;

public class FinalUniqueList<E> extends ArrayList<E> {

    public FinalUniqueList() {
        super();
    }

    @Override
    public boolean add(E element) {
        if (!this.contains(element)) {
            return super.add(element);
        }
        return false;
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object obj) {
        throw new UnsupportedOperationException();
    }
}