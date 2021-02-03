package view;

import client.Client;
import model.Category;
import model.Dish;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;

public class ViewSwing extends JFrame {

    /** Поле для окна */
    private static JFrame frame;
    /** Поле для файла блюд */
    private static File file;
    /** Поле для файла клиента*/
    private static Client client;

    /**
     * Конструктор - создает клиент и запускает программу
     */
    ViewSwing() throws IOException {
        client = new Client(8000);
        begin();
    }

    /**
     * Метод окна с кнопками открыть и загрузить
     */
    private void begin(){
        frame = new JFrame("Меню");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(550, 350));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.stop();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel plain = new JPanel();

        GridLayout layout = new GridLayout(5, 0, 5, 15);
        plain.setLayout(layout);

        plain.setBackground(new Color(176, 224, 230));

        plain.add(new JLabel("<html> <br> <br></html>"));
        JLabel name = new JLabel("  Редактор меню ресторана.");
        name.setFont(new Font("Italic", Font.PLAIN, 20));
        plain.add(name);
        plain.add(new JLabel(" Загрузите файл с меню или создайте новый:"));
        JButton open = new JButton("Создать");
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    client.file("newDish");
                    newFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(open);

        JButton download = new JButton("Загрузить");
        download.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    download(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(download);

        JPanel plain0 = new JPanel(new FlowLayout(FlowLayout.CENTER));

        plain0.add(plain);
        plain0.setBackground(new Color(176, 224, 230));

        frame.add(plain0);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Метод окна для создания нового файла
     */
    private void newFile(){

        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));
        GridLayout layout = new GridLayout(3, 0, 5, 15);
        plain.setLayout(layout);

        plain.add(new JLabel("<html> <br> <br></html>"));

        plain.add(new JLabel("<html>     У вас пустое меню, <br>чтобы продолжить работу нужно добавить блюда.</html>"));
        JButton add = new JButton("Добавить");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    addDish();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(add);

        JPanel plain0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        plain0.add(plain);
        plain0.setBackground(new Color(176, 224, 230));
        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain0);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод получения файла
     */
    private void download(int k) throws IOException {
        JFileChooser dialog = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "json file", "json");
        dialog.setFileFilter(filter);
        int result = dialog.showOpenDialog(this);
        file = dialog.getSelectedFile();
        String s = "";
        if (result == JFileChooser.APPROVE_OPTION ){
            if (k == 0) {
                s = client.file(file.getName());
                if (s.equals("yes")){
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setPreferredSize(new Dimension(250, 150));

                    JPanel panel = new JPanel();

                    panel.add(new JLabel("<html>Такой файл уже есть на сервере.<br>Вы хотите его перезаписать?</html>"));

                    JButton yes = new JButton("Да");
                    yes.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            try {
                                if (file.length() == 0){
                                    newFile(); return;
                                }
                                else {
                                    String s = client.file("newData", file.getAbsolutePath());
                                }
                                frame.setVisible(false);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    JButton no = new JButton("Нет");
                    no.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            try {
                                client.file();
                                frame.setVisible(false);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    panel.add(yes);
                    panel.add(no);
                    frame.add(panel);
                    frame.pack();
                    frame.setVisible(true);
                }
                else if (s.equals("not")){
                    if (file.length() == 0){
                        newFile(); return;
                    }
                    else {
                        client.serialize(file.getAbsolutePath());
                        s = client.file0();
                    }
                }
            } else if (k == 1){
                client.save("saveNewFile",file.getAbsolutePath());
                JOptionPane.showMessageDialog(null, "Сохранение прошло успешно!");
            } else{
                s  = client.addFile("addFile",file.getAbsolutePath());
                if (s.equals("No")){
                    JOptionPane.showMessageDialog(null, "Что-то пошло не так!");
                } else{
                    JOptionPane.showMessageDialog(null, "Добавление прошло успешно!");
                }
            }
            if (!s.equals("empty")){
                menu();
            }
        }
    }

    /**
     * Метод с меню
     */
    private void menu(){
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));

        GridLayout layout = new GridLayout(6, 0, 5, 15);
        plain.setLayout(layout);

        plain.add(new JLabel("<html> <br> <br></html>"));
        JButton view = new JButton("Посмотреть меню");
        view.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                view();
            }
        });
        plain.add(view);

        JButton add = new JButton("Добавить данные");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });
        plain.add(add);

        JButton edit = new JButton("Редактировать меню");
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    edit();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        plain.add(edit);

        JButton search = new JButton("Поиск по меню");
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        plain.add(search);

        JButton save = new JButton("Сохранить");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        plain.add(save);

        JPanel plain0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        plain0.add(plain);
        plain0.setBackground(new Color(176, 224, 230));

        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain0);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод с меню для просмотра
     */
    private void view(){

        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));

        GridLayout layout = new GridLayout(5, 0, 5, 15);
        plain.setLayout(layout);

        plain.add(new JLabel("<html> <br> <br></html>"));

        JButton viewFull = new JButton("Все меню");
        viewFull.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    viewFullMenu();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(viewFull);

        JButton viewMC = new JButton("Меню по категориям");
        viewMC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    viewDishByCategory();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        plain.add(viewMC);

        JButton viewCat = new JButton("Категории");
        viewCat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    viewCategory();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        plain.add(viewCat);

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu();
            }
        });
        plain.add(cancel);

        JPanel plain0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        plain0.add(plain);
        plain0.setBackground(new Color(176, 224, 230));
        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain0);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод для просмотра всего меню
     */
    private void viewFullMenu() throws IOException {
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));

        Object[] columnsHeader = new String[] {"№","Блюдо", "Категория", "Цена"};
        if (client.updateCheck()) update();
        String[] dish_arr = client.print("printDish").split("\\*");
        Object[][] array = new String[dish_arr.length/4][4];
        for (int i = 0; i < dish_arr.length/4;i++){
            for (int k = 0; k < 4; k ++){
                array[i][k] = dish_arr[i*4+k];
            }
        }
        Comparator comparator = new ComparatorMenu();
        sort(comparator,array);
        for (int i = 0; i < dish_arr.length/4;i++){
            array[i][0] = Integer.toString(i);
        }


        // Таблица с настройками
        JTable table = new JTable(array,columnsHeader){
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
        table.setFillsViewportHeight(true);
        JScrollPane table_scroll = new JScrollPane(table);
        table_scroll.setPreferredSize(new Dimension(500,200));

        JButton edit = new JButton("Редактировать");
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    edit();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                view();
            }
        });
        plain.add(table_scroll);
        plain.add(edit);
        plain.add(cancel);

        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод окна для просмотра блюд по категориям
     */
    private void viewDishByCategory() throws IOException {
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));

        if (client.updateCheck()) update();
        String[] category = client.print("printCategory").split("\\*");

        JComboBox comboBox = new JComboBox(category);
        comboBox.setEditable(false);
        plain.add(comboBox);


        Object[] columnsHeader = new String[] {"№","Блюдо", "Категория",
                "Цена"};
        Object[][] array = new String[0][0];
        DefaultTableModel model = new DefaultTableModel(array, columnsHeader);
        // Таблица с настройками
        JTable table = new JTable(model){
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
        table.setFillsViewportHeight(true);
        JScrollPane table_scroll = new JScrollPane(table);
        table_scroll.setPreferredSize(new Dimension(500,200));
        plain.add(table_scroll);

        JButton ok = new JButton("Показать");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (client.updateCheck()) update();
                    String[] dish_arr = client.print("printDishByCategory",comboBox.getSelectedItem().toString()).split("\\*");
                    Object[][] arrayNew = new String[dish_arr.length/4][4];
                    for (int i = 0; i < dish_arr.length/4;i++){
                        for (int k = 0; k < 4; k ++){
                            arrayNew[i][k] = dish_arr[i*4+k];
                        }
                    }
                    Comparator comparator = new ComparatorMenu();
                    sort(comparator,arrayNew);
                    for (int i = 0; i < dish_arr.length/4;i++){
                        arrayNew[i][0] = Integer.toString(i);
                    }
                    model.setRowCount(0);
                    DefaultTableModel modelNew = (DefaultTableModel)table.getModel();
                    for (Object[] row : arrayNew) {
                        modelNew.addRow(row);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(ok);

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                view();
            }
        });
        plain.add(cancel);

        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод окна для просмотра категорий
     */
    private void viewCategory() throws IOException {
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));

        Object[] columnsHeader = new String[] {"№","Категории"};
        if (client.updateCheck()) update();
        String[] category = client.print("printCategory").split("\\*");
        Object[][] array = new String[category.length][2];
        for (int i = 0; i < category.length; i++){
            array[i][0] = Integer.toString(i+1);
            array[i][1] = category[i];
        }
        Comparator comparator = new ComparatorMenu();
        sort(comparator,array);
        for (int i = 0; i < category.length/4;i++){
            array[i][0] = Integer.toString(i);
        }
        // Таблица с настройками
        JTable table = new JTable(array,columnsHeader){
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
        table.setFillsViewportHeight(true);
        JScrollPane table_scroll = new JScrollPane(table);
        table_scroll.setPreferredSize(new Dimension(500,200));

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                view();
            }
        });
        plain.add(table_scroll);
        plain.add(cancel);

        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод окна с меню добавления
     */
    private void add(){
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));

        GridLayout layout = new GridLayout(5, 0, 5, 15);
        plain.setLayout(layout);

        plain.add(new JLabel("<html> <br> <br></html>"));
        JButton addDish = new JButton("Добавить блюдо");
        addDish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    addDish();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(addDish);

        JButton addCategory = new JButton("Добавить категорию");
        addCategory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCategory();

            }
        });
        plain.add(addCategory);

        JButton addFile = new JButton("Добавить данные из файла");
        addFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFile();
            }
        });
        plain.add(addFile);

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu();
            }
        });
        plain.add(cancel);

        JPanel plain0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        plain0.add(plain);
        plain0.setBackground(new Color(176, 224, 230));
        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain0);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод окна с добавлением блюд
     */
    private void addDish() throws IOException {
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));
        GridLayout layout = new GridLayout(9, 2, 5, 7);
        plain.setLayout(layout);

        //plain.add(new JLabel(""));

        JTextField name = new JTextField(10);
        name.setFont(new Font("Dialog", Font.PLAIN, 20));
        name.setHorizontalAlignment(JTextField.LEFT);

        name.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if ( !isName(name.getText())){
                    JOptionPane.showMessageDialog(null, "Имя блюда/категории может содержать только буквы, цифры, пробелы и тире!");
                    name.setText("");
                }
            }
        });


        if (client.updateCheck()) update();
        String[] category = client.print("printCategory").split("\\*");

        JComboBox comboBox = new JComboBox(category);
        comboBox.setEditable(true);
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if ( !isName(comboBox.getSelectedItem().toString())){
                    JOptionPane.showMessageDialog(null, "Имя блюда/категории может содержать только буквы, цифры, пробелы и тире!");
                    comboBox.setSelectedItem("");
                }
            }
        });

        JTextField price = new JTextField(10);
        price.setFont(new Font("Dialog", Font.PLAIN, 20));
        price.setHorizontalAlignment(JTextField.LEFT);

        price.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if ( !isPrice(price.getText())){
                    JOptionPane.showMessageDialog(null, "Цена - это положительное число!");
                    price.setText("");
                }
            }
        });

        plain.add(new JLabel("Название категории :"));
        plain.add(name);
        plain.add(new JLabel("Категория :"));
        plain.add(comboBox);
        plain.add(new JLabel("Цена :"));
        plain.add(price);

        JButton add = new JButton("Добавить");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dish dish = new Dish(name.getText(),
                        new Category(comboBox.getSelectedItem().toString()),
                        Double.parseDouble(price.getText()));
                try {
                    if (client.updateCheck()) update();
                    String res = client.addData("addData",dish);
                    if (res.equals("Yes")){
                        JOptionPane.showMessageDialog(null, "Добавление прошло успешно");
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Такое блюдо уже есть!");
                    }
                    addDish();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(add);

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });

        plain.add(cancel);

        JPanel plain0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        plain0.add(plain);
        plain0.setBackground(new Color(176, 224, 230));
        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain0);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод окна с добавлением категорий
     */
    private void addCategory(){
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));

        GridLayout layout = new GridLayout(5, 0, 5, 15);
        plain.setLayout(layout);

        plain.add(new JLabel("<html> <br> <br></html>"));

        JTextField name = new JTextField(25);
        name.setFont(new Font("Dialog", Font.PLAIN, 14));
        name.setHorizontalAlignment(JTextField.LEFT);

        name.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if ( !isName(name.getText())){
                    JOptionPane.showMessageDialog(null, "Имя блюда/категории может содержать только буквы, цифры, пробелы и тире!");
                    name.setText("");
                }
            }
        });

        plain.add(new JLabel("Название категории :"));
        plain.add(name);

        JButton add = new JButton("Добавить");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (client.updateCheck()) update();
                    String res = client.addData("addCategory",name.getText());
                    if (res.equals("Yes")){
                        JOptionPane.showMessageDialog(null, "Добавление прошло успешно");
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Такая категория уже есть!");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(add);

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu();
            }
        });
        plain.add(cancel);

        JPanel plain0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        plain0.add(plain);
        plain0.setBackground(new Color(176, 224, 230));
        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain0);
        frame.getContentPane().revalidate();
    }

    private void addFile(){
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));
        GridLayout layout = new GridLayout(4, 0, 5, 15);
        plain.setLayout(layout);

        plain.add(new JLabel("<html> <br> <br><br></html>"));
        JButton addF = new JButton("Добавить данные из файла");
        addF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Выберите файл с данными");
                try {
                    download(2);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(addF);

        JButton addFS = new JButton("Добавить данные из файла с сервера");
        addFS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    addFS();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        plain.add(addFS);

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });
        plain.add(cancel);

        JPanel plain0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        plain0.add(plain);
        plain0.setBackground(new Color(176, 224, 230));
        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain0);
        frame.getContentPane().revalidate();

    }

    private void addFS() throws IOException {
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));

        GridLayout layout = new GridLayout(4, 0, 5, 15);
        plain.setLayout(layout);

        plain.add(new JLabel("<html> <br> <br><br></html>"));

        if (client.updateCheck()) update();

        String[] category = client.print("searchFile").split("\\*");
        JComboBox comboBox = new JComboBox(category);
        comboBox.setEditable(false);
        plain.add(comboBox);

        JButton add = new JButton("Добавить");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (client.updateCheck()) update();
                    String s = client.addFile("addFileServer",comboBox.getSelectedItem().toString());
                    if (s.equals("No")){
                        JOptionPane.showMessageDialog(null, "Данные не добавились! Возможно файл пустой");
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Данные успешно добавились");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(add);

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });
        plain.add(cancel);

        JPanel plain0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        plain0.add(plain);
        plain0.setBackground(new Color(176, 224, 230));
        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain0);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод окна с поиском
     */
    private void search(){
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));

        plain.add(new Label("Введите запрос для поиска(Например, тор?ик*)"));
        JTextField search = new JTextField(25);
        search.setFont(new Font("Dialog", Font.PLAIN, 14));
        search.setHorizontalAlignment(JTextField.LEFT);
        plain.add(search);

        plain.add(new Label("Как искать: "));

        JComboBox comboBox = new JComboBox(new String[]{"по категории","по блюду"});
        comboBox.setEditable(false);
        plain.add(comboBox);


        Object[] columnsHeader = new String[] {"№","Блюдо", "Категория",
                "Цена"};
        Object[][] array = new String[0][0];
        DefaultTableModel model = new DefaultTableModel(array, columnsHeader);
        // Таблица с настройками
        JTable table = new JTable(model){
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
        table.setFillsViewportHeight(true);
        JScrollPane table_scroll = new JScrollPane(table);
        table_scroll.setPreferredSize(new Dimension(500,200));
        plain.add(table_scroll);

        JButton searchB = new JButton("Найти");
        searchB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (client.updateCheck()) update();
                    String method = "getDataByCategory";
                    if (comboBox.getSelectedIndex() == 1){
                        method = "getDataByName";
                    }
                    String[] dish_arr = client.print(method,search.getText()).split("\\*");
                    Object[][] arrayNew = new String[dish_arr.length/4][4];
                    for (int i = 0; i < dish_arr.length/4;i++){
                        for (int k = 0; k < 4; k ++){
                            arrayNew[i][k] = dish_arr[i*4+k];
                        }
                    }
                    Comparator comparator = new ComparatorMenu();
                    sort(comparator,arrayNew);
                    for (int i = 0; i < dish_arr.length/4;i++){
                        arrayNew[i][0] = Integer.toString(i);
                    }
                    model.setRowCount(0);
                    DefaultTableModel modelNew = (DefaultTableModel)table.getModel();
                    for (Object[] row : arrayNew) {
                        modelNew.addRow(row);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(searchB);

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu();
            }
        });

        plain.add(cancel);

        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод окна с сохранением
     */
    private void save(){
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));
        GridLayout layout = new GridLayout(4, 0, 5, 15);
        plain.setLayout(layout);

        plain.add(new JLabel("<html> <br> <br><br></html>"));

        JButton save = new JButton("Сохранить");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (client.updateCheck()) update();
                    client.save("saveFile");
                    JOptionPane.showMessageDialog(null, "Сохранение прошло успешно!");

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(save);

        JButton addCategory = new JButton("Сохранить как копию");
        addCategory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Выберите файл для сохранения");
                try {
                    download(1);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(addCategory);

        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu();
            }
        });
        plain.add(cancel);

        JPanel plain0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        plain0.add(plain);
        plain0.setBackground(new Color(176, 224, 230));

        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain0);
        frame.getContentPane().revalidate();
    }

    /**
     * Метод окна с редактированием
     */
    private void edit() throws IOException {
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));

        Object[] columnsHeader = new String[] {"№","Блюдо", "Категория", "Цена"};
        if (client.updateCheck()) update();
        String ff =  client.print("printDish");
        String[] dish_arr =ff.split("\\*");
        Object[][] array = new String[dish_arr.length/4][4];
        Object[][] arrayCopy = new String[dish_arr.length/4][4];
        for (int i = 0; i < dish_arr.length/4;i++){
            for (int k = 0; k < 4; k ++){
                array[i][k] = dish_arr[i*4+k];
                arrayCopy[i][k] = dish_arr[i*4+k];
            }
        }
        Comparator comparator = new ComparatorMenu();
        sort(comparator,array);
        sort(comparator,arrayCopy);
        for (int i = 0; i < dish_arr.length/4;i++){
            array[i][0] = Integer.toString(i);
        }
        DefaultTableModel model = new DefaultTableModel(array, columnsHeader);
        // Таблица с настройками
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);

        CellEditorListener ChangeNotification = new CellEditorListener() {
            public void editingCanceled(ChangeEvent e) { }

            public void editingStopped(ChangeEvent e) {
                int row = table.getSelectionModel().getLeadSelectionIndex() ;
                int col = table.getColumnModel().getSelectionModel().getLeadSelectionIndex();

                if (col == 0)  table.setValueAt(array[row][col],row,col);
                else if ((col == 1 || col==2) && !isName(table.getValueAt(row,col).toString())){
                    JOptionPane.showMessageDialog(null, "Имя блюда/категории может содержать только буквы, цифры, пробелы и тире!");
                    table.setValueAt(array[row][col],row,col);
                }
                else if (col == 3 && !isPrice(table.getValueAt(row,col).toString())){
                    JOptionPane.showMessageDialog(null, "Цена - это положительное число!");
                    table.setValueAt(array[row][col],row,col);
                }
            }
        };

        table.getDefaultEditor(String.class).addCellEditorListener(ChangeNotification);

        JScrollPane table_scroll = new JScrollPane(table);
        table_scroll.setPreferredSize(new Dimension(500,200));
        plain.add(table_scroll);

        JButton edit = new JButton("Сохранить изменения");
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    table.changeSelection(table.getEditingRow(), table.getEditingColumn(), false, false);
                    for (int i = 0; i < dish_arr.length/4; i ++){
                        for (int k = 1; k < 4; k++){
                            if (!table.getValueAt(i,k).toString().equals(arrayCopy[i][k])){
                                if (client.updateCheck()) update();
                                if (k == 1) {
                                    client.setData("setNameByName",arrayCopy[i][1].toString(), table.getValueAt(i,k).toString());
                                } else if (k == 2) {
                                    client.setData("setCategoryByName",arrayCopy[i][1].toString(), table.getValueAt(i,k).toString());
                                } else if (k == 3) {
                                    client.setData("setPriceByName",arrayCopy[i][1].toString(),Double.parseDouble(table.getValueAt(i,k).toString()));
                                }
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Изменение прошло успешно!");
                    frame.getContentPane().removeAll();
                    frame.getContentPane().invalidate();
                    edit();
                }catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(edit);

        JButton discard = new JButton("Сбросить");
        discard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    edit();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(discard);

        JButton delete = new JButton("Удалить выделенную строку");
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int idx = table.getSelectedRow();
                    model.removeRow(idx);
                    if (client.updateCheck()) update();
                    client.setData("deleteData",array[idx][1].toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        plain.add(delete);



        JButton cancel = new JButton("Назад");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu();
            }
        });
        plain.add(cancel);

        frame.getContentPane().removeAll();
        frame.getContentPane().invalidate();
        frame.getContentPane().add(plain);
        frame.getContentPane().revalidate();
    }

    public static void main (String [] args) throws IOException {
        ViewSwing windowApplication = new ViewSwing();
        //client.stop();
    }

    /**
     * Метод сортировки для таблицы с блюдами или категориями по алфавиту
     * @param comparator - компаратор
     * @param o - объект, который будут сортировать
     */
    private static <T> void sort(Comparator<T> comparator, T... o){
        for (int i = 0; i < o.length; i++){
            for (int j = i+1; j < o.length; j++){
                if (comparator.compare(o[i],o[j]) > 0){
                    T temp = o[i];
                    o[i] = o[j];
                    o[j] = temp;
                }
            }
        }
    }

    /**
     * Метод окна на случай обновления файла другим пользователем
     */
    public void update(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(250, 250));
        JPanel plain = new JPanel();
        plain.setBackground(new Color(176, 224, 230));
        plain.setLayout(new BoxLayout(plain, BoxLayout.PAGE_AXIS));
        plain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        plain.add(new Label("<html>Файл был обновлен другим пользователем! <br>Вы хотите продолжить работу с обноленным файлом?</html>"));

        JButton yes = new JButton("Да");
        yes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    client.newFile("openNewFile");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        JButton yesCopy = new JButton("Да, но сохранить мои изменения как копию");
        yesCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Выберите файл для сохранения");
                try {
                    download(1);
                    client.newFile("openNewFile");
                    frame.setVisible(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton no = new JButton("Нет");
        no.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        });

        plain.add(yes);
        plain.add(yesCopy);
        plain.add(no);

        frame.add(plain);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Метод проверки цены
     * @param str - цена
     * * @return значение истина или ложь
     */
    public static boolean isPrice(String str) {
        try {
            double d = Double.parseDouble(str);
            if (d < 0) return false;
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Метод проверки имени
     * @param str - имя
     * * @return значение истина или ложь
     */
    public static boolean isName(String str) {
        if (str.matches("^[0-9а-яА-яa-zA-z[-]]{2,20}$")){
            return true;
        }
       return false;
    }



}
