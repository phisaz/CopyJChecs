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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.*;

/**
 * Composant graphique dédié à l'impression d'un plateau de jeu.
 *
 * @author David Cotton
 */
final class PrintableBoard extends JPanel implements Printable {
    /**
     * Identifiant de la classe pour la sérialisation.
     */
    private static final long serialVersionUID = -5113657285135508189L;

    /**
     * Instancie un nouveau composant d'impression d'un plateau.
     */
    PrintableBoard() {
        // Rien de spécifique...
    }

    /**
     * Dessine le composant.
     *
     * @param pGraph Contexte graphique à utiliser.
     */
    private void drawBoard(final Graphics2D pGraph) {
        // TODO: à implémenter...
        pGraph.getClass();
    }

    /**
     * Dessine le composant, pour prévisualisation.
     *
     * @param pGraph Contexte graphique de dessin du composant.
     * @see JComponent#paintComponent(Graphics)
     */
    @Override
    public void paintComponent(final Graphics pGraph) {
        super.paintComponent(pGraph);

        drawBoard((Graphics2D) pGraph);
    }

    /**
     * Imprime une page.
     *
     * @param pGraph  Contexte graphique d'impression.
     * @param pFormat Format de page de l'édition.
     * @param pIndex  Numéro de page.
     * @return PAGE_EXISTS si la page a été imprimée, NO_SUCH_PAGE sinon.
     * @see Printable#print(Graphics, PageFormat, int)
     */
    @Override
    public int print(final Graphics pGraph, final PageFormat pFormat,
                     final int pIndex) {
        if (pIndex != 0) {
            return NO_SUCH_PAGE;
        }

        final Graphics2D g2d = (Graphics2D) pGraph;
        g2d.setPaint(Color.BLACK);
        g2d.translate(pFormat.getImageableX(), pFormat.getImageableY());

        drawBoard(g2d);

        return PAGE_EXISTS;
    }

}
