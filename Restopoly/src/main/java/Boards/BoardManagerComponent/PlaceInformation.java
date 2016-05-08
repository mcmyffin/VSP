package Boards.BoardManagerComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 02.05.16.
 */
public enum PlaceInformation {

    // non estates
    LOS("Los",0,-1),
    GEMEINSCHAFTSFELD("Gemeinschaftsfeld",0,-1),
    EINKOMMENSSTEUER("Einkommenssteuer",0,-1),
    EREIGNISFELD("Ereignisfeld",0,-1),
    GEFAENGNIS_ZU_BESUCH("Gefaengnis/nur zu Besuch",0,-1),
    FREI_PARKEN("Fei parken",0,-1),
    GEHE_INS_GEFAENGNIS("Gehe ins Gefaengnis",0,-1),
    ZUSATZSTEUER("Zusatzsteuer",0,-1),

    // estates
    ESTATE_1("Badstrasse", 60, 0),
    ESTATE_2("Turmstrasse", 60, 0),

    ESTATE_3("Chaussee-strasse", 100, 1),
    ESTATE_4("Elisenstrasse", 100, 1),
    ESTATE_5("Poststrasse", 120, 1),

    ESTATE_6("Seestrasse", 140, 2),
    ESTATE_7("Hafenstrasse", 140, 2),
    ESTATE_8("Neue Strasse", 160, 2),

    ESTATE_9("Münchner Strasse", 180, 3),
    ESTATE_10("Wiener Straße", 180, 3),
    ESTATE_11("Berliner Straße", 200, 3),

    ESTATE_12("Theater-strasse", 220, 4),
    ESTATE_13("Museums-strasse", 220, 4),
    ESTATE_14("Opernplatz", 240, 4),

    ESTATE_15("Lessing-strasse", 260, 5),
    ESTATE_16("Schiller-straße", 260, 5),
    ESTATE_17("Goethe-strasse", 280, 5),

    ESTATE_18("Rathausplatz", 300, 6),
    ESTATE_19("Hauptstrasse", 300, 6),
    ESTATE_20("Bahnhofstrasse", 320, 6),

    ESTATE_21("Parkstrasse", 350, 7),
    ESTATE_22("Schlossallee", 400, 7),

    ESTATE_23("Süd-bahnhof", 200, 8),
    ESTATE_24("Westbahnhof", 200, 8),
    ESTATE_25("Nord-bahnhof", 200, 8),
    ESTATE_26("Hauptbahnhof", 200, 8),

    ESTATE_27("Elektrizitaetswerk", 150, 9),
    ESTATE_28("Wasserwerk", 150, 9);



    private String name;
    private int cost;
    private int group;

    PlaceInformation(String name, int cost, int group) {
        this.name = name;
        this.cost = cost;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getGroup() {
        return group;
    }

    public static List<PlaceInformation> getEsttates(){

        String name = "ESTATE_";
        List<PlaceInformation> list = new ArrayList();

        for(int i = 0; i < 28; i++){

            PlaceInformation placeInformation = PlaceInformation.valueOf(name+(i+1));
            list.add(placeInformation);
        }

        return list;
    }
}
