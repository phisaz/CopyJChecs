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

import static fr.free.jchecs.core.Constants.FILE_COUNT;
import static fr.free.jchecs.core.Constants.RANK_COUNT;

/**
 * Version modifiable d'une description d'état du jeu.
 * <p>
 * Attention : cette classe a pour unique but de faciliter l'initialisation
 * d'une première instance immuable de l'état. Elle ne dispose volontairement
 * pas d'une implémentation pour les méthodes indispensables aux moteurs d'IA.
 * </p>
 *
 * @author David Cotton
 */
final class MutableBoard extends AbstractBoard {
    /**
     * Identifiant de la classe pour la sérialisation.
     */
    private static final long serialVersionUID = -3845129626288554731L;

    /**
     * Description du plateau.
     */
    private final Piece[][] _pieces = new Piece[FILE_COUNT][RANK_COUNT];

    /**
     * Crée une nouvelle instance de description modifiable d'état de jeu.
     */
    MutableBoard() {
        // Rien de spécifique...
    }

    /**
     * Renvoi l'éventuelle pièce présente sur la case indiquée.
     *
     * @param pCase Case à tester.
     * @return Pièce présente sur la case (ou null si aucune).
     */
    @Override
    public Piece getPieceAt(final Square pCase) {
        return getPieceAt(pCase.getFile(), pCase.getRank());
    }

    /**
     * Renvoi l'éventuelle pièce présente sur la case dont les coordonnées sont
     * indiquées.
     *
     * @param pColonne Colonne de la case à tester (de 0 à 7).
     * @param pLigne   Ligne de la case à tester (de 0 à 7).
     * @return Pièce présente sur la case (ou null).
     */
    @Override
    public Piece getPieceAt(final int pColonne, final int pLigne) {
        return _pieces[pColonne][pLigne];
    }

    /**
     * Alimente une case avec une (éventuelle) pièce.
     *
     * @param pPiece Pièce à mettre dans la case (ou null si aucune).
     * @param pCase  Cellule cible.
     */
    void setPieceAt(final Piece pPiece, final Square pCase) {
        _pieces[pCase.getFile()][pCase.getRank()] = pPiece;
    }

    /**
     * Renvoie le nombre total des pièces présentes sur le plateau.
     *
     * @return Nombre total de pièces.
     */
    @Override
    public int getPiecesCount() {
        int res = 0;

        for (int x = FILE_COUNT; --x >= 0; /* Pré-décrémenté */) {
            for (int y = RANK_COUNT; --y >= 0; /* Pré-décrémenté */) {
                if (_pieces[x][y] != null) {
                    res++;
                }
            }
        }

        return res;
    }
}
