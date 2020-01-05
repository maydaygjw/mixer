package org.gejunwen.mixer.concurrent.stm.sample;

public class Main {

    public static void main(String[] args) {
        Account from = new Account(1000);
        Account to = new Account(2000);

        for(int i=0; i<10; i++) {
            Thread t = new TransferTask(from, to, 100);
            t.start();
        }

    }
}

class TransferTask extends Thread {

    private Account from;
    private Account to;

    private int amt;

    public TransferTask(Account from, Account to, int amt) {
        this.from = from;
        this.to = to;
        this.amt = amt;
    }

    @Override
    public void run() {
        from.safeTransfer(to, this.amt);
        System.out.println("balance of from is: " + from.getBalance());
        System.out.println("balance of to is: " + to.getBalance());
    }
}
