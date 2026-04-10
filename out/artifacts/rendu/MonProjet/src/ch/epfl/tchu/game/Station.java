package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 1 mars 2021
 *représente ce que nous avons appelé une station
 */
public final class Station {
    private final int id;
    private final String  name;

    /**
     * constructeur
     * @param id identifiant de la station
     * @param name nom de la station
     */
    public Station(int id, String name){
        Preconditions.checkArgument(id>=0);
        this.id=id;
        this.name=name;
    }

    /**
     * getter de id
     * @return identifiant de la station
     */
    public int id(){ return id;}

    /**
     * getter de name
     * @return nom de la station
     */
    public String name(){ return name;}

    /**
     * redefinition de toString()
     * @return nom de la station
     */
    @Override
    public String toString(){
        return name;
    }
}
