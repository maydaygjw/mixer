package org.gejunwen.mixer.concurrent.stm;

import static org.gejunwen.mixer.utils.DebugUtils.p;

public final class STM {

    private STM() {

    }

    static final Object commitLock = new Object();

    public static void atomic(TxnRunnable action) {

        long start = System.currentTimeMillis();

        STMTxn txn = null;

        boolean committed = false;
        while(!committed) {
            //创建新的事务
            txn = new STMTxn();
            //执行业务逻辑
            action.run(txn);
            //提交事务
            committed = txn.commit();
        }

        long end = System.currentTimeMillis();
        p("elapsed time for txn: " + txn.getTxnId() + " is: " + (end - start));
    }


}


