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

    private final boolean state;

    public Transfer(String id, Account from, Account to, int amount, String reason, boolean state) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.reason = reason;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
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
                getFrom().getId(),
                getTo().getId(),
                getAmount(),
                getReason()
        );
    }
}
