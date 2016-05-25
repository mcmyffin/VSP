package Brokers.BrokerManagerComponent.DTOs;

import java.util.List;

/**
 * Created by sasa on 13.05.16.
 */
public class BrokerPlaceDTO {
    private String id;
    private String name;
    private String place;
    private String owner;
    private int value;
    private List<Integer> rentListe;
    private List<Integer> costList;
    private int anzahlHaeuser;
    private String visit;
    private String hypoCredit;


    public BrokerPlaceDTO(String id, String name, String place, String owner, int value, List<Integer> rentListe,
                          List<Integer> costList, int anzahlHaeuser, String visit, String hypoCredit) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.owner = owner;
        this.value = value;
        this.rentListe = rentListe;
        this.costList = costList;
        this.anzahlHaeuser = anzahlHaeuser;
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

    public List<Integer> getRentListe() {
        return rentListe;
    }

    public void setRentListe(List<Integer> rentListe) {
        this.rentListe = rentListe;
    }

    public List<Integer> getCostList() {
        return costList;
    }

    public void setCostList(List<Integer> costList) {
        this.costList = costList;
    }

    public int getHousest() {
        return anzahlHaeuser;
    }

    public void setHouses(int anzahlHaeuser) {
        this.anzahlHaeuser = anzahlHaeuser;
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
