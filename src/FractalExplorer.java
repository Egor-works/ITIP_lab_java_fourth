import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

public class FractalExplorer {
    /** Целое число «размер экрана», которое является шириной и высотой
     отображения в пикселях **/
    private int size;

    /** Ссылка JImageDisplay, для обновления отображения в разных
     методах в процессе вычисления фрактала **/
    private JImageDisplay jDisplay;

    /** Будет использоваться ссылка на базовый
     класс для отображения других видов фракталов в будущем **/
    private FractalGenerator fractal;

    /** Объект Rectangle2D.Double, указывающий диапазона комплексной
     плоскости, которая выводится на экран **/
    private Rectangle2D.Double range;

    public FractalExplorer (int display_size) {
        size = display_size;

        range = new Rectangle2D.Double();
        fractal = new Mandelbrot();
        fractal.getInitialRange(range);
        jDisplay = new JImageDisplay(display_size, display_size);
    }

    /** метод createAndShowGUI (), инициализирует
     графический интерфейс Swing: JFrame, содержащий объект JImageDisplay, и
     кнопку для сброса отображения **/
    public void createAndShowGUI () {
        JFrame frame = new JFrame("Fractal Explorer");

        jDisplay.setLayout(new BorderLayout());
        frame.add(jDisplay, BorderLayout.CENTER);

        JButton button = new JButton("Reset Display");
        frame.add(button, BorderLayout.SOUTH);

        InActionListener clearAction = new InActionListener();
        button.addActionListener(clearAction);

        InMouseListener mouseListener = new InMouseListener();
        jDisplay.addMouseListener(mouseListener);

        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

        //Данные операции правильно разметят содержимое окна, сделают его
        //видимым (окна первоначально не отображаются при их создании для того,
        //чтобы можно было сконфигурировать их прежде, чем выводить на экран), и
        //затем запретят изменение размеров окна
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    /** drawFractal проходится по каждому пикселю и
     * зарисовывает его в соответствии с количеством проделанных итераций **/
    private void drawFractal () {
        for (int x = 0; x < size; x ++) {
            for (int y = 0; y < size; y ++) {
                //x, y - пиксельные координаты; xCoord, yCoord - координаты в пространстве фрактала
                double xCoord = fractal.getCoord(range.x,range.x + range.width, size, x);
                double yCoord = fractal.getCoord(range.y, range.y + range.height, size, y);
                int numIters = fractal.numIterations(xCoord,yCoord);

                if (numIters == -1) jDisplay.drawPixel(x,y,0);

                else {
                    float hue = 0.7f + (float) numIters / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    //получается плавная последовательность цветов от
                    //красного к желтому, зеленому, синему, фиолетовому и затем обратно к
                    //красному
                    jDisplay.drawPixel(x, y, rgbColor);
                }
            }
        }
        jDisplay.repaint();
    }

    public class InActionListener implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent event) {
            fractal.getInitialRange(range);
            drawFractal();
        }
    }

    private class InMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            int x = event.getX();
            double xCoord = fractal.getCoord(range.x, range.x+range.width, size,x);

            int y = event.getY();
            double yCoord = fractal.getCoord(range.y, range.y+range.height, size,y);

            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);
            // При получении события о щелчке мышью, класс
            //отображает пиксельные кооринаты щелчка в область фрактала, а затем вызвает
            //метод генератора recenterAndZoomRange() с координатами, по которым
            //щелкнули, и масштабом 0.5
            drawFractal();
        }
    }

    public static void main (String[] args) {

        FractalExplorer display = new FractalExplorer(800);
        display.createAndShowGUI();
        display.drawFractal();
    }
}