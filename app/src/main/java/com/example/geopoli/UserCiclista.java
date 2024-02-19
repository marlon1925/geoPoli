package com.example.geopoli;

import java.util.ArrayList;

public class UserCiclista {
    private Ciclista ciclista;
    private ArrayList<LocationUser> lastLocations;

    public UserCiclista(Ciclista ciclista, ArrayList<LocationUser> lastLocations) {
        this.ciclista = ciclista;
        this.lastLocations = lastLocations;
    }
    public void addLastLocation(LocationUser locationUser){
        this.lastLocations.add(locationUser);
    }
    public LocationUser getLastLocation(int position){
        return this.lastLocations.get(position);
    }
    public Ciclista getCiclista() {
        return ciclista;
    }

    public void setCiclista(Ciclista ciclista) {
        this.ciclista = ciclista;
    }

    public ArrayList<LocationUser> getLastLocations() {
        return lastLocations;
    }

    public void setLastLocations(ArrayList<LocationUser> lastLocations) {
        this.lastLocations = lastLocations;
    }
}
