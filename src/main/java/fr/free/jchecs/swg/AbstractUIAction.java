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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

import static fr.free.jchecs.swg.ResourceUtils.getI18NChar;
import static fr.free.jchecs.swg.ResourceUtils.getI18NString;

/**
 * Classe mère des actions supportées par l'interface Swing.
 *
 * @author David Cotton
 */
abstract class AbstractUIAction extends AbstractAction {
    /**
     * Identifiant pour la sérialisation.
     */
    private static final long serialVersionUID = -3190090644658542802L;

    /**
     * Référence vers le composant maitre de l'interface Swing.
     */
    private final UI _ui;

    /**
     * Instancie une nouvelle action.
     *
     * @param pNom Nom identifiant l'action.
     * @param pUI  Référence du composant maitre de l'interface.
     */
    AbstractUIAction(final String pNom, final UI pUI) {
        super(pNom);

        _ui = pUI;

        final String racine = "action." + pNom + '.';
        putValue(Action.NAME, getI18NString(racine + "name"));
        putValue(Action.SHORT_DESCRIPTION,
                getI18NString(racine + "description"));
        putValue(Action.MNEMONIC_KEY, (int) getI18NChar(racine + "mnemonic"));
    }

    /**
     * Renvoi la référence de la fenêtre principale.
     *
     * @return Fenêtre principale.
     */
    final JFrame getMainFrame() {
        return getUI().getMainFrame();
    }

    /**
     * Renvoi la référence vers le composant maitre.
     *
     * @return Composant maitre.
     */
    final UI getUI() {
        return _ui;
    }
}
