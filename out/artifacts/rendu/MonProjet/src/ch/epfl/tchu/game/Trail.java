package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 8 mars 2021
 * represente un chemin
 */
public final class Trail{
     private final int length;
     private final Station s1;
     private final Station s2;
     private final List<Route> routes;

    /**
     * constructeur
     * @param routes liste de routes
     * @param s1 station de depart(non nulle)
     * @param s2 station d'arrivée(non nulle)
     * @param length longueur du chemin entreles 2 stations(>0)
     */
    private Trail(List<Route> routes, Station s1, Station s2, int length){
      this.length=length;
      this.routes=routes;
      this.s1=s1;
      this.s2=s2;
    }

    /**
     * represente la methode qui donne le plus long chemin
     * @param routes liste des routes du chemin
     * @return le plus long chemin
     */
    public static Trail longest(List<Route> routes) {
        ArrayList<Route> rs = new ArrayList<>();
        for(Route r :routes){
            rs.add(r);
            rs.add(new Route(r.id(),r.station2(),r.station1(),r.length(),r.level(),r.color())); }
        Trail result=new Trail(null,null,null,0);
        for (Route route : rs) {
            ArrayList<Route> array = new ArrayList<>();
            array.add(route);
            Trail t = new Trail(array, route.station1(), route.station2(), route.length());
            Trail c = longestRec(rs, t);
            if ( result.length() < c.length()) {
                result = c; }
        }
        return result;
    }

    /**
     * methode auxiliaire de longest(...)
     */
     private static Trail longestRec(List<Route> routes, Trail base) {
        ArrayList<Route> addableRoutes = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            int oppositeRouteIndex = i % 2 == 0 ? i + 1 : i - 1;
            if (base.routes.contains(routes.get(i)) || base.routes.contains(routes.get(oppositeRouteIndex))) continue;
            if (routes.get(i).station1().equals(base.station2()))
                addableRoutes.add(routes.get(i)); }
        Trail calculated = base;
        for (Route route : addableRoutes) {
            ArrayList<Route> routesDeBase = new ArrayList<>(base.routes);
            routesDeBase.add(route);
            Trail temp = new Trail(routesDeBase, routesDeBase.get(0).station1(),
                    routesDeBase.get(routesDeBase.size() - 1).station2(), base.length + route.length());
            Trail tempResult = longestRec(routes, temp);
            if (calculated.length() < tempResult.length()) {
                calculated = tempResult; }
        }
        return calculated;
    }

    /**
     * getter de l'attribut length
     * @return la longueur du chemin
     */
    public int length(){
        return this.length;
    }

    /**
     * getter de l'attribut station1
     * @return la station du depart
     */
    public Station station1(){
        if(this.length==0) return null;
        return this.s1;
    }

    /**
     *getter de l'attribut station2
     * @return la station d'arrivée
     */
    public Station station2(){
        if(this.length==0) return null;
        return this.s2;
    }

    /**
     * redef de toString
     * @return la representation textuelle du chemin
     */
    public String toString(){
       if(s1==null||s2==null||routes.isEmpty()) return "";
     String result = s1.toString();
        for (Route route:routes) {
            result +=" - " + route.station2().toString(); }
        result+= " (" + length() +") ";
        return result;
    }

}
