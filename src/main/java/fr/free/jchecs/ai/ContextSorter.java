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
package fr.free.jchecs.ai;

import java.io.Serializable;
import java.util.Comparator;

import fr.free.jchecs.core.Move;

/**
 * Interface présentée par les méthodes de tri des mouvements tenant compte du
 * contexte de la recherche.
 * 
 * @author David Cotton
 */
interface ContextSorter extends Comparator<Move>, Serializable {
	/**
	 * Efface le contexte mémorisé.
	 */
	void clear();

	/**
	 * Renvoie l'évaluation d'un mouvement.
	 * 
	 * @param pMouvement
	 *            Mouvement à rechercher.
	 * @return Valeur liée au mouvement.
	 */
	int get(final Move pMouvement);

	/**
	 * Mémorise un mouvement important.
	 * 
	 * @param pMouvement
	 *            Mouvement à mémoriser.
	 */
	void put(final Move pMouvement);
}
