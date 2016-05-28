package Banks.BankManagerComponent;

import Common.Exceptions.IllegalTransactionStateException;
import Common.Exceptions.UndefinedTransactionStateException;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 27.05.16.
 */
public class Transaction {

    private final String id;
    private TransferState state;
    private List<Transfer> transfersList;

    public Transaction(String id) {
        checkNotNull(id);
        this.id = id;
        this.state = TransferState.READY;
    }

    public synchronized void setTransferAction(String state) throws UndefinedTransactionStateException, IllegalTransactionStateException {
        checkNotNull(state);

        if(this.state.equals(TransferState.COMMITED)) throw new IllegalTransactionStateException();
        if(this.state.equals(TransferState.ROLLBACK)) throw new IllegalTransactionStateException();

        try{
            TransferAction transferAction = TransferAction.valueOf(state);

            // Transaction wird noch befüllt
            if(this.state.equals(TransferState.READY)){

                if(transferAction.equals(TransferAction.RADY)) return; // status bleibt unverändert
                else if(transferAction.equals(TransferAction.COMMIT)) this.state = TransferState.COMMITED;
                else throw new IllegalTransactionStateException();

            // Transaction ist beim Commit fehlgeschlagen
            }else if(this.state.equals(TransferState.FAILED)){
                if(transferAction.equals(TransferAction.ROLLBACK)){
                    this.state = TransferState.ROLLBACK;
                    runRollback();
                }else throw new IllegalTransactionStateException();
            }


        }catch (IllegalArgumentException e){
            throw new UndefinedTransactionStateException("State: '"+state+"' is undefined");
        }
    }

    private void runRollback() {
        for(Transfer t : transfersList) {
            if (t.getState()) {

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

    public TransferState getState() {
        return state;
    }

    public List<Transfer> getTransfersList() {
        return transfersList;
    }
}



enum TransferState {

    READY("ready"),
    FAILED("failed"),
    COMMITED("commited"),
    ROLLBACK("rollback");

    private String val;

    TransferState(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}

enum TransferAction {

    COMMIT("commit"),
    ROLLBACK("rollback"),
    RADY("ready");

    private String val;

    TransferAction(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
