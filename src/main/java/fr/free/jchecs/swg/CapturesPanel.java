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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.free.jchecs.core.Game;
import fr.free.jchecs.core.Move;
import fr.free.jchecs.core.Piece;

import static fr.free.jchecs.core.Piece.BLACK_KING;

/**
 * Composant affichant la liste des pièces capturées pour une couleur.
 *
 * @author David Cotton
 */
final class CapturesPanel implements PropertyChangeListener {
    /**
     * Composant graphique externe.
     */
    private final JComponent _component;

    /**
     * Partie liée au composant.
     */
    private final Game _game;

    /**
     * Objet chargé du rendu des pièces.
     */
    private final PieceUI _pieceUI;

    /**
     * Liste des labels affichant les icônes des pièces prises.
     */
    private final JLabel[] _labels;

    /**
     * A vrai si le panel liste les pièces blanches prises.
     */
    private boolean _color;

    /**
     * Construit un nouveau composant d'affichage de la liste des pièces prises.
     *
     * @param pPartie Définition de partie liée au composant.
     * @param pRendu  Objet chargé du rendu des pièces.
     * @param pTrait  A vrai si l'on veut la liste des pièces blanches prises.
     */
    CapturesPanel(final Game pPartie, final PieceUI pRendu, final boolean pTrait) {
        _game = pPartie;
        _pieceUI = pRendu;
        _color = pTrait;

        _labels = new JLabel[16];
        final JPanel fond = new JPanel(new GridLayout(1, _labels.length)) {
            /** Identifiant de la classe pour la sérialisation. */
            private static final long serialVersionUID = 6954553000735433951L;

            /**
             * Surcharge pour adapter la taille minimale du composant en
             * fonction de la taille des pièces.
             *
             * @return Taille préférée du composant.
             */
            @Override
            public Dimension getPreferredSize() {
                final Image img = pRendu.getSmallImage(BLACK_KING);
                return new Dimension(img.getWidth(null), img.getHeight(null));
            }
        };
        for (int i = 0; i < _labels.length; i++) {
            _labels[i] = new JLabel();
            fond.add(_labels[i]);
        }
        fond.setBorder(BorderFactory.createLoweredBevelBorder());

        _component = fond;

        initCapturesPaint();

        pPartie.addPropertyChangeListener("position", this);
    }

    /**
     * Renvoi le composant graphique affichable.
     *
     * @return Composant graphique.
     */
    JComponent getComponent() {
        return _component;
    }

    /**
     * Initialise l'affichage de la liste de pièces.
     */
    void initCapturesPaint() {
        int i = 0;
        for (final Move m : _game.getMovesToCurrent()) {
            final Piece prise = m.getCaptured();
            if ((prise != null) && (prise.isWhite() == _color)) {
                _labels[i]
                        .setIcon(new ImageIcon(_pieceUI.getSmallImage(prise)));
                _labels[i].setVisible(true);
                i++;
            }
        }
        while (i < _labels.length) {
            _labels[i].setVisible(false);
            i++;
        }
    }

    /**
     * Réagit au changement dans la définition de la partie.
     *
     * @param pEvt Evènement signalant le changement.
     */
    @Override
    public void propertyChange(final PropertyChangeEvent pEvt) {
        initCapturesPaint();
    }

    /**
     * Indique si le panel doit afficher les pièces prises blanches.
     *
     * @param pCouleur A vrai pour afficher les pièces blanches prises.
     */
    void setWhite(final boolean pCouleur) {
        _color = pCouleur;

        initCapturesPaint();
    }
}
