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

/**
 * Lance l'interface Swing du jeu d'échecs.
 *
 * @author David Cotton
 */
final class Start {
    /**
     * Classe utilitaire : ne pas instancier !
     */
    private Start() {
        // Rien de spécifique...
    }

    /**
     * Méthode de démarrage.
     *
     * @param pArgs Arguments de la ligne de commande : ignorés, aucun argument
     *              attendu.
     */
    public static void main(final String[] pArgs) {
        final ExceptionHandler gestionErreur = new ExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(gestionErreur);

        final UI ihm = new SwingUI();
        gestionErreur.setUI(ihm);
        ihm.start();
    }
}
