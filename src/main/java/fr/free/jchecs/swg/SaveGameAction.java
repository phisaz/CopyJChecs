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
import javax.swing.SwingUtilities;

import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Action de l'option "Enregistrer la partie".
 * 
 * @author David Cotton
 */
final class SaveGameAction extends AbstractUIAction {
	/** Identifiant de la classe pour la sérialisation. */
	private static final long serialVersionUID = -2549841549251758935L;

	/** Petite icône. */
	private static final Icon ICON16 = getImageIcon("savePGN16.png");

	/** Icône moyenne. */
	private static final Icon ICON22 = getImageIcon("savePGN22.png");

	/**
	 * Crée une nouvelle instance de l'action.
	 * 
	 * @param pUI
	 *            Référence du composant maitre de l'interface.
	 */
	SaveGameAction(final UI pUI) {
		super("saveGame", pUI);

		putValue(Action.SMALL_ICON, ICON16);
		putValue(Action.LARGE_ICON_KEY, ICON22);
	}

	/**
	 * Réaction face au déclenchement de l'action.
	 * 
	 * @param pEvent
	 *            Evènement signalant le déclenchement.
	 */
	@Override
	public void actionPerformed(final ActionEvent pEvent) {
		SwingUtilities.invokeLater(new GameSaver(getUI()));
	}
}
