package org.gejunwen.mixer.concurrent.stm;

public interface Txn {

    <T> T get(TxnRef ref);

    <T> void set(TxnRef ref, T value);
}
