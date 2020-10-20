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

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.free.jchecs.core.Game;

import static fr.free.jchecs.swg.ResourceUtils.getI18NString;

/**
 * Boite de dialogue de l'option "Imprimer".
 *
 * @author David Cotton
 */
final class PrintPreviewDialog {
    /**
     * Panel principal de la boite de dialogue.
     */
    private static JOptionPane S_optionPane;

    /**
     * Classe utilitaire : ne pas instancier.
     */
    private PrintPreviewDialog() {
        // Rien de spécifique...
    }

    /**
     * Renvoi, après l'avoir construit si nécessaire, le panel principal de la
     * boite de dialogue.
     *
     * @return Panel principal de la boite de dialogue.
     */
    private static synchronized JOptionPane getOptionPane() {
        if (S_optionPane == null) {
            final JPanel fond = new JPanel();
            fond.setPreferredSize(new Dimension(210, 297));
            S_optionPane = new JOptionPane(fond, JOptionPane.PLAIN_MESSAGE);
            S_optionPane.setOptions(new Object[0]);
        }

        return S_optionPane;
    }

    /**
     * Affiche la boite de dialogue.
     *
     * @param pParent Composant parent de la boite de dialogue (peut être à null).
     * @param pPartie Partie à imprimer.
     */
    static void showPrintPreviewDialog(final Component pParent,
                                       final Game pPartie) {
        final JDialog dlg = getOptionPane().createDialog(pParent,
                getI18NString("dialog.print.title"));
        dlg.pack();
        dlg.setLocationRelativeTo(pParent);
        dlg.setVisible(true);
    }
}
