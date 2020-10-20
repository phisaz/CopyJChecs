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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import fr.free.jchecs.core.Game;
import fr.free.jchecs.core.Player;

import static fr.free.jchecs.swg.ResourceUtils.getI18NString;
import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Composant affichant les caractéristiques des joueurs.
 *
 * @author David Cotton
 */
final class PlayersPanel implements ActionListener, PropertyChangeListener {
    /**
     * Icône du joueur noir.
     */
    private static final Icon BLACK_ICON = getImageIcon("black22.png");

    /**
     * Icône du joueur blanc.
     */
    private static final Icon WHITE_ICON = getImageIcon("white22.png");

    /**
     * Marge par défaut.
     */
    private static final Insets DEFAULT_INSETS = new Insets(0, 0, 0, 0);

    /**
     * Interface utilisateur liée au panel.
     */
    private final UI _ui;

    /**
     * Bouton affichant le nom du joueur noir.
     */
    private final JButton _blackName;

    /**
     * Composant graphique externe.
     */
    private final JComponent _component;

    /**
     * Bouton affichant le nom du joueur blanc.
     */
    private final JButton _whiteName;

    /**
     * Construit un nouveau composant d'affichage des joueurs.
     *
     * @param pUI Interface graphique liée.
     */
    PlayersPanel(final UI pUI) {
        _ui = pUI;

        final JPanel fond = new JPanel(new GridLayout(1, 2));

        final Game partie = _ui.getGame();
        final String toolTip = getI18NString("panel.players.tooltip");
        Player joueur = partie.getPlayer(true);
        _whiteName = new JButton(joueur.getName(), WHITE_ICON);
        _whiteName.setMargin(DEFAULT_INSETS);
        _whiteName.setHorizontalAlignment(SwingConstants.LEFT);
        _whiteName.setToolTipText(toolTip);
        _whiteName.addActionListener(this);
        joueur.addPropertyChangeListener("name", this);
        fond.add(_whiteName);

        joueur = partie.getPlayer(false);
        _blackName = new JButton(joueur.getName(), BLACK_ICON);
        _blackName.setHorizontalTextPosition(SwingConstants.LEFT);
        _blackName.setMargin(DEFAULT_INSETS);
        _blackName.setHorizontalAlignment(SwingConstants.RIGHT);
        _blackName.setToolTipText(toolTip);
        _blackName.addActionListener(this);
        joueur.addPropertyChangeListener("name", this);
        fond.add(_blackName);

        _component = fond;
    }

    /**
     * Réagit au déclenchement d'un des boutons d'édition.
     *
     * @param pEvent Evènement signalant le déclenchement.
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent pEvent) {
        final Object src = pEvent.getSource();
        if (src instanceof JComponent) {
            PlayerDialog.showAboutDialog((JComponent) src, _ui.getGame()
                    .getPlayer(src == _whiteName));
        }
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
     * Réagit aux modifications des définitions des joueurs.
     *
     * @param pEvent Evènement signalant la modification.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent pEvent) {
        final Object src = pEvent.getSource();
        if (src instanceof Player) {
            final Player joueur = (Player) src;
            if (joueur.isWhite()) {
                _whiteName.setText(joueur.getName());
            } else {
                _blackName.setText(joueur.getName());
            }
        }
    }
}
