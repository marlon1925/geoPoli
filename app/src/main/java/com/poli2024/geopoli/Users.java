package com.poli2024.geopoli;

import java.util.ArrayList;

public class Users {
    private Mineros mineros;
    private ArrayList<LocationUser> lastLocations;

    public Users(Mineros mineros, ArrayList<LocationUser> lastLocations) {
        this.mineros = mineros;
        this.lastLocations = lastLocations;
    }
    public void addLastLocation(LocationUser locationUser){
        this.lastLocations.add(locationUser);
    }
    public LocationUser getLastLocation(int position){
        return this.lastLocations.get(position);
    }
    public Mineros getCiclista() {
        return mineros;
    }

    public void setCiclista(Mineros mineros) {
        this.mineros = mineros;
    }

    public ArrayList<LocationUser> getLastLocations() {
        return lastLocations;
    }

    public void setLastLocations(ArrayList<LocationUser> lastLocations) {
        this.lastLocations = lastLocations;
    }
}
