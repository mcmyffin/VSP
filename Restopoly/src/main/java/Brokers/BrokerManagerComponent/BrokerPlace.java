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
    private List<Integer> housestList;
    private String visit;
    private String hypoCredit;

    public BrokerPlace(String id, String name, String place, String owner, int value, int[] rentList,
                       int[]  costList, int[]  housestList, String visit, String hypoCredit) {
        this.id = id;
        List<Integer> costListArray = new ArrayList(Arrays.asList(costList));
        List<Integer> housesListArray = new ArrayList(Arrays.asList(housestList));
        List<Integer> rentListArray = new ArrayList(Arrays.asList(rentList));

        new BrokerPlace(id,name,place,owner,value,rentListArray,costListArray,housesListArray,visit,hypoCredit);
    }
    public BrokerPlace(String id, String name, String place, String owner, int value, List<Integer> rentListe,
                       List<Integer> costList, List<Integer> housestList, String visit, String hypoCredit) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.owner = owner;
        this.value = value;
        this.rentListe = rentListe;

        this.costList = costList;
        this.housestList = housestList;
        this.visit = visit;
        this.hypoCredit = hypoCredit;
    }
    

    public String getId() {return id;}

    public String getName() {return name;}

    public String getPlace() {return place;}

    public String getOwner() {return owner;}

    public int getValue() {return value;}

    public List<Integer> getRentListe() {return rentListe;}

    public List<Integer> getCostList() {return costList;}

    public List<Integer> getHousestList() {return housestList;}

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
                housestList,
                visit,
                hypoCredit
                );
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
