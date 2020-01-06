package org.gejunwen.mixer.concurrent.cow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CustomCopyOnWriteSet<E> implements Iterable<E> {

    private Set<E> elements = new HashSet<>();

    public void add(E e){

    }

    public void remove(E e) {

    }

    static class CustomCopyOnWriteSetIterator<E> implements Iterator<E> {

        public CustomCopyOnWriteSetIterator(CustomCopyOnWriteSet<E> set) {
            this.set = set;
        }

        private CustomCopyOnWriteSet<E> set;

        @Override
        public boolean hasNext() {
            return set.elements.iterator().hasNext();
        }

        @Override
        public E next() {
            return set.elements.iterator().next();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new CustomCopyOnWriteSetIterator<>(this);
    }
}
