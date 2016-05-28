package Banks.BankManagerComponent.DTOs;

/**
 * Created by dima on 28.05.16.
 */
public class TransferDTO {

    private String id;
    private String from;
    private String to;
    private int amount;
    private String reason;

    public TransferDTO(String id, String from, String to, int amount, String reason) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
