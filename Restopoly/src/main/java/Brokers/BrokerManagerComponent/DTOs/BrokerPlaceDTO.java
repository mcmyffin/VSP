package Brokers.BrokerManagerComponent.DTOs;

/**
 * Created by sasa on 13.05.16.
 */
public class BrokerPlaceDTO {
    private String id;
    private String name;
    private String place;
    private String owner;
    private int value;
    private int[] rentListe;
    private int[] costList;
    private int houses;
    private String visit;
    private String hypoCredit;


    public BrokerPlaceDTO(String id, String name, String place, String owner, int value, int[] rentListe,
                          int[] costList, int houses, String visit, String hypoCredit) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.owner = owner;
        this.value = value;
        this.rentListe = rentListe;
        this.costList = costList;
        this.houses = houses;
        this.visit = visit;
        this.hypoCredit = hypoCredit;
    }

    public String getId() { return id;}

    public void setId(String id) {this.id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int[] getRentListe() {
        return rentListe;
    }

    public void setRentListe(int[] rentListe) {
        this.rentListe = rentListe;
    }

    public int[] getCostList() {
        return costList;
    }

    public void setCostList(int[] costList) {
        this.costList = costList;
    }

    public int getHousest() {
        return houses;
    }

    public void setHouses(int houses) {
        this.houses = houses;
    }

    public String getVisit() {
        return visit;
    }

    public void setVisit(String visit) {
        this.visit = visit;
    }

    public String getHypoCredit() {
        return hypoCredit;
    }

    public void setHypoCredit(String hypoCredit) {
        this.hypoCredit = hypoCredit;
    }
}
