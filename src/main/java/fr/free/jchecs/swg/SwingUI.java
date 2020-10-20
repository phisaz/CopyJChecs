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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import fr.free.jchecs.ai.Engine;
import fr.free.jchecs.ai.EngineFactory;
import fr.free.jchecs.core.Game;
import fr.free.jchecs.core.Move;
import fr.free.jchecs.core.MoveGenerator;
import fr.free.jchecs.core.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fr.free.jchecs.core.Constants.APPLICATION_NAME;
import static fr.free.jchecs.core.Constants.APPLICATION_VERSION;
import static fr.free.jchecs.core.Game.State.IN_PROGRESS;
import static fr.free.jchecs.swg.ResourceUtils.getI18NChar;
import static fr.free.jchecs.swg.ResourceUtils.getI18NString;
import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Classe principale de l'interface graphique Swing du jeu d'échecs.
 *
 * @author David Cotton
 */
final class SwingUI implements MoveListener, UI {
    /**
     * Clé de la propriété "cellsSize".
     */
    static final String CELLS_SIZE_PROPERTY = "cellsSize";

    /**
     * Clé de la propriété "enableSounds".
     */
    static final String ENABLE_SOUNDS_PROPERTY = "enableSounds";

    /**
     * Clé de la propriété "flipView".
     */
    static final String FLIP_VIEW_PROPERTY = "flipView";

    /**
     * Clé de la propriété "highlightValid".
     */
    static final String HIGHLIGHT_LAST_MOVE_PROPERTY = "highlightLastMove";

    /**
     * Clé de la propriété "highlightValid".
     */
    static final String HIGHLIGHT_VALIDS_PROPERTY = "highlightValids";

    /**
     * Clé de la propriété "showCaptures".
     */
    static final String SHOW_CAPTURES_PROPERTY = "showCaptures";

    /**
     * Clé de la propriété "showCoords".
     */
    static final String SHOW_COORDS_PROPERTY = "showCoords";

    /**
     * Clé de la propriété "showFEN".
     */
    static final String SHOW_FEN_PROPERTY = "showFEN";

    /**
     * Clé de la propriété "showMenuBar".
     */
    static final String SHOW_MENUBAR_PROPERTY = "showMenuBar";

    /**
     * Clé de la propriété "showToolBar".
     */
    static final String SHOW_TOOLBAR_PROPERTY = "showToolBar";

    /**
     * Clé de la propriété portant l'apparence des pièces.
     */
    static final String PIECES_LF_PROPERTY = "lf.pieces";

    /**
     * Clé de la propriété portant l'apparence du plateau.
     */
    static final String BOARD_LF_PROPERTY = "lf.board";

    /**
     * Identifiant du panel affichant les joueurs.
     */
    private static final String PLAYERS_PANEL_ID = "panel.players";

    /**
     * Clé de la propriété indiquant si le joueur des noirs est humain.
     */
    private static final String PLAYER_BLACKS_HUMAN_PROPERTY = "player.blacks.human";

    /**
     * Clé de la propriété portant le nom du joueur des noirs.
     */
    private static final String PLAYER_BLACKS_NAME_PROPERTY = "player.blacks.name";

    /**
     * Clé de la propriété portant la profondeur de recherche du joueur des
     * noirs.
     */
    private static final String PLAYER_BLACKS_DEPTH_PROPERTY = "player.blacks.depth";

    /**
     * Clé de la propriété indiquant si le joueur des blancs est humain.
     */
    private static final String PLAYER_WHITES_HUMAN_PROPERTY = "player.whites.human";

    /**
     * Clé de la propriété portant le nom du joueur des blancs.
     */
    private static final String PLAYER_WHITES_NAME_PROPERTY = "player.whites.name";

    /**
     * Clé de la propriété portant la profondeur de recherche du joueur des
     * blancs.
     */
    private static final String PLAYER_WHITES_DEPTH_PROPERTY = "player.whites.depth";

    /**
     * Identifiant du panel affichant l'horloge.
     */
    private static final String CLOCK_PANEL_ID = "panel.clock";

    /**
     * Identifiant du panel affichant la feuille de match.
     */
    private static final String SCORESHEET_PANEL_ID = "panel.scoresheet";

    /**
     * Identifiant du panel affichant l'évaluation de la position.
     */
    private static final String EVAL_PANEL_ID = "panel.eval";

    /**
     * Identifiant du panel affichant l'enregistreur.
     */
    private static final String RECORDER_PANEL_ID = "panel.recorder";

    /**
     * Identifiant du panel affichant les préférences.
     */
    private static final String SETTINGS_PANEL_ID = "panel.settings";

    /**
     * Identifiant du panel de sélection de l'apparence.
     */
    private static final String LF_PANEL_ID = "panel.lf";

    /**
     * Icone de la fenêtre principale.
     */
    private static final Image MAIN_FRAME_ICON = getImageIcon("icon16.png")
            .getImage();

    /**
     * Icone du panel des joueurs.
     */
    private static final Icon PLAYERS_ICON = getImageIcon("players16.png");

    /**
     * Icone du panel de l'horloge.
     */
    private static final Icon CLOCK_ICON = getImageIcon("clock16.png");

    /**
     * Icone du panel de la feuille de match.
     */
    private static final Icon SHEET_ICON = getImageIcon("sheet16.png");

    /**
     * Icone du panel d'évaluation.
     */
    private static final Icon EVAL_ICON = getImageIcon("eval16.png");

    /**
     * Icone du panel de l'enregistreur.
     */
    private static final Icon RECORD_ICON = getImageIcon("record16.png");

    /**
     * Icone du panel de l'enregistreur.
     */
    private static final Icon CONFIG_ICON = getImageIcon("config16.png");

    /**
     * Icone du panel de sélection de l'apparence.
     */
    private static final Icon LF_ICON = getImageIcon("lf16.png");

    /**
     * Identifiant de l'action "About".
     */
    private static final String ABOUT_ACTION = "about";

    /**
     * Identifiant de l'action "Copy".
     */
    private static final String COPY_ACTION = "copy";

    /**
     * Identifiant de l'action "Exit".
     */
    private static final String EXIT_ACTION = "exit";

    /**
     * Identifiant de l'action "Help".
     */
    private static final String HELP_ACTION = "help";

    /**
     * Identifiant de l'action "Load game".
     */
    private static final String LOAD_GAME_ACTION = "loadGame";

    /**
     * Identifiant de l'action "Load position".
     */
    private static final String LOAD_POSITION_ACTION = "loadPosition";

    /**
     * Identifiant de l'action "Paste".
     */
    private static final String PASTE_ACTION = "paste";

    /**
     * Identifiant de l'action "Print".
     */
    private static final String PRINT_ACTION = "print";

    /**
     * Identifiant de l'action "Reset".
     */
    private static final String RESET_ACTION = "reset";

    /**
     * Identifiant de l'action "Save game".
     */
    private static final String SAVE_GAME_ACTION = "saveGame";

    /**
     * Identifiant de l'action "Save game as".
     */
    private static final String SAVE_GAME_AS_ACTION = "saveGameAs";

    /**
     * Identifiant de l'action "Save position".
     */
    private static final String SAVE_POSITION_ACTION = "savePosition";

    /**
     * Identifiant de l'action "Save position as".
     */
    private static final String SAVE_POSITION_AS_ACTION = "savePositionAs";

    /**
     * Nom du fichier de configuration.
     */
    private static final String CONFIG_FILE_NAME = APPLICATION_NAME + ".config";

    /**
     * Log de la classe.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SwingUI.class);

    /**
     * Son signalant un mouvement.
     */
    private static final Clip MOVE_SOUND;

    static {
        final URL url = Start.class.getResource("move.wav");
        Clip clip = null;
        try {
            final Line.Info info = new Line.Info(Clip.class);
            if (AudioSystem.isLineSupported(info)) {
                final AudioInputStream ficIn = AudioSystem
                        .getAudioInputStream(url);
                clip = (Clip) AudioSystem.getLine(info);
                clip.open(ficIn);
            }
        } catch (final UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            LOG.error("Audio failed:", e);
            clip = null;
        }
        MOVE_SOUND = clip;
    }

    /**
     * Propriétés de l'interface.
     */
    private final Properties _properties;

    /**
     * Actions liées à l'interface.
     */
    private final Map<String, Action> _actions;

    /**
     * Définition de la partie en cours.
     */
    private final Game _game;

    /**
     * Composant représentant l'échiquier.
     */
    private final BoardUI _boardUI;

    /**
     * Panneau d'affichage des prises du haut (noires par défaut).
     */
    private final CapturesPanel _capturesUp;

    /**
     * Panneau d'affichage des prises du bas (blancs par défaut).
     */
    private final CapturesPanel _capturesDown;

    /**
     * Panneau d'affichage de la chaine FEN.
     */
    private final FENPanel _fenPanel;

    /**
     * Fenêtre principale.
     */
    private JFrame _mainFrame;

    /**
     * Barre de menu.
     */
    private JMenuBar _menuBar;

    /**
     * Barre d'outils.
     */
    private JToolBar _toolBar;

    /**
     * Dernier mouvement reçu (peut être à null).
     */
    private Move _lastMove;

    /**
     * Eventuel fichier PGN contenant la partie (peut être à null).
     */
    private File _gameFile;

    /**
     * Eventuel fichier FEN contenant la position (peut être à null).
     */
    private File _positionFile;

    /**
     * Echantillon sonore en cours d'exécution.
     */
    private Clip _activeSample;

    /**
     * Drapeau indiquant si le son est activé.
     */
    private boolean _soundEnabled;

    /**
     * Crée une nouvelle instance.
     */
    SwingUI() {
        _soundEnabled = MOVE_SOUND != null;

        _properties = new Properties();
        initProperties();

        _actions = new HashMap<>();

        _game = new Game();
        final PieceUI pieceUI = new PieceUI();
        _boardUI = new BoardUI(_game, pieceUI);
        _capturesUp = new CapturesPanel(_game, pieceUI, true);
        _capturesDown = new CapturesPanel(_game, pieceUI, false);
        _fenPanel = new FENPanel(_game);
    }

    /**
     * Renvoi l'action correspondant à une clé, après l'avoir instanciée si
     * nécessaire.
     *
     * @param pCle Clé identifiant l'action.
     * @return Action correspondante (ou null).
     */
    private Action getAction(final String pCle) {
        Action res = _actions.get(pCle);
        if (res == null) {
            if (ABOUT_ACTION.equals(pCle)) {
                res = new AboutAction(this);
            } else if (COPY_ACTION.equals(pCle)) {
                res = new CopyAction(this);
            } else if (EXIT_ACTION.equals(pCle)) {
                res = new ExitAction(this);
            } else if (HELP_ACTION.equals(pCle)) {
                res = new HelpAction(this);
            } else if (LOAD_GAME_ACTION.equals(pCle)) {
                res = new LoadGameAction(this);
            } else if (LOAD_POSITION_ACTION.equals(pCle)) {
                res = new LoadPositionAction(this);
            } else if (PASTE_ACTION.equals(pCle)) {
                res = new PasteAction(this);
            } else if (PRINT_ACTION.equals(pCle)) {
                res = new PrintAction(this);
            } else if (RESET_ACTION.equals(pCle)) {
                res = new ResetAction(this);
            } else if (SAVE_GAME_ACTION.equals(pCle)) {
                res = new SaveGameAction(this);
            } else if (SAVE_GAME_AS_ACTION.equals(pCle)) {
                res = new SaveGameAsAction(this);
            } else if (SAVE_POSITION_ACTION.equals(pCle)) {
                res = new SavePositionAction(this);
            } else if (SAVE_POSITION_AS_ACTION.equals(pCle)) {
                res = new SavePositionAsAction(this);
            } else {
                LOG.warn("Unknown action [{}]", pCle);
            }
            _actions.put(pCle, res);
        }

        return res;
    }

    /**
     * Renvoi la valeur d'une propriété booléenne.
     *
     * @param pCle Cle identifiant la propriété.
     * @return Valeur de la propriété.
     */
    @Override
    public boolean getBooleanProperty(final String pCle) {
        return Boolean.valueOf(_properties.getProperty(pCle));
    }

    /**
     * Renvoi une référence vers la partie en cours.
     *
     * @return Partie en cours.
     */
    @Override
    public Game getGame() {
        return _game;
    }

    /**
     * Renvoi une référence vers l'éventuel fichier PGN contenant la partie.
     *
     * @return Fichier PGN de la partie (peut être à null).
     */
    @Override
    public File getGameFile() {
        return _gameFile;
    }

    /**
     * Renvoi une référence vers la fenêtre principale.
     *
     * @return Fenêtre principale.
     */
    @Override
    public JFrame getMainFrame() {
        if (_mainFrame == null) {
            Player joueur = _game.getPlayer(true);
            joueur.setName(_properties.getProperty(PLAYER_WHITES_NAME_PROPERTY));
            if (getBooleanProperty(PLAYER_WHITES_HUMAN_PROPERTY)) {
                joueur.setEngine(null);
            } else {
                final Engine moteur = EngineFactory
                        .newInstance(APPLICATION_NAME + '.' + joueur.getName());
                moteur.setSearchDepthLimit(Integer.parseInt(_properties
                        .getProperty(PLAYER_WHITES_DEPTH_PROPERTY)));
                joueur.setEngine(moteur);
            }
            joueur = _game.getPlayer(false);
            joueur.setName(_properties.getProperty(PLAYER_BLACKS_NAME_PROPERTY));
            if (getBooleanProperty(PLAYER_BLACKS_HUMAN_PROPERTY)) {
                joueur.setEngine(null);
            } else {
                final Engine moteur = EngineFactory
                        .newInstance(APPLICATION_NAME + '.' + joueur.getName());
                moteur.setSearchDepthLimit(Integer.parseInt(_properties
                        .getProperty(PLAYER_BLACKS_DEPTH_PROPERTY)));
                joueur.setEngine(moteur);
            }

            _mainFrame = new JFrame(APPLICATION_NAME + ' '
                    + APPLICATION_VERSION);
            _mainFrame.setIconImage(MAIN_FRAME_ICON);

            getAction(SAVE_GAME_ACTION).setEnabled(false);
            getAction(SAVE_POSITION_ACTION).setEnabled(false);

            final JMenuBar menu = getMenuBar();
            _mainFrame.setJMenuBar(menu);
            menu.setVisible(getBooleanProperty(SHOW_MENUBAR_PROPERTY));

            final JPanel fond = new JPanel(new BorderLayout(4, 4));

            final JToolBar barre = getToolBar();
            barre.setVisible(getBooleanProperty(SHOW_TOOLBAR_PROPERTY));
            fond.add(barre, BorderLayout.NORTH);

            final JPanel cadreCentral = new JPanel(new BorderLayout());
            final boolean flip = getBooleanProperty(FLIP_VIEW_PROPERTY);
            _capturesUp.setWhite(!flip);
            cadreCentral.add(_capturesUp.getComponent(), BorderLayout.NORTH);
            _boardUI.setFlipView(flip);
            cadreCentral.add(_boardUI, BorderLayout.CENTER);
            _capturesDown.setWhite(flip);
            cadreCentral.add(_capturesDown.getComponent(), BorderLayout.SOUTH);

            final boolean capturesVisibles = getBooleanProperty(SHOW_CAPTURES_PROPERTY);
            _capturesUp.getComponent().setVisible(capturesVisibles);
            _capturesDown.getComponent().setVisible(capturesVisibles);

            fond.add(cadreCentral, BorderLayout.CENTER);

            final TaskPanel tp = new TaskPanel();
            tp.add(new TaskItem(this, PLAYERS_PANEL_ID, new PlayersPanel(this)
                    .getComponent(), PLAYERS_ICON));
            tp.add(new TaskItem(this, CLOCK_PANEL_ID, new Clock(_game)
                    .getComponent(), CLOCK_ICON));
            tp.add(new TaskItem(this, SCORESHEET_PANEL_ID,
                    new ScoreSheet(_game).getComponent(), SHEET_ICON));
            tp.add(new TaskItem(this, EVAL_PANEL_ID, new EvalPanel(_game)
                    .getComponent(), EVAL_ICON));
            tp.add(new TaskItem(this, RECORDER_PANEL_ID, new Recorder(_game)
                    .getComponent(), RECORD_ICON));
            tp.add(new TaskItem(this, SETTINGS_PANEL_ID,
                    new SettingsPanel(this).getComponent(), CONFIG_ICON));
            tp.add(new TaskItem(this, LF_PANEL_ID, new LFPanel(this)
                    .getComponent(), LF_ICON));
            fond.add(tp.getComponent(), BorderLayout.EAST);

            _fenPanel.getComponent().setVisible(
                    getBooleanProperty(SHOW_FEN_PROPERTY));
            fond.add(_fenPanel.getComponent(), BorderLayout.SOUTH);

            _mainFrame.setContentPane(fond);

            _boardUI.setBoardLF(Integer.parseInt(_properties
                    .getProperty(BOARD_LF_PROPERTY)));
            _boardUI.setCellSideLength(Integer.parseInt(_properties
                    .getProperty(CELLS_SIZE_PROPERTY)));
            _boardUI.setCoordinatesPainted(getBooleanProperty(SHOW_COORDS_PROPERTY));
            _boardUI.setHighlightLastMove(getBooleanProperty(HIGHLIGHT_LAST_MOVE_PROPERTY));
            _boardUI.setHighlightValids(getBooleanProperty(HIGHLIGHT_VALIDS_PROPERTY));
            _boardUI.setPiecesLF(Integer.parseInt(_properties
                    .getProperty(PIECES_LF_PROPERTY)));
            _boardUI.addMoveListener(this);

            _mainFrame
                    .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            _mainFrame.addWindowListener(new MainWindowAdapter(
                    getAction(EXIT_ACTION)));
            resizeFrame();

            getAction(RESET_ACTION).actionPerformed(
                    new ActionEvent(this, 0, null));
        }

        return _mainFrame;
    }

    /**
     * Renvoi une référence vers la barre de menu.
     *
     * @return Barre de menu.
     */
    private JMenuBar getMenuBar() {
        if (_menuBar == null) {
            final JMenuBar menu = new JMenuBar();
            final JMenu menuFichier = new JMenu(getI18NString("menu.file.name"));
            menuFichier.setMnemonic(getI18NChar("menu.file.mnemonic"));
            menuFichier.add(getAction(RESET_ACTION));
            menuFichier.addSeparator();
            menuFichier.add(getAction(LOAD_GAME_ACTION));
            menuFichier.add(getAction(SAVE_GAME_ACTION));
            menuFichier.add(getAction(SAVE_GAME_AS_ACTION));
            menuFichier.addSeparator();
            getAction(PRINT_ACTION).setEnabled(false);
            menuFichier.add(getAction(PRINT_ACTION));
            menuFichier.addSeparator();
            menuFichier.add(getAction(LOAD_POSITION_ACTION));
            menuFichier.add(getAction(SAVE_POSITION_ACTION));
            menuFichier.add(getAction(SAVE_POSITION_AS_ACTION));
            menuFichier.addSeparator();
            menuFichier.add(getAction(EXIT_ACTION));
            menu.add(menuFichier);

            final JMenu menuEdition = new JMenu(getI18NString("menu.edit.name"));
            menuEdition.setMnemonic(getI18NChar("menu.edit.mnemonic"));
            menuEdition.add(getAction(COPY_ACTION));
            menuEdition.add(getAction(PASTE_ACTION));
            menu.add(menuEdition);

            final JMenu menuAide = new JMenu(getI18NString("menu.help.name"));
            menuAide.setMnemonic(getI18NChar("menu.help.mnemonic"));
            getAction(HELP_ACTION).setEnabled(false);
            menuAide.add(getAction(HELP_ACTION));
            menuAide.addSeparator();
            menuAide.add(getAction(ABOUT_ACTION));
            menu.add(menuAide);
            _menuBar = menu;
        }

        return _menuBar;
    }

    /**
     * Renvoi une référence vers l'éventuel fichier FEN contenant la position.
     *
     * @return Fichier FEN de position (peut être à null).
     */
    @Override
    public File getPositionFile() {
        return _positionFile;
    }

    /**
     * Renvoi la valeur d'une propriété.
     *
     * @param pCle Cle identifiant la propriété.
     * @return Valeur de la propriété.
     */
    @Override
    public String getProperty(final String pCle) {
        return _properties.getProperty(pCle);
    }

    /**
     * Renvoi une référence vers la barre d'outils.
     *
     * @return Barre d'outils.
     */
    private JToolBar getToolBar() {
        if (_toolBar == null) {
            final JToolBar barre = new JToolBar();
            barre.setFloatable(false);
            barre.add(getAction(RESET_ACTION));
            barre.addSeparator();
            barre.add(getAction(LOAD_GAME_ACTION));
            barre.add(getAction(SAVE_GAME_ACTION));
            barre.add(getAction(SAVE_GAME_AS_ACTION));
            barre.addSeparator();
            barre.add(getAction(PRINT_ACTION));
            barre.addSeparator();
            barre.add(getAction(LOAD_POSITION_ACTION));
            barre.add(getAction(SAVE_POSITION_ACTION));
            barre.add(getAction(SAVE_POSITION_AS_ACTION));
            barre.addSeparator();
            barre.add(getAction(EXIT_ACTION));
            barre.addSeparator();
            barre.add(getAction(COPY_ACTION));
            barre.add(getAction(PASTE_ACTION));
            barre.addSeparator();
            barre.add(getAction(HELP_ACTION));
            barre.addSeparator();
            barre.add(getAction(ABOUT_ACTION));
            _toolBar = barre;
        }

        return _toolBar;
    }

    /**
     * Charge les propriétés.
     */
    private void initProperties() {
        try (final FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + File.separatorChar + CONFIG_FILE_NAME)) {
            _properties.load(fis);
        } catch (final IOException e) {
            // Pas grave, on continue avec les valeurs par défaut...
            LOG.warn("Config unavailable.", e);
        }

        final int hauteurEcran = Toolkit.getDefaultToolkit().getScreenSize().height;
        final String taille;
        if (hauteurEcran < 675) {
            taille = "48";
        } else if (hauteurEcran > 925) {
            taille = "80";
        } else {
            taille = "64";
        }
        _properties.setProperty(CELLS_SIZE_PROPERTY, _properties.getProperty(CELLS_SIZE_PROPERTY, taille));
        _properties.setProperty(ENABLE_SOUNDS_PROPERTY, _properties.getProperty(ENABLE_SOUNDS_PROPERTY, Boolean.toString(_soundEnabled)));
        _soundEnabled = getBooleanProperty(ENABLE_SOUNDS_PROPERTY);
        _properties.setProperty(HIGHLIGHT_LAST_MOVE_PROPERTY, _properties.getProperty(HIGHLIGHT_LAST_MOVE_PROPERTY, "true"));
        _properties.setProperty(HIGHLIGHT_VALIDS_PROPERTY, _properties.getProperty(HIGHLIGHT_VALIDS_PROPERTY, "true"));
        String prop = PLAYERS_PANEL_ID + ".folded";
        _properties.setProperty(prop, _properties.getProperty(prop, "false"));
        prop = CLOCK_PANEL_ID + ".folded";
        _properties.setProperty(prop, _properties.getProperty(prop, "false"));
        prop = SCORESHEET_PANEL_ID + ".folded";
        _properties.setProperty(prop, _properties.getProperty(prop, "false"));
        prop = EVAL_PANEL_ID + ".folded";
        _properties.setProperty(prop, _properties.getProperty(prop, "false"));
        prop = RECORDER_PANEL_ID + ".folded";
        _properties.setProperty(prop, _properties.getProperty(prop, "false"));
        prop = SETTINGS_PANEL_ID + ".folded";
        _properties.setProperty(prop, _properties.getProperty(prop, "true"));
        prop = LF_PANEL_ID + ".folded";
        _properties.setProperty(prop, _properties.getProperty(prop, "true"));
        _properties.setProperty(SHOW_CAPTURES_PROPERTY, _properties.getProperty(SHOW_CAPTURES_PROPERTY, "true"));
        _properties.setProperty(SHOW_COORDS_PROPERTY, _properties.getProperty(SHOW_COORDS_PROPERTY, "true"));
        _properties.setProperty(SHOW_FEN_PROPERTY, _properties.getProperty(SHOW_FEN_PROPERTY, "true"));
        _properties.setProperty(SHOW_MENUBAR_PROPERTY, _properties.getProperty(SHOW_MENUBAR_PROPERTY, "true"));
        _properties.setProperty(SHOW_TOOLBAR_PROPERTY, _properties.getProperty(SHOW_TOOLBAR_PROPERTY, "true"));
        _properties.setProperty(FLIP_VIEW_PROPERTY, _properties.getProperty(FLIP_VIEW_PROPERTY, "false"));
        _properties.setProperty(PLAYER_WHITES_HUMAN_PROPERTY, _properties.getProperty(PLAYER_WHITES_HUMAN_PROPERTY, "true"));
        _properties.setProperty(PLAYER_WHITES_NAME_PROPERTY, _properties.getProperty(PLAYER_WHITES_NAME_PROPERTY, getI18NString("label.player.human")));
        _properties.setProperty(PLAYER_WHITES_DEPTH_PROPERTY, _properties.getProperty(PLAYER_WHITES_DEPTH_PROPERTY, "0"));
        _properties.setProperty(PLAYER_BLACKS_HUMAN_PROPERTY, _properties.getProperty(PLAYER_BLACKS_HUMAN_PROPERTY, "false"));
        _properties.setProperty(PLAYER_BLACKS_NAME_PROPERTY, _properties.getProperty(PLAYER_BLACKS_NAME_PROPERTY, "NegaScout"));
        _properties.setProperty(PLAYER_BLACKS_DEPTH_PROPERTY, _properties.getProperty(PLAYER_BLACKS_DEPTH_PROPERTY, "5"));
        _properties.setProperty(BOARD_LF_PROPERTY, _properties.getProperty(BOARD_LF_PROPERTY, "0"));
        _properties.setProperty(PIECES_LF_PROPERTY, _properties.getProperty(PIECES_LF_PROPERTY, "0"));
    }

    /**
     * Tient compte des évènements signalant un mouvement.
     *
     * @param pEvent Evènement signalant un mouvement.
     */
    @Override
    public synchronized void moved(final MoveEvent pEvent) {
        _lastMove = pEvent.getMove();
        notifyAll();
    }

    /**
     * Redimensionne la fenetre.
     */
    private void resizeFrame() {
        final JFrame fen = getMainFrame();
        fen.setResizable(true);
        _capturesUp.initCapturesPaint();
        _capturesDown.initCapturesPaint();
        fen.pack();
        fen.setResizable(false);
    }

    /**
     * Enregistre les propriétés dans le fichier de configuration.
     */
    private void saveProperties() {
        Player joueur = _game.getPlayer(true);
        Engine moteur = joueur.getEngine();
        setBooleanProperty(PLAYER_WHITES_HUMAN_PROPERTY, moteur == null);
        _properties.setProperty(PLAYER_WHITES_NAME_PROPERTY, joueur.getName());
        String limite;
        if (moteur == null) {
            limite = "0";
        } else {
            limite = Integer.toString(moteur.getSearchDepthLimit());
        }
        _properties.setProperty(PLAYER_WHITES_DEPTH_PROPERTY, limite);
        joueur = _game.getPlayer(false);
        moteur = joueur.getEngine();
        setBooleanProperty(PLAYER_BLACKS_HUMAN_PROPERTY, moteur == null);
        _properties.setProperty(PLAYER_BLACKS_NAME_PROPERTY, joueur.getName());
        if (moteur == null) {
            limite = "0";
        } else {
            limite = Integer.toString(moteur.getSearchDepthLimit());
        }
        _properties.setProperty(PLAYER_BLACKS_DEPTH_PROPERTY, limite);

        try (final FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + File.separatorChar + CONFIG_FILE_NAME)) {
            _properties.store(fos, APPLICATION_NAME + ' ' + APPLICATION_VERSION + " configuration file");
        } catch (final IOException e) {
            // Pas grave, on peut s'en passer...
            LOG.error("Config not saved.", e);
        }
    }

    /**
     * Alimente la valeur d'une propriété booléenne.
     *
     * @param pCle    Cle identifiant la propriété.
     * @param pValeur Valeur de la propriété.
     */
    @Override
    public void setBooleanProperty(final String pCle, final boolean pValeur) {
        _properties.setProperty(pCle, Boolean.toString(pValeur));
        if (ENABLE_SOUNDS_PROPERTY.equals(pCle)) {
            _soundEnabled = pValeur;
        } else if (HIGHLIGHT_LAST_MOVE_PROPERTY.equals(pCle)) {
            _boardUI.setHighlightLastMove(pValeur);
        } else if (HIGHLIGHT_VALIDS_PROPERTY.equals(pCle)) {
            _boardUI.setHighlightValids(pValeur);
        } else if (SHOW_CAPTURES_PROPERTY.equals(pCle)) {
            _capturesUp.getComponent().setVisible(pValeur);
            _capturesDown.getComponent().setVisible(pValeur);
            resizeFrame();
        } else if (SHOW_COORDS_PROPERTY.equals(pCle)) {
            _boardUI.setCoordinatesPainted(pValeur);
            resizeFrame();
        } else if (SHOW_FEN_PROPERTY.equals(pCle)) {
            _fenPanel.getComponent().setVisible(pValeur);
            resizeFrame();
        } else if (SHOW_MENUBAR_PROPERTY.equals(pCle)) {
            getMenuBar().setVisible(pValeur);
            resizeFrame();
        } else if (SHOW_TOOLBAR_PROPERTY.equals(pCle)) {
            getToolBar().setVisible(pValeur);
            resizeFrame();
        } else if (FLIP_VIEW_PROPERTY.equals(pCle)) {
            _boardUI.setFlipView(pValeur);
            _capturesUp.setWhite(!pValeur);
            _capturesDown.setWhite(pValeur);
        }
    }

    /**
     * Stocke la référence de l'éventuel fichier PGN de partie.
     *
     * @param pFichier Fichier PGN de partie (peut être à null).
     */
    @Override
    public void setGameFile(final File pFichier) {
        _gameFile = pFichier;
        getAction(SAVE_GAME_ACTION).setEnabled(_gameFile != null);
    }

    /**
     * Stocke la référence de l'éventuel fichier FEN de position.
     *
     * @param pFichier Fichier FEN de position (peut être à null).
     */
    @Override
    public void setPositionFile(final File pFichier) {
        _positionFile = pFichier;
        getAction(SAVE_POSITION_ACTION).setEnabled(_positionFile != null);
    }

    /**
     * Alimente la valeur d'une propriété.
     *
     * @param pCle    Cle identifiant la propriété.
     * @param pValeur Valeur de la propriété.
     */
    @Override
    public void setProperty(final String pCle, final String pValeur) {
        _properties.setProperty(pCle, pValeur);
        if (CELLS_SIZE_PROPERTY.equals(pCle)) {
            _boardUI.setCellSideLength(Integer.parseInt(pValeur));
            resizeFrame();
        } else if (BOARD_LF_PROPERTY.equals(pCle)) {
            _boardUI.setBoardLF(Integer.parseInt(pValeur));
        } else if (PIECES_LF_PROPERTY.equals(pCle)) {
            _boardUI.setPiecesLF(Integer.parseInt(pValeur));
        }
    }

    /**
     * Lance l'interface graphique.
     *
     * @see UI#start()
     */
    @Override
    public void start() {
        final JFrame fenetre = getMainFrame();
        fenetre.setLocationRelativeTo(null);
        fenetre.setVisible(true);

        while (true) {
            while (_game.getState() == IN_PROGRESS) {
                _lastMove = null;

                final MoveGenerator plateau = _game.getBoard();
                final Engine ia = _game.getPlayer(plateau.isWhiteActive())
                        .getEngine();
                if (ia == null) {
                    _boardUI.setEnabled(true);
                    waitForMove();
                } else {
                    _boardUI.setEnabled(false);
                    _lastMove = ia.getMoveFor(plateau);
                }

                if (_activeSample != null) {
                    _activeSample.stop();
                    _activeSample = null;
                }
                if (_soundEnabled) {
                    _activeSample = MOVE_SOUND;
                    if (_activeSample != null) {
                        _activeSample.setFramePosition(0);
                        _activeSample.start();
                    }
                }

                if (ia != null) {
                    final long etime = ia.getElapsedTime();
                    final int hmc = ia.getHalfmoveCount();
                    LOG.info("{} : {} nodes in {}ms <=> {} nodes/s", ia.getClass().getSimpleName(), hmc, etime, (int) (1000.0 / etime * hmc));
                }

                _game.moveFromCurrent(_lastMove);
            }
            _boardUI.setEnabled(false);
            try {
                Thread.sleep(250);
            } catch (final InterruptedException e) {
                LOG.warn("UI thread interrupted.", e);
            }
        }
    }

    /**
     * Arrète l'interface utilisateur.
     *
     * @see UI#stop()
     */
    @Override
    public void stop() {
        saveProperties();

        System.exit(0);
    }

    /**
     * Attend que le joueur humain ait fait un mouvement.
     */
    private synchronized void waitForMove() {
        try {
            while (_lastMove == null) {
                wait();
            }
        } catch (final InterruptedException e) {
            LOG.warn("Move wait interrupted.", e);
        }
    }

    /**
     * Classe gérant les actions sur la fenêtre principale.
     */
    private static final class MainWindowAdapter extends WindowAdapter {
        /**
         * Action prenant en charge la fermeture de l'application.
         */
        private final Action _exitAction;

        /**
         * Instancie un nouvel écouteur d'actions sur la fenêtre.
         *
         * @param pAction Action "Quitter".
         */
        MainWindowAdapter(final Action pAction) {
            _exitAction = pAction;
        }

        /**
         * Gère les demandes de fermeture de la fenêtre.
         *
         * @param pEvent Evènement signalant la demande.
         */
        @Override
        public void windowClosing(final WindowEvent pEvent) {
            final ActionEvent ev = new ActionEvent(pEvent.getSource(),
                    pEvent.getID(), pEvent.toString());
            _exitAction.actionPerformed(ev);
        }
    }
}
