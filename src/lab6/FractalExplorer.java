package lab6;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import javax.swing.JFileChooser.*;
import javax.swing.filechooser.*;
import javax.imageio.ImageIO.*;
import java.awt.image.*;
public class FractalExplorer {
	 /** Fields for save button, reset button, and combo box for enableUI. **/
    private JButton saveButton;//Поле для кнопки сохранения
    private JButton resetButton;//Поле для кнопки сброса 
    private JComboBox myComboBox;//Поле cо списком 
    
    private int rowsRemaining; //Количество строк, оставшихся для рисования

    private int display_size;// Целое число «размер экрана», которое является шириной и высотой отображения в пикселях 
    
    private JImageDisplay display; //Ссылка JImageDisplay, для обновления отображения в разных методах в процессе вычисления фрактала
    
    private FractalGenerator fractal; // Объект FractalGenerator
    
    private Rectangle2D.Double range;//Объект Rectangle2D.Double, указывающий диапазон комплексной плоскости, которая выводится на экран
   
    public FractalExplorer(int size) /* Конструктор, который принимает значение размера отображения в качестве аргумента, 
        затем сохраняет это значение в соответствующем поле, а также инициализирует объекты диапазона и фрактального генератора */
    {
        display_size = size; 
        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        display = new JImageDisplay(display_size, display_size);
    }
    


    /** Метод, который инициализирует графический интерфейс Swing */
    public void createAndShowGUI()
    {
        display.setLayout(new BorderLayout());
        JFrame frame = new JFrame("Fractal Explorer");
        frame.add(display, BorderLayout.CENTER);
    
        // кнопка сброса изображения
        JButton resetButton = new JButton("Reset");
        frame.add(resetButton, BorderLayout.SOUTH);
        ButtonHandler resetHandler = new ButtonHandler();
        resetButton.addActionListener(resetHandler);
    
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // операция закрытия окна по умолчанию

        JComboBox myComboBox = new JComboBox();//Создание бокса combo box
        //Добавление разных видов (объектов) фракталов в myСomboBox 
        FractalGenerator mandelbrotFractal = new Mandelbrot();
        myComboBox.addItem(mandelbrotFractal);
        FractalGenerator tricornFractal = new Tricorn();
        myComboBox.addItem(tricornFractal);
        FractalGenerator burningShipFractal = new BurningShip();
        myComboBox.addItem(burningShipFractal);
    
        ButtonHandler fractalChooser = new ButtonHandler();
        myComboBox.addActionListener(fractalChooser);

        JPanel myPanel = new JPanel();//Cоздание нового объекта Jpanel
        JLabel myLabel = new JLabel("Fractal:");//Добавление объекта label в разрабатываемый пользовательский интерфейс
        //Добавление объектов
        myPanel.add(myLabel);
        myPanel.add(myComboBox);
        frame.add(myPanel, BorderLayout.NORTH);

        //Создание кнопки сохранения
        JButton saveButton = new JButton("Save");
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(saveButton);
        myBottomPanel.add(resetButton);
        frame.add(myBottomPanel, BorderLayout.SOUTH);

        ButtonHandler saveHandler = new ButtonHandler();
        saveButton.addActionListener(saveHandler);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false); // запрет изменения размеров окна
    }   

    /** Метод, который должен циклически проходить через каждый пиксель в отображении */
    private void drawFractal()
    {
        
        rowsRemaining = display_size;
      //Проход каждой строки через FractalWorker
        for (int x=0; x<display_size; x++){
            FractalWorker drawRow = new FractalWorker(x);
            drawRow.execute();
        } 
         enableUI(false);
    }
    //Включение или выключение кнопок интерфейса и поля со списком
    private void enableUI(boolean val) {
        myComboBox.setEnabled(val);
        resetButton.setEnabled(val);
        saveButton.setEnabled(val);
    }
    /** Внутренний класс для обработки событий 
     * java.awt.event.ActionListener от кнопки сброса */
    public class ButtonHandler implements ActionListener 
    {
	    @Override
	    public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();

            //Получение фрактала находящегося в списке 
            if (e.getSource() instanceof JComboBox) {
                JComboBox mySource = (JComboBox) e.getSource();
                fractal = (FractalGenerator) mySource.getSelectedItem();
                fractal.getInitialRange(range);
                drawFractal();    
            }
        
            else if (command.equals("Reset")) //Вырисовка фрактала заново
            {
                fractal.getInitialRange(range);
                drawFractal();
            } 
            else if (command.equals("Save")) //Сохранение фракталов 
            {
            JFileChooser myFileChooser = new JFileChooser();
            FileFilter extensionFilter =new FileNameExtensionFilter("PNG Images", "png");//Сохранение файлов только в png
            myFileChooser.setFileFilter(extensionFilter);

            myFileChooser.setAcceptAllFileFilterUsed(false);
            //Выбор папки сохранения
            int userSelection = myFileChooser.showSaveDialog(display);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File file = myFileChooser.getSelectedFile();
                String file_name = file.toString();
                try {//попытка сохранения 
                    BufferedImage displayImage = display.getImage();
                    javax.imageio.ImageIO.write(displayImage, "png", file);
                    }
                catch (Exception exception) {
                    JOptionPane.showMessageDialog(display,
                    exception.getMessage(), "Cannot Save Image",
                    JOptionPane.ERROR_MESSAGE);
                    }
            }
            else return;
        } 
    }
} 
    private  class MouseHandler extends MouseAdapter
    {
        @Override
        public void mouseClicked (MouseEvent e)
        {
            int x = e.getX();
            double xCoord = fractal.getCoord(range.x, range.x + range.width, display_size, x);
            int y = e.getY();
            double yCoord = fractal.getCoord(range.y,range.y + range.height, display_size, y);
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            drawFractal();
        }
    }
   private class FractalWorker extends SwingWorker<Object, Object>//отвечать за вычисление значений цвета для одной строки фрактала
    {
        int yCoordinate;//координата вычисляемой строки
        int[] computedRGBValues;//для хранения вычисленных значений RGB для каждого пикселя в этой строке
        private FractalWorker(int row) {
            yCoordinate = row;
        }

        protected Object doInBackground() {
            computedRGBValues = new int[display_size];

             for (int i = 0; i < computedRGBValues.length; i++)//проход по пикселям в строке 
             {
            //Нахождение соответствующих координаты xCoord и yCoord
            double xCoord = fractal.getCoord(range.x,range.x + range.width, display_size, i);
            double yCoord = fractal.getCoord(range.y,range.y + range.height, display_size, yCoordinate);
            
            int iteration = fractal.numIterations(xCoord, yCoord);//Количество итераций
             if (iteration == -1){
                    computedRGBValues[i] = 0;
                }
            
                else {
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    computedRGBValues[i] = rgbColor;
                }
            }
            return null;
        }
        protected void done() {
         for (int i = 0; i < computedRGBValues.length; i++) {
                display.drawPixel(i, yCoordinate, computedRGBValues[i]);
            }
            display.repaint(0, 0, yCoordinate, display_size, 1);
            rowsRemaining--;
            if (rowsRemaining == 0) {
                enableUI(true);
            }
        }
    }
   public static void main(String[] args)
    {
        FractalExplorer displayExplorer = new FractalExplorer(600);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }

}
