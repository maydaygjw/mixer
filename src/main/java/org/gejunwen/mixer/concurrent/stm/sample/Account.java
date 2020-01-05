package org.gejunwen.mixer.concurrent.stm.sample;

import org.gejunwen.mixer.concurrent.stm.STM;
import org.gejunwen.mixer.concurrent.stm.STMTxn;
import org.gejunwen.mixer.concurrent.stm.Txn;
import org.gejunwen.mixer.concurrent.stm.TxnRef;

import java.util.Random;

public class Account {

    private TxnRef<Integer> balance;

    public Account(int balance) {
        this.balance = new TxnRef<>(balance);
    }

    public Integer getBalance() {
        return balance.getValue(new STMTxn());
    }

    // @non-threadsafe
    public void fastTransfer(Account target, int amt) {
        this.doTransfer(target, amt, null);
    }

    // @thread-safe
    public void safeTransfer(Account target, int amt) {
        STM.atomic((txn) -> {
            doTransfer(target, amt, txn);
        });
    }

    private void doTransfer(Account target, int amt, Txn txn) {
        System.out.println("开始转账");
        Integer from = balance.getValue(txn);
        balance.setValue(from - amt, txn);

        //模拟转账时间
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException ignored) {

        }

        Integer to = target.balance.getValue(txn);
        target.balance.setValue(to + amt, txn);
        System.out.println("转账完毕");
    }

}
