package TextEdit;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class TextEdit extends JFrame implements ActionListener, TextProcessing, SecondaryUIComponents {
    private static JTextArea area;
    private static JFrame frame;
    private static int returnValue = 0;
    private final String DEFAULT_TEXT = "";
    private String prev = DEFAULT_TEXT;
    private String next = DEFAULT_TEXT;
    private SmartUndoManager smartUndoManager = new SmartUndoManager();

    public TextEdit() {
        run();
    }

    public void run() {
        frame = new JFrame(EnumWindowCaption.PRIMARY.caption);

        // Set the look-and-feel (LNF) of the application
        // Try to default to whatever the host system prefers
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(TextEdit.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Set attributes of the app window
        area = new JTextArea();
        area.setLineWrap(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(area);
        frame.setSize(640, 480);
        frame.setVisible(true);

        // Build the menu
        JMenuBar menu_main = new JMenuBar();

        JMenu menu_file = new JMenu(EnumCommandCaption.FILE.getCaption());
        JMenu menu_edit = new JMenu(EnumCommandCaption.EDIT.getCaption());
        JMenu menu_help = new JMenu(EnumCommandCaption.HELP.getCaption());

        JMenuItem menuitem_new = new JMenuItem(EnumCommandCaption.NEW.getCaption());
        JMenuItem menuitem_open = new JMenuItem(EnumCommandCaption.OPEN.getCaption());
        JMenuItem menuitem_save = new JMenuItem(EnumCommandCaption.SAVE.getCaption());
        JMenuItem menuitem_quit = new JMenuItem(EnumCommandCaption.QUIT.getCaption());

        JMenuItem menuitem_clear_edit_history = new JMenuItem(EnumCommandCaption.CLEAR_LAST_EDIT_HISTORY.getCaption());
        JMenuItem menuitem_clear_edit_history_by_n_steps = new JMenuItem(EnumCommandCaption.CLEAR_LAST_EDIT_HISTORY_BY_N_STEPS.getCaption());
        JMenuItem menuitem_clear_edit_history_by_elapsed_Time = new JMenuItem(EnumCommandCaption.CLEAR_LAST_EDIT_HISTORY_BY_ELAPSED_TIME.getCaption());
        JMenuItem menuitem_redo = new JMenuItem(EnumCommandCaption.REDO.getCaption());
        JMenuItem menuitem_undo = new JMenuItem(EnumCommandCaption.UNDO.getCaption());
        JMenuItem menuitem_undo_by_n_steps = new JMenuItem(EnumCommandCaption.UNDO_SEQUENTIALLY_BY_N_STEPS.getCaption());
        JMenuItem menuitem_undo_by_elapsed_time = new JMenuItem(EnumCommandCaption.UNDO_SEQUENTIALLY_BY_ELAPSED_TIME.getCaption());
        JMenuItem menuitem_undo_by_any_order = new JMenuItem(EnumCommandCaption.UNDO_BY_ANY_ORDER.getCaption());
        JMenuItem menuitem_view_last_edits = new JMenuItem(EnumCommandCaption.VIEW_LAST_EDIT_HISTORY.getCaption());

        JMenuItem menuitem_about = new JMenuItem(EnumCommandCaption.ABOUT.getCaption());

        menuitem_new.addActionListener(this);
        menuitem_open.addActionListener(this);
        menuitem_save.addActionListener(this);
        menuitem_quit.addActionListener(this);

        menuitem_clear_edit_history.addActionListener(this);
        menuitem_clear_edit_history_by_n_steps.addActionListener(this);
        menuitem_clear_edit_history_by_elapsed_Time.addActionListener(this);
        menuitem_redo.addActionListener(this);
        menuitem_undo.addActionListener(this);
        menuitem_undo_by_n_steps.addActionListener(this);
        menuitem_undo_by_elapsed_time.addActionListener(this);
        menuitem_undo_by_any_order.addActionListener(this);
        menuitem_view_last_edits.addActionListener(this);

        menuitem_about.addActionListener(this);

        menu_main.add(menu_file);
        menu_main.add(menu_edit);
        menu_main.add(menu_help);

        menu_file.add(menuitem_new);
        menu_file.add(menuitem_open);
        menu_file.add(menuitem_save);
        menu_file.add(menuitem_quit);

        menu_edit.add(menuitem_clear_edit_history);
        menu_edit.add(menuitem_clear_edit_history_by_n_steps);
        menu_edit.add(menuitem_clear_edit_history_by_elapsed_Time);

        menu_edit.add(menuitem_redo);
        menu_edit.add(menuitem_undo);
        menu_edit.add(menuitem_undo_by_n_steps);
        menu_edit.add(menuitem_undo_by_elapsed_time);
        menu_edit.add(menuitem_undo_by_any_order);
        menu_edit.add(menuitem_view_last_edits);

        menu_help.add(menuitem_about);

        frame.setJMenuBar(menu_main);
        frame.setVisible(true);

        area.getDocument().addUndoableEditListener(
                new UndoableEditListener() {
                    @Override
                    public void undoableEditHappened(UndoableEditEvent e) {
                        UndoableEdit edit = e.getEdit();

                        //undoManager.addEdit(edit);

                        // update next
                        next = area.getText();
                        smartUndoManager.addEdit(edit, prev, next );
                        updateLastEditView(smartUndoManager.lastEdits);

                        // update prev
                        prev = next;
                    }
                }
        );

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ingest = DEFAULT_TEXT;
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        String ae = e.getActionCommand();
        EnumCommandCaption command = EnumCommandCaption.get(ae);
        switch (command) {

            case NEW:

                area.setText(DEFAULT_TEXT);
                prev = area.getText();
                break;

            case OPEN:

                returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File f = new File(jfc.getSelectedFile().getAbsolutePath());
                    try{
                        FileReader read = new FileReader(f);
                        Scanner scan = new Scanner(read);
                        while(scan.hasNextLine()){
                            String line = scan.nextLine() + "\n";
                            ingest = ingest + line;
                        }
                        area.setText(ingest);
                        prev = area.getText();
                    }
                    catch ( FileNotFoundException ex) { ex.printStackTrace(); }
                }
                break;

            case SAVE:

                returnValue = jfc.showSaveDialog(null);
                try {
                    File f = new File(jfc.getSelectedFile().getAbsolutePath());
                    FileWriter out = new FileWriter(f);
                    out.write(area.getText());
                    out.close();
                } catch (FileNotFoundException ex) {
                    Component f = null;
                    JOptionPane.showMessageDialog(f,"File not found.");
                } catch (IOException ex) {
                    Component f = null;
                    JOptionPane.showMessageDialog(f,"Error.");
                }
                break;

            case QUIT:

                System.exit(0);
                break;

            case CLEAR_LAST_EDIT_HISTORY:

                // clear all edits
                this.smartUndoManager.clear();
                updateLastEditView(this.smartUndoManager.lastEdits);
                break;

            case CLEAR_LAST_EDIT_HISTORY_BY_N_STEPS:

                // TODO: ..
                break;

            case CLEAR_LAST_EDIT_HISTORY_BY_ELAPSED_TIME:

                // TODO: ...
                break;

            case REDO:

                // TODO: (OPTIONAL) review, and if possible, add an extra adjustment for multiple edits
                // NOTE: a simple redo would work as this

                /*
                if (undoManager.canRedo()) {
                    undoManager.redo();
                    next = area.getText();
                    System.out.println("applied redo");
                } else
                    System.out.println("cannot redo");
                    */

                /*
                if (smartUndoManager.undoManager.canRedo()) {
                    smartUndoManager.undoManager.redo();
                    next = area.getText();
                    System.out.println("applied redo");
                } else
                    System.out.println("cannot redo");
                    */

                this.smartUndoManager.redo();

                // update view
                updateLastEditView(this.smartUndoManager.lastEdits);

                break;

            case UNDO:

                // NOTE: a simple undo would work as this:

                /*
                if (undoManager.canUndo()) {
                    undoManager.undo();
                    next = area.getText();
                    System.out.println("applied undo");
                } else
                    System.out.println("cannot undo");
                 */

                // but, it the same as this with N=1
                //this.smartUndoManager.undoMultipleNToLastEdits(1);
                this.smartUndoManager.undo();

                // update view
                updateLastEditView(this.smartUndoManager.lastEdits);

                break;

            case UNDO_SEQUENTIALLY_BY_N_STEPS:

                int steps;
                try {
                    steps = Integer.parseInt(getUserInput(
                            frame,
                            "To undo latest edits, please specify how many of them below:",
                            "1"
                    ));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    steps = 0;
                }
                this.smartUndoManager.undoMultipleNToLastEdits(steps);

                // update view
                updateLastEditView(this.smartUndoManager.lastEdits);

                break;

            case UNDO_SEQUENTIALLY_BY_ELAPSED_TIME:

                double seconds;
                try {
                    seconds = Double.parseDouble(getUserInput(
                            frame,
                            "To undo recent edits, please specify a time span (in seconds) below:",
                            "5"
                    ));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    seconds = 0.0;

                }
                int expectedSteps = this.smartUndoManager.getStepsToUndoInLatestSeconds(seconds);
                this.smartUndoManager.undoMultipleNToLastEdits(expectedSteps);

                // update view
                updateLastEditView(this.smartUndoManager.lastEdits);

                break;

            case UNDO_BY_ANY_ORDER:

                // TODO: implement or remove
                String input = getUserInput(
                        frame,
                        "To undo recent edits, please list them below (e.g. 7,35):",
                        null);
                System.out.println("TODO: implement any-order undo... (user entered:" + input + ")");
                break;

            case VIEW_LAST_EDIT_HISTORY:

                updateLastEditView(this.smartUndoManager.lastEdits, true);
                break;

            case ABOUT:

                // TODO: (OPTIONAL) add a Splash Screen that can get reloaded from here
                System.out.println("ABOUT THIS...");
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + ae);
        }
    }


}


