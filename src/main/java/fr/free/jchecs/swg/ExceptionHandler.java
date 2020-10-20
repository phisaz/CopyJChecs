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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import fr.free.jchecs.core.Game;
import fr.free.jchecs.core.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fr.free.jchecs.core.Constants.APPLICATION_MAIL;
import static fr.free.jchecs.core.Constants.APPLICATION_NAME;
import static fr.free.jchecs.core.Constants.APPLICATION_VERSION;
import static fr.free.jchecs.core.Constants.APPLICATION_WEB;
import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Classe prenant en charge les erreurs irrécupérables.
 *
 * @author David Cotton
 */
final class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    /**
     * Log de la classe.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    /**
     * Marges des boutons.
     */
    private static final Insets DEFAULT_INSETS = new Insets(0, 0, 0, 0);

    /**
     * Référence de l'éventuelle IHM liée.
     */
    private UI _ui;

    /**
     * Instancie un nouveau gestionnaire d'erreurs irrécupérables.
     */
    ExceptionHandler() {
        // Rien de spécifique...
    }

    /**
     * Créer un rapport d'erreur en HTML.
     *
     * @param pErreur Erreur rencontrée.
     * @return Rapport d'erreur.
     */
    private String makeHTMLReport(final Throwable pErreur) {
        final StringBuilder sb = new StringBuilder("<html><h2>");
        sb.append(pErreur.toString());
        sb.append("</h2>");
        final StackTraceElement[] stk = pErreur.getStackTrace();
        final int l = stk.length;
        if (l > 0) {
            sb.append("<h3>").append(stk[0].toString()).append("</h3><ul>");
            for (int i = 1; i < l; i++) {
                sb.append("<li>").append(stk[i].toString()).append("</li>");
            }
            sb.append("</ul>");
        }
        sb.append("<hr><table>");
        sb.append("<tr><td>Application</td><td><i>").append(APPLICATION_NAME)
                .append(" v").append(APPLICATION_VERSION)
                .append("</i></td></tr>");
        sb.append("<tr><td>Runtine</td><td><i>")
                .append(System.getProperty("java.runtime.name")).append(' ')
                .append(System.getProperty("java.runtime.version"))
                .append("</i></td></tr>");
        sb.append("<tr><td>OS</td><td><i>")
                .append(System.getProperty("os.name")).append(' ')
                .append(System.getProperty("os.version"))
                .append("</i></td></tr>");
        sb.append("<tr><td>Locale</td><td><i>")
                .append(System.getProperty("user.language")).append('_')
                .append(System.getProperty("user.country"))
                .append("</i></td></tr>");
        if (_ui != null) {
            final Game partie = _ui.getGame();
            if (partie != null) {
                Player joueur = partie.getPlayer(true);
                if (joueur != null) {
                    sb.append("<tr><td>White</td><td><i>")
                            .append(joueur.getEngine())
                            .append("</i></td></tr>");
                }
                joueur = partie.getPlayer(false);
                if (joueur != null) {
                    sb.append("<tr><td>Black</td><td><i>")
                            .append(joueur.getEngine())
                            .append("</i></td></tr>");
                }
                sb.append("<tr><td>Position</td><td><i>")
                        .append(partie.getFENPosition())
                        .append("</i></td></tr>");
                sb.append("<tr><td>Moves</td><td><i>");
                for (final String san : partie.getSANStrings()) {
                    sb.append(san).append(' ');
                }
                sb.append("</i></td></tr>");
            }
        }
        sb.append("</table></html>");

        return sb.toString();
    }

    /**
     * Créer un rapport d'erreur simple.
     *
     * @param pErreur Erreur rencontrée.
     * @return Rapport d'erreur.
     */
    private String makeTextReport(final Throwable pErreur) {
        final StringBuilder sb = new StringBuilder(
                "---------- Start of bug report ----------\n");
        sb.append(pErreur.toString()).append("\n\n");
        final StackTraceElement[] stk = pErreur.getStackTrace();
        final int l = stk.length;
        if (l > 0) {
            sb.append(stk[0].toString()).append('\n');
            for (int i = 1; i < l; i++) {
                sb.append('\t').append(stk[i].toString()).append('\n');
            }
        }
        sb.append('\n');
        sb.append("Application: ").append(APPLICATION_NAME).append(" v")
                .append(APPLICATION_VERSION).append('\n');
        sb.append("Runtine    : ")
                .append(System.getProperty("java.runtime.name")).append(' ')
                .append(System.getProperty("java.runtime.version"))
                .append('\n');
        sb.append("OS         : ").append(System.getProperty("os.name"))
                .append(' ').append(System.getProperty("os.version"))
                .append('\n');
        sb.append("Locale     : ").append(System.getProperty("user.language"))
                .append('_').append(System.getProperty("user.country"))
                .append('\n');
        if (_ui != null) {
            final Game partie = _ui.getGame();
            if (partie != null) {
                Player joueur = partie.getPlayer(true);
                if (joueur != null) {
                    sb.append("White      : ").append(joueur.getEngine())
                            .append('\n');
                }
                joueur = partie.getPlayer(false);
                if (joueur != null) {
                    sb.append("Black      : ").append(joueur.getEngine())
                            .append('\n');
                }
                sb.append("Position   : ").append(partie.getFENPosition())
                        .append('\n');
                sb.append("Moves      : ");
                for (final String san : partie.getSANStrings()) {
                    sb.append(san).append(' ');
                }
                sb.append('\n');
            }
        }
        sb.append("---------- End of bug report ----------\n");

        return sb.toString();
    }

    /**
     * Alimente la référence de l'éventuelle IHM.
     *
     * @param pUI IHM mère (peut être à null).
     */
    void setUI(final UI pUI) {
        _ui = pUI;
    }

    /**
     * Prend en charge les erreurs inattendues.
     *
     * @param pThread Thread dans lequel s'est produite l'erreur.
     * @param pErreur Erreur rencontrée.
     */
    @Override
    public void uncaughtException(final Thread pThread, final Throwable pErreur) {
        if (pErreur == null) {
            LOG.error("UncaughtException without exception !?");
        } else {
            LOG.error(pErreur.toString());

            if (GraphicsEnvironment.isHeadless()) {
                pErreur.printStackTrace();
            } else {
                final JPanel fond = new JPanel(new BorderLayout(4, 4));

                final JPanel haut = new JPanel(new BorderLayout());
                final JButton copyBt = new JButton(
                        getImageIcon("copyBug22.png"));
                copyBt.setToolTipText("Copy error report to clipboard");
                copyBt.setMargin(DEFAULT_INSETS);
                haut.add(copyBt, BorderLayout.WEST);
                final JLabel msg = new JLabel(pErreur.getMessage(),
                        getImageIcon("fatalError.png"), SwingConstants.CENTER);
                final Font ft = msg.getFont();
                msg.setFont(ft.deriveFont(Font.BOLD, ft.getSize() * 1.5F));
                haut.add(msg, BorderLayout.CENTER);
                final JButton mailBt = new JButton(
                        getImageIcon("mailBug22.png"));
                mailBt.setToolTipText("Create a mail with error report");
                mailBt.setMargin(DEFAULT_INSETS);
                haut.add(mailBt, BorderLayout.EAST);

                fond.add(haut, BorderLayout.NORTH);

                final JEditorPane pile = new JEditorPane("text/html",
                        makeHTMLReport(pErreur));
                pile.setEditable(false);
                fond.add(new JScrollPane(pile), BorderLayout.CENTER);

                fond.add(
                        new JLabel(
                                "<html><center>"
                                        + APPLICATION_NAME
                                        + ' '
                                        + APPLICATION_VERSION
                                        + " has encountered a fatal error and will be closed.<br/>You can report this bug at "
                                        + APPLICATION_WEB + "</center></html>",
                                SwingConstants.CENTER), BorderLayout.SOUTH);

                final JOptionPane options = new JOptionPane(fond,
                        JOptionPane.PLAIN_MESSAGE);
                final Window parent;
                if (_ui == null) {
                    parent = null;
                } else {
                    parent = _ui.getMainFrame();
                }
                final JDialog dialog = options.createDialog(parent,
                        "Fatal error");

                final ActionListener ecouteur = new ActionListener() {
                    /**
                     * Tient compte du déclenchement des boutons.
                     *
                     * @param pEvent
                     *            Evènement signalant le déclenchement.
                     */
                    @Override
                    public void actionPerformed(final ActionEvent pEvent) {
                        final Object source = pEvent.getSource();
                        final String rapport = makeTextReport(pErreur);

                        if (source == copyBt) {
                            final StringSelection r = new StringSelection(
                                    rapport);
                            final Clipboard clp = Toolkit.getDefaultToolkit()
                                    .getSystemClipboard();
                            clp.setContents(r, r);
                        } else if (source == mailBt) {
                            try {
                                final URI mailto = new URI("mailto",
                                        APPLICATION_MAIL + "?subject="
                                                + APPLICATION_NAME
                                                + "-BugReport&body=" + rapport,
                                        null);
                                Desktop.getDesktop().mail(mailto);
                            } catch (final URISyntaxException e) {
                                LOG.error(e.toString());
                                mailBt.setEnabled(false);
                            } catch (final IOException e) {
                                LOG.error(e.toString());
                                mailBt.setEnabled(false);
                                JOptionPane.showMessageDialog(dialog,
                                        "Mail is unusable.", "Mail error",
                                        JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    }
                };
                copyBt.addActionListener(ecouteur);
                if (Desktop.isDesktopSupported()
                        && Desktop.getDesktop()
                        .isSupported(Desktop.Action.MAIL)) {
                    mailBt.addActionListener(ecouteur);
                } else {
                    mailBt.setEnabled(false);
                }

                dialog.setResizable(true);
                dialog.setSize(550, 300);
                dialog.validate();
                dialog.setLocationRelativeTo(parent);
                dialog.setVisible(true);
            }
        }

        System.exit(-1);
    }
}
