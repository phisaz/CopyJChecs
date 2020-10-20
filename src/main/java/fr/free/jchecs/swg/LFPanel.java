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

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static fr.free.jchecs.swg.ResourceUtils.getI18NString;
import static fr.free.jchecs.swg.SwingUI.BOARD_LF_PROPERTY;
import static fr.free.jchecs.swg.SwingUI.PIECES_LF_PROPERTY;

/**
 * Composant permettant de sélectionner l'apparence des pièces et du plateau.
 *
 * @author David Cotton
 */
final class LFPanel implements ChangeListener {
    /**
     * Interface utilisateur liée au panel.
     */
    private final UI _ui;

    /**
     * Composant graphique externe.
     */
    private final JComponent _component;

    /**
     * Composant de sélection de l'apparence du plateau.
     */
    private final JSpinner _boardSpinner;

    /**
     * Composant de sélection de l'apparence des pièces.
     */
    private final JSpinner _piecesSpinner;

    /**
     * Instancie un composant de sélection de l'apparence.
     *
     * @param pUI Interface graphique liée au panel.
     */
    LFPanel(final UI pUI) {
        _ui = pUI;
        _component = new JPanel(new GridLayout(1, -1));
        _component.add(new JLabel(getI18NString("panel.lf.board")));
        _boardSpinner = new JSpinner(new SpinnerNumberModel(
                Integer.parseInt(_ui.getProperty(BOARD_LF_PROPERTY)), 0, 5, 1));
        _component.add(_boardSpinner);
        _component.add(new JLabel(getI18NString("panel.lf.pieces")));
        _piecesSpinner = new JSpinner(new SpinnerNumberModel(
                Integer.parseInt(_ui.getProperty(PIECES_LF_PROPERTY)), 0, 5, 1));
        _component.add(_piecesSpinner);

        _boardSpinner.addChangeListener(this);
        _piecesSpinner.addChangeListener(this);
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
     * Réagit aux modifications de la sélection.
     *
     * @param pEvent Evènement signalant la modification.
     * @see ChangeListener#stateChanged(ChangeEvent)
     */
    @Override
    public void stateChanged(final ChangeEvent pEvent) {
        final Object src = pEvent.getSource();
        if (src == _boardSpinner) {
            _ui.setProperty(BOARD_LF_PROPERTY, _boardSpinner.getModel()
                    .getValue().toString());
        } else if (src == _piecesSpinner) {
            _ui.setProperty(PIECES_LF_PROPERTY, _piecesSpinner.getModel()
                    .getValue().toString());
        }
    }
}
