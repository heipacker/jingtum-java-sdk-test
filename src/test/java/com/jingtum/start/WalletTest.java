package com.jingtum.start;

import com.jingtum.exception.*;
import com.jingtum.model.*;
import com.jingtum.net.JingtumWebSocket;
import com.jingtum.net.SubscribeEventHandler;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by IntelliJ IDEA.
 *
 * @author heipacker
 * @date 17-1-14 上午10:22.
 */
public class WalletTest {

    private FinGate jingtumFinGate;

    private String jingtumFinGateAddress = "jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS";

    private Wallet giftWallet;

    private static final String giftAddress = "jG4oHTKopzG1JXjCRd23HdXvXBAAvCSSjr";

    private static final String giftSecret = "sn5bGPAExY7H4xaDn2PJzoUbzpcbz";

    private static String coinsCode = "00000005";
    private static String coinsSecret = "b33802b7f345fc44e6bd1d3b11c86b412de9ec38";

    private static String coins = "8200000005000020170006000000000020000001";

    private static String address = "jBTd7oqs9iypPGyp4C1kZNYsqWtggEtyJv";

    private static String secret = "snrdu7Ff8E9ETk89cBSe8VciwLUrQ";

    @Before
    public void init() throws InvalidParameterException {
        jingtumFinGate = FinGate.getInstance();
        jingtumFinGate.setTest(true);
//        jingtumFinGate.setFinGate("jpztUv95LKhf2tTEK13ajfPfPD6xtKDvbb", "sh4fSpNwQixkRKEoPFLKaV5dSbf3i");
        jingtumFinGate.setFinGate("jpLpucnjfX7ksggzc9Qw6hMSm1ATKJe3AF", "sha4eGoQujTi9SsRSxGN5PamV3YQ4");
        jingtumFinGate.setActivateAmount(10);

        giftWallet = new Wallet(giftAddress, giftSecret);
    }

    private Wallet createWallet() {
        return jingtumFinGate.createWallet();
    }

    @Test
    public void testCreateWallet() {
        Wallet wallet = createWallet();
        Assert.assertTrue(wallet != null && StringUtils.isNotBlank(wallet.getAddress()) && StringUtils.isNotBlank(wallet.getSecret()));
        System.out.println(wallet.getAddress());
        System.out.println(wallet.getSecret());
    }

    @Test
    public void testActivateWallet() throws APIException, InvalidParameterException {
        Wallet wallet = createWallet();
        Assert.assertTrue(jingtumFinGate.activateWallet(wallet.getAddress()));
    }

    @Test
    public void testGetBalanceAndPayments() throws APIException, InvalidParameterException, ChannelException, FailedException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Wallet wallet = new Wallet(address, secret);
        System.out.println(wallet.getAddress());
        System.out.println(wallet.getSecret());
        boolean b = jingtumFinGate.activateWallet(wallet.getAddress());
        Assert.assertTrue(b);
        System.out.println("wallet activate successfully");
        //设定支付的货币金额
        Amount jtc = new Amount();
        jtc.setCounterparty(jingtumFinGateAddress); //货币发行方
        jtc.setCurrency(coins); //货币单位
        jtc.setValue(1.1); //金额
        giftWallet.submitPayment(wallet.getAddress(), jtc, true, jingtumFinGate.getNextUUID());
    }

    @Test
    public void testGetBalance() throws APIException, InvalidParameterException, ChannelException, FailedException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Wallet wallet = new Wallet(address, secret);
        System.out.println(wallet.getAddress());
        System.out.println(wallet.getSecret());

        BalanceCollection balanceCollection = wallet.getBalance();
        List<Balance> balanceList = balanceCollection.getData();
        for (Balance balance : balanceList) {
            if (balance.getCounterparty().equals(jingtumFinGateAddress)) {
                Assert.assertTrue(coins.equals(balance.getCurrency()));
                Assert.assertTrue(balance.getValue() > 1.1);
            }
        }
    }

    @Test
    public void testPaymentPath() throws APIException, InvalidParameterException, ChannelException, FailedException, AuthenticationException, InvalidRequestException,
            APIConnectionException {
        Wallet wallet = new Wallet(address, secret);
        System.out.println("wallet activate successfully");
        //设定支付的货币金额
        Amount jtc = new Amount();
        jtc.setCounterparty(jingtumFinGateAddress); //货币发行方
        jtc.setCurrency(coins); //货币单位
        jtc.setValue(1.1); //金额
        PaymentCollection paymentCollection = giftWallet.getPathList(wallet.getAddress(), jtc);
        List<Payment> paymentList = paymentCollection.getData();
        for (Payment payment : paymentList) {
            System.out.println(payment);
        }
//        giftWallet.submitPayment(wallet.getAddress(), jtc, true, jingtumFinGate.getNextUUID());
    }

    @Test
    public void testPaymentList() throws InvalidParameterException, FailedException, APIException, ChannelException, InvalidRequestException, APIConnectionException, AuthenticationException {
        PaymentCollection paymentCollection = giftWallet.getPaymentList();
        Assert.assertTrue(paymentCollection != null);
        List<Payment> paymentList = paymentCollection.getData();
        Assert.assertTrue(paymentList != null && !paymentList.isEmpty());
        for (Payment payment : paymentList) {
            System.out.println(payment);
        }
    }

    /**
     * 测试挂单接口
     * 先创建一个挂单, 再创建一个反的挂单， 进行成交, 最后查询是否成交
     *
     * @throws InvalidParameterException
     * @throws FailedException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws AuthenticationException
     */
    @Test
    public void testOrder() throws InvalidParameterException, FailedException, APIException, ChannelException, InvalidRequestException, APIConnectionException, AuthenticationException {
        //用现有井通账号的地址和密钥产生一个井通账号对象
        Wallet myWallet = new Wallet(address, secret);

        //设定挂单的卖出和买入金额
        Amount pay = new Amount(); //构建Amount实例
        pay.setCounterparty(""); //counterparty
        pay.setCurrency("SWT"); //Currency
        pay.setValue(0.01); //value

        Amount get = new Amount();
        get.setCounterparty(jingtumFinGateAddress);
        get.setCurrency(coins);
        get.setValue(2);

        //提交挂单操作
        RequestResult myRequestResult = myWallet.createOrder(Order.OrderType.sell, pay, get, true);
        System.out.println(myRequestResult);
        Assert.assertTrue(myRequestResult.getSuccess());

        //挂一个反单 进行成交

        //设定挂单的卖出和买入金额
        Amount get1 = new Amount(); //构建Amount实例
        get1.setCounterparty(""); //counterparty
        get1.setCurrency("SWT"); //Currency
        get1.setValue(0.01); //value

        Amount pay1 = new Amount();
        pay1.setCounterparty(jingtumFinGateAddress);
        pay1.setCurrency(coins);
        pay1.setValue(2);
        RequestResult giftRequestResult = giftWallet.createOrder(Order.OrderType.sell, pay1, get1, true);
        System.out.println(giftRequestResult);
        Assert.assertTrue(giftRequestResult.getSuccess());

        Order myWalletOrder = myWallet.getOrder(myRequestResult.getHash());
        Assert.assertTrue(myWalletOrder.getSuccess());
    }

    /**
     * 测试取消挂单接口
     * 先创建一个挂单, 取消挂单, 再创建一个反的挂单， 进行成交, 最后查询是否成交
     *
     * @throws InvalidParameterException
     * @throws FailedException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws AuthenticationException
     */
    @Test
    public void testCancelOrder() throws InvalidParameterException, FailedException, APIException, ChannelException, InvalidRequestException, APIConnectionException,
            AuthenticationException {
        //用现有井通账号的地址和密钥产生一个井通账号对象
        Wallet myWallet = new Wallet(address, secret);

        //设定挂单的卖出和买入金额
        Amount pay = new Amount(); //构建Amount实例
        pay.setCounterparty(""); //counterparty
        pay.setCurrency("SWT"); //Currency
        pay.setValue(0.01); //value

        Amount get = new Amount();
        get.setCounterparty(jingtumFinGateAddress);
        get.setCurrency(coins);
        get.setValue(2);

        //提交挂单操作
        RequestResult myRequestResult = myWallet.createOrder(Order.OrderType.sell, pay, get, true);
        System.out.println(myRequestResult);
        Assert.assertTrue(myRequestResult.getSuccess());


        //取消挂单
        myWallet.cancelOrder(myRequestResult.getSequence(), true);
        //挂一个反单 进行成交

        //设定挂单的卖出和买入金额
        Amount get1 = new Amount(); //构建Amount实例
        get1.setCounterparty(""); //counterparty
        get1.setCurrency("SWT"); //Currency
        get1.setValue(0.01); //value

        Amount pay1 = new Amount();
        pay1.setCounterparty(jingtumFinGateAddress);
        pay1.setCurrency(coins);
        pay1.setValue(2);
        RequestResult giftRequestResult = giftWallet.createOrder(Order.OrderType.buy, pay1, get1, true);
        System.out.println(giftRequestResult);
        Assert.assertTrue(giftRequestResult.getSuccess());

        Order myWalletOrder = myWallet.getOrder(giftRequestResult.getHash());
        Assert.assertTrue(myWalletOrder.getSuccess());
    }

    @Test
    public void testTransaction() throws InvalidParameterException, APIException, ChannelException, FailedException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Wallet wallet = new Wallet(address, secret);
        System.out.println(wallet.getAddress());
        System.out.println(wallet.getSecret());
        boolean b = jingtumFinGate.activateWallet(wallet.getAddress());
        Assert.assertTrue(b);
        System.out.println("wallet activate successfully");
        //设定支付的货币金额
        Amount jtc = new Amount();
        jtc.setCounterparty(jingtumFinGateAddress); //货币发行方
        jtc.setCurrency(coins); //货币单位
        jtc.setValue(1.1); //金额
        RequestResult requestResult = giftWallet.submitPayment(wallet.getAddress(), jtc, true, jingtumFinGate.getNextUUID());
        Transaction transaction = wallet.getTransaction(requestResult.getHash());
        System.out.println(transaction);
        Assert.assertTrue(requestResult.getHash().equals(transaction.getHash()));
        TransactionCollection transactionList = wallet.getTransactionList();
        System.out.println(transactionList);
        TransactionCollection transactionCollection = wallet.getTransactionList(address, true, Transaction.DirectionType.all, 1, 100);
        System.out.println(transactionCollection);
        TransactionCollection transactionCollection1 = wallet.getTransactionList(address, true, Transaction.DirectionType.incoming, 1, 100);
        System.out.println(transactionCollection1);
    }

    @Test
    public void testTransactionList() throws InvalidParameterException, FailedException, APIException, ChannelException, InvalidRequestException, APIConnectionException, AuthenticationException {
        Wallet wallet = new Wallet(address, secret);
        TransactionCollection transactionCollection = wallet.getTransactionList();
        Assert.assertTrue(transactionCollection != null && transactionCollection.getData() != null
                && !transactionCollection.getData().isEmpty());
    }

    @Test
    public void testTransactionListWithParams() throws InvalidParameterException, FailedException, APIException, ChannelException, InvalidRequestException, APIConnectionException,
            AuthenticationException {
        Wallet wallet = new Wallet(address, secret);
        //设定支付的货币金额
        Amount jtc = new Amount();
        jtc.setCounterparty(jingtumFinGateAddress); //货币发行方
        jtc.setCurrency(coins); //货币单位
        jtc.setValue(1.1); //金额
        RequestResult requestResult = giftWallet.submitPayment(wallet.getAddress(), jtc, true, jingtumFinGate.getNextUUID());
        System.out.println(requestResult);
        TransactionCollection transactionCollection = giftWallet.getTransactionList(wallet.getAddress(), true,
                Transaction.DirectionType.all, 1, 100);
        Assert.assertTrue(transactionCollection != null && transactionCollection.getData() != null
                && !transactionCollection.getData().isEmpty());
    }

    @Test
    public void testPayments() throws APIException, InvalidParameterException, ChannelException, FailedException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Wallet wallet = new Wallet(address, secret);
        System.out.println(wallet.getAddress());
        System.out.println(wallet.getSecret());
        boolean b = jingtumFinGate.activateWallet(wallet.getAddress());
        Assert.assertTrue(b);
        System.out.println("wallet activate successfully");
        //设定支付的货币金额
        Amount jtc = new Amount();
        jtc.setCounterparty(jingtumFinGateAddress); //货币发行方
        jtc.setCurrency(coins); //货币单位
        jtc.setValue(1.1); //金额
        giftWallet.submitPayment(wallet.getAddress(), jtc, false, jingtumFinGate.getNextUUID());
    }

    @Test
    public void testIssueCustom() throws FailedException, APIException, ChannelException, InvalidRequestException, APIConnectionException, AuthenticationException, InvalidParameterException {
        jingtumFinGate.setCustom(coinsCode);
        jingtumFinGate.setCustomSecret(coinsSecret);
        boolean customTum = jingtumFinGate.issueCustomTum(jingtumFinGate.getNextUUID(), coins, 11, address);
        Assert.assertTrue(customTum);
    }

    @Test
    public void testWebsocket() throws URISyntaxException, FailedException, InvalidRequestException, APIException, ChannelException, InvalidParameterException, APIConnectionException, AuthenticationException, InterruptedException {
        JingtumWebSocket jingtumWebSocket = new JingtumWebSocket();
        final CountDownLatch latch = new CountDownLatch(3);
        final AtomicReference<String> atomicReference = new AtomicReference<String>();
        jingtumWebSocket.openWebSocket(new SubscribeEventHandler() {
            public void onMessage(JSONObject msg) {
                System.out.println("on message------" + msg);
                JSONObject transaction = msg.getJSONObject("transaction");
                if (transaction != null) {
                    String counterParty = transaction.getString("counterparty");
                    Assert.assertTrue(giftAddress.equals(counterParty));
                    JSONObject jsonObject = transaction.getJSONObject("amount");
                    String currency = jsonObject.getString("currency");
                    Assert.assertTrue(coins.equals(currency));
                    String issuer = jsonObject.getString("issuer");
                    Assert.assertTrue(jingtumFinGateAddress.equals(issuer));
                    String value = jsonObject.getString("value");
                    Assert.assertTrue("1.1".equals(value));

                    String clientResourceId = transaction.getString("client_resource_id");
                    Assert.assertTrue(atomicReference.get().equals(clientResourceId));
                }
                latch.countDown();
            }

            public void onDisconnected(int code, String reason, boolean remote) {
                System.out.println(code + "" + reason + "" + remote);
            }

            public void onError(Exception error) {
                System.out.println(error);
            }

            public void onConnected() {
                System.out.println("on connected");
            }
        });
        jingtumWebSocket.subscribe(address, secret);
        Wallet wallet = new Wallet(address, secret);
        //设定支付的货币金额
        Amount jtc = new Amount();
        jtc.setCounterparty(jingtumFinGateAddress); //货币发行方
        jtc.setIssuer(jingtumFinGateAddress); //货币发行方
        jtc.setCurrency(coins); //货币单位
        jtc.setValue(1.1); //金额
        String nextUUID = jingtumFinGate.getNextUUID();
        System.out.println(nextUUID);
        RequestResult requestResult = giftWallet.submitPayment(wallet.getAddress(), jtc, false, nextUUID);
        System.out.println(requestResult);
        Assert.assertTrue(nextUUID.equals(requestResult.getClient_resource_id()));
        atomicReference.set(nextUUID);
        latch.await();
    }
}