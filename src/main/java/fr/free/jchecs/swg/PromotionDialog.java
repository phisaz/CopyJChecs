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
package fr.free.jchecs.swg;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import fr.free.jchecs.core.Piece;

import static fr.free.jchecs.swg.ResourceUtils.getI18NString;

/**
 * Boite de dialogue permettant de choisir la pièce cible en cas de promotion
 * d'un pion.
 *
 * @author David Cotton
 */
final class PromotionDialog {
    /**
     * Classe utilitaire : ne pas instancier.
     */
    private PromotionDialog() {
        // Rien de spécifique...
    }

    /**
     * Affiche la boite de dialogue.
     *
     * @param pParent Composant parent de la boite de dialogue (peut être à null).
     * @param pPiece  Pion à promouvoir.
     * @param pRendu  Objet chargé du rendu des pièces.
     * @return Pièce après promotion.
     */
    static Piece showPromotionDialog(final Component pParent,
                                     final Piece pPiece, final PieceUI pRendu) {
        Component parent = pParent;
        while ((parent != null) && !(parent instanceof Frame)) {
            parent = parent.getParent();
        }
        final JDialog dlg = new JDialog((Frame) parent, true);
        dlg.setTitle(getI18NString("dialog.promotion.title"));
        final JPanel fond = new JPanel();
        final JButton queenBtn;
        final JButton rookBtn;
        final JButton bishopBtn;
        final JButton knightBtn;
        if (pPiece.isWhite()) {
            queenBtn = new JButton(new ImageIcon(
                    pRendu.getImage(Piece.WHITE_QUEEN)));
            rookBtn = new JButton(new ImageIcon(
                    pRendu.getImage(Piece.WHITE_ROOK)));
            bishopBtn = new JButton(new ImageIcon(
                    pRendu.getImage(Piece.WHITE_BISHOP)));
            knightBtn = new JButton(new ImageIcon(
                    pRendu.getImage(Piece.WHITE_KNIGHT)));
        } else {
            queenBtn = new JButton(new ImageIcon(
                    pRendu.getImage(Piece.BLACK_QUEEN)));
            rookBtn = new JButton(new ImageIcon(
                    pRendu.getImage(Piece.BLACK_ROOK)));
            bishopBtn = new JButton(new ImageIcon(
                    pRendu.getImage(Piece.BLACK_BISHOP)));
            knightBtn = new JButton(new ImageIcon(
                    pRendu.getImage(Piece.BLACK_KNIGHT)));
        }
        final ActionEvent choix = new ActionEvent(queenBtn, 0, "");
        final BoxLayout bl = new BoxLayout(fond, BoxLayout.LINE_AXIS);
        fond.setLayout(bl);
        final ActionListener listener = pEvent -> {
            choix.setSource(pEvent.getSource());
            dlg.dispose();
        };
        queenBtn.addActionListener(listener);
        rookBtn.addActionListener(listener);
        bishopBtn.addActionListener(listener);
        knightBtn.addActionListener(listener);
        fond.add(queenBtn);
        fond.add(rookBtn);
        fond.add(bishopBtn);
        fond.add(knightBtn);
        dlg.add(fond);
        dlg.pack();
        dlg.setLocationRelativeTo(parent);
        dlg.setResizable(false);
        dlg.setVisible(true);

        final Piece res;

        if (choix.getSource() == queenBtn) {
            res = pPiece.isWhite() ? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
        } else if (choix.getSource() == rookBtn) {
            res = pPiece.isWhite() ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
        } else if (choix.getSource() == bishopBtn) {
            res = pPiece.isWhite() ? Piece.WHITE_BISHOP : Piece.BLACK_BISHOP;
        } else if (choix.getSource() == knightBtn) {
            res = pPiece.isWhite() ? Piece.WHITE_KNIGHT : Piece.BLACK_KNIGHT;
        } else {
            res = pPiece.isWhite() ? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
        }

        return res;
    }
}
