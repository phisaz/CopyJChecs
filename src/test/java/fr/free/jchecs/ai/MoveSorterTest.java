/*
 jChecs: a simple Java chess game sample

 Copyright (C) 2006-2017 by David Cotton

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package fr.free.jchecs.ai;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Test;

import static fr.free.jchecs.core.Piece.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import fr.free.jchecs.core.Move;
import fr.free.jchecs.core.Square;

/**
 * Tests unitaires des classes d'ordenancement des mouvements.
 *
 * @author David Cotton
 */
public final class MoveSorterTest {
    /**
     * Pour que JUnit puisse instancier les tests.
     */
    public MoveSorterTest() {
        // Rien de spécifique...
    }

    /**
     * Valide la méthode de comparaison du tri static.
     */
    @Test
    public void valideStaticCompareTo() {
        final Comparator<Move> comp = new StaticMoveSorter();
        final Move mvt1 = new Move(BLACK_PAWN, Square.valueOf("a2"),
                Square.valueOf("a4"));
        final Move mvt2 = new Move(BLACK_PAWN, Square.valueOf("a2"),
                Square.valueOf("b3"), WHITE_PAWN);
        final Move mvt3 = new Move(BLACK_ROOK, Square.valueOf(16),
                Square.valueOf(24));
        final Move mvt4 = new Move(BLACK_PAWN, Square.valueOf(12),
                Square.valueOf(20));
        final Move mvt5 = new Move(BLACK_PAWN, Square.valueOf(21),
                Square.valueOf(22), WHITE_BISHOP);

        assertTrue(comp.compare(mvt1, mvt1) == 0);

        assertTrue(comp.compare(mvt1, mvt2) > 0);
        assertTrue(comp.compare(mvt2, mvt1) < 0);

        assertTrue(comp.compare(mvt1, mvt3) > 0);
        assertTrue(comp.compare(mvt3, mvt1) < 0);

        assertTrue(comp.compare(mvt2, mvt3) < 0);
        assertTrue(comp.compare(mvt3, mvt2) > 0);

        assertTrue(comp.compare(mvt1, mvt4) == 0);
        assertTrue(comp.compare(mvt4, mvt1) == 0);

        assertTrue(comp.compare(mvt2, mvt5) > 0);
        assertTrue(comp.compare(mvt5, mvt1) < 0);

        final Move[] mvts = {mvt1, mvt2, mvt3, mvt4, mvt5};
        Arrays.sort(mvts, comp);

        assertEquals(mvts[0], mvt5);
        assertEquals(mvts[1], mvt2);
        assertEquals(mvts[2], mvt3);
        assertEquals(mvts[3], mvt1);
        assertEquals(mvts[4], mvt4);
    }
}
