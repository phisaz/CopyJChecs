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

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;

import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Action de l'option "Aide".
 *
 * @author David Cotton
 */
final class HelpAction extends AbstractUIAction {
    /**
     * Identifiant de la classe pour la sérialisation.
     */
    private static final long serialVersionUID = 5669035224626159581L;

    /**
     * Petite icône.
     */
    private static final Icon ICON16 = getImageIcon("help16.png");

    /**
     * Icône moyenne.
     */
    private static final Icon ICON22 = getImageIcon("help22.png");

    /**
     * Crée une nouvelle instance de l'action.
     *
     * @param pUI Référence du composant maitre de l'interface.
     */
    HelpAction(final UI pUI) {
        super("help", pUI);

        putValue(Action.SMALL_ICON, ICON16);
        putValue(Action.LARGE_ICON_KEY, ICON22);
    }

    /**
     * Réaction face au déclenchement de l'action.
     *
     * @param pEvent Evènement signalant le déclenchement.
     */
    @Override
    public void actionPerformed(final ActionEvent pEvent) {
        // TODO : implémenter l'affichage de l'aide.
    }
}
