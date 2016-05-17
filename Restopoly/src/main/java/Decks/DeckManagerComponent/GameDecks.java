package Decks.DeckManagerComponent;

import Common.Exceptions.DeckException;
import Common.Util.URIParser;
import Decks.DeckManagerComponent.DTOs.GameDecksDTO;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 13.05.16.
 */
public class GameDecks {

    private final String id;
    private final String gameService;
    private Deck communityDeck;
    private Deck chanceDeck;

    public GameDecks(String game) throws URISyntaxException {

        this.id             = "/decks"+ URIParser.getIDFromURI(game);
        this.gameService    = URIParser.getHostFromURI(game);


    }

    void initializeDecks(){
        this.communityDeck  = new Deck(createCommunityCards());
        this.chanceDeck     = new Deck(createChanceCards());
    }

    private Card createCard(String name, String txt){ return new Card(name,txt);}

    private List<Card> createCommunityCards(){
        List<Card> cards = new ArrayList();

        cards.add(createCard("Gefaengnis frei","Sie kommen aus dem Gefängnis frei! Behalten Sie diese Karte, bis Sie sie benötigen oder verkaufen."));
        cards.add(createCard("Schulgeld","Schulgeld. Zahlen Sie 50"));
        cards.add(createCard("Urlaubsgeld","Sie erhalten 100"));
        cards.add(createCard("Lebensversicherung","Ihre Lebensversicherung wird fällig. Sie erhalten 100"));
        cards.add(createCard("Arzt-Kosten","Zahlen Sie 50"));
        cards.add(createCard("Einkommenssteuerrueckerstattung","Sie erhalten 20"));
        cards.add(createCard("Krankenhausgebuehren","Zahlen Sie 100."));
        cards.add(createCard("Gefaengnis","Gehen Sie in das Gefägnis. Begeben Sie sich direkt dorthin. Gehen Sie nicht über Los. Ziehen Sie nicht 200 ein"));
        cards.add(createCard("Aktien","Sie erhalten auf Vorzugs-Aktien 7% Dividende. 25"));
        cards.add(createCard("Gebutstag","Sie haben Geburtstag. Jeder Spieler schenkt Ihnen 10"));
        cards.add(createCard("Erben","Sie erben 100"));
        cards.add(createCard("Lagerverkauf","Aus Lagerverkäufen erhalten Sie 50"));
        cards.add(createCard("Zweiter Preis","Zweiter Preis im Schönheitswettbewerb. Sie erhalten 10."));
        cards.add(createCard("Straßenausbesserungsarbeiten","Sie werden zu Straßenausbesserungsarbeiten herangezogen. Zahlen Sie 40 je Haus und 115 je Hotel an die Bank"));

        return cards;
    }

    private List<Card> createChanceCards(){
        List<Card> cards = new ArrayList();

        cards.add(createCard("zurueck","Gehe drei Felder zurück"));
        cards.add(createCard("vor","Gehe drei Felder vor"));
        cards.add(createCard("skateboard strafe","Du wirs wegen Skateboardens auf einer öffentlichen Strasse verhaftet. Zahle 200 Strafe"));
        cards.add(createCard("angestellte","Steuern für Angestellte und Geschäftsreinigung  werden fällig. Zahle 400 fuer jedes Haus und 1200 fuer jedes Hotel."));
        cards.add(createCard("West-Bahnhof","Gehe zum Westbahnhof. Wenn du über Los kommst, ziehe 200 ein"));
        cards.add(createCard("verreisen","Du willst verreisen, gehe zum Hauptbahnhof. Wenn du über Los kommst, ziehe 200 ein"));
        cards.add(createCard("bank","Die Bank zahlt dir eine Dividende von 750"));
        cards.add(createCard("rennen","Da du sportlich bist, hast du beim Rennen gewonnen. Ziehe 1500 ein"));

        return cards;
    }

    public String getId() {
        return id;
    }

    public String getGameService() {
        return gameService;
    }

    public Card getCommunityCard() throws DeckException {
        return communityDeck.getNextCard();
    }

    public Card getChanceCard() throws DeckException {
        return chanceDeck.getNextCard();
    }

    public GameDecksDTO toDTO(){
        return new GameDecksDTO(
                id,
                id+"/community",
                id+"/chance"
        );
    }
}
