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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import fr.free.jchecs.core.BoardFactory;
import fr.free.jchecs.core.FENException;
import fr.free.jchecs.core.MoveGenerator;

import static fr.free.jchecs.core.BoardFactory.Type.FASTEST;
import static fr.free.jchecs.core.FENUtils.toBoard;

/**
 * Tests unitaires de la fonction de requètage du Service Web des fins de
 * parties.
 * 
 * @author David Cotton
 */
public final class EndgamesWSRequestTest {
	/**
	 * Pour que JUnit puisse instancier les tests.
	 */
	public EndgamesWSRequestTest() {
		// Rien de spécifique...
	}

	/**
	 * Test du cas d'une position non évaluée.
	 */
	@Test
	public void testEvaluationNotFound() {
		final EndgamesWSRequest wsRequest = new EndgamesWSRequest(
				BoardFactory.valueOf(FASTEST, BoardFactory.State.STARTING));
		wsRequest.start();
		try {
			wsRequest.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		assertNull(wsRequest.getMove());
		assertEquals(wsRequest.getResponse(), "Error: evaluation not found");
	}

	/**
	 * Test du cas d'une position connue.
	 */
	@Test
	public void testKnownPosition() {
		MoveGenerator board = BoardFactory.valueOf(FASTEST,
				BoardFactory.State.STARTING);
		try {
			board = board.derive(toBoard("3k4/8/8/8/8/8/3P4/3K4 w - - 0 1"));
			board = board.derive(toBoard("8/8/8/4k3/8/2K5/3p4/8 b - - 0 1"));
		} catch (final FENException e) {
			fail(e.toString());
		}

		final EndgamesWSRequest wsRequest = new EndgamesWSRequest(board);
		wsRequest.start();
		try {
			wsRequest.join();
		} catch (final InterruptedException e) {
			fail(e.toString());
		}

		assertNotNull(wsRequest.getMove());
		assertFalse(wsRequest.getResponse().startsWith("Error"));
	}
}
