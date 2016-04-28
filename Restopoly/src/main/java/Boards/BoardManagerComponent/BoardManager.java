package Boards.BoardManagerComponent;

import Common.Exceptions.BoardAlreadyExistsException;
import Common.Exceptions.BoardNotFoundException;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 21.04.16.
 */
public class BoardManager {

    private final Gson gson;
    private final Map<String,Board> boardGameIDMap; // Map<GameID,Board>
    private final Map<String,Board> boardMap;       // Map<BoardID,Board>

    public BoardManager() {
        this.gson = new Gson();
        this.boardMap = new HashMap();
        this.boardGameIDMap = new HashMap();
    }


    private synchronized Board getBoardObjectByGameId(String gameID) throws BoardNotFoundException {
        checkNotNull(gameID);

        if(!boardGameIDMap.containsKey(gameID)) throw new BoardNotFoundException();
        return boardGameIDMap.get(gameID);
    }
    private synchronized Board getBoardObjectByBoardId(String boardID) throws BoardNotFoundException {
        checkNotNull(boardID);

        if(!boardMap.containsKey(boardID)) throw new BoardNotFoundException();
        return boardMap.get(boardID);
    }

    public synchronized String createBoard(String gameID) throws BoardAlreadyExistsException {
        checkNotNull(gameID);

        if(boardGameIDMap.containsKey(gameID)) throw new BoardAlreadyExistsException();

        String id       = Integer.toString(boardMap.size());
        String boardID  = "boards/"+id;
        Board board     = new Board(boardID,gameID);

        boardGameIDMap.put(gameID,board);
        boardMap.put(id,board);

        // hole dir service broker

        // hole dir alle Places vom Broker
        // speichere diese in das Board
        return boardID;
    }


    public String getBoards() {
        Collection<Board> boardCollection = boardMap.values();
        Collection<String> boards = new ArrayList<>();

        for(Board b : boardCollection){
            boards.add(b.getId());
        }
        return gson.toJson(boards);
    }

    public static void main(String[] args) {
        BoardManager manager = new BoardManager();
    }

    public String createPawn(String jsonBody) {
        throw new UnsupportedOperationException();
    }
}
