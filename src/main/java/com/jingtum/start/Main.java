package com.jingtum.start;

import com.jingtum.model.FinGate;
import com.jingtum.model.Wallet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author heipacker
 * @date 16-10-21 下午4:15.
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        FinGate.getInstance().setTest(false);
        FinGate.getInstance().setFinGate("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", "snqFcHzRe22JTM8j7iZVpQYzxEEbW");

        boolean isConnected = FinGate.getInstance().getStatus();
        System.out.println(isConnected);
        System.out.println(FinGate.getInstance().getFinGate());
        System.out.println(FinGate.getInstance().getActivateAmount());
        System.out.println(FinGate.getInstance().getTrustLimit());
        System.out.println(FinGate.getInstance().getPrefix());
//        Wallet wallet = new Wallet("jBnapU49sXYZh6jpH4KNUxDoTDvm8gr3E8", "shPrvGfPjJWLerRzbNgGTdhoF3Bnk");
        Wallet wallet = FinGate.getInstance().createWallet();
        System.out.println(wallet);
        String address = wallet.getAddress();
        boolean activateWallet = FinGate.getInstance().activateWallet(address);
        System.out.println(activateWallet);
        System.out.println(wallet.getBalance());
    }
}
