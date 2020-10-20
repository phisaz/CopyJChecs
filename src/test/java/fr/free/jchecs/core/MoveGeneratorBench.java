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
package fr.free.jchecs.core;

import static fr.free.jchecs.core.BoardFactory.Type.FASTEST;
import static fr.free.jchecs.core.FENUtils.toBoard;
import static fr.free.jchecs.core.Piece.BLACK_BISHOP;
import static fr.free.jchecs.core.Piece.BLACK_KNIGHT;
import static fr.free.jchecs.core.Piece.BLACK_PAWN;
import static fr.free.jchecs.core.Piece.WHITE_BISHOP;
import static fr.free.jchecs.core.Piece.WHITE_PAWN;
import static fr.free.jchecs.core.Piece.WHITE_QUEEN;

/**
 * Classe utilitaire testant les performances des classes permettant de générer
 * des états de partie.
 *
 * @author David Cotton
 */
@SuppressWarnings("UnusedAssignment")
final class MoveGeneratorBench {
    /**
     * Classe utilitaire : ne pas intancier.
     */
    private MoveGeneratorBench() {
        // Rien de spécifique...
    }

    /**
     * Teste la vitesse de la méthode de dérivation.
     */
    private static void benchDerive() {
        final int nbTests = 100000;

        final Move mvt1 = new Move(WHITE_PAWN, Square.valueOf("e2"),
                Square.valueOf("e3"));
        final Move mvt2 = new Move(BLACK_PAWN, Square.valueOf("b7"),
                Square.valueOf("b6"));
        final Move mvt3 = new Move(WHITE_BISHOP, Square.valueOf("f1"),
                Square.valueOf("c4"));
        final Move mvt4 = new Move(BLACK_KNIGHT, Square.valueOf("g8"),
                Square.valueOf("h6"));
        final Move mvt5 = new Move(WHITE_QUEEN, Square.valueOf("d1"),
                Square.valueOf("f3"));
        final Move mvt6 = new Move(BLACK_BISHOP, Square.valueOf("c8"),
                Square.valueOf("b7"));

        System.out.println("Benchmark (" + nbTests * 12
                + ") : derive(Move,boolean)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator depart = BoardFactory.valueOf(t,
                    BoardFactory.State.STARTING);
            MoveGenerator etat;
            final long debut = System.currentTimeMillis();
            for (int i = nbTests; i > 0; i--) {
                etat = depart.derive(mvt1, true);
                etat = etat.derive(mvt2, true);
                etat = etat.derive(mvt3, true);
                etat = etat.derive(mvt4, true);
                etat = etat.derive(mvt5, true);
                etat = etat.derive(mvt6, true);
                etat = depart.derive(mvt1, false);
                etat = etat.derive(mvt2, false);
                etat = etat.derive(mvt3, false);
                etat = etat.derive(mvt4, false);
                etat = etat.derive(mvt5, false);
                etat = etat.derive(mvt6, false);
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + depart.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de recherche des cases cibles d'une position.
     */
    private static void benchGetAllTargets() {
        final int nbTests = 10000;

        System.out.println("Benchmark (" + 64 * nbTests * 4
                + ") : getAllTargets(Square)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat = BoardFactory.valueOf(t,
                    BoardFactory.State.STARTING);
            final Square[] lst = Square.values();
            final long debut = System.currentTimeMillis();
            for (int f = lst.length - 1; f >= 0; f--) {
                final Square s = lst[f];
                for (int i = nbTests; i > 0; i--) {
                    etat.getAllTargets(s);
                    etat.getAllTargets(s);
                    etat.getAllTargets(s);
                    etat.getAllTargets(s);
                }
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de recherche des cases cibles d'un fou.
     */
    private static void benchGetBishopTargets() {
        final int nbTests = 10000;

        System.out.println("Benchmark (" + 64 * nbTests * 4
                + ") : getBishopTargets(Square,boolean)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat = BoardFactory.valueOf(t,
                    BoardFactory.State.EMPTY);
            final Square[] lst = Square.values();
            final long debut = System.currentTimeMillis();
            for (int f = lst.length - 1; f >= 0; f--) {
                final Square s = lst[f];
                for (int i = nbTests; i > 0; i--) {
                    etat.getBishopTargets(s, true);
                    etat.getBishopTargets(s, false);
                    etat.getBishopTargets(s, true);
                    etat.getBishopTargets(s, false);
                }
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de recherche de la case d'un roi.
     */
    private static void benchGetKingSquare() {
        final int nbTests = 3200000;

        System.out.println("Benchmark (" + nbTests * 8
                + ") : getKingSquare(boolean)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat = BoardFactory.valueOf(t,
                    BoardFactory.State.STARTING);
            final long debut = System.currentTimeMillis();
            for (int i = nbTests; i > 0; i--) {
                etat.getKingSquare(true);
                etat.getKingSquare(false);
                etat.getKingSquare(true);
                etat.getKingSquare(false);
                etat.getKingSquare(true);
                etat.getKingSquare(false);
                etat.getKingSquare(true);
                etat.getKingSquare(false);
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de recherche des cases cibles d'un roi.
     */
    private static void benchGetKingTargets() {
        final int nbTests = 10000;

        System.out.println("Benchmark (" + 64 * nbTests * 4
                + ") : getKingTargets(Square,boolean)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat = BoardFactory.valueOf(t,
                    BoardFactory.State.EMPTY);
            final Square[] lst = Square.values();
            final long debut = System.currentTimeMillis();
            for (int f = lst.length - 1; f >= 0; f--) {
                final Square s = lst[f];
                for (int i = nbTests; i > 0; i--) {
                    etat.getKingTargets(s, true);
                    etat.getKingTargets(s, false);
                    etat.getKingTargets(s, true);
                    etat.getKingTargets(s, false);
                }
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de recherche des cases cibles d'un cavalier.
     */
    private static void benchGetKnightTargets() {
        final int nbTests = 10000;

        System.out.println("Benchmark (" + 64 * nbTests * 4
                + ") : getKnightTargets(Square,boolean)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat = BoardFactory.valueOf(t,
                    BoardFactory.State.EMPTY);
            final Square[] lst = Square.values();
            final long debut = System.currentTimeMillis();
            for (int f = lst.length - 1; f >= 0; f--) {
                final Square s = lst[f];
                for (int i = nbTests; i > 0; i--) {
                    etat.getKnightTargets(s, true);
                    etat.getKnightTargets(s, false);
                    etat.getKnightTargets(s, true);
                    etat.getKnightTargets(s, false);
                }
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de recherche des cases cibles d'un pion.
     */
    private static void benchGetPawnTargets() {
        final int nbTests = 10000;

        System.out.println("Benchmark (" + 64 * nbTests * 4
                + ") : getPawnTargets(Square,boolean)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat = BoardFactory.valueOf(t,
                    BoardFactory.State.EMPTY);
            final Square[] lst = Square.values();
            final long debut = System.currentTimeMillis();
            for (int f = lst.length - 1; f >= 0; f--) {
                final Square s = lst[f];
                for (int i = nbTests; i > 0; i--) {
                    etat.getPawnTargets(s, true);
                    etat.getPawnTargets(s, false);
                    etat.getPawnTargets(s, true);
                    etat.getPawnTargets(s, false);
                }
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de recherche des cases cibles d'une dame.
     */
    private static void benchGetQueenTargets() {
        final int nbTests = 10000;

        System.out.println("Benchmark (" + 64 * nbTests * 4
                + ") : getQueenTargets(Square,boolean)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat = BoardFactory.valueOf(t,
                    BoardFactory.State.EMPTY);
            final Square[] lst = Square.values();
            final long debut = System.currentTimeMillis();
            for (int f = lst.length - 1; f >= 0; f--) {
                final Square s = lst[f];
                for (int i = nbTests; i > 0; i--) {
                    etat.getQueenTargets(s, true);
                    etat.getQueenTargets(s, false);
                    etat.getQueenTargets(s, true);
                    etat.getQueenTargets(s, false);
                }
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de recherche des cases cibles d'une tour.
     */
    private static void benchGetRookTargets() {
        final int nbTests = 10000;

        System.out.println("Benchmark (" + 64 * nbTests * 4
                + ") : getRookTargets(Square,boolean)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat = BoardFactory.valueOf(t,
                    BoardFactory.State.EMPTY);
            final Square[] lst = Square.values();
            final long debut = System.currentTimeMillis();
            for (int f = lst.length - 1; f >= 0; f--) {
                final Square s = lst[f];
                for (int i = nbTests; i > 0; i--) {
                    etat.getRookTargets(s, true);
                    etat.getRookTargets(s, false);
                    etat.getRookTargets(s, true);
                    etat.getRookTargets(s, false);
                }
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de recherche des mouvements pour une couleur.
     */
    private static void benchGetValidMoves() {
        final int nbTests = 2500;

        System.out.println("Benchmark (" + nbTests * 8
                + ") : getValidMoves(boolean)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat = BoardFactory.valueOf(t,
                    BoardFactory.State.STARTING);
            final long debut = System.currentTimeMillis();
            for (int i = nbTests; i > 0; i--) {
                etat.getValidMoves(true);
                etat.getValidMoves(false);
                etat.getValidMoves(true);
                etat.getValidMoves(false);
                etat.getValidMoves(true);
                etat.getValidMoves(false);
                etat.getValidMoves(true);
                etat.getValidMoves(false);
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de recherche des cases cibles valides d'une position.
     */
    private static void benchGetValidTargets() {
        final int nbTests = 2500;

        System.out.println("Benchmark (" + 64 * nbTests * 4
                + ") : getValidTargets(Square)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat = BoardFactory.valueOf(t,
                    BoardFactory.State.STARTING);
            final Square[] lst = Square.values();
            final long debut = System.currentTimeMillis();
            for (int f = lst.length - 1; f >= 0; f--) {
                final Square s = lst[f];
                for (int i = nbTests; i > 0; i--) {
                    etat.getValidTargets(s);
                    etat.getValidTargets(s);
                    etat.getValidTargets(s);
                    etat.getValidTargets(s);
                }
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Teste la vitesse de détection des pièces attaquées.
     */
    private static void benchIsAttacked() {
        final int nbTests = 25000;

        System.out.println("Benchmark (" + 64 * nbTests * 4
                + ") : isAttacked(Square,boolean)");
        for (final BoardFactory.Type t : BoardFactory.Type.values()) {
            if (t == FASTEST) {
                continue;
            }
            final MoveGenerator etat1 = BoardFactory.valueOf(t,
                    BoardFactory.State.EMPTY);
            MoveGenerator etat2 = etat1;
            try {
                etat2 = etat1
                        .derive(toBoard("r2q1rk1/pp2bp1p/2p2n2/3p4/1nPPp3/1B2PpP1/PB3P1P/RN3QK1 w - - 0 16"));
            } catch (final FENException e) {
                e.printStackTrace();
            }
            final Square[] lst = Square.values();
            final long debut = System.currentTimeMillis();
            for (int f = lst.length - 1; f >= 0; f--) {
                final Square s = lst[f];
                for (int i = nbTests; i > 0; i--) {
                    etat1.isAttacked(s, true);
                    etat2.isAttacked(s, true);
                    etat1.isAttacked(s, false);
                    etat2.isAttacked(s, false);
                }
            }
            final long fin = System.currentTimeMillis();
            System.out.println("  " + etat1.getClass().getSimpleName() + " = "
                    + (fin - debut) + "ms");
        }
    }

    /**
     * Lance les différents tests de performance.
     *
     * @param pArgs Arguments de la ligne de commande : ignorés, aucun argument
     *              attendu.
     */
    public static void main(final String[] pArgs) {

        benchDerive();
        benchGetAllTargets();
        benchGetBishopTargets();
        benchGetKingSquare();
        benchGetKingTargets();
        benchGetKnightTargets();
        benchGetPawnTargets();
        benchGetQueenTargets();
        benchGetRookTargets();
        benchGetValidMoves();
        benchGetValidTargets();
        benchIsAttacked();
    }
}
