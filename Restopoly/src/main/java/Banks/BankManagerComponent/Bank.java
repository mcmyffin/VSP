package Banks.BankManagerComponent;

import Banks.BankManagerComponent.DTOs.AccountDTO;
import Banks.BankManagerComponent.DTOs.BankDTO;
import Common.Exceptions.*;
import Common.Util.URIObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 27.05.16.
 */
public class Bank {

    private final String id;
    private final URIObject gameURIObject;
    private final Map<String,Account> accountMap;
    private final Map<String,Transaction> transactionMap;
    private final Map<String,Transfer> transfersMap;

    private int accountIdCounter = 0;
    private int transactionIdCounter = 0;
    private int transferIdCounter = 0;

    public Bank(URIObject uriObject) {
        checkNotNull(uriObject);

        this.gameURIObject  = uriObject;
        this.id             = "/banks"+gameURIObject.getId();
        this.accountMap     = new HashMap();
        this.transactionMap = new HashMap();
        this.transfersMap   = new HashMap();
    }

    Bank updateBankGameID(URIObject uriObject){
        checkNotNull(uriObject);

        Bank updateBank = new Bank(uriObject);
        updateBank.accountMap.putAll(accountMap);
        updateBank.transactionMap.putAll(transactionMap);
        return updateBank;
    }

    private synchronized String getNextAccountID(){
        int nextID = accountIdCounter;
        accountIdCounter++;

        return this.id+"/accounts/"+nextID;
    }

    private synchronized String getNextTransferID(){
        int nextID = transferIdCounter;
        transferIdCounter++;
        return this.id+"/transfers/"+nextID;
    }

    private synchronized String getNextTransactionID(){
        int nextID = transactionIdCounter;
        transactionIdCounter++;
        return this.id+"/transaction/"+nextID;
    }

    public String getId() {
        return id;
    }

    public URIObject getGameURIObject() {
        return gameURIObject;
    }

    public Collection<String> getAccountList(){
        return this.accountMap.keySet();
    }

    public Collection<String> getTransactionList(){
        return this.transactionMap.keySet();
    }


    public BankDTO toDTO() {
        return new BankDTO(
                this.id,
                this.id+"/accounts",
                this.id+"/transfers"
        );
    }

    public synchronized String createAccount(AccountDTO accountDTO) {
        checkNotNull(accountDTO);

        String accID = getNextAccountID();
        Account acc = new Account(accID,accountDTO.getPlayer(),accountDTO.getSaldo());

        accountMap.put(accID,acc);
        return accID;
    }

    public Account getAccountById(String accountID) throws AccountNotFoundException {
        checkNotNull(accountID);

        if(!accountMap.containsKey(accountID)) throw new AccountNotFoundException();
        return accountMap.get(accountID);
    }

    public Collection<String> getTransfersList() {
        return this.transfersMap.keySet();
    }

    public Transfer getTransfer(String transferID) throws TransferNotFoundException {
        checkNotNull(transferID);

        if(!transfersMap.containsKey(transferID)) throw new TransferNotFoundException();
        return transfersMap.get(transferID);
    }

    public synchronized String createTransferFromTo(String fromAccount, String toAccount, int amount, String reason) throws TransferFailedException, AccountNotFoundException {
        checkNotNull(fromAccount);
        checkNotNull(toAccount);
        checkNotNull(reason);
        if(amount <= 0) throw new TransferFailedException("Illegal amount value");

        Account from = getAccountById(fromAccount);
        Account to   = getAccountById(toAccount);

        if(from.getSaldo() < amount) throw new TransferFailedException("Tranfer faild. Account: "+fromAccount+" insufficient fonds !");

        String transferID = getNextTransferID();
        Transfer newTransfer = new Transfer(transferID,from,to,amount,reason,true);

        from.substractMoney(amount);
        to.addMoney(amount);

        transfersMap.put(transferID,newTransfer);
        return transferID;
    }

    public synchronized String createTransferFromTo(String fromAccount, String toAccount, int amount, String reason, String transactionID)
            throws TransferFailedException, AccountNotFoundException, TransactionNotFoundException, IllegalTransactionStateException {
        checkNotNull(fromAccount);
        checkNotNull(toAccount);
        checkNotNull(reason);
        checkNotNull(transactionID);

        // get Transaction
        Transaction transaction = getTransactionById(transactionID);

        // create Transfer Object
        String transferID = getNextTransferID();
        Transfer newTransfer = new Transfer(transferID,null,null,amount,reason,false);

        transaction.addTransfer(newTransfer);

        if(amount <= 0) throw new TransferFailedException("Illegal amount value");

        Account from = getAccountById(fromAccount);
        Account to   = getAccountById(toAccount);

        if(from.getSaldo() < amount){
            transaction.checkTransfers();
            throw new TransferFailedException("Tranfer faild. Account: "+fromAccount+" insufficient fonds !");
        }

        newTransfer.setFrom(from);
        newTransfer.setTo(to);
        newTransfer.setState(true);

        from.substractMoney(amount);
        to.addMoney(amount);

        transfersMap.put(transferID,newTransfer);

        // check transfers
        transaction.checkTransfers();

        return transferID;
    }

    public String createTransferTo(String toAccount, int amount, String reason) throws TransferFailedException, AccountNotFoundException {
        checkNotNull(toAccount);
        checkNotNull(reason);
        if(amount <= 0) throw new TransferFailedException("Illegal amount value");

        Account to   = getAccountById(toAccount);

        if(to.getSaldo() < amount) throw new TransferFailedException("Tranfer faild. Account: "+toAccount+" insufficient fonds !");

        String transferID = getNextTransferID();
        Transfer newTransfer = new Transfer(transferID,null,to,amount,reason,true);

        to.addMoney(amount);

        transfersMap.put(transferID,newTransfer);
        return transferID;
    }

    public String createTransferTo(String toAccount, int amount, String reason, String transactionID) throws TransferFailedException, AccountNotFoundException, TransactionNotFoundException, IllegalTransactionStateException {
        checkNotNull(toAccount);
        checkNotNull(reason);

        // get Transaction
        Transaction transaction = getTransactionById(transactionID);

        // create Transfer Object
        String transferID = getNextTransferID();
        Transfer newTransfer = new Transfer(transferID,null,null,amount,reason,false);

        transaction.addTransfer(newTransfer);

        if(amount <= 0) throw new TransferFailedException("Illegal amount value");
        Account to   = getAccountById(toAccount);

        if(to.getSaldo() < amount) throw new TransferFailedException("Tranfer faild. Account: "+toAccount+" insufficient fonds !");

        newTransfer.setTo(to);
        newTransfer.setState(true);
        to.addMoney(amount);

        transfersMap.put(transferID,newTransfer);
        return transferID;
    }

    public String createTransferFrom(String fromAccount, int amount, String reason) throws TransferFailedException, AccountNotFoundException {
        checkNotNull(fromAccount);
        checkNotNull(reason);
        if(amount <= 0) throw new TransferFailedException("Illegal amount value");

        Account from = getAccountById(fromAccount);

        if(from.getSaldo() < amount) throw new TransferFailedException("Tranfer faild. Account: "+from+" insufficient fonds !");

        String transferID = getNextTransferID();
        Transfer newTransfer = new Transfer(transferID,from,null,amount,reason,true);

        from.substractMoney(amount);

        transfersMap.put(transferID,newTransfer);
        return transferID;
    }

    public String createTransferFrom(String fromAccount, int amount, String reason, String transactionID) throws TransferFailedException, AccountNotFoundException, TransactionNotFoundException, IllegalTransactionStateException {
        checkNotNull(fromAccount);
        checkNotNull(reason);

        // get Transaction
        Transaction transaction = getTransactionById(transactionID);

        // create Transfer Object
        String transferID = getNextTransferID();
        Transfer newTransfer = new Transfer(transferID,null,null,amount,reason,false);

        transaction.addTransfer(newTransfer);

        if(amount <= 0) throw new TransferFailedException("Illegal amount value");

        Account from = getAccountById(fromAccount);
        if(from.getSaldo() < amount) throw new TransferFailedException("Tranfer faild. Account: "+from+" insufficient fonds !");

        newTransfer.setFrom(from);
        newTransfer.setState(true);
        from.substractMoney(amount);

        transfersMap.put(transferID,newTransfer);
        return transferID;
    }

    public String createTransactionId() {

        String transactionID = getNextTransactionID();
        Transaction t = new Transaction(transactionID);

        transactionMap.put(transactionID,t);
        return transactionID;
    }

    public Transaction getTransactionById(String transactionID) throws TransactionNotFoundException {
        checkNotNull(transactionID);
        if(!transactionMap.containsKey(transactionID)) throw new TransactionNotFoundException("Transaktion nicht gefunden");

        return transactionMap.get(transactionID);
    }
}
