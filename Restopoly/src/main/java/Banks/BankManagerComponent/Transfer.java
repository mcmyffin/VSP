package Banks.BankManagerComponent;

import Banks.BankManagerComponent.DTOs.TransferDTO;

/**
 * Created by dima on 28.05.16.
 */
public class Transfer {

    private final String id;
    private Account from;
    private Account to;
    private int amount;
    private String reason;

    private boolean state;

    public Transfer(String id, Account from, Account to, int amount, String reason, boolean state) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.reason = reason;
        this.state = state;
    }

    void setFrom(Account from) {
        this.from = from;
    }

    void setTo(Account to) {
        this.to = to;
    }

    void setAmount(int amount) {
        this.amount = amount;
    }

    void setReason(String reason) {
        this.reason = reason;
    }

    void setState(boolean state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public Account getFrom() {
        return from;
    }

    public String getFromId() {
        return (from == null ? "" : from.getId());
    }

    public Account getTo() {
        return to;
    }

    public String getToId() {
        return (to == null ? "" : to.getId());
    }

    public int getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public boolean getState(){
        return state;
    }

    public TransferDTO toDTO() {
        return new TransferDTO(
                getId(),
                getFromId(),
                getToId(),
                getAmount(),
                getReason()
        );
    }


}
