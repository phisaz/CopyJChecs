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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Formatter;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.free.jchecs.ai.Engine;
import fr.free.jchecs.core.Game;
import fr.free.jchecs.core.MoveGenerator;

import static fr.free.jchecs.swg.ResourceUtils.getI18NString;

/**
 * Composant affichant la valeur de l'évaluation de la position courante d'une
 * partie.
 *
 * @author David Cotton
 */
final class EvalPanel implements PropertyChangeListener {
    /**
     * Composant graphique externe.
     */
    private final JComponent _component;

    /**
     * Partie liée au composant.
     */
    private final Game _game;

    /**
     * Label affichant la valeur de l'évaluation.
     */
    private final JLabel _eval;

    /**
     * Construit un nouveau composant d'affichage de l'évaluation de la
     * position.
     *
     * @param pPartie Définition de partie liée au composant.
     */
    EvalPanel(final Game pPartie) {
        _game = pPartie;

        final JPanel fond = new JPanel();
        fond.setAlignmentX(Component.CENTER_ALIGNMENT);
        _eval = new JLabel("0.00");
        fond.add(_eval);

        _component = fond;

        _game.addPropertyChangeListener("position", this);
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
     * Réagit au changement dans la définition de la partie.
     *
     * @param pEvt Evènement signalant le changement.
     */
    @Override
    public void propertyChange(final PropertyChangeEvent pEvt) {
        final MoveGenerator disposition = _game.getBoard();
        boolean traitReference = disposition.isWhiteActive();
        final boolean traitAdversaire = !traitReference;
        Engine moteur = _game.getPlayer(traitAdversaire).getEngine();
        if (moteur == null) {
            moteur = _game.getPlayer(!traitAdversaire).getEngine();
            traitReference = traitAdversaire;
        }
        if (moteur == null) {
            _eval.setText("?.??");
        } else {
            final int note = moteur.getHeuristic().evaluate(disposition,
                    traitReference);
            final StringBuilder sb = new StringBuilder();
            final Formatter formateur = new Formatter(sb);
            final String pointDeVue;
            if (traitReference) {
                pointDeVue = getI18NString("panel.eval.white");
            } else {
                pointDeVue = getI18NString("panel.eval.black");
            }
            formateur.format("%+5.2f / %s", note / 100.0F, pointDeVue);
            _eval.setText(sb.toString());
        }
    }
}
