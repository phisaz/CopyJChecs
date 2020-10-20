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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Afficheur de lignes paires / impaires dans les tables.
 * 
 * @author David Cotton
 */
final class EvenOddRowsRenderer extends DefaultTableCellRenderer {
	/** Identifiant de la classe pour la sérialisation. */
	private static final long serialVersionUID = -9197486441175297316L;

	/** Couleur de fond des lignes paires. */
	private static final Color EVEN_BACKGROUND_COLOR = new Color(235, 245, 255);

	/** Couleur de fond des lignes impaires. */
	private static final Color ODD_BACKGROUND_COLOR = Color.WHITE;

	/**
	 * Constructeur par défaut.
	 */
	EvenOddRowsRenderer() {
		// Rien de spécifique...
	}

	/**
	 * Surcharge pour diférencier les lignes paires/impaires.
	 * 
	 * @param pTable
	 *            Table en cours de dessin.
	 * @param pObjet
	 *            Objet à afficher.
	 * @param pSelection
	 *            Drapeau indiquant si la cellule est sélectionnée.
	 * @param pFocus
	 *            Drapeau indiquant si la cellule a le focus.
	 * @param pLigne
	 *            Numéro de ligne de la cellule.
	 * @param pColonne
	 *            Numéro de colonne de la cellule.
	 * @return Composant à afficher.
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable pTable,
			final Object pObjet, final boolean pSelection,
			final boolean pFocus, final int pLigne, final int pColonne) {
		final Component res = super.getTableCellRendererComponent(pTable,
				pObjet, pSelection, pFocus, pLigne, pColonne);

		if (!pSelection) {
			if (pLigne % 2 == 0) {
				res.setBackground(ODD_BACKGROUND_COLOR);
			} else {
				res.setBackground(EVEN_BACKGROUND_COLOR);
			}
		}

		return res;
	}
}
