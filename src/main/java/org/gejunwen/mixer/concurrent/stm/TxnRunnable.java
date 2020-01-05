package org.gejunwen.mixer.concurrent.stm;

@FunctionalInterface
public interface TxnRunnable {
    void run(Txn txn);
}
