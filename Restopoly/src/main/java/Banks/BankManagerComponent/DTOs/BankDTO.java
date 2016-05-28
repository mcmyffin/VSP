package Banks.BankManagerComponent.DTOs;

/**
 * Created by dima on 27.05.16.
 */
public class BankDTO {

    private String id;
    private String accounts;
    private String transfers;

    public BankDTO(String id, String accounts, String transfers) {
        this.id = id;
        this.accounts = accounts;
        this.transfers = transfers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccounts() {
        return accounts;
    }

    public void setAccounts(String accounts) {
        this.accounts = accounts;
    }

    public String getTransfers() {
        return transfers;
    }

    public void setTransfers(String transfers) {
        this.transfers = transfers;
    }
}
