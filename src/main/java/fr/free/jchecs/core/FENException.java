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
package fr.free.jchecs.core;

/**
 * Signale une erreur dans une chaîne FEN.
 * <p>
 * Les instances de cette classe sont des <b>immuables</b> : classe sûre
 * vis-à-vis des threads.
 * </p>
 * 
 * @author David Cotton
 */
public final class FENException extends Exception {
	/** Identifiant de la classe pour la sérialisation. */
	private static final long serialVersionUID = -762995174135403435L;

	/**
	 * Instancie une nouvelle exception.
	 * 
	 * @param pMessage
	 *            Message d'erreur lié à l'exception (peut être à null).
	 * @param pErreur
	 *            Erreur à l'origine de l'exception (peut être à null).
	 */
	FENException(final String pMessage, final Throwable pErreur) {
		super(pMessage, pErreur);
	}
}
