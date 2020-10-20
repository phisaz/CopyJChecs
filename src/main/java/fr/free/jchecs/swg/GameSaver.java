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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import static fr.free.jchecs.core.PGNUtils.toPGN;
import static fr.free.jchecs.swg.ResourceUtils.getI18NString;
import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Classe prenant en charge la sauvegarde de la partie dans le fichier PGN
 * courant.
 *
 * @author David Cotton
 */
final class GameSaver implements Runnable {
    /**
     * Grande icône, pour la boite de dialogue d'erreur.
     */
    private static final Icon ICON32 = getImageIcon("error32.png");

    /**
     * Référence de l'interface graphique liée.
     */
    private final UI _ui;

    /**
     * Instancie un nouveau objet de sauvegarde d'une partie.
     *
     * @param pUI Interface graphique mêre.
     */
    GameSaver(final UI pUI) {
        _ui = pUI;
    }

    /**
     * Affiche la boite de dialogue signalant l'impossibilité d'écrire le
     * fichier.
     */
    private void errorDialog() {
        JOptionPane.showMessageDialog(_ui.getMainFrame(),
                getI18NString("action.saveGame.error.message"),
                getI18NString("action.saveGame.error.title"),
                JOptionPane.ERROR_MESSAGE, ICON32);
    }

    /**
     * Réalise la sauvegarde.
     */
    @Override
    public void run() {
        final File fichier = _ui.getGameFile();
        if (fichier != null) {
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(fichier, true));
            } catch (final IOException e) {
                errorDialog();
            }
            if (out != null) {
                try {
                    out.write('\n');
                    out.write(toPGN(_ui.getGame()));
                    out.close();
                } catch (final IOException e) {
                    errorDialog();
                    try {
                        out.close();
                    } catch (final IOException e1) {
                        // Tant pis, la fermeture de la JVM s'en chargera.
                    }
                }
            }
        }
    }
}
