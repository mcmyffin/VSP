package Brokers.BrokerManagerComponent;

import Brokers.BrokerManagerComponent.DTOs.BrokerPlaceDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sasa on 12.05.16.
 */
public class BrokerPlace {
    private String id;
    private String name;
    private String place;
    private String owner;
    private int value;
    private int[]rentListe;
    private int[]costList;
    private int houses;
    private String visit;
    private String hypoCredit;

    private int hypo = 0;

    public BrokerPlace(String id, String name, String place, String owner, int value,
                       int[] rentListe, int[] costList, int houses, String visit, String hypoCredit) {
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


    void updateBrokerplaceID(String id){
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getId() {return id;}

    public String getName() {return name;}

    public String getPlace() {return place;}

    public String getOwner() {return owner;}

    public int getValue() {return value;}

    public int[] getRentListe() {return rentListe;}

    public int[] getCostList() {return costList;}

    public int getHouses() {return houses;}

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
                houses,
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

    @Override
    public String toString() {
        return "BrokerPlace{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", owner='" + owner + '\'' +
                ", value=" + value +
                ", rentListe=" + rentListe +
                ", costList=" + costList +
                ", houses=" + houses +
                ", visit='" + visit + '\'' +
                ", hypoCredit='" + hypoCredit + '\'' +
                ", hypo=" + hypo +
                '}';
    }
}
