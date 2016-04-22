package Games.GameManagerComponent.DTO;

/**
 * Created by dima on 20.04.16.
 */
public class PlayerDTO {

    private String id;
    private String user;
    private String pawn;
    private String account;
    private String ready;

    public PlayerDTO(String id, String user, String pawn, String account, String ready) {
        this.id = id;
        this.user = user;
        this.pawn = pawn;
        this.account = account;
        this.ready = ready;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPawn(String pawn) {
        this.pawn = pawn;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setReady(String ready) {
        this.ready = ready;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getPawn() {
        return pawn;
    }

    public String getAccount() {
        return account;
    }

    public String getReady() {
        return ready;
    }
}
