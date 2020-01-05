package org.gejunwen.mixer.concurrent.stm;

public class TxnRef<T> {

    volatile VersionedRef<T> curRef;

    public TxnRef(T value) {
        this.curRef = new VersionedRef<>(value, 0L);
    }

    public T getValue(Txn txn) {

        if(txn == null) {
            return curRef.value;
        }

        return txn.get(this);
    }

    public void setValue(T value, Txn txn) {

        if(txn == null) {
            curRef = new VersionedRef<>(value, 0L);
        } else {
            txn.set(this, value);
        }
    }
}
