package org.gejunwen.mixer.concurrent.stm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class STMTxn implements Txn {

    private static AtomicLong txnSeq = new AtomicLong(0);
    //当前事务所有的相关数据
    private Map<TxnRef, VersionedRef> inTxnMap = new HashMap<>();
    //当前事务所有需要修改的数据
    private Map<TxnRef, Object> writeMap = new HashMap<>();
    //当前事务ID
    private long txnId;
    //构造函数，自动生成当前事务ID


    public long getTxnId() {
        return txnId;
    }

    public STMTxn() {
        txnId = txnSeq.incrementAndGet();
    }

    public <T> T get(TxnRef ref) {
        if(!inTxnMap.containsKey(ref)) {
            inTxnMap.put(ref, ref.curRef);
        }
        return (T) inTxnMap.get(ref).value;
    }

    public <T> void set(TxnRef ref, T value) {
        //将需要修改的数据，加入inTxnMap
        if (!inTxnMap.containsKey(ref)) {
            inTxnMap.put(ref, ref.curRef);
        }

        writeMap.put(ref, value);
    }

    // 提交事务
    boolean commit() {

        //TODO: 减小锁的粒度
        synchronized (STM.commitLock) {

            boolean isValid = true;

            for(Map.Entry<TxnRef, VersionedRef> entry: inTxnMap.entrySet()) {
                VersionedRef curRef = entry.getKey().curRef;
                VersionedRef readRef = entry.getValue();

                //通过版本号来验证数据是否发生过变化
                if(curRef.version != readRef.version) {
                    isValid = false;
                    break;
                }
            }

            if(isValid) {
                writeMap.forEach((k, v) -> {
                    k.curRef = new VersionedRef(v, txnId);
                });
            }

            return isValid;

        }
    }
}
