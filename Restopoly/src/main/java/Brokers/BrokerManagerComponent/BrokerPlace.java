package Brokers.BrokerManagerComponent;

import Brokers.BrokerManagerComponent.DTOs.BrokerPlaceDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sasa on 12.05.16.
 */
public class BrokerPlace {
    private final String id;
    private String name;
    private String place;
    private String owner;
    private int value;
    private List<Integer> rentListe;
    private List<Integer> costList;
    private int anzahlHaeuser;
    private String visit;
    private String hypoCredit;

    private int hypo = 0;

    public BrokerPlace(String id, String name, String place, String owner, int value, int[] rentList,
                       int[]  costList, int anzahlHaeuser, String visit, String hypoCredit) {
        this.id = id;
        List<Integer> costListArray = new ArrayList(Arrays.asList(costList));
        List<Integer> rentListArray = new ArrayList(Arrays.asList(rentList));

        new BrokerPlace(id,name,place,owner,value,rentListArray,costListArray,anzahlHaeuser,visit,hypoCredit);
    }
    public BrokerPlace(String id, String name, String place, String owner, int value, List<Integer> rentListe,
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

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getId() {return id;}

    public String getName() {return name;}

    public String getPlace() {return place;}

    public String getOwner() {return owner;}

    public int getValue() {return value;}

    public List<Integer> getRentListe() {return rentListe;}

    public List<Integer> getCostList() {return costList;}

    public int getHouses() {return anzahlHaeuser;}

    public String getVisit() {return visit;}

    public String getHypoCredit() {return hypoCredit;}

    public BrokerPlaceDTO toDTO() {
        return new BrokerPlaceDTO(
                id,
                name,
                place,
                owner,
                value,
                rentListe,
                costList,
                anzahlHaeuser,
                visit,
                hypoCredit
                );
    }

    public void setPlace(String place) {
        this.place = place;
    }

    boolean isHypothecSet(){
        return hypo > 0;
    }

    int getHypothec(){
        return hypo;
    }

    void setHypo(int hypo){
        if(hypo < 0) return;
        this.hypo = hypo;
    }

    public boolean hasOwner() {
        return this.owner != null && !this.owner.isEmpty();
    }
}
