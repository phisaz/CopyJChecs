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
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * Composant d'affichage d'une liste de tâches, style XP.
 *
 * @author David Cotton
 */
final class TaskPanel {
    /**
     * Couleur du fond du composant.
     */
    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;

    /**
     * Composant graphique externe.
     */
    private final JComponent _component;

    /**
     * Dernier des conteneurs imbriqués.
     */
    private JComponent _lastContainer;

    /**
     * Instancie un nouveau composant d'affichage de tâches.
     */
    TaskPanel() {
        _lastContainer = new JPanel(new BorderLayout(0, 8));
        _lastContainer.setBackground(BACKGROUND_COLOR);
        _lastContainer.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        _component = new JScrollPane(_lastContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    /**
     * Ajoute une tâche à la suite de la liste.
     *
     * @param pTache Tâche à ajouter.
     */
    void add(final TaskItem pTache) {
        _lastContainer.add(pTache.getComponent(), BorderLayout.NORTH);

        final Dimension taillePanel = _component.getSize();
        final int largeurComposant = pTache.getBestWidth() + 24;
        if (largeurComposant > taillePanel.width) {
            taillePanel.width = largeurComposant;
            _component.setSize(taillePanel);
            _component.setPreferredSize(taillePanel);
        }

        final JPanel suivant = new JPanel(new BorderLayout(0, 8));
        suivant.setBackground(BACKGROUND_COLOR);
        _lastContainer.add(suivant, BorderLayout.CENTER);

        _lastContainer = suivant;
    }

    /**
     * Renvoi le composant graphique affichable.
     *
     * @return Composant graphique.
     */
    JComponent getComponent() {
        return _component;
    }
}
