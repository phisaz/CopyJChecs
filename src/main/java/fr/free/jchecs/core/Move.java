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

import java.io.Serializable;

/**
 * Description d'un mouvement d'une pièce.
 * <p>
 * Les instances de cette classe sont <b>immuables</b>, donc sûres vis-à-vis des
 * threads.
 * </p>
 *
 * @author David Cotton
 */
public final class Move implements Serializable {
    /**
     * Identifiant de la classe pour la sérialisation.
     */
    private static final long serialVersionUID = 8372326813848101389L;

    /**
     * Pièce déplacée.
     */
    private final Piece _piece;

    /**
     * Case de départ.
     */
    private final Square _from;

    /**
     * Case d'arrivée.
     */
    private final Square _to;

    /**
     * Eventuelle pièce capturée.
     */
    private final Piece _captured;

    /**
     * Eventuelle promotion.
     */
    private final Piece _promotion;

    /**
     * Buffer stockant l'entier identifiant un mouvement.
     */
    private Integer _id;

    /**
     * Instancie une nouvelle description de mouvement, sans prise.
     *
     * @param pPiece       Pièce à bouger.
     * @param pOrigine     Case à l'origine du mouvement.
     * @param pDestination Case finale du mouvement.
     */
    public Move(final Piece pPiece, final Square pOrigine,
                final Square pDestination) {
        _piece = pPiece;
        _from = pOrigine;
        _to = pDestination;
        _captured = null;
        _promotion = null;
    }

    /**
     * Instancie une nouvelle description de mouvement.
     *
     * @param pPiece       Pièce à bouger.
     * @param pOrigine     Case à l'origine du mouvement.
     * @param pDestination Case finale du mouvement.
     * @param pPrise       Pièce prise (ou null si aucune).
     */
    public Move(final Piece pPiece, final Square pOrigine,
                final Square pDestination, final Piece pPrise) {
        _piece = pPiece;
        _from = pOrigine;
        _to = pDestination;
        _captured = pPrise;
        _promotion = null;
    }

    /**
     * Instancie une nouvelle description de mouvement.
     *
     * @param pPiece       Pièce à bouger.
     * @param pOrigine     Case à l'origine du mouvement.
     * @param pDestination Case finale du mouvement.
     * @param pPrise       Pièce prise (ou null si aucune).
     * @param pPromotion   Promotion (ou null si aucune).
     */
    public Move(final Piece pPiece, final Square pOrigine,
                final Square pDestination, final Piece pPrise,
                final Piece pPromotion) {
        _piece = pPiece;
        _from = pOrigine;
        _to = pDestination;
        _captured = pPrise;
        _promotion = pPromotion;
    }

    /**
     * Teste l'égalité entre deux mouvements.
     *
     * @param pObjet Objet avec lequel comparer le mouvement (peut être à null).
     * @return "true" si l'on a deux mouvements identiques.
     */
    @Override
    public boolean equals(final Object pObjet) {
        if (this == pObjet) {
            return true;
        }

        if (pObjet instanceof Move) {
            final Move m = (Move) pObjet;
            return (_from == m._from) && (_piece == m._piece) && (_to == m._to)
                    && (_captured == m._captured)
                    && (_promotion == m._promotion);
        }

        return false;
    }

    /**
     * Renvoi l'éventuelle pièce capturée.
     *
     * @return Pièce capturée (ou null si aucune).
     */
    public Piece getCaptured() {
        return _captured;
    }

    /**
     * Renvoi la case de départ du mouvement.
     *
     * @return Case de départ.
     */
    public Square getFrom() {
        return _from;
    }

    /**
     * Renvoi la pièce déplacée.
     *
     * @return Pièce déplacée.
     */
    public Piece getPiece() {
        return _piece;
    }

    /**
     * Renvoi l'éventuelle promotion.
     *
     * @return Promotion (ou null si aucune).
     */
    public Piece getPromotion() {
        return _promotion;
    }

    /**
     * Renvoi la case d'arrivée du mouvement.
     *
     * @return Case d'arrivée.
     */
    public Square getTo() {
        return _to;
    }

    /**
     * Renvoi le code de hachage du mouvement.
     *
     * @return Code de hachage.
     */
    @Override
    public int hashCode() {
        return toId();
    }

    /**
     * Renvoi l'entier identifiant un mouvement.
     *
     * @return Entier identifiant un mouvement.
     */
    public int toId() {
        if (_id == null) {
            int id = (_piece.ordinal() << 20) + (_from.getIndex() << 14)
                    + (_to.getIndex() << 8);
            if (_captured != null) {
                id += (_captured.ordinal() + 1) << 4;
            }
            if (_promotion != null) {
                id += _promotion.ordinal() + 1;
            }
            _id = id;
        }

        return _id;
    }

    /**
     * Renvoi une chaine représentant le mouvement.
     *
     * @return Chaine décrivant le mouvement.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[piece=" + _piece + ",from=" + _from +
                ",to=" + _to + ",captured=" + _captured + ",promotion=" + _promotion + ']';
    }

    /**
     * Renvoi une instance de mouvement correspondant à l'entier identifiant
     * reçu.
     *
     * @param pId Entier identifiant un mouvement.
     * @return Instance du mouvement correspondant.
     */
    public static Move valueOf(final int pId) {
        final Piece pce = Piece.values()[(pId >> 20) & 0xF];
        final Square src = Square.valueOf((pId >> 14) & 0x3F);
        final Square dst = Square.valueOf((pId >> 8) & 0x3F);
        final int idCpt = (pId >> 4) & 0xF;
        final Piece cpt;
        if (idCpt <= 0) {
            cpt = null;
        } else {
            cpt = Piece.values()[idCpt - 1];
        }
        final int idProm = pId & 0xF;
        final Piece prom;
        if (idProm <= 0) {
            prom = null;
        } else {
            prom = Piece.values()[idProm - 1];
        }

        return new Move(pce, src, dst, cpt, prom);
    }
}
