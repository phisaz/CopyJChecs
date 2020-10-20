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

import java.util.EventObject;

import fr.free.jchecs.core.Move;

/**
 * Evènement signalant le mouvement d'une pièce.
 * 
 * @author David Cotton
 */
final class MoveEvent extends EventObject {
	/** Identifiant de la classe pour la sérialisation. */
	private static final long serialVersionUID = -558192316749148649L;

	/** Mouvement lié à l'évènement. */
	private final Move _move;

	/**
	 * Instancie un nouvel évènement décrivant un mouvement.
	 * 
	 * @param pSource
	 *            Objet émetteur de l'évènement (peut être à null).
	 * @param pMouvement
	 *            Mouvement lié à l'évènement.
	 */
	MoveEvent(final Object pSource, final Move pMouvement) {
		super(pSource);

		_move = pMouvement;
	}

	/**
	 * Renvoi le mouvement lié à l'évènement.
	 * 
	 * @return Mouvement lié.
	 */
	Move getMove() {
		return _move;
	}
}
