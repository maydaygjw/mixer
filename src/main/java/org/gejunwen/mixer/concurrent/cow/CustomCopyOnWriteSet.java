package org.gejunwen.mixer.concurrent.cow;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 自己实现一个CopyOnWrite策略的Set数据结构，底层使用HashSet数据结构，增加并发能力
 * @param <E>
 */
public class CustomCopyOnWriteSet<E> implements Iterable<E> {

    private Set<E> elements = new HashSet<>();

    public void add(E e){
        Set<E> newSet = copyHashSet(elements);
        newSet.add(e);
        elements = newSet;
    }

    public void remove(E e) {
        Set<E> newSet = copyHashSet(elements);
        newSet.remove(e);
        elements = newSet;
    }

    public int size() {
        return elements.size();
    }

    static class CustomCopyOnWriteSetIterator<E> implements Iterator<E> {

        private Iterator<E> hashSetIterator;

        public CustomCopyOnWriteSetIterator(Iterator<E> hashSetIterator) {
            this.hashSetIterator = hashSetIterator;
        }

        @Override
        public boolean hasNext() {
            return hashSetIterator.hasNext();
        }

        @Override
        public E next() {
            return hashSetIterator.next();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new CustomCopyOnWriteSetIterator<E>(this.elements.iterator());
    }

    public static <E> Set<E> copyHashSet(Set<E> originals) {
        Set<E> newOne = new HashSet<>();
        for(E e: originals) {
            try {
                //写入字节数组
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(e);
                byte[] bytes = baos.toByteArray();

                //从字节数组读取
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                //noinspection unchecked
                e = (E) ois.readObject();
            }catch(IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }

            newOne.add(e);
        }

        return newOne;
    }
}
