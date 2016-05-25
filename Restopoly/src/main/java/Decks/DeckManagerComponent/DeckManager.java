package Decks.DeckManagerComponent;

import Common.Exceptions.DeckException;
import Common.Exceptions.GameDeckNotFoundException;
import Common.Exceptions.GameDecksAlreadyExistException;
import Common.Exceptions.WrongFormatException;
import Games.GameManagerComponent.DTO.GameDTO;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 13.05.16.
 */
public class DeckManager {

    private final Gson gson;
    private final Map<String,GameDecks> gameDecksMap;

    public DeckManager() {
        this.gameDecksMap = new HashMap();
        this.gson = new Gson();
    }

    private GameDecks getGameDecksById(String id) throws GameDeckNotFoundException {
        checkNotNull(id);
        if(!gameDecksMap.containsKey(id)) throw new GameDeckNotFoundException();
        return gameDecksMap.get(id);
    }

    public String getDecks() {
        return gson.toJson(gameDecksMap.keySet());
    }

    public String getDeckById(String gameID) throws GameDeckNotFoundException {
        GameDecks gameDecks = getGameDecksById(gameID);
        return gson.toJson(gameDecks.toDTO());
    }

    public synchronized String createDeck(String body) throws WrongFormatException, URISyntaxException, GameDecksAlreadyExistException {
        checkNotNull(body);

        JSONObject jsonObject = new JSONObject(body);
        if(!jsonObject.has("game")) throw new WrongFormatException("Game param not found");

        String gameID = jsonObject.getString("game");
        String encodedGameID = URLEncoder.encode(gameID);

        GameDecks gameDecks = new GameDecks(gameID);
        if(gameDecksMap.containsKey(gameDecks.getId())) throw new GameDecksAlreadyExistException();

        gameDecksMap.put(gameDecks.getId(),gameDecks);
        gameDecks.initializeDecks();

        return gameDecks.getId();

    }

    public String getNextChanceCard(String gameID) throws GameDeckNotFoundException, DeckException {
        GameDecks gameDecks = getGameDecksById(gameID);
        Card card = gameDecks.getChanceCard();
        return gson.toJson(card.toDTO());
    }

    public String getNextCommunityCard(String gameID) throws GameDeckNotFoundException, DeckException {
        GameDecks gameDecks = getGameDecksById(gameID);
        Card card = gameDecks.getCommunityCard();
        return gson.toJson(card.toDTO());
    }
}
