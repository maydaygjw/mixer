package org.gejunwen.mixer.concurrent.immutable;

import java.util.concurrent.atomic.AtomicReference;

public class SafeVM {

    final AtomicReference<VMRange> rf = new AtomicReference<>(new VMRange(0, 0));

    //设置库存上限
    void setUpper(int v) {
        while(true) {
            VMRange or = rf.get();

            //检查参数合法性
            if(v < or.lower) {
                throw new IllegalArgumentException();
            }

            VMRange nr = new VMRange(v, or.lower);
            if(rf.compareAndSet(or, nr)) {
                return;
            }
        }
    }
}

class VMRange {
    final int upper;
    final int lower;

    public VMRange(int upper, int lower) {
        this.upper = upper;
        this.lower = lower;
    }
}
