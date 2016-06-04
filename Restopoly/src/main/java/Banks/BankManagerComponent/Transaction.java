package Banks.BankManagerComponent;

import Common.Exceptions.IllegalTransactionStateException;
import Common.Exceptions.UndefinedTransactionStateException;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 27.05.16.
 */
public class Transaction {

    private final String id;
    private TransactionState state;
    private List<Transfer> transfersList;

    public Transaction(String id) {
        checkNotNull(id);
        this.id = id;
        this.state = TransactionState.READY;
        this.transfersList = new ArrayList();
    }

    public synchronized void setTransferAction(String state) throws UndefinedTransactionStateException, IllegalTransactionStateException {
        checkNotNull(state);

        if(this.state.equals(TransactionState.COMMITED)) throw new IllegalTransactionStateException("Transaktion state COMMITED");
        if(this.state.equals(TransactionState.ROLLBACK)) throw new IllegalTransactionStateException("Transaktion state ROLLBACK");

        try{
            TransferAction transferAction = TransferAction.valueOf(state);

            // Transaction wird noch befüllt
            if(this.state.equals(TransactionState.READY)){

                if(transferAction.equals(TransferAction.RADY)) return; // status bleibt unverändert
                else if(transferAction.equals(TransferAction.COMMIT)) this.state = TransactionState.COMMITED;
                else if(transferAction.equals(TransferAction.ROLLBACK)){
                    this.state = TransactionState.ROLLBACK;
                    runRollback();
                }
                else throw new IllegalTransactionStateException("ungültiger Transaktion Status");

            // Transaction ist beim Commit fehlgeschlagen
            }else if(this.state.equals(TransactionState.FAILED)){
                if(transferAction.equals(TransferAction.ROLLBACK) || transferAction.equals(TransferAction.RADY)){
                    this.state = TransactionState.ROLLBACK;
                    runRollback();
                }
                else throw new IllegalTransactionStateException("ungültiger Transaktion Status");
            }


        }catch (IllegalArgumentException e){
            throw new UndefinedTransactionStateException("State: '"+state+"' is undefined");
        }
    }

    private void runRollback() {
        for(Transfer t : transfersList) {
            if (t.getState()) {
                revertMoney(t);
            }
        }
    }

    void revertMoney(Transfer t){
        Account from = t.getFrom();
        Account to   = t.getTo();
        int amount   = t.getAmount();

        if(from == null){
            to.substractMoney(amount);
        }else if(to == null){
            from.addMoney(amount);
        }else {
            from.addMoney(amount);
            to.substractMoney(amount);
        }
    }

    public String getId() {
        return id;
    }

    public TransactionState getState() {
        return state;
    }

    public List<Transfer> getTransfersList() {
        return transfersList;
    }

    public void addTransfer(Transfer t) throws IllegalTransactionStateException {
        checkNotNull(t);
        if(this.state == TransactionState.READY || this.state == TransactionState.FAILED){
            this.transfersList.add(t);
        }else{
            throw new IllegalTransactionStateException("Transaktion abgelaufen");
        }
    }

    void checkTransfers(){
        for(Transfer t : transfersList){

            // Wenn ein Transfer Object fehlerhaft, dann setzte Status auf "FAILED
            // Somit ist weitere Suche unnötig"
            if(!t.getState()){
                this.state = TransactionState.FAILED;
                return;
            }

        }
    }
}

