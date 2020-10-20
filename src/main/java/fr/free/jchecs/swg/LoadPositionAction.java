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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.free.jchecs.core.BoardFactory;
import fr.free.jchecs.core.FENException;
import fr.free.jchecs.core.MoveGenerator;

import static fr.free.jchecs.core.BoardFactory.State.EMPTY;
import static fr.free.jchecs.core.BoardFactory.Type.FASTEST;
import static fr.free.jchecs.core.FENUtils.toBoard;
import static fr.free.jchecs.swg.ResourceUtils.getI18NString;
import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Action de l'option "Charger une position".
 *
 * @author David Cotton
 */
final class LoadPositionAction extends AbstractUIAction {
    /**
     * Identifiant de la classe pour la sérialisation.
     */
    private static final long serialVersionUID = -6984312640581693931L;

    /**
     * Petite icône.
     */
    private static final Icon ICON16 = getImageIcon("loadFEN16.png");

    /**
     * Icône moyenne.
     */
    private static final Icon ICON22 = getImageIcon("loadFEN22.png");

    /**
     * Crée une nouvelle instance de l'action.
     *
     * @param pUI Référence du composant maitre de l'interface.
     */
    LoadPositionAction(final UI pUI) {
        super("loadPosition", pUI);

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
        SwingUtilities.invokeLater(new PositionLoader(getUI()));
    }

    /**
     * Classe prenant en charge le chargement de la position.
     */
    private static final class PositionLoader implements Runnable {
        /**
         * Grande icône, pour la boite de dialogue d'erreur.
         */
        private static final Icon ICON32 = getImageIcon("error32.png");

        /**
         * Référence de l'interface graphique liée.
         */
        private final UI _ui;

        /**
         * Instancie un nouveau chargeur.
         *
         * @param pUI Interface graphique mêre.
         */
        PositionLoader(final UI pUI) {
            _ui = pUI;
        }

        /**
         * Affiche la boite de dialogue signalant l'impossibilité de lire le
         * fichier.
         */
        private void errorDialog() {
            JOptionPane.showMessageDialog(_ui.getMainFrame(),
                    getI18NString("action.loadPosition.error.message"),
                    getI18NString("action.loadPosition.error.title"),
                    JOptionPane.ERROR_MESSAGE, ICON32);
        }

        /**
         * Réalise le chargement.
         */
        @Override
        public void run() {
            final JFileChooser dialog = new JFileChooser();
            dialog.setDialogTitle(getI18NString("action.loadPosition.name"));
            dialog.addChoosableFileFilter(new FileNameExtensionFilter(
                    getI18NString("action.loadPosition.filter"), "fen"));
            if (dialog.showOpenDialog(_ui.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
                final File fichier = dialog.getSelectedFile();
                if (fichier != null) {
                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(new FileReader(fichier));
                    } catch (final FileNotFoundException e) {
                        errorDialog();
                    }

                    if (in != null) {
                        String ligne = null;
                        try {
                            ligne = in.readLine();
                            while ((ligne != null)
                                    && (ligne.trim().length() == 0)) {
                                ligne = in.readLine();
                            }
                            in.close();
                        } catch (final IOException e) {
                            errorDialog();
                            try {
                                in.close();
                            } catch (final IOException e1) {
                                // Tant pis, la fermeture de la JVM s'en
                                // chargera.
                            }
                        }

                        if (ligne != null) {
                            try {
                                final MoveGenerator etat = BoardFactory
                                        .valueOf(FASTEST, EMPTY).derive(
                                                toBoard(ligne));
                                _ui.getGame().resetTo(etat);
                                _ui.setPositionFile(fichier);
                            } catch (final FENException e) {
                                errorDialog();
                            }
                        }
                    }
                }
            }
        }
    }
}
