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

import java.io.File;

import javax.swing.JFrame;

import fr.free.jchecs.core.Game;

/**
 * Interface présentée par le composant maitre de l'interface graphique.
 * 
 * @author David Cotton
 */
interface UI {
	/**
	 * Renvoi la valeur d'une propriété booléenne.
	 * 
	 * @param pCle
	 *            Cle identifiant la propriété.
	 * @return Valeur de la propriété.
	 */
	boolean getBooleanProperty(final String pCle);

	/**
	 * Renvoi une référence vers la partie en cours.
	 * 
	 * @return Partie en cours.
	 */
	Game getGame();

	/**
	 * Renvoi une référence vers l'éventuel fichier PGN contenant la partie.
	 * 
	 * @return Fichier PGN de la partie (peut être à null).
	 */
	File getGameFile();

	/**
	 * Renvoi une référence vers la fenêtre principale.
	 * 
	 * @return Fenêtre principale.
	 */
	JFrame getMainFrame();

	/**
	 * Renvoi une référence vers l'éventuel fichier FEN contenant la position.
	 * 
	 * @return Fichier FEN de position (peut être à null).
	 */
	File getPositionFile();

	/**
	 * Renvoi la valeur d'une propriété.
	 * 
	 * @param pCle
	 *            Cle identifiant la propriété.
	 * @return Valeur de la propriété.
	 */
	String getProperty(final String pCle);

	/**
	 * Alimente la valeur d'une propriété booléenne.
	 * 
	 * @param pCle
	 *            Cle identifiant la propriété.
	 * @param pValeur
	 *            Valeur de la propriété.
	 */
	void setBooleanProperty(final String pCle, final boolean pValeur);

	/**
	 * Stocke la référence de l'éventuel fichier PGN de partie.
	 * 
	 * @param pFichier
	 *            Fichier PGN de partie (peut être à null).
	 */
	void setGameFile(final File pFichier);

	/**
	 * Stocke la référence de l'éventuel fichier FEN de position.
	 * 
	 * @param pFichier
	 *            Fichier FEN de position (peut être à null).
	 */
	void setPositionFile(final File pFichier);

	/**
	 * Alimente la valeur d'une propriété.
	 * 
	 * @param pCle
	 *            Cle identifiant la propriété.
	 * @param pValeur
	 *            Valeur de la propriété.
	 */
	void setProperty(final String pCle, final String pValeur);

	/**
	 * Lance l'interface graphique.
	 */
	void start();

	/**
	 * Arrète l'interface utilisateur.
	 */
	void stop();
}
