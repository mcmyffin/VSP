package Banks.BankManagerComponent;

/**
 * Created by dima on 02.06.16.
 */
public enum TransferAction {

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
