package Games.GameManagerComponent;

/**
 * Created by dima on 04.06.16.
 */
public enum GameStatus {
    REGISTRATION,
    RUNNING,
    FINISHED;

    @Override
    public String toString(){
        return this.name();
    }
}
