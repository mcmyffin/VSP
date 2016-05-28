package Banks.BankManagerComponent;

import Banks.BankManagerComponent.DTOs.AccountDTO;

/**
 * Created by dima on 27.05.16.
 */
public class Account {

    private final String id;
    private String  player;
    private int     saldo;

    public Account(String id, String player, int saldo) {
        this.id = id;
        this.player = player;
        this.saldo = saldo;
    }

    public String getId() {
        return id;
    }

    public String getPlayer() {
        return player;
    }

    public int getSaldo() {
        return saldo;
    }

    public AccountDTO toDTO() {
        return new AccountDTO(
                this.getId(),
                this.getPlayer(),
                this.getSaldo()
        );
    }

    public void addMoney(int val){
        saldo+=val;
    }

    public void substractMoney(int val){
        saldo-= val;
    }
}
