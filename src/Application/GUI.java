package Application;

import Maze.Board;
import Maze.Items.Key;
import RecordAndReplay.RecordGame;
import RecordAndReplay.Replay;
import Render.MazeRenderer;
import Render.Music;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;


/**
 * This class defines the GUI and application logic of the game.
 * This class creates and controls all Swing objects, including
 * JFrames, JButtons, JRadioButtons, JMenus, JMenuItems, etc.
 *
 * This class renders the application window that displays the time left to play,
 * the current level, the number of treasures that still need to be collected,
 * keys collected and chips collected in the inventory.
 *
 * In order to countdown the time and execute the game at the same time,
 * this class implements two Threads.
 *
 * It implements another thread (named animationThread) to draw the animation
 * before a game starts.
 *
 * @author Qing (Cecilia) Lu (Student ID: 300363602)
 *
 */

public class GUI extends JFrame implements ActionListener, KeyListener {

    /** Preferred Fonts in an array. */
    private static String[] preferredFonts = {"Arial", "Times New Roman"};

    /** Game menu. */
    private JMenu menuGame;

    /** Options menu. */
    private JMenu menuOptions;

    /** Level menu. */
    private JMenu menuLevel;

    /** Help menu. */
    private JMenu menuHelp;

    /** All the menu items in the menu bar. */
    private JMenuItem i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11;

    /** Menu bar. */
    private JMenuBar mb;

    /** left Inner Panel: board */
    private JPanel leftInnerPanel;

    /** right Inner Panel: information-display window. */
    private JPanel rightInnerPanel;

    /** board Canvas. */
    private BoardCanvas boardCanvas;

    /** Information canvas. */
    private InfoCanvas infoCanvas;

    /** A frame to show that the game pauses. */
    private JFrame jf_Pause;

    /** Record if the game has started. */
    private boolean gameStarted = false;

    /** Record if the game has paused. */
    private boolean gamePaused = false;

    /** Record if a game can be loaded from a ".json" file. */
    private boolean canLoadGame = false;

    /** Board of the game. */
    private Board board;

    /** Renderer of the board. */
    private MazeRenderer renderer;

    /** Default value of level is 1. */
    private int level = 1;

    /** Number of keys that have been collected. */
    int keysize = 0;

    /** Number of chips at the start of the game. */
    private int originalChipNumber = 0;

    /** Record the game. */
    private RecordGame recordGame = new RecordGame();

    /** This colour is used to draw the frame of the information-display window. */
    private Color Light_Gray = new Color(232, 232, 232);

    /** This colour is used to draw the frame of the information-display window. */
    private Color Dark_Gray = new Color(161, 161, 161);

    /** This colour is used to draw the frame of the information-display window. */
    private Color Gray = new Color(207, 207, 207);

    /** This colour is used to draw the frame of the information-display window. */
    private Color PauseColor = new Color(255, 106, 106);

    /** Another thread for time countdown and displaying. */
    private RunnableThread timeRuunableThread = null;

    /** Another thread to draw the loading screen animation before game starts. */
    private AnimationThread animationThread = null;

    /** The music it should play. */
    private Music music = new Music();

    /** If the music is muted, it is true; otherwise, it's false. */
    private boolean muteMusic = false;

    /** The name of the current music. */
    private String musicName = null;

    /** An replay object records information of the saved game. */
    private Replay replay = new Replay();

    /** If "Ctrl+X" are pressed, it becomes true. */
    private boolean exited = false; // false: save; true: exit

    /**
     * GUI constructor.
     */
    public GUI() {
        super("Chip's Challenge");

        // Game menu: New Game, Save Game, Load Game, Exit.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mb = new JMenuBar();
        menuGame = new JMenu("Game");
        i2 = new JMenuItem("New Game");
        i2.setToolTipText("Start a new game");
        i2.addActionListener(new MenuActionListener2());
        i7 = new JMenuItem("Save Game");
        i7.setToolTipText("Save current game");
        i7.addActionListener(new MenuActionListener3());
        i8 = new JMenuItem("Load Game");
        i8.setToolTipText("Load an existing game");
        i8.addActionListener(new MenuActionListener4());
        i4 = new JMenuItem("Exit");   // Stop running the program
        i4.setToolTipText("Exit current programming process");
        i4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        menuGame.add(i2);
        menuGame.add(i7);
        menuGame.add(i8);
        menuGame.add(i4);

        // Option menu: Pause, Mute Music, Unmute Music, End Current Game.
        menuOptions = new JMenu("Options");
        i10 = new JMenuItem("Pause");
        i10.setToolTipText("Pause current game");
        i10.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (gameStarted == true) {
                    pauseAndContinue();
                }
            }
        });
        i3 = new JMenuItem("End Current Game");
        i3.setToolTipText("End current game");
        i3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(gameStarted == false){ return; }
                gamePaused = true;
                timeRuunableThread.setPause(gamePaused);
                int saveOrNot = JOptionPane.showConfirmDialog(null, "Save current game?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                if(saveOrNot == 2){         // cancel
                    gamePaused = false;
                    timeRuunableThread.setPause(gamePaused);
                }else if (saveOrNot == 0) { // yes
                    // save current game
                    SaveCurrentGame();
                } else {                   // no
                    gamePaused = true;
                    timeRuunableThread.setPause(gamePaused);
                }
            }
        });
        i9 = new JMenuItem("Mute Game Music");
        i9.setToolTipText("Mute Game Music");
        i9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                muteMusic = true; // don't play music
                music.stopPlayingAudio("All");
            }
        });
        i11 = new JMenuItem("Unmute Game Music");
        i11.setToolTipText("Unmute Game Music");
        i11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                muteMusic = false;  // start to play music
                music.playAudio(musicName);
            }
        });
        menuOptions.add(i10);
        menuOptions.add(i9);
        menuOptions.add(i11);
        menuOptions.add(i3);

        // Level menu: Level 1, and Level 2. Start the corresponding level when clicking one of the two JMenuItems.
        menuLevel = new JMenu("Level");
        i5 = new JMenuItem("Level 1");
        i5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // board at level 1
                try {
                    startGameByLevel(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        i6 = new JMenuItem("Level 2");
        i6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // board at level 2
                try {
                    startGameByLevel(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        menuLevel.add(i5);
        menuLevel.add(i6);

        // Help menu: Instruction of the game.
        menuHelp = new JMenu("Help");
        i1 = new JMenuItem("Instruction"); // instruction
        i1.setToolTipText("Basic instructions for the game");
        i1.addActionListener(new MenuActionListener1());
        menuHelp.add(i1);
        mb.add(menuGame);
        mb.add(menuOptions);
        mb.add(menuLevel);
        mb.add(menuHelp);
        setJMenuBar(mb);

        // Board canvas on the left.
        boardCanvas = new BoardCanvas();

        // Add mouse listener to the board canvas.
        boardCanvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(gameStarted == false){return; }
                int x = e.getX();
                int y = e.getY();
                int col = x / (boardCanvas.getWidth() / 9) + renderer.getCurrentAbsoluteTopColumn();
                int row = y / (boardCanvas.getHeight() / 9) + renderer.getCurrentAbsoluteLeftRow();
                boolean moved = board.moveByMouse(row, col);
                if(moved){
                    checkMovedRedraw();
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) { }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) { }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) { }

            @Override
            public void mouseExited(MouseEvent mouseEvent) { }
        });

        // Add key listener to the board canvas.
        boardCanvas.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    KeyListenerAdded(e);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) { }
        });

        // Left panel to contain the board canvas.
        leftInnerPanel = new JPanel();
        leftInnerPanel.setLayout(new BorderLayout());
        Border cb = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(100, 100, 100, 100), BorderFactory
                .createLineBorder(Color.white));
        leftInnerPanel.setBorder(cb);
        leftInnerPanel.add(boardCanvas, BorderLayout.CENTER);

        // Right panel for the information-display window.
        rightInnerPanel = new JPanel(); //new GridLayout(0, 1, 50, 380)
        rightInnerPanel.setLayout(new BorderLayout());
        cb = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(100, 100, 100, 100), BorderFactory
                .createLineBorder(Color.white));
        rightInnerPanel.setBorder(cb);
        // Side bar to show information of the game.
        infoCanvas = new InfoCanvas();
        // Add key listener to the SideBar.
        infoCanvas.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    KeyListenerAdded(e);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) { }
        });
        rightInnerPanel.setLayout(new GridLayout(1, 2));
        rightInnerPanel.add(infoCanvas);

        // Add panels onto current frame.
        add(leftInnerPanel, BorderLayout.CENTER);
        add(rightInnerPanel, BorderLayout.EAST);


        // Add key listener to the current frame.
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    KeyListenerAdded(e);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) { }
        });

        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setResizable(false);
        boardCanvas.drawAnimationAtStart();
    }

    /**
     * Initialise fields' values to start a new game.
     *
     * @param level the level to be played at.
     */
    public void newGameStart(int level) {
        animationThread.stop();
        if(muteMusic == false){
            music.stopPlayingAudio("All");
            music.playAudio("LevelMusic");
        }
        musicName = "LevelMusic";
        setTitle("Chip's Challenge: Level " + level);
        replay.clearMovement();
        exited = false;
        timeRuunableThread.setDrawNumber(true);
        timeRuunableThread.setSecond(101);
        gameStarted = true;
        gamePaused = false;
        timeRuunableThread.setPause(gamePaused);
        timeRuunableThread.setGameStart(gameStarted);
        infoCanvas.drawLevelNumber(level);
        board = new Board(level);
        keysize = 0;
        originalChipNumber = board.getTreasureRemainingAmount();
        infoCanvas.drawChipsLeftNumber(board.getTreasureRemainingAmount());
        infoCanvas.drawSquares((Graphics2D) infoCanvas.getGraphics(), 14 * infoCanvas.getHeight() / 20, infoCanvas.getWidth(), infoCanvas.getHeight());
        renderer = new MazeRenderer(boardCanvas.getGraphics());
        renderer.redraw(board, boardCanvas.getWidth(),  boardCanvas.getHeight());
    }


    /**
     * Start a new by clicking the level in the menu bar.
     * @param level the level picked.
     * @throws InterruptedException exception for thread.sleep.
     */
    public void startGameByLevel(int level) throws InterruptedException {
        if(gameStarted == false){
            animationThread.stop();
            JOptionPane.showMessageDialog(null, "Level " + level + "! Go!", "Message", JOptionPane.INFORMATION_MESSAGE);
            Thread.sleep(100);
            timeRuunableThread = new RunnableThread("Time", infoCanvas.getGraphics(), infoCanvas.getWidth(), infoCanvas.getHeight(), gui);
            Thread timeTread = new Thread(timeRuunableThread);
            timeTread.start();
        }
        this.level = level;
        setTitle("Chip's Challenge: Level " + level);
        newGameStart(level);
    }

    /**
     * When the game pauses, pop up a window to show the player a message.
     * Player can either click the "Continue" button
     *       or press "ESC" key to continue the game.
     */
    public void pauseAndContinue(){
        if(gamePaused == true){ return; }
        gamePaused = true;
        timeRuunableThread.setPause(gamePaused);
        music.stopPlayingAudio("All");
        jf_Pause = new JFrame("Game is paused");
        jf_Pause.setSize(new Dimension(350, 120));
        jf_Pause.setLocation(400, 400);
        jf_Pause.setBackground(PauseColor);
        jf_Pause.setLayout(new GridLayout(2, 1));
        JLabel jl_Pause = new JLabel(" Game is paused; click button or press ESC to continue.");
        JPanel jp_Pause = new JPanel();
        JButton jb_Pause = new JButton("Continue");
        jb_Pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gamePaused = false;
                if(muteMusic == false){
                    music.playAudio("LevelMusic");
                }
                musicName = "LevelMusic";
                timeRuunableThread.setPause(gamePaused);
                jf_Pause.dispose();
            }
        });

        jb_Pause.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) { }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    gamePaused = false;
                    timeRuunableThread.setPause(gamePaused);
                    jf_Pause.dispose();
                    if(muteMusic == false){
                        music.playAudio(musicName);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) { }
        });
        jp_Pause.add(jb_Pause);
        jf_Pause.add(jl_Pause);
        jf_Pause.add(jp_Pause);
        jf_Pause.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    gamePaused = false;
                    timeRuunableThread.setPause(gamePaused);
                    jf_Pause.dispose();
                }
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) { }

            @Override
            public void keyReleased(KeyEvent keyEvent) { }
        });
        jf_Pause.setVisible(true);
        jf_Pause.setFocusable(false);
    }

    /**
     * Read the txt file Instruction.txt and return a string.
     *
     * @return content of the txt file.
     */
    public String readAllLines() {
        String ans = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("./assets/Instruction.txt"));
            String str;
            while ((str = br.readLine()) != null) {
                ans += (str + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        assert ans != null;
        return ans;
    }

    /**
     * When clicking the menu item "Instruction" in "Help" in the menu bar,
     * pop up a window and show game rules.
     */
    class MenuActionListener1 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, readAllLines(), "Game Instruction", JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * Replay the current level when time runs out.
     */
    public void ReplayCurrentLevel(){
        gamePaused = true;
        timeRuunableThread.setPause(gamePaused);
        if(muteMusic == false){
            music.stopPlayingAudio("All");
            music.playAudio("TimeoutSound");
        }
        musicName = "TimeoutSound";
        JOptionPane.showMessageDialog(null, "Oops! Time out. Please replay current level.", "Timeout", JOptionPane.INFORMATION_MESSAGE);
        newGameStart(level);
    }

    /**
     * Pass the reference of current GUI to gui variable.
     */
    GUI gui = this;

    /**
     * This is an action listener of the New Game in the menu bar.
     * When clicking New Game, it will:
     *    resume from the last unfinished level if the user clicked "Ctrl - X";
     *    start a new game by asking for a level if the user didn't save game or exit from a game;
     *    replay the saved game if the user has saved a game;
     *    otherwise, ask the user to save the current game or not.
     *       If yes, save it and replay the game again;
     *       if no, start a new game by selecting a level.
     */
    class MenuActionListener2 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(gamePaused == true && exited == true){
                animationThread.stop();
                JOptionPane.showMessageDialog(null, "Resume from the last unfinished level: " + level, "Message", 1);
                newGameStart(level);
                exited = false;
            } else if ((gameStarted == false || gamePaused == true) && canLoadGame == false) {
                animationThread.stop();
                selectLevel();
            } else if(gamePaused == true && canLoadGame == true && canLoadGame == true){
                animationThread.stop();
                JOptionPane.showMessageDialog(null, "Resume saved game!", "Message", 1);
                // loadGame();
                replayGame();
                canLoadGame = false;
            } else {
                gamePaused = true;
                timeRuunableThread.setPause(gamePaused);
                // ask the user to save current game
                int saveOrNot = JOptionPane.showConfirmDialog(null, "Save current game?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                if(saveOrNot == 2){ // cancel
                    gamePaused = false;
                    timeRuunableThread.setPause(gamePaused);
                }else if (saveOrNot == 0) { // yes
                    // save current game
                    SaveCurrentGame();
                } else {
                    gamePaused = true;
                    timeRuunableThread.setPause(gamePaused);
                    selectLevel(); // start a new game
                }
            }
        }
    }

    /**
     * Pop up a window to allow the player to pick a level.
     */
    public void selectLevel(){
        // allows the player to pick a level
        JFrame jframe = new JFrame("Level");

        // Message label
        JLabel jlabel = new JLabel("    Please select the game level: ");

        // RatioButton panel
        JPanel jp1 = new JPanel();
        JRadioButton jrb1 = new JRadioButton("Level 1");
        JRadioButton jrb2 = new JRadioButton("Level 2");
        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(jrb1);
        bg1.add(jrb2);
        jp1.add(jrb1);
        jp1.add(jrb2);
        jp1.setLayout(new GridLayout(2, 1));
        jp1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Level"));

        // Confirm button's panel
        JPanel jp2 = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String suggestLevel = getSelectedButtonText(bg1);
                if(suggestLevel == null){ return; }
                if(suggestLevel.equals("Level 1")){
                    level = 1;
                }else if(suggestLevel.equals("Level 2")){
                    level = 2;
                }
                jframe.dispose();
                if(gameStarted == false){
                    timeRuunableThread = new RunnableThread("Time", infoCanvas.getGraphics(), infoCanvas.getWidth(), infoCanvas.getHeight(), gui);
                    Thread timeTread = new Thread(timeRuunableThread);
                    timeTread.start();
                }
                setTitle("Chip's Challenge: Level " + level);
                // start to count down time
                newGameStart(level);
            }
        });

        jp2.add(okButton);
        jframe.setLayout(new GridLayout(3, 1));
        jframe.setSize(new Dimension(330, 330));
        jframe.add(jlabel);
        jframe.add(jp1);
        jframe.add(jp2);
        jframe.setLocation(300, 300);
        jframe.setVisible(true);
    }

    /**
     * Get the returned value of a button group.
     *
     * @param buttonGroup a bunch of buttons.
     * @return the name of the button selected.
     */
    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    /**
     * This is an action listener of Save Game in the menu bar.
     * It will save current game if the user clicks Save Game in menu bar during a game.
     */
    class MenuActionListener3 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(gameStarted == false || canLoadGame == true){
                return;
            }
            SaveGame();
        }
    }

    /**
     * This is an action listener of Load Game in the menu bar.
     * It will load the saved game if there is any.
     */
    class MenuActionListener4 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(canLoadGame == false){
                return;
            }
            replayGame();
        }
    }

    /**
     * If a game has been saved, it replays the game.
     * It allows the user to pick the way of replaying:
     *      step-by-step, auto-reply, and set replay speed.
     */
    public void replayGame(){
        animationThread.stop();
        // allows the player to control replay
        JFrame jframe = new JFrame("Replay Mode");
        // Message label
        JLabel jlabel = new JLabel("    Please select replay mode: ");

        // RatioButton panel
        JPanel jp1 = new JPanel();
        JRadioButton jrb1 = new JRadioButton("Step-By-Step");
        JRadioButton jrb2 = new JRadioButton("Auto-Replay");
        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(jrb1);
        bg1.add(jrb2);
        jp1.add(jrb1);
        jp1.add(jrb2);
        jp1.setLayout(new GridLayout(2, 1));
        jp1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Replay"));

        // Confirm button's panel
        JPanel jp2 = new JPanel();
        JButton okButton = new JButton("Next");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String suggestLevel = getSelectedButtonText(bg1);
                if(suggestLevel == null){ return; }
                jframe.dispose();
                if(suggestLevel.equals("Step-By-Step")){
                    SelectReplaySpeed();
                }else if(suggestLevel.equals("Auto-Replay")){
                    loadGame();
                }
                canLoadGame = false;
                board.clearMoves();
                replay.clearMovement();
            }
        });

        jp2.add(okButton);
        jframe.setLayout(new GridLayout(3, 1));
        jframe.setSize(new Dimension(330, 330));
        jframe.add(jlabel);
        jframe.add(jp1);
        jframe.add(jp2);
        jframe.setLocation(300, 300);
        jframe.setVisible(true);
    }

    /**
     * When the user selects "Step by Step" to replay the game,
     * there are three options for the replay speed:
     *     1. Slow: no thread sleep between movements;
     *     2. Medium: thread sleeps for 500 milliseconds between movements;
     *     3. Fast: thread sleeps for 1000 milliseconds between movements.
     */
    public void SelectReplaySpeed(){
        JFrame jframe = new JFrame("Replay Speed");
        // Message label
        JLabel jlabel = new JLabel("    Please select replay speed: ");
        // RatioButton panel
        JPanel jp1 = new JPanel();
        JRadioButton jrb1 = new JRadioButton("Slow");
        JRadioButton jrb2 = new JRadioButton("Medium");
        JRadioButton jrb3 = new JRadioButton("Fast");
        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(jrb1);
        bg1.add(jrb2);
        bg1.add(jrb3);
        jp1.add(jrb1);
        jp1.add(jrb2);
        jp1.add(jrb3);
        jp1.setLayout(new GridLayout(2, 1));
        jp1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Replay Speed"));

        // Confirm button's panel
        JPanel jp2 = new JPanel();
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String suggestLevel = getSelectedButtonText(bg1);
                if(suggestLevel == null){ return; }
                jframe.dispose();
                if(suggestLevel.equals("Slow")){
                    replay.setReplaySpeed(1000);
                }else if(suggestLevel.equals("Medium")){
                    replay.setReplaySpeed(500);
                }else if(suggestLevel.equals("Fast")){
                    replay.setReplaySpeed(0);
                }
                replayByStep();
            }
        });

        jp2.add(okButton);
        jframe.setLayout(new GridLayout(3, 1));
        jframe.setSize(new Dimension(330, 330));
        jframe.add(jlabel);
        jframe.add(jp1);
        jframe.add(jp2);
        jframe.setLocation(300, 300);
        jframe.setVisible(true);
    }

    /**
     * When the user selects auto-replay, it will not show the previous movements.
     * The hero is directly placed at the position when the game is saved.
     */
    public void loadGame(){
        if(gamePaused == false){ return; }
        // animationThread.stop();
        // JOptionPane.showMessageDialog(null, "Game Loaded", "Message", 1);
        replay.load();
        board = replay.autoReplay();
        level = replay.getLevel();
        if(muteMusic == false){
            music.stopPlayingAudio("All");
            music.playAudio("LevelMusic");
        }
        musicName = "LevelMusic";
        setTitle("Chip's Challenge: Level " + level);
        timeRuunableThread.setDrawNumber(true);
        timeRuunableThread.setSecond(replay.getTime());
        gameStarted = true;
        gamePaused = false;
        timeRuunableThread.setPause(gamePaused);
        timeRuunableThread.setGameStart(gameStarted);
        infoCanvas.drawLevelNumber(level);
        keysize = 0;
        infoCanvas.drawChipsLeftNumber(board.getTreasureRemainingAmount());
        infoCanvas.drawSquares((Graphics2D) infoCanvas.getGraphics(), 14 * infoCanvas.getHeight() / 20, infoCanvas.getWidth(), infoCanvas.getHeight());
        try {
            infoCanvas.drawKeysChipsPics();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        renderer = new MazeRenderer(boardCanvas.getGraphics());
        renderer.redraw(board, boardCanvas.getWidth(),  boardCanvas.getHeight());
        checkMovedRedraw();
    }

    /**
     * When the user selects Step by step replay mode,
     * it will show the previous movements in the selected speed.
     */
    public void replayByStep(){
        if(gamePaused == false){ return; }
        // animationThread.stop();
        replay.load();
        level = replay.getLevel();
        board = new Board(level);
        if(muteMusic == false){
            music.stopPlayingAudio("All");
            music.playAudio("LevelMusic");
        }
        musicName = "LevelMusic";
        setTitle("Chip's Challenge: Level " + level);
        timeRuunableThread.setDrawNumber(true);
        timeRuunableThread.setSecond(replay.getTime());
        infoCanvas.drawLevelNumber(level);
        keysize = 0;
        originalChipNumber = board.getTreasureRemainingAmount();
        infoCanvas.drawChipsLeftNumber(board.getTreasureRemainingAmount());
        infoCanvas.drawSquares((Graphics2D) infoCanvas.getGraphics(), 14 * infoCanvas.getHeight() / 20, infoCanvas.getWidth(), infoCanvas.getHeight());
        try {
            infoCanvas.drawKeysChipsPics();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        renderer = new MazeRenderer(boardCanvas.getGraphics());
        renderer.redraw(board, boardCanvas.getWidth(),  boardCanvas.getHeight());
        for(String s : replay.getMovement()) {
            // board.moveChip(s);
            board = replay.replayByStep(board);
            checkMovedRedraw();
            replay.selectReplaySpeed();
        }
        gameStarted = true;
        gamePaused = false;
        timeRuunableThread.setPause(gamePaused);
        timeRuunableThread.setGameStart(gameStarted);
    }

    /**
     * This class defines the board canvas.
     * The board canvas could always be divided by 9, no matter how large or small the screen is.
     *
     */
    private class BoardCanvas extends Canvas {
        /**
         * Font.
         */
        private Font font;

        /**
         * A constructor.
         * When constructing a board canvas, initialise a Board object and MazeRenderer object.
         */
        public BoardCanvas() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int height1 = screenSize.height * 9 / 14;
            int remainNum = height1 % 9;
            int heightBoard = height1 - remainNum;
            setBounds(0, 0, heightBoard, heightBoard);
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            HashSet<String> availableNames = new HashSet();
            for (String name : env.getAvailableFontFamilyNames()) {
                availableNames.add(name);
            }
            for (String pf : preferredFonts) {
                if (availableNames.contains(pf)) {
                    font = new Font(pf, Font.PLAIN, 20);
                    break;
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            // super.paint(g);
        }

        /**
         * Draw the animation pictures before a game get started.
         */
        public void drawAnimationAtStart(){
            animationThread = new AnimationThread(this.getGraphics(), getWidth(), getHeight());
            animationThread.start();
            music.playAudio("LoadingScreenMusic");
            musicName = "LoadingScreenMusic";
        }
    }

    /**
     * This class defines the information-display canvas.
     * It displays the time left to play, the current level,
     * the number of treasures that still need to be collected,
     * keys and chips collected in the inventory.
     * It is 3D looking.
     *
     * The time left to play is controlled by another thread (timeRunnableThread).
     *
     */
    private class InfoCanvas extends Canvas {
        private Font font;

        /**
         * Constructor.
         */
        public InfoCanvas() {
            setBounds(0, 420, 400, 100);
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            HashSet<String> availableNames = new HashSet();

            for (String name : env.getAvailableFontFamilyNames()) {
                availableNames.add(name);
            }

            for (String pf : preferredFonts) {
                if (availableNames.contains(pf)) {
                    font = new Font(pf, Font.PLAIN, 20);
                    break;
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;
            int wid = infoCanvas.getWidth();
            int hei = infoCanvas.getHeight();
            // draw infoCanvas background
            drawBackground(g2d, wid, hei);
            // draw four "pushpins"
            drawPushpins(g, wid, hei);
            // draw Strings
            drawStrings(g, wid, hei);
            // draw black boxes
            drawBox(g2d, hei / 10 + 6, wid, hei);
            drawBox(g2d, hei / 10 * 3 + 6, wid, hei);
            drawBox(g2d, 11 * hei / 20 + 11, wid, hei);
            // drawSquares
            drawSquares(g2d, 14 * hei / 20, wid, hei);
        }

        /**
         * Draw the frame of the canvas.
         * @param g2d graphics for drawing.
         * @param wid width of the canvas.
         * @param hei height of the canvas.
         */
        private void drawBackground(Graphics2D g2d, int wid, int hei) {
            g2d.setColor(Light_Gray);
            Triangle triangle1 = new Triangle(new Point(0, 0), new Point(0, hei), new Point(wid, 0));
            triangle1.fill(g2d);
            g2d.setColor(Dark_Gray);
            Triangle triangle2 = new Triangle(new Point(wid, hei), new Point(0, hei), new Point(wid, 0));
            triangle2.fill(g2d);
            Triangle triangle3 = new Triangle(new Point(wid, 0), new Point(wid, 15), new Point(wid - 15, 15));
            triangle3.fill(g2d);
            g2d.setColor(Light_Gray);
            Triangle triangle4 = new Triangle(new Point(0, hei), new Point(0, hei - 15), new Point(15, hei - 15));
            triangle4.fill(g2d);
            g2d.setColor(Gray);
            g2d.fillRect(6, 6, wid - 12, hei - 12);
        }

        /**
         * Draw the four pushpins on the four corners.
         * @param g graphics for drawing.
         * @param wid width of the canvas.
         * @param hei height of the canvas.
         */
        private void drawPushpins(Graphics g, int wid, int hei) {
            g.setColor(Color.black);
            g.drawOval(8, 8, 14, 14);
            g.drawOval(wid - 22, 8, 14, 14);
            g.drawOval(8, hei - 22, 14, 14);
            g.drawOval(wid - 22, hei - 22, 14, 14);
            g.drawLine(8, 15, 22, 15);
            g.drawLine(wid - 20, 18, wid - 10, 13);
            g.drawLine(10, hei - 17, 20, hei - 12);
            g.drawLine(wid - 22, hei - 15, wid - 8, hei - 15);
        }

        /**
         * Draw the Strings: LEVEL, TIME, and CHIPS LEFT.
         * @param g graphics for drawing.
         * @param wid width of the canvas.
         * @param hei height of the canvas.
         */
        private void drawStrings(Graphics g, int wid, int hei) {
            Font f = new Font("Font.PLAIN", Font.BOLD, hei / 20);
            g.setFont(f);
            g.setColor(Color.white);
            g.drawString("LEVEL", wid / 2 - 3 * wid / 20 - 2, hei / 10 - 2);
            g.drawString("TIME", wid / 2 - 3 * wid / 20 + 5, hei / 10 * 3 - 2);
            g.drawString("CHIPS", wid / 2 - 3 * wid / 20 - 2, hei / 2 - 2);
            g.drawString("LEFT", wid / 2 - 3 * wid / 20 + hei / 63 - 2, hei / 2 + hei / 20 + 3);
            g.setColor(Color.black);
            g.drawString("LEVEL", wid / 2 - 3 * wid / 20 + 2, hei / 10 + 2);
            g.drawString("TIME", wid / 2 - 3 * wid / 20 + 9, hei / 10 * 3 + 2);
            g.drawString("CHIPS", wid / 2 - 3 * wid / 20 + 2, hei / 2 + 2);
            g.drawString("LEFT", wid / 2 - 3 * wid / 20 + hei / 63 + 2, hei / 2 + hei / 20 + 7);
            g.setColor(Color.red);
            g.drawString("LEVEL", wid / 2 - 3 * wid / 20, hei / 10);
            g.drawString("TIME", wid / 2 - 3 * wid / 20 + 7, hei / 10 * 3);
            g.drawString("CHIPS", wid / 2 - 3 * wid / 20, hei / 2);
            g.drawString("LEFT", wid / 2 - 3 * wid / 20 + hei / 63, hei / 2 + hei / 20 + 5);
        }

        /**
         * Draw the number-display box.
         * @param g2d graphics for drawing.
         * @param wid width of the canvas.
         * @param hei height of the canvas.
         */
        private void drawBox(Graphics2D g2d, int y, int wid, int hei) {
            g2d.setColor(Dark_Gray);
            Triangle triangle1 = new Triangle(new Point(wid / 3, y), new Point(2 * wid / 3, y), new Point(wid / 3, y + hei / 10));
            triangle1.fill(g2d);
            g2d.setColor(Light_Gray);
            Triangle triangle2 = new Triangle(new Point(2 * wid / 3, y + hei / 10), new Point(2 * wid / 3, y), new Point(wid / 3, y + hei / 10));
            triangle2.fill(g2d);
            Triangle triangle3 = new Triangle(new Point(wid / 3, y + hei / 10), new Point(wid / 3 + 8, y + hei / 10), new Point(wid / 3 + 8, y + hei / 10 - 8));
            triangle3.fill(g2d);
            g2d.setColor(Dark_Gray);
            Triangle triangle4 = new Triangle(new Point(2 * wid / 3, y), new Point(2 * wid / 3 - 8, y), new Point(2 * wid / 3 - 8, y + 8));
            triangle4.fill(g2d);
            g2d.setColor(Color.BLACK);
            g2d.fillRect(wid / 3 + 4, y + 4, wid / 3 - 8, hei / 10 - 8);
        }

        /**
         * Draw the eight squares to show the chips left.
         * @param g2d graphics for drawing.
         * @param wid width of the canvas.
         * @param hei height of the canvas.
         */
        private void drawSquares(Graphics2D g2d, int startY, int wid, int hei) {
            int size = hei / 16;
            int startX = wid / 2 - size * 3;
            for (int i = 0; i < 6; i++) {
                drawSquare(g2d, startX + size * i, startY, size);
                drawSquare(g2d, startX + size * i, startY + size, size);
                drawSquare(g2d, startX + size * i, startY + size * 2, size);
                drawSquare(g2d, startX + size * i, startY + size * 3, size);
            }
        }

        /**
         * Draw each square.
         * @param g2d graphics for drawing.
         * @param x x axis of the square.
         * @param y y axis of the square.
         * @param size size of the square.
         */
        private void drawSquare(Graphics2D g2d, int x, int y, int size) {
            g2d.setColor(Light_Gray);
            Triangle triangle1 = new Triangle(new Point(x, y), new Point(x + size, y), new Point(x, y + size));
            triangle1.fill(g2d);
            g2d.setColor(Dark_Gray);
            Triangle triangle2 = new Triangle(new Point(x + size, y + size), new Point(x + size, y), new Point(x, y + size));
            triangle2.fill(g2d);
            g2d.setColor(Gray);
            g2d.fillRect(x + 2, y + 2, size - 4, size - 4);
        }

        /**
         * Draw the keys and chips collected in the inventory.
         *
         * @throws IOException Throw an exception when errors occur for thread.sleep.
         */
        public void drawKeysChipsPics() throws IOException {
            Graphics g = infoCanvas.getGraphics();

            // draw keys collected.
            List<Key> keys = board.getChip().getKeys();
            int size = infoCanvas.getHeight() / 16;
            int startX = infoCanvas.getWidth() / 2 - size * 3;
            for (int i = 0; i < 5; i++) {
                drawSquare((Graphics2D) g, startX + size * i, 14 * infoCanvas.getHeight() / 20, size);
            }
            if(keys.size() > 0){
                for(int i = 0; i < keys.size(); i++){
                    Color color = keys.get(i).getColour();
                    String pathName = "./assets/Items/";
                    if(color.equals(Color.red)){
                        pathName += "KeyRed.png";
                    }else if(color.equals(Color.blue)){
                        pathName += "KeyBlue.png";
                    }else if(color.equals(Color.green)){
                        pathName += "KeyGreen.png";
                    }
                    BufferedImage bi = ImageIO.read(new File(pathName));
                    g.drawImage(bi, startX + size * i + 2, 14 * infoCanvas.getHeight()/20 + 2, size - 4, size -4, null);
                }
            }

            // draw chips collected.
            int chipsNum = originalChipNumber - board.getTreasureRemainingAmount();
            BufferedImage bi = ImageIO.read(new File("./assets/Items/Treasure.png"));
            if(chipsNum > 0){
                int lineNumber = chipsNum / 6;
                int remainingInLastLine = chipsNum % 6;
                if(lineNumber > 0){
                    for(int i = 0; i < lineNumber; i++){
                        for(int j = 0; j < 6; j++){
                            g.drawImage(bi, startX + size * j + 2, 14 * infoCanvas.getHeight()/20 + size * (i + 1) + 2, size - 4, size -4, null);
                        }
                    }
                }
                for(int i = 0; i < remainingInLastLine; i++){
                    g.drawImage(bi, startX + size * i + 2, 14 * infoCanvas.getHeight()/20 + size * (1 + lineNumber) + 2, size - 4, size -4, null);
                }
            }
        }

        /**
         * Draw the level number on the infoCanvas.
         * @param lvl the current game level
         */
        public void drawLevelNumber(int lvl){
            Graphics2D g2d = (Graphics2D) infoCanvas.getGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(infoCanvas.getWidth() / 3 + 4, infoCanvas.getHeight() / 10 + 6, infoCanvas.getWidth() / 3 - 8, infoCanvas.getHeight() / 10 - 8);
            Font f = new Font("Font.PLAIN", Font.BOLD, infoCanvas.getHeight() / 12);
            g2d.setFont(f);
            g2d.setColor(Color.GREEN);
            int startX = infoCanvas.getWidth() / 3 + 4;
            int startY = infoCanvas.getHeight() / 10 + 10;
            int stringX = startX + (infoCanvas.getWidth() / 3 - 8)/3 * 2;
            g2d.drawString(String.valueOf(lvl), stringX, startY + infoCanvas.getHeight() / 10 - 18);
        }

        /**
         * Draw the number of chips left on the board.
         * @param chipsNum the number of chips left on the board
         */
        public void drawChipsLeftNumber(int chipsNum){
            Graphics2D g2d = (Graphics2D) infoCanvas.getGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(infoCanvas.getWidth() / 3 + 4, 11 * infoCanvas.getHeight() / 20 + 11 , infoCanvas.getWidth() / 3 - 8, infoCanvas.getHeight() / 10 - 8);
            Font f = new Font("Font.PLAIN", Font.BOLD, infoCanvas.getHeight() / 12);
            g2d.setFont(f);
            g2d.setColor(Color.GREEN);
            int startX = infoCanvas.getWidth() / 3 + 4;
            int startY = 11 * infoCanvas.getHeight() / 20 + 15;
            int stringX = startX + (infoCanvas.getWidth() / 3 - 8)/3;
            g2d.drawString(String.valueOf(chipsNum), stringX, startY + infoCanvas.getHeight() / 10 - 18);
        }
    }

    /**
     * Save current game and redraw the BoardCanvas and InfoCanvas.
     */
    public void SaveGame(){
        if(gameStarted == false  || canLoadGame == true){ return; }
        gamePaused = true;
        timeRuunableThread.setPause(gamePaused);
        int input = JOptionPane.showOptionDialog(null, "Do you want to save game?", "Save Game", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        if(input == JOptionPane.OK_OPTION) {
            SaveCurrentGame();
            animationThread.start();
            if(muteMusic == false){
                music.stopPlayingAudio("All");
                music.playAudio("LoadingScreenMusic");
            }
            musicName = "LoadingScreenMusic";
        }else{
            gamePaused = false;
            timeRuunableThread.setPause(gamePaused);
        }
    }

    /**
     * Save current game by recording game's information.
     * Also, the information in the side bar will be cleaned.
     */
    public void SaveCurrentGame(){
        if(gameStarted == false){ return; }
        recordGame.write(board, timeRuunableThread, level);
        timeRuunableThread.setDrawNumber(false);
        canLoadGame = true;
        // update infoCanvas
        CleanInfoCanvas();
    }

    /**
     * Clean the side bar by redrawing the black boxes and inventory.
     */
    public void CleanInfoCanvas(){
        Graphics g = infoCanvas.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(infoCanvas.getWidth()/3 + 4, infoCanvas.getHeight()/10 + 10, infoCanvas.getWidth()/3-8, infoCanvas.getHeight()/10-8);
        g.fillRect(infoCanvas.getWidth()/3 + 4, 3 * infoCanvas.getHeight()/10 + 10, infoCanvas.getWidth()/3-8, infoCanvas.getHeight()/10-8);
        g.fillRect(infoCanvas.getWidth()/3 + 4, 11 * infoCanvas.getHeight()/20 + 15, infoCanvas.getWidth()/3-8, infoCanvas.getHeight()/10-8);
        infoCanvas.drawSquares((Graphics2D) infoCanvas.getGraphics(), 14 * infoCanvas.getHeight() / 20, infoCanvas.getWidth(), infoCanvas.getHeight());
    }


    /**
     * Add key listener to the whole frame.
     * @param e key press event.
     * @throws InterruptedException throw exception if there is any error when thread is sleeping.
     */
    public void KeyListenerAdded(KeyEvent e) throws InterruptedException {
        char keyPressed = e.getKeyChar();
        // CTRL-1: start a new game at level 1
        if(keyPressed == '1' || (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_1)){
            startGameByLevel(1);
        }

        if(gameStarted == false){return; }
        String str = "";
        boolean moved= false;

        // Press wasd or UP, DOWN, LEFT, RIGHT ARROWS.
        if (keyPressed == 'w' || keyPressed == 'W' || e.getKeyCode() == KeyEvent.VK_UP) {
            str = "up";
            moved = board.moveChip(str);
            if(moved == false && muteMusic == false){
                music.playAudio("ErrorSound");
            }
        } else if (keyPressed == 's' || keyPressed == 'S' || e.getKeyCode() == KeyEvent.VK_DOWN) {
            str = "down";
            moved = board.moveChip(str);
            if(moved == false && muteMusic == false){
                music.playAudio("ErrorSound");
            }
        } else if (keyPressed == 'a' || keyPressed == 'A' || e.getKeyCode() == KeyEvent.VK_LEFT) {
            str = "left";
            moved = board.moveChip(str);
            if(moved == false && muteMusic == false){
                music.playAudio("ErrorSound");
            }
        } else if (keyPressed == 'd' || keyPressed == 'D' || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            str = "right";
            moved = board.moveChip(str);
            if(moved == false && muteMusic == false){
                music.playAudio("ErrorSound");
            }
        }
        if(moved){
            checkMovedRedraw();
        }
        // CTRL-X:
        if(keyPressed == 'x' || keyPressed == 'X' || (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_X)){
            if(gameStarted == false) { return; }
            exited = true;
            gamePaused = true;
            timeRuunableThread.setPause(gamePaused);
            timeRuunableThread.setDrawNumber(false);
            if(muteMusic == false){
                music.stopPlayingAudio("All");
                music.playAudio("LoadingScreenMusic");
            }
            musicName = "LoadingScreenMusic";
            animationThread.start();

            // clean the infoCanvas
            CleanInfoCanvas();
        }

        // CTRL-S: exit the game, saves the game state, game will resume next time the application will be started
        if((e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S)){
            SaveGame();
        }

        // CTRL-R: resume a saved game
        if(keyPressed == 'r' || keyPressed == 'R' || (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R)){
            if(canLoadGame == false){ return; }
            // loadGame();
            replayGame();
        }

        // CTRL-P: start a new game at the last unfinished level
        if(keyPressed == 'p' || keyPressed == 'P' || (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_P)){
            animationThread.stop();
            Thread.sleep(100);
            JOptionPane.showMessageDialog(null, "Resume from the last unfinished level: " + level, "Message", 1);
            newGameStart(level);
        }

        // SPACE: pause and continue
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            pauseAndContinue();
        }

        // ESC: escape from pause
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            gamePaused = false;
            timeRuunableThread.setPause(gamePaused);
            jf_Pause.dispose();
        }
    }

    /**
     * When the hero is not alive, pop up a window to inform game over.
     * When the chip has moved, but not onto an exit tile, redraw chips and keys on infoCanvas.
     * When the current level is passed, ask whether to go to the next level.
     * When the hero moves, update the inventory.
     */
    public void checkMovedRedraw(){
        if((board.isTreasureCollected() || board.isKeyCollected()) && muteMusic == false){
            music.playAudio("PickupSound");
        }
        renderer.redraw(board, boardCanvas.getWidth(),  boardCanvas.getHeight());
        if(board.isChipAlive() == false){
            gamePaused = true;
            timeRuunableThread.setPause(gamePaused);
            if(muteMusic == false){
                music.stopPlayingAudio("All");
                music.playAudio("LevelCompleteSound");
            }
            musicName = "LevelCompleteSound";
            JOptionPane.showMessageDialog(null, "Oops! Better luck next time!", "Failed", 1);
            canLoadGame = false;
        } else if(board.isLevelFinished()){
            gamePaused = true;
            timeRuunableThread.setPause(gamePaused);
            if(muteMusic == false){
                music.stopPlayingAudio("All");
                music.playAudio("LevelCompleteSound");
            }
            musicName = "LevelCompleteSound";
            if(level == 1){
                int saveOrNot = JOptionPane.showConfirmDialog(null, "congratulations! Level 1 passed! Go to next level?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (saveOrNot == 0) { // yes
                    level = level + 1;
                    newGameStart(level);
                } else {
                    gamePaused = true;
                }
            }else if(level == 2){
                JOptionPane.showMessageDialog(null, "congratulations! Level 2 passed!", "Win Level 2", JOptionPane.INFORMATION_MESSAGE);
            }
        }else if(board.onInfoTile()){
            JOptionPane.showMessageDialog(null, readAllLines(), "Information", JOptionPane.INFORMATION_MESSAGE);
        }else{
            try {
                infoCanvas.drawChipsLeftNumber(board.getTreasureRemainingAmount());
                infoCanvas.drawKeysChipsPics();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Defines a Triangle class.
     */
    class Triangle {
        /**
         * One point of a triangle.
         */
        private Point p1;

        /**
         * One point of a triangle.
         */
        private Point p2;

        /**
         * One point of a triangle.
         */
        private Point p3;

        /**
         * Path of a triangle.
         */
        private GeneralPath path;

        /**
         * Construct a triangle using three points.
         * @param p1 first point.
         * @param p2 second point.
         * @param p3 third point.
         */
        public Triangle(Point p1, Point p2, Point p3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.path = buildPath();
        }

        /**
         * Draw triangle.
         * @param g2d graphics for drawing.
         */
        public void draw(Graphics2D g2d) {
            g2d.draw(path);
        }

        /**
         * Fill triangle.
         * @param g2d graphics for drawing.
         */
        public void fill(Graphics2D g2d) {
            g2d.fill(path);
        }

        /**
         * Build the path of triangle.
         * @return the general path of the triangle.
         */
        private GeneralPath buildPath() {
            path = new GeneralPath();
            path.moveTo(p1.x, p1.y);
            path.lineTo(p2.x, p2.y);
            path.lineTo(p3.x, p3.y);
            path.closePath();
            assert path != null;
            return path;
        }
    }

    /**
     * Get the time from the time thread.
     * @return current time in second.
     */
    public int getSecond() {
        return this.timeRuunableThread.getSecond();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

}