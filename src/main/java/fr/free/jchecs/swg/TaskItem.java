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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import static fr.free.jchecs.swg.ResourceUtils.getI18NString;
import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Composant d'affichage d'une tâche.
 *
 * @author David Cotton
 */
final class TaskItem {
    /**
     * Icone permettant de replier (masquer) la tâche.
     */
    private static final Icon FOLD_ICON = getImageIcon("fold.png");

    /**
     * Icone permettant de déplier (afficher) la tâche.
     */
    private static final Icon UNFOLD_ICON = getImageIcon("unfold.png");

    /**
     * Interface utilisateur liée à la tâche.
     */
    private final UI _ui;

    /**
     * Composant graphique externe.
     */
    private final JComponent _component;

    /**
     * Barre de titre.
     */
    private final JPanel _titleBar;

    /**
     * Titre.
     */
    private final JLabel _title;

    /**
     * Icône d'état plié/déplié.
     */
    private final JLabel _foldingIcon;

    /**
     * Corps de la tâche.
     */
    private final JPanel _body;

    /**
     * Largeur optimale du composant.
     */
    private final int _bestWidth;

    /**
     * Identifiant de la propriété "plié".
     */
    private final String _foldedProperty;

    /**
     * Etat plié/déplié.
     */
    private boolean _folded;

    /**
     * Instancie un nouveau composant de tâche.
     *
     * @param pUI        Interface graphique liée à la tâche.
     * @param pIdTache   Clé identifiant la tâche.
     * @param pComposant Composant spécifique de la tâche.
     * @param pIcone     Icône représentant la tâche (peut être à null)
     */
    TaskItem(final UI pUI, final String pIdTache, final JComponent pComposant,
             final Icon pIcone) {
        _ui = pUI;
        _titleBar = new JPanel(new BorderLayout(8, 0));
        _titleBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        _title = new JLabel(getI18NString(pIdTache + ".title"));
        _title.setFont(UIManager.getFont("InternalFrame.titleFont"));
        _title.setIcon(pIcone);
        _titleBar.add(_title, BorderLayout.WEST);
        _body = new JPanel(new BorderLayout());
        _body.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        _body.add(pComposant, BorderLayout.CENTER);
        _foldingIcon = new JLabel();
        _foldedProperty = pIdTache + ".folded";
        setFolded(_ui.getBooleanProperty(_foldedProperty));
        _titleBar.add(_foldingIcon, BorderLayout.EAST);

        setMouseOver(false);

        _component = new JPanel(new BorderLayout());
        _component.add(_titleBar, BorderLayout.NORTH);
        _component.add(_body, BorderLayout.CENTER);

        Dimension dim = _titleBar.getPreferredSize();
        int largeur = dim.width;
        dim = _body.getPreferredSize();
        if (dim.width > largeur) {
            largeur = dim.width;
        }
        _bestWidth = largeur;

        _titleBar.addMouseListener(new TaskItemListener());
    }

    /**
     * Renvoi la largeur optimale du composant.
     *
     * @return Largeur optimale du composant.
     */
    int getBestWidth() {
        return _bestWidth;
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
     * Renvoi l'état du drapeau plié/déplié.
     *
     * @return Etat du drapeau "plié".
     */
    private boolean isFolded() {
        return _folded;
    }

    /**
     * Modifié l'état plié/déplié.
     *
     * @param pPlie A "true" pour indiquer que la tâche est repliée.
     */
    private void setFolded(final boolean pPlie) {
        _folded = pPlie;

        if (_folded) {
            _foldingIcon.setIcon(UNFOLD_ICON);
            _body.setVisible(false);
        } else {
            _foldingIcon.setIcon(FOLD_ICON);
            _body.setVisible(true);
        }

        _ui.setBooleanProperty(_foldedProperty, _folded);
    }

    /**
     * Modifie l'indicateur de survol du composant par la souris.
     *
     * @param pSurvol Drapeau à "vrai" pour indiquer le survol du composant par la
     *                souris.
     */
    private void setMouseOver(final boolean pSurvol) {
        if (pSurvol) {
            _titleBar.setBackground(UIManager
                    .getColor("InternalFrame.activeTitleBackground"));
            _title.setForeground(UIManager
                    .getColor("InternalFrame.activeTitleForeground"));
        } else {
            _titleBar.setBackground(UIManager
                    .getColor("InternalFrame.inactiveTitleBackground"));
            _title.setForeground(UIManager
                    .getColor("InternalFrame.inactiveTitleForeground"));
        }
    }

    /**
     * Gestionnaire d'évènement lié au composant.
     */
    private final class TaskItemListener extends MouseAdapter {
        /**
         * Instancie un nouveau gestionnaire.
         */
        TaskItemListener() {
            // Rien de spécifique...
        }

        /**
         * Tient compte de l'entrée de la souris sur la barre de titre.
         *
         * @param pEvent Evènement signalant l'entrée de la souris.
         */
        @Override
        public void mouseEntered(final MouseEvent pEvent) {
            setMouseOver(true);
        }

        /**
         * Tient compte de la sortie de la souris de sur la barre de titre.
         *
         * @param pEvent Evènement signalant la sortie de la souris.
         */
        @Override
        public void mouseExited(final MouseEvent pEvent) {
            setMouseOver(false);
        }

        /**
         * Tient compte du déclenchement de la souris sur la barre de titre.
         *
         * @param pEvent Evènement signalant l'appuie sur un bouton.
         */
        @Override
        public void mousePressed(final MouseEvent pEvent) {
            setFolded(!isFolded());
        }
    }
}
