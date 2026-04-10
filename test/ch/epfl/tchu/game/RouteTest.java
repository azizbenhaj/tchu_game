package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;
//import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {
    private static final List<Color> COLORS =
            List.of(
                    Color.BLACK,
                    Color.VIOLET,
                    Color.BLUE,
                    Color.GREEN,
                    Color.YELLOW,
                    Color.ORANGE,
                    Color.RED,
                    Color.WHITE);
    private static final List<Card> CAR_CARDS =
            List.of(
                    Card.BLACK,
                    Card.VIOLET,
                    Card.BLUE,
                    Card.GREEN,
                    Card.YELLOW,
                    Card.ORANGE,
                    Card.RED,
                    Card.WHITE);

    @Test
    void routeConstructorFailsWhenBothStationsAreEqual() {
        var s = new Station(0, "Lausanne");
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("id", s, s, 1, Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenLengthIsInvalid() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("id", s1, s2, 0, Level.OVERGROUND, Color.BLACK);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("id", s1, s2, 7, Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenIdIsNull() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        assertThrows(NullPointerException.class, () -> {
            new Route(null, s1, s2, 1, Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenOneStationIsNull() {
        var s = new Station(0, "EPFL");
        assertThrows(NullPointerException.class, () -> {
            new Route("id", null, s, 1, Level.OVERGROUND, Color.BLACK);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("id", s, null, 1, Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenLevelIsNull() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        assertThrows(NullPointerException.class, () -> {
            new Route("id", s1, s2, 1, null, Color.BLACK);
        });
    }

    @Test
    void routeIdReturnsId() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var routes = new Route[100];
        for (int i = 0; i < routes.length; i++)
            routes[i] = new Route("id" + i, s1, s2, 1, Level.OVERGROUND, Color.BLACK);
        for (int i = 0; i < routes.length; i++)
            assertEquals("id" + i, routes[i].id());
    }

    @Test
    void routeStation1And2ReturnStation1And2() {
        var rng = TestRandomizer.newRandom();
        var stations = new Station[100];
        for (int i = 0; i < stations.length; i++)
            stations[i] = new Station(i, "Station " + i);
        var routes = new Route[100];
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var l = 1 + rng.nextInt(6);
            routes[i] = new Route("r" + i, s1, s2, l, Level.OVERGROUND, Color.RED);
        }
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var r = routes[i];
            assertEquals(s1, r.station1());
            assertEquals(s2, r.station2());
        }
    }

    @Test
    void routeLengthReturnsLength() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        var routes = new Route[6];
        for (var l = 1; l <= 6; l++)
            routes[l - 1] = new Route(id, s1, s2, l, Level.OVERGROUND, Color.BLACK);
        for (var l = 1; l <= 6; l++)
            assertEquals(l, routes[l - 1].length());

    }

    @Test
    void routeLevelReturnsLevel() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        var ro = new Route(id, s1, s2, 1, Level.OVERGROUND, Color.BLACK);
        var ru = new Route(id, s1, s2, 1, Level.UNDERGROUND, Color.BLACK);
        assertEquals(Level.OVERGROUND, ro.level());
        assertEquals(Level.UNDERGROUND, ru.level());
    }

    @Test
    void routeColorReturnsColor() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        var routes = new Route[8];
        for (var c : COLORS)
            routes[c.ordinal()] = new Route(id, s1, s2, 1, Level.OVERGROUND, c);
        for (var c : COLORS)
            assertEquals(c, routes[c.ordinal()].color());
        var r = new Route(id, s1, s2, 1, Level.OVERGROUND, null);
        assertNull(r.color());
    }

    @Test
    void routeStationsReturnsStations() {
        var rng = TestRandomizer.newRandom();
        var stations = new Station[100];
        for (int i = 0; i < stations.length; i++)
            stations[i] = new Station(i, "Station " + i);
        var routes = new Route[100];
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var l = 1 + rng.nextInt(6);
            routes[i] = new Route("r" + i, s1, s2, l, Level.OVERGROUND, Color.RED);
        }
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            assertEquals(List.of(s1, s2), routes[i].stations());
        }
    }

    @Test
    void routeStationOppositeFailsWithInvalidStation() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var s3 = new Station(1, "EPFL");
        var r = new Route("id", s1, s2, 1, Level.OVERGROUND, Color.RED);
        assertThrows(IllegalArgumentException.class, () -> {
            r.stationOpposite(s3);
        });
    }

    @Test
    void routeStationOppositeReturnsOppositeStation() {
        var rng = TestRandomizer.newRandom();
        var stations = new Station[100];
        for (int i = 0; i < stations.length; i++)
            stations[i] = new Station(i, "Station " + i);
        var routes = new Route[100];
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var l = 1 + rng.nextInt(6);
            routes[i] = new Route("r" + i, s1, s2, l, Level.OVERGROUND, Color.RED);
        }
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var r = routes[i];
            assertEquals(s1, r.stationOpposite(s2));
            assertEquals(s2, r.stationOpposite(s1));
        }
    }

    @Test
    void routePossibleClaimCardsWorksForOvergroundColoredRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var i = 0; i < COLORS.size(); i++) {
            var color = COLORS.get(i);
            var card = CAR_CARDS.get(i);
            for (var l = 1; l <= 6; l++) {
                var r = new Route(id, s1, s2, l, Level.OVERGROUND, color);
                System.out.println(SortedBag.of(l, card).toString());
                assertEquals(List.of(SortedBag.of(l, card)), r.possibleClaimCards());
            }
        }
    }

    @Test
    void routePossibleClaimCardsWorksOnOvergroundNeutralRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Level.OVERGROUND, null);
            var expected = List.of(
                    SortedBag.of(l, Card.BLACK),
                    SortedBag.of(l, Card.VIOLET),
                    SortedBag.of(l, Card.BLUE),
                    SortedBag.of(l, Card.GREEN),
                    SortedBag.of(l, Card.YELLOW),
                    SortedBag.of(l, Card.ORANGE),
                    SortedBag.of(l, Card.RED),
                    SortedBag.of(l, Card.WHITE));
            System.out.println(expected.toString());
            assertEquals(expected, r.possibleClaimCards());
        }
    }

    @Test
    void routePossibleClaimCardsWorksOnUndergroundColoredRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var i = 0; i < COLORS.size(); i++) {
            var color = COLORS.get(i);
            var card = CAR_CARDS.get(i);
            for (var l = 1; l <= 6; l++) {
                var r = new Route(id, s1, s2, l, Level.UNDERGROUND, color);

                var expected = new ArrayList<SortedBag<Card>>();
                for (var locomotives = 0; locomotives <= l; locomotives++) {
                    var cars = l - locomotives;

                    expected.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
                System.out.println(expected.toString());
                assertEquals(expected, r.possibleClaimCards());
            }
        }
    }

    @Test
    void routePossibleClaimCardsWorksOnUndergroundNeutralRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, null);

            var expected = new ArrayList<SortedBag<Card>>();
            for (var locomotives = 0; locomotives <= l; locomotives++) {
                var cars = l - locomotives;
                if (cars == 0)
                    expected.add(SortedBag.of(locomotives, Card.LOCOMOTIVE));
                else {
                    for (var card : CAR_CARDS)
                        expected.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
            }
            assertEquals(expected, r.possibleClaimCards());
        }
    }

    @Test
    void routeAdditionalClaimCardsCountWorksWithColoredCardsOnly() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        for (var l = 1; l <= 6; l++) {
            for (var color : COLORS) {
                var matchingCard = CAR_CARDS.get(color.ordinal());
                var nonMatchingCard = color == Color.BLACK
                        ? Card.WHITE
                        : Card.BLACK;
                var claimCards = SortedBag.of(l, matchingCard);
                var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, color);
                for (var m = 0; m <= 3; m++) {
                    for (var locomotives = 0; locomotives <= m; locomotives++) {
                        var drawnB = new SortedBag.Builder<Card>();
                        drawnB.add(locomotives, Card.LOCOMOTIVE);
                        drawnB.add(m - locomotives, matchingCard);
                        drawnB.add(3 - m, nonMatchingCard);
                        var drawn = drawnB.build();
                        assertEquals(m, r.additionalClaimCardsCount(claimCards, drawn));
                    }
                }
            }
        }
    }

    @Test
    void routeAdditionalClaimCardsCountWorksWithLocomotivesOnly() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        for (var l = 1; l <= 6; l++) {
            for (var color : COLORS) {
                var matchingCard = CAR_CARDS.get(color.ordinal());
                var nonMatchingCard = color == Color.BLACK
                        ? Card.WHITE
                        : Card.BLACK;
                var claimCards = SortedBag.of(l, Card.LOCOMOTIVE);
                var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, color);
                for (var m = 0; m <= 3; m++) {
                    for (var locomotives = 0; locomotives <= m; locomotives++) {
                        var drawnB = new SortedBag.Builder<Card>();
                        drawnB.add(locomotives, Card.LOCOMOTIVE);
                        drawnB.add(m - locomotives, matchingCard);
                        drawnB.add(3 - m, nonMatchingCard);
                        var drawn = drawnB.build();
                        assertEquals(locomotives, r.additionalClaimCardsCount(claimCards, drawn));
                    }
                }
            }
        }
    }

    @Test
    void routeAdditionalClaimCardsCountWorksWithMixedCards() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        for (var l = 2; l <= 6; l++) {
            for (var color : COLORS) {
                var matchingCard = CAR_CARDS.get(color.ordinal());
                var nonMatchingCard = color == Color.BLACK
                        ? Card.WHITE
                        : Card.BLACK;
                for (var claimLoc = 1; claimLoc < l; claimLoc++) {
                    var claimCards = SortedBag.of(
                            l - claimLoc, matchingCard,
                            claimLoc, Card.LOCOMOTIVE);
                    var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, color);
                    for (var m = 0; m <= 3; m++) {
                        for (var locomotives = 0; locomotives <= m; locomotives++) {
                            var drawnB = new SortedBag.Builder<Card>();
                            drawnB.add(locomotives, Card.LOCOMOTIVE);
                            drawnB.add(m - locomotives, matchingCard);
                            drawnB.add(3 - m, nonMatchingCard);
                            var drawn = drawnB.build();
                            assertEquals(m, r.additionalClaimCardsCount(claimCards, drawn));
                        }
                    }
                }
            }
        }
    }

    @Test
    void routeClaimPointsReturnsClaimPoints() {
        var expectedClaimPoints =
                List.of(Integer.MIN_VALUE, 1, 2, 4, 7, 10, 15);
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Route.Level.OVERGROUND, Color.BLACK);
            assertEquals(expectedClaimPoints.get(l), r.claimPoints());
        }
    }

}
/*


   @Test
   void constructorSameStation(){
   Station var =new Station(1,"y");
    assertThrows(IllegalArgumentException.class, () -> {
     new Route("x",var,
            var, 5, Route.Level.UNDERGROUND, Color.RED);
    });
   }
    @Test
    void constructorMinLength(){
     assertThrows(IllegalArgumentException.class, () -> {
      new Route("x",new Station(1,"y"),
              new Station(1,"y"), Constants.MIN_ROUTE_LENGTH-1, Route.Level.UNDERGROUND, Color.RED);
     });
    }

 @Test
 void constructorMaxLength(){
  assertThrows(IllegalArgumentException.class, () -> {
   new Route("x",new Station(1,"y"),
           new Station(1,"y"), Constants.MAX_ROUTE_LENGTH+1, Route.Level.UNDERGROUND, Color.RED);
  });
 }
 @Test
 void constructorNull() {
       Station var=new Station(1, "y");
  assertThrows(IllegalArgumentException.class, () -> {
   new Route("x", var,
           var, 5, null, Color.RED);
  });
 }

    @Test
    void id() {
     Route route= new Route("x",new Station(1,"y"),
             new Station(3,"z"), 5, Route.Level.UNDERGROUND, Color.RED);

        assertEquals("x",route.id());
    }

    @Test
    void station1() {
    Station var=new Station(1,"y");
     Route route= new Route("x",var,
             new Station(3,"z"), 5, Route.Level.UNDERGROUND, Color.RED);
     assertEquals(var,route.station1());
    }

    @Test
    void station2() {
      Station var=new Station(1,"y");
      Route route= new Route("x",new Station(3,"z"),
              var, 5, Route.Level.UNDERGROUND, Color.RED);
      assertEquals(var,route.station2());
     }


    @Test
    void length() {
     Route route= new Route("x",new Station(3,"z"),
             new Station(1,"y"), 5, Route.Level.UNDERGROUND, Color.RED);
     assertEquals(5,route.length());

    }

    @Test
    void level() {
     Route route= new Route("x",new Station(3,"z"),
             new Station(1,"y"), 5, Route.Level.UNDERGROUND, Color.RED);
     assertEquals(Route.Level.UNDERGROUND,route.level());
    }

    @Test
    void color() {
     Route route= new Route("x",new Station(3,"z"),
             new Station(1,"y"), 5, Route.Level.UNDERGROUND, Color.RED);
     assertEquals(Color.RED,route.color());
    }

    @Test
    void stations() {
    Station var1=new Station(3,"z");
    Station var2=new Station(2,"y");
     Route route= new Route("x",var1, var2, 5, Route.Level.UNDERGROUND, Color.RED);
     assertEquals(List.of(var1,var2),List.of(route.station1(),route.station2()));
     //assertEquals(List.of(var2,var1),List.of(route.station1(),route.station2()));
    }

    @Test
    void stationOpposite() {
     Station var1=new Station(3,"z");
     Station var2=new Station(2,"y");
     Station var3=new Station(1,"y");
     Route route= new Route("x",var1, var3, 5, Route.Level.UNDERGROUND, Color.RED);
     //Route route2= new Route("x",new Station(4,"z"), new Station(5,"z"), 5, Route.Level.UNDERGROUND, Color.RED);
     //assertEquals(var1,route.stationOpposite(var3));
     assertThrows(IllegalArgumentException.class,()->{ route.stationOpposite(var2); });

    }

    @Test
    void possibleClaimCards() {
       Route route=new Route("x",new Station(4,"z"),
               new Station(5,"z"), 5, Route.Level.UNDERGROUND, Color.RED);
      assertEquals("[{5×RED}, {4×RED, LOCOMOTIVE}, {3×RED, 2×LOCOMOTIVE}, {2×RED, 3×LOCOMOTIVE}, {RED, 4×LOCOMOTIVE}, {5×LOCOMOTIVE}]",
        route.possibleClaimCards().toString());
    }

    @Test
    void additionalClaimCardsCount() {
        Route route=new Route("x",new Station(4,"z"),
                new Station(5,"z"), 3, Route.Level.UNDERGROUND, Color.RED);
        SortedBag<Card> claimCards=SortedBag.of(1, Card.LOCOMOTIVE,1,Card.RED);
        SortedBag<Card> drawnCards=SortedBag.of(1,Card.ORANGE,2,Card.RED);

       assertEquals(2,route.additionalClaimCardsCount(claimCards,drawnCards));

    }
    @Test
    void additionalClaimCardsCountOverground() {
        Route route = new Route("x", new Station(4, "z"),
                new Station(5, "z"), 3, Route.Level.OVERGROUND, Color.RED);
        SortedBag<Card> claimCards=SortedBag.of(1, Card.LOCOMOTIVE,1,Card.RED);
        SortedBag<Card> drawnCards=SortedBag.of(1,Card.ORANGE,2,Card.RED);
        assertThrows(IllegalArgumentException.class, () -> {
            route.additionalClaimCardsCount(claimCards,drawnCards);

        });
    }

    @Test
    void additionalClaimCardsCountSizeDrawnCards() {
        Route route = new Route("x", new Station(4, "z"),
                new Station(5, "z"), 3, Route.Level.UNDERGROUND, Color.RED);
        SortedBag<Card> claimCards = SortedBag.of(1, Card.LOCOMOTIVE, 1, Card.RED);
        SortedBag<Card> drawnCards = SortedBag.of(1, Card.ORANGE, 1, Card.RED);
        assertThrows(IllegalArgumentException.class, () -> {
            route.additionalClaimCardsCount(claimCards, drawnCards);
        });
    }


    @Test
    void claimPoints() {
        Route route2= new Route("x",new Station(4,"z"), new Station(3,"z"),4, Route.Level.UNDERGROUND, Color.RED);
       assertEquals(7, route2.claimPoints());

    }
}*/