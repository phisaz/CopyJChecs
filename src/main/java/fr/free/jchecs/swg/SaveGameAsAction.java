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

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import static fr.free.jchecs.swg.ResourceUtils.getI18NString;
import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Action de l'option "Enregistrer la partie sous...".
 *
 * @author David Cotton
 */
final class SaveGameAsAction extends AbstractUIAction {
    /**
     * Identifiant de la classe pour la sérialisation.
     */
    private static final long serialVersionUID = 5665867645611699394L;

    /**
     * Petite icône.
     */
    private static final Icon ICON16 = getImageIcon("savePGNAs16.png");

    /**
     * Icône moyenne.
     */
    private static final Icon ICON22 = getImageIcon("savePGNAs22.png");

    /**
     * Crée une nouvelle instance de l'action.
     *
     * @param pUI Référence du composant maitre de l'interface.
     */
    SaveGameAsAction(final UI pUI) {
        super("saveGameAs", pUI);

        putValue(Action.SMALL_ICON, ICON16);
        putValue(Action.LARGE_ICON_KEY, ICON22);
    }

    /**
     * Réaction face au déclenchement de l'action.
     *
     * @param pEvent Evènement signalant le déclenchement.
     */
    @Override
    public void actionPerformed(final ActionEvent pEvent) {
        final JFileChooser dialog = new JFileChooser();
        dialog.setDialogTitle(getI18NString("action.saveGame.name"));
        dialog.addChoosableFileFilter(new FileNameExtensionFilter(
                getI18NString("action.saveGame.filter"), "pgn"));
        final UI ui = getUI();
        if (dialog.showSaveDialog(ui.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            File fichier = dialog.getSelectedFile();
            if (!fichier.getName().toLowerCase(Locale.getDefault())
                    .endsWith(".pgn")) {
                fichier = new File(fichier.getAbsolutePath() + ".pgn");
            }
            ui.setGameFile(fichier);
            SwingUtilities.invokeLater(new GameSaver(ui));
        }
    }
}
