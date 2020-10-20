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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static fr.free.jchecs.swg.ResourceUtils.getI18NString;
import static fr.free.jchecs.swg.SwingUI.CELLS_SIZE_PROPERTY;
import static fr.free.jchecs.swg.SwingUI.ENABLE_SOUNDS_PROPERTY;
import static fr.free.jchecs.swg.SwingUI.FLIP_VIEW_PROPERTY;
import static fr.free.jchecs.swg.SwingUI.HIGHLIGHT_LAST_MOVE_PROPERTY;
import static fr.free.jchecs.swg.SwingUI.HIGHLIGHT_VALIDS_PROPERTY;
import static fr.free.jchecs.swg.SwingUI.SHOW_CAPTURES_PROPERTY;
import static fr.free.jchecs.swg.SwingUI.SHOW_COORDS_PROPERTY;
import static fr.free.jchecs.swg.SwingUI.SHOW_FEN_PROPERTY;
import static fr.free.jchecs.swg.SwingUI.SHOW_MENUBAR_PROPERTY;
import static fr.free.jchecs.swg.SwingUI.SHOW_TOOLBAR_PROPERTY;

/**
 * Composant permettant de modifier les paramètres de configuration.
 *
 * @author David Cotton
 */
final class SettingsPanel implements ChangeListener, ItemListener {
    /**
     * Interface utilisateur liée au panel.
     */
    private final UI _ui;

    /**
     * Composant graphique externe.
     */
    private final JComponent _component;

    /**
     * Liste des cases à cocher.
     */
    private final Map<JCheckBox, String> _checkBoxes;

    /**
     * Curseur permettant de règler la taille du plateau.
     */
    private final JSlider _cellSize;

    /**
     * Instancie un composant de configuration.
     *
     * @param pUI Interface graphique liée au panel.
     */
    SettingsPanel(final UI pUI) {
        _ui = pUI;
        _component = new JPanel(new GridLayout(-1, 1));
        _checkBoxes = new HashMap<>();
        addCheckBox("panel.settings.showMenuBar", SHOW_MENUBAR_PROPERTY);
        addCheckBox("panel.settings.showToolBar", SHOW_TOOLBAR_PROPERTY);
        addCheckBox("panel.settings.showCaptures", SHOW_CAPTURES_PROPERTY);
        addCheckBox("panel.settings.showCoords", SHOW_COORDS_PROPERTY);
        addCheckBox("panel.settings.showFEN", SHOW_FEN_PROPERTY);
        addCheckBox("panel.settings.highlightValids", HIGHLIGHT_VALIDS_PROPERTY);
        addCheckBox("panel.settings.highlightLastMove",
                HIGHLIGHT_LAST_MOVE_PROPERTY);
        addCheckBox("panel.settings.flipView", FLIP_VIEW_PROPERTY);
        addCheckBox("panel.settings.enableSound", ENABLE_SOUNDS_PROPERTY);

        final JPanel modifTaille = new JPanel(new BorderLayout(4, 0));
        modifTaille.add(new JLabel(getI18NString("panel.settings.cellsSize")),
                BorderLayout.WEST);
        _cellSize = new JSlider(1, 5);
        _cellSize.setValue((Integer.parseInt(_ui
                .getProperty(CELLS_SIZE_PROPERTY)) - 40) / 8);
        _cellSize.setMajorTickSpacing(1);
        _cellSize.setSnapToTicks(true);
        _cellSize.setPreferredSize(new Dimension(50, _cellSize
                .getPreferredSize().height));
        modifTaille.add(_cellSize, BorderLayout.CENTER);
        _cellSize.addChangeListener(this);
        _component.add(modifTaille);
    }

    /**
     * Ajoute une case à cocher au composant.
     *
     * @param pIdLabel   Clé identifiant le label de la case à cocher.
     * @param pPropriete Propriété liée à la case à cocher.
     */
    private void addCheckBox(final String pIdLabel, final String pPropriete) {
        final JCheckBox cb = new JCheckBox(getI18NString(pIdLabel),
                _ui.getBooleanProperty(pPropriete));
        _checkBoxes.put(cb, pPropriete);
        _component.add(cb);
        cb.addItemListener(this);
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
     * Réagit aux modifications des cases-à-cocher.
     *
     * @param pEvent Evènement signalant le changement d'état d'une case-à-cocher.
     * @see ItemListener#itemStateChanged(ItemEvent)
     */
    @Override
    public void itemStateChanged(final ItemEvent pEvent) {
        final Object src = pEvent.getSource();
        if (src instanceof JCheckBox) {
            final JCheckBox cb = (JCheckBox) src;
            final String propriete = _checkBoxes.get(cb);
            if (propriete != null) {
                _ui.setBooleanProperty(propriete, cb.isSelected());
            }
        }
    }

    /**
     * Réagit aux modifications du curseur.
     *
     * @param pEvent Evènement signalant la modification.
     * @see ChangeListener#stateChanged(ChangeEvent)
     */
    @Override
    public void stateChanged(final ChangeEvent pEvent) {
        if (pEvent.getSource() == _cellSize) {
            if (!_cellSize.getValueIsAdjusting()) {
                _ui.setProperty(CELLS_SIZE_PROPERTY,
                        Integer.toString(_cellSize.getValue() * 8 + 40));
            }
        }
    }
}
