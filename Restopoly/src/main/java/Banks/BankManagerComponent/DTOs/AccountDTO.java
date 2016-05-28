package Banks.BankManagerComponent.DTOs;

import Common.Exceptions.RequiredJsonParamsNotFoundException;

/**
 * Created by dima on 27.05.16.
 */
public class AccountDTO {

    private String  id;
    private String  player;
    private int     saldo;

    public AccountDTO(String id, String player, int saldo) {
        this.id = id;
        this.player = player;
        this.saldo = saldo;
    }

    public AccountDTO(String player, int saldo) {
        this.player = player;
        this.saldo = saldo;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public void checkContructorArguments() throws RequiredJsonParamsNotFoundException {
        if(isNull(player) || saldo <= 0) throw new RequiredJsonParamsNotFoundException();
    }

    private boolean isNull(Object o1, Object... objects){
        for(Object o : objects){
            if(o == null) return true;
        }
        return o1 == null;
    }
}
